package org.masonapps.robogui;

import android.graphics.Color;
import android.graphics.PointF;

/**
 * Created by Bob on 12/29/2015.
 */

public class DataPoint{
    public PointF point;
    public int color;

    public DataPoint(){
        this(0, 0, Color.GRAY);
    }

    public DataPoint(float x, float y, int color) {
        this.point = new PointF(x, y);
        this.color = color;
    }
}
