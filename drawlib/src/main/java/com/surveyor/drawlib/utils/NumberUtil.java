package com.surveyor.drawlib.utils;

import android.graphics.PointF;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import srs.Geometry.IPoint;
import srs.Geometry.Point;

/**
 * Created by willc on 17-10-18.
 */
public class NumberUtil {
    private static NumberFormat defaultFormat = null;
    private static NumberFormat format = null;

    static {
        defaultFormat = NumberFormat.getInstance(new Locale("zh", "CN"));
        defaultFormat.setRoundingMode(RoundingMode.HALF_UP);
        defaultFormat.setMaximumFractionDigits(1);
        format = NumberFormat.getInstance(new Locale("zh", "CN"));
    }

    public static String format(float number) {
        return defaultFormat.format((double) number);
    }

    public static String format(double number) {
        return defaultFormat.format(number);
    }

    public static String format(float number, RoundingMode roundingMode, int maximumFractionDigits) {
        format.setRoundingMode(roundingMode);
        format.setMaximumFractionDigits(maximumFractionDigits);
        return format.format(number);
    }

    public static String format(double number, RoundingMode roundingMode, int maximumFractionDigits) {
        format.setRoundingMode(roundingMode);
        format.setMaximumFractionDigits(maximumFractionDigits);
        return format.format(number);
    }

    public static double getDistance(PointF pt1, PointF pt2) {
        return getDistance(pt1.x, pt1.y, pt2.x, pt2.y);
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static PointF getMidPoint(PointF pt1, PointF pt2) {
        return getMidPoint(pt1.x, pt1.y, pt2.x, pt2.y);
    }

    public static PointF getMidPoint(float x1, float y1, PointF pt2) {
        return getMidPoint(x1, y1, pt2.x, pt2.y);
    }

    public static PointF getMidPoint(PointF pt1, float x2, float y2) {
        return getMidPoint(pt1.x, pt1.y, x2, y2);
    }

    public static PointF getMidPoint(float x1, float y1, float x2, float y2) {
        float x = (x1 + x2) / 2;
        float y = (y1 + y2) / 2;
        return new PointF(x, y);
    }

    public static IPoint getMidPoint(IPoint p1, IPoint p2) {
        double x = (p1.X() + p2.X()) / 2;
        double y = (p1.Y() + p2.Y()) / 2;
        return new Point(x, y);
    }
}
