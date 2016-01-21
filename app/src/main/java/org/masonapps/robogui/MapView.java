package org.masonapps.robogui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

/**
 * Created by Bob on 12/29/2015.
 */
public class MapView  extends View {

    private static final float DEG2RAD = (float) Math.PI / 180f;
    private float pointRadius;
    private TextPaint mTextPaint;
    ArrayList<RobotState> robotStates = new ArrayList<>();
    private RectF contentRect = new RectF();
    private PointF translation = new PointF();
    private float scale = 1f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){

        public Matrix inverseM = new Matrix();
        public float[] tempPoints = new float[2];

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            translation.set(contentRect.centerX(), contentRect.centerY());
            scale = 1f;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            translation.offset(-distanceX / scale, -distanceY / scale);
            return true;
        }
    };
    private final ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.min(10f, Math.max(0.1f, scale));
            return true;
        }
    };
    private Matrix matrix = new Matrix();
    private Paint positionPaint;
    private Paint gridPaint;
    private float lineWidth;
    private float originHalfSize;
    private float density;
    private int gridMin;
    private int gridMax;
    private int gridStep;
    private Paint barrierPaint;
    private float cm2pixels;
    private final PointF position = new PointF();
    private final PointF tempP = new PointF();

    public MapView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.MapView, defStyle, 0);
//
//        a.recycle();

        gestureDetector = new GestureDetector(getContext(), gestureListener);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        cm2pixels = 10f * density;
        pointRadius = 10f * density;
        lineWidth = 2f * density;
        originHalfSize = 20f * density;
        gridMin = Math.round(-1000 * density);
        gridMax = Math.round(1000 * density);
        gridStep = Math.round(100 * density);
        cm2pixels = gridStep / 10f;

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        positionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        positionPaint.setColor(Color.MAGENTA);
        positionPaint.setStrokeWidth(lineWidth);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0xaaffffff);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(lineWidth);

        barrierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barrierPaint.setColor(Color.GREEN);
        barrierPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        matrix.reset();
        matrix.postTranslate(translation.x, translation.y);
        matrix.postScale(scale, scale);
        canvas.setMatrix(matrix);

        canvas.drawCircle(0, 0, originHalfSize, gridPaint);
        for (int x = gridMin; x <= gridMax; x += gridStep) {
            canvas.drawLine(x, gridMin, x, gridMax, gridPaint);
        }
        for (int y = gridMin; y <= gridMax; y += gridStep) {
            canvas.drawLine(gridMin, y, gridMax, y, gridPaint);
        }
        try {
            position.set(0f, 0f);
            for (int i = 0; i < robotStates.size(); i++) {
                RobotState state = robotStates.get(i);
                if(i >= 1){
                    tempP.set(position);
                    final float a = state.getHeading() * DEG2RAD;
                    final float r = state.getMovement() * cm2pixels;
                    position.x += (float) Math.cos(a) * r;
                    position.y += (float) Math.sin(a) * r;
                    positionPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawLine(tempP.x, tempP.y, position.x, position.y, positionPaint);
                    positionPaint.setStyle(Paint.Style.FILL);
                }
                canvas.drawCircle(position.x, position.y, pointRadius * 0.5f, positionPaint);
                drawBarrierPoint(canvas, state, tempP);
            }
        } catch (ConcurrentModificationException ignored){}
    }
    
    private void drawBarrierPoint(Canvas c, RobotState state, PointF position){
        if(state.getSensorReading() > 0) {
            final float a = (state.getHeading() + state.getSensorAngle()) * DEG2RAD;
            final float r = state.getSensorReading() * cm2pixels;
            c.drawCircle(position.x + (float) Math.cos(a) * r, position.y + (float) Math.sin(a) * r, pointRadius, barrierPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        contentRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        translation.set(contentRect.centerX(), contentRect.centerY());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean gestureResult = gestureDetector.onTouchEvent(event);
        final boolean scaleGestureResult = scaleGestureDetector.onTouchEvent(event);
        final boolean result = gestureResult || scaleGestureResult;
        if(result) invalidate();
        return result;
    }

    public void addState(RobotState state){
        try {
            robotStates.add(state);
        } catch (ConcurrentModificationException ignored){}
        invalidate();
    }
    
    public void addStates(Collection<RobotState> states){
        try {
            robotStates.addAll(states);
        } catch (ConcurrentModificationException ignored){}
        invalidate();
    }

    public void clearMap() {
        try {
            robotStates.clear();
        } catch (ConcurrentModificationException ignored){}
        invalidate();
    }
}
