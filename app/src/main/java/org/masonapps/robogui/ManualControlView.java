package org.masonapps.robogui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Bob on 1/21/2016.
 */
public class ManualControlView extends View {
    
    private TextPaint textPaint;
    private Paint compassPaint;
    
    public ManualControlView(Context context) {
        super(context);
        init();
    }

    public ManualControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManualControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        
        compassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
    }
}
