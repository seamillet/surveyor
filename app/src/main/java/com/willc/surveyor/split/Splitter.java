package com.willc.surveyor.split;

import com.surveyor.drawlib.utils.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.Point;
import srs.Geometry.SpatialOp;

/**
 * @author chengkeqian
 */
public abstract class Splitter {

    protected List<LinkedPoint> mGeoPoints = null;
    protected List<LinkedPoint> mInterPoints = null;

    protected boolean isFstPtInPolygon = false;
    protected boolean isOutSameEdge = false;
    protected boolean isSeparable = false;

    protected Splitter() {
        // 将要裁剪的Geometry的点集合封装成LinkedPoint集合
        mGeoPoints = new ArrayList<LinkedPoint>();
        // 初始化交点的LinkedPoint集合
        mInterPoints = new ArrayList<LinkedPoint>();
    }

    /**
     * 设置裁剪的几何对象的LinkedPoint集合
     *
     * @param geoPoints Geometry(Polygon)的LinkedPoint集合
     */
    public void setLinkedGeoPoints(List<LinkedPoint> geoPoints) {
        mGeoPoints = geoPoints;
    }

    /**
     * 获取裁剪的几何对象的LinkedPoint集合
     *
     * @return Geometry(Polygon)的LinkedPoint集合
     */
    public List<LinkedPoint> getLinkedGeoPoints() {
        return mGeoPoints;
    }

    public boolean isSeparable() {
        return isSeparable;
    }

    public boolean isPointInPolygon(IPoint point) {
        IPoint[] points = new Point[mGeoPoints.size() + 1];
        for (int i = 0; i < mGeoPoints.size(); i++) {
            points[i] = mGeoPoints.get(i).getPoint();
        }
        points[mGeoPoints.size()] = mGeoPoints.get(0).getPoint();

        if (SpatialOp.Point_In_Polygon(point, points)) {
            isFstPtInPolygon = true;
        }
        return isFstPtInPolygon;
    }

    public void checkSeparable(IPoint pStart, IPoint pEnd, int pointIndex) {
        for (int i = 0; i < mGeoPoints.size(); i++) {
            LinkedPoint lp1 = mGeoPoints.get(i);
            LinkedPoint lp2 = mGeoPoints.get(lp1.getNext());
            if (GeometryUtil.hasIntersection(pStart, pEnd, lp1.getPoint(), lp2.getPoint())) {
                IPoint intersectPt = GeometryUtil.getIntersection(pStart, pEnd, lp1.getPoint(), lp2.getPoint());

                LinkedPoint intersectLP = new LinkedPoint();
                intersectLP.setPoint(intersectPt);
                intersectLP.setIndex(pointIndex);
                intersectLP.setPre(i);
                intersectLP.setNext((i + 1) % mGeoPoints.size());
                intersectLP.setIsIntersection(true);
                mInterPoints.add(intersectLP);

                mGeoPoints.get(i).setIsNextIntersection(true);
                mGeoPoints.get(i).setNextIntsectIndex(mInterPoints.size() - 1);
                mGeoPoints.get((i + 1) % mGeoPoints.size())
                        .setIsPreIntersection(true);
                mGeoPoints.get((i + 1) % mGeoPoints.size()).setPreIntsectIndex(
                        mInterPoints.size() - 1);
            } else {
                continue;
            }
        }

        if (isFstPtInPolygon) {
            isSeparable = mInterPoints.size() >= 3 ? true : false;
        } else {
            isSeparable = mInterPoints.size() >= 2 ? true : false;
        }
    }

    /**
     * 裁剪(分割)Geometry对象
     *
     * @param linePoints 裁剪线(分割线)点的集合
     * @return 返回分割后的Geometry对象集合
     */
    public abstract List<IGeometry> split(List<IPoint> linePoints);

    protected int addGeoPts(IPart part, int geoStartIndex, int geoEndIndex) {
        if (geoEndIndex == -1) {
            // 结束标志为遇到交点
            int next = geoStartIndex;
            while (true) {
                LinkedPoint lpStart = mGeoPoints.get(next);
                // add first point
                part.AddPoint(lpStart.getPoint());
                if (lpStart.isNextIntersection()) {
                    break;
                } else {
                    next = lpStart.getNext();
                }
            }
            return next;
        } else {
            int next = geoStartIndex;
            while (true) {
                LinkedPoint lpStart = mGeoPoints.get(next);
                // add first point
                part.AddPoint(lpStart.getPoint());
                if (next == geoEndIndex) {
                    break;
                } else {
                    next = lpStart.getNext();
                }
            }
            return -1;
        }
    }

    protected int addLinePts(IPart part, int next, List<IPoint> linePts) {
        LinkedPoint lp = mGeoPoints.get(next);
        LinkedPoint startLPt = mInterPoints.get(lp.getNextIntsectIndex());
        LinkedPoint endLPt = null;
        if (lp.getNextIntsectIndex() == mInterPoints.size() - 1) {
            endLPt = mInterPoints.get(lp.getNextIntsectIndex() - 1);
        } else {
            endLPt = mInterPoints.get(lp.getNextIntsectIndex() + 1);
        }

        if (startLPt.getIndex() == endLPt.getIndex()) {
            // 直线切割
            part.AddPoint(startLPt.getPoint());
            part.AddPoint(endLPt.getPoint());
        } else {
            // 折线切割
            part.AddPoint(startLPt.getPoint());
            if (startLPt.getIndex() > endLPt.getIndex()) {
                for (int i = startLPt.getIndex(); i >= endLPt.getIndex() + 1; i--) {
                    part.AddPoint(linePts.get(i));
                }
            } else {
                for (int i = startLPt.getIndex() + 1; i <= endLPt.getIndex(); i++) {
                    part.AddPoint(linePts.get(i));
                }
            }
            part.AddPoint(endLPt.getPoint());
        }

        if (startLPt.getPre() == endLPt.getPre()) {
            isOutSameEdge = true;
        }

        return endLPt.getNext();
    }

}
