package com.surveyor.drawlib.utils;

import srs.Geometry.IPoint;
import srs.Geometry.Point;

/**
 * Created by chengkeqian on 17/11/13.
 */
public class GeometryUtil {

    /**
     * 求两线段的交点(直线方程算法) Line1: 方程:y = k1x + b1; Line2: 方程:y = k2x + b2
     * 根据四个点的坐标，可以推导出来k1,k2和b1,b2
     *
     * @param p1
     * @param p2
     *            p1p2为一条线段
     * @param p3
     * @param p4
     *            p3p4为另一条线段
     * @return 交点IPoint对象
     */
    public static IPoint getIntersection(IPoint p1, IPoint p2, IPoint p3, IPoint p4) {
        IPoint interPoint = new Point();
        double k1, k2, b1, b2;
        if (p1.X() != p2.X() && p3.X() != p4.X()) {
            k1 = (p2.Y() - p1.Y()) / (p2.X() - p1.X());
            k2 = (p4.Y() - p3.Y()) / (p4.X() - p3.X());
            b1 = p1.Y() - (p2.Y() - p1.Y()) / (p2.X() - p1.X()) * p1.X();
            b2 = p3.Y() - (p4.Y() - p3.Y()) / (p4.X() - p3.X()) * p3.X();
            interPoint.X((b2 - b1) / (k1 - k2));
            interPoint.Y((b2 - b1) / (k1 - k2) * k1 + b1);
        } else if (p1.X() == p2.X()) {
            k2 = (p4.Y() - p3.Y()) / (p4.X() - p3.X());
            b2 = p3.Y() - (p4.Y() - p3.Y()) / (p4.X() - p3.X()) * p3.X();
            interPoint.X(p1.X());
            interPoint.Y(k2 * interPoint.X() + b2);
        } else if (p3.X() == p4.X()) {
            k1 = (p2.Y() - p1.Y()) / (p2.X() - p1.X());
            b1 = p1.Y() - (p2.Y() - p1.Y()) / (p2.X() - p1.X()) * p1.X();
            interPoint.X(p3.X());
            interPoint.Y(k1 * interPoint.X() + b1);
        }
        return interPoint;
    }

    /**
     * 判断两线段是否想交(包括相交在端点处)
     *
     * @param p1
     * @param p2
     *            p1p2为一条线段
     * @param q1
     * @param q2
     *            q1q2为另一条线段
     * @return 相交返回True,否则返回False
     */
    public static boolean hasIntersection(final IPoint p1, final IPoint p2, final IPoint q1, final IPoint q2) {
        // 排斥实验
        boolean isRectCross = Math.min(p1.X(), p2.X()) <= Math.max(q1.X(), q2.X())
                && Math.min(q1.X(), q2.X()) <= Math.max(p1.X(), p2.Y())
                && Math.min(p1.Y(), p2.Y()) <= Math.max(q1.Y(), q2.Y())
                && Math.min(q1.Y(), q2.Y()) <= Math.max(p1.Y(), p2.Y());
        // 跨立实验
        // 若P1P2跨立Q1Q2，则矢量(P1-Q1)和(P2-Q1)位于矢量(Q2-Q1)的两侧，
        // 即( P1 - Q1 ) × ( Q2 - Q1 ) * ( P2 - Q1 ) × ( Q2 - Q1 ) < 0。
        // 若Q1Q2跨立P1P2，则矢量(Q1-P1)和(Q2-P1)位于矢量(P2-P1)的两侧，
        // 即( Q1 - P1 ) × ( P2 - P1 ) * ( Q2 - P1 ) × ( P2 - P1 ) < 0。
        boolean isSegsCross = crossMult(p1, q2, q1) * crossMult(p2, q2, q1) < 0
                && crossMult(q1, p2, p1) * crossMult(q2, p2, p1) < 0;
        return isRectCross && isSegsCross;
    }

    /**
     * 计算(sp-op)*(ep-op)的叉积 r>0:ep在矢量opsp的逆时针方向； r=0：opspep三点共线；
     * r<0:ep在矢量opsp的顺时针方向
     *
     * @param sp
     * @param ep
     * @param op
     * @return
     */
    public static double crossMult(final IPoint sp, final IPoint ep, final IPoint op) {
        return (sp.X() - op.X()) * (ep.Y() - op.Y()) - (ep.X() - op.X()) * (sp.Y() - op.Y());
    }
}
