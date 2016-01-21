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
import java.util.ConcurrentModificationException;


public class PathView extends View {

    private float pointRadius;
    private static final int MAX_POINTS = 100;
    private TextPaint mTextPaint;
    ArrayList<DataPoint> dataPoints = new ArrayList<>();
    private RectF contentRect = new RectF();
    private PointF translation = new PointF();
    private float scale = 1f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean touchToAddEnabled = true;
    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){

        public Matrix inverseM = new Matrix();
        public float[] tempPoints = new float[2];

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(!touchToAddEnabled) return false;
            matrix.reset();
            matrix.postTranslate(translation.x, translation.y);
            matrix.postScale(scale, scale);
            matrix.invert(inverseM);
            tempPoints[0] = e.getX();
            tempPoints[1] = e.getY();
            inverseM.mapPoints(tempPoints);
            addDataPoint(tempPoints[0], tempPoints[1], Color.GREEN);
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
    private Paint pointPaint;
    private Paint linePaint;
    private float lineWidth;
    private float originHalfSize;
    private float density;
    private int gridMin;
    private int gridMax;
    private int gridStep;

    public PathView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.PathView, defStyle, 0);
//
//        a.recycle();

        gestureDetector = new GestureDetector(getContext(), gestureListener);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);
        
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        pointRadius = 10f * density;
        lineWidth = 2f * density;
        originHalfSize = 20f * density;
        gridMin = Math.round(-1000 * density);
        gridMax = Math.round(1000 * density);
        gridStep = Math.round(100 * density);
        
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(lineWidth);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xaaffffff);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        matrix.reset();
        matrix.postTranslate(translation.x, translation.y);
        matrix.postScale(scale, scale);
        canvas.setMatrix(matrix);

        canvas.drawCircle(0, 0, originHalfSize, linePaint);
        for (int x = gridMin; x <= gridMax; x += gridStep) {
            canvas.drawLine(x, gridMin, x, gridMax, linePaint);
        }
        for (int y = gridMin; y <= gridMax; y += gridStep) {
            canvas.drawLine(gridMin, y, gridMax, y, linePaint);
        }
        try {
            for (int i = 0; i < dataPoints.size(); i++) {
                DataPoint dataPoint = dataPoints.get(i);
                pointPaint.setColor(dataPoint.color);
                pointPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(dataPoint.point.x, dataPoint.point.y, pointRadius, pointPaint);
                pointPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(dataPoint.point.x, dataPoint.point.y, pointRadius * 4f, pointPaint);
                if(i > 1){
                    canvas.drawLine(dataPoints.get(i - 1).point.x, dataPoints.get(i - 1).point.y, dataPoint.point.x, dataPoint.point.y, pointPaint);
                }
            }
        } catch (ConcurrentModificationException ignored){}
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

    public void addDataPoint(float x, float y, int color){
        try {
            if (dataPoints.size() >= MAX_POINTS) {
                dataPoints.remove(0);
            }
            dataPoints.add(new DataPoint(x, y, color));
            invalidate();
        } catch (ConcurrentModificationException ignored){}
    }
}
