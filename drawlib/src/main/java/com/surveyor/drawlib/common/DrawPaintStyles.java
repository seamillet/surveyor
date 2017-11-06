package com.surveyor.drawlib.common;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * @author keqian Canvas图形绘制显示样式
 */
public final class DrawPaintStyles {
    public static Paint pointLastPaint = null;
    public static Paint pointFocusedPaint = null;
    public static Paint pointNoFocusedPaint = null;
    public static Paint midPointPaint = null;
    public static Paint linePaintPaint = null;
    public static Paint lineCollectingPaint = null;
    public static Paint polygonPaint = null;

    public static Paint textDistancePaint = null;

    static {
        pointLastPaint = generatePaint(Color.RED, Style.FILL_AND_STROKE, 8, true, true);
        pointFocusedPaint = generatePaint(Color.RED, Style.FILL_AND_STROKE, 5, true, true);
        pointNoFocusedPaint = generatePaint(Color.WHITE, Style.FILL, 5, true, true);
        midPointPaint = generatePaint(Color.WHITE, Style.FILL, 2, true, true);
        linePaintPaint = generatePaint(Color.BLACK, Style.STROKE, 3, true, true);
        lineCollectingPaint = generatePaint(Color.RED, Style.STROKE, 3, true, true);
        polygonPaint = generatePaint(Color.argb(120, 242, 240, 26), Style.STROKE, 1, true, true);

        textDistancePaint = generateTextPaint(Color.rgb(255,140,0), Style.FILL, 24.0f, Paint.Align.CENTER, true, true);  //橙色
    }

    private static Paint generatePaint(int color, Style style, int width, boolean isAntiAlias, boolean isDither) {
        Paint paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        paint.setAntiAlias(isAntiAlias);
        paint.setDither(isDither);
        return paint;
    }

    private static Paint generateTextPaint(int color, Style style, float textSize, Paint.Align textAlign, boolean isAntiAlias, boolean isDither) {
        Paint paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(color);
        paint.setStyle(style);
        paint.setTextAlign(textAlign);
        paint.setTextSize(textSize);
        paint.setAntiAlias(isAntiAlias);
        paint.setDither(isDither);
        return paint;
    }
}
