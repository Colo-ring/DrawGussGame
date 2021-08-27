package com.example.drawguess.point;

import ohos.agp.render.Paint;
import ohos.agp.utils.Color;

import java.nio.channels.FileLock;

public class PointStyles {

//    public static final Float STROKE_WIDTH_5 = 5f;
//    public static final Float STROKE_WIDTH_10 = 10f;
//    public static final Float STROKE_WIDTH_15 = 15f;
//
//    public static final Color BLACK = Color.BLACK;
//    public static final Color GREEN = Color.GREEN;
//    public static final Color RED = Color.RED;

    public static Color COLOR = Color.BLACK;
    public static Float STROKE_WIDTH = 15f;

    public void setBlack() {
        COLOR = Color.BLACK;
    }

    public void setRed() {
        COLOR = Color.RED;
    }

    public void setGreen() {
        COLOR = Color.GREEN;
    }

    public void setStrokeWidth5() {
        STROKE_WIDTH = 5f;
    }

    public void setStrokeWidth10() {
        STROKE_WIDTH = 10f;
    }

    public void setStrokeWidth15() {
        STROKE_WIDTH = 15f;
    }
}
