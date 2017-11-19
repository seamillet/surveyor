package com.willc.surveyor.split;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import com.surveyor.drawlib.common.DrawPaintStyles;
import com.surveyor.drawlib.common.ElementStyles;
import com.surveyor.drawlib.elements.FillElement;
import com.surveyor.drawlib.elements.IElement;
import com.surveyor.drawlib.elements.IFillElement;
import com.surveyor.drawlib.elements.ILineElement;
import com.surveyor.drawlib.elements.IPointElement;
import com.surveyor.drawlib.elements.LineElement;
import com.surveyor.drawlib.elements.PointElement;
import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.mapview.MapView;
import com.surveyor.drawlib.utils.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IPolyline;
import srs.Geometry.Part;
import srs.Geometry.Point;
import srs.Geometry.Polyline;
import srs.Geometry.srsGeometryType;

/**
 * 剪裁几何对象管理类 单例模式，储存剪裁几何对象数据结构，返回剪裁后的IGeometry对象集合
 *
 * @author keqian
 */
public class GeoSplitManager {
    private static final String TAG = GeoSplitManager.class.getSimpleName();
    private static GeoSplitManager mShearGeoManager = null;

    private final int rangeNearPoint = 24;

    private MapView mapView = null;
    private IMap mMap = null;

    /**
     * 需要裁剪的IGeometry对象集合
     */
    private List<IGeometry> mShearGeometries = null;
    /**
     * 要裁剪的IGeometry对象对应的裁剪器
     */
    private List<Splitter> mSplitters = null;
    /**
     * 用户所画裁剪线的点的集合
     */
    private List<IPoint> mDrawPoints = null;

    /**
     * 需要裁剪的IGeometry对象集合的栈，用于撤销操作
     */
    private Stack<List<IGeometry>> mGeosStack = null;
    /**
     * 裁剪线的点的集合的栈，用于切割线撤销操作
     */
    private Stack<List<IPoint>> mDrawPointStack = null;
    // private Stack<Integer> mCheckIndexStack = null;

    private List<IPoint> mGeoVertexs = null;

    private List<IGeometry> mGeos = null;
    private int currCheckIndex = -1;

    private List<Integer> mSplitIndex = null;
    private List<List<IPolygon>> temPolygons = null;

    private GeoSplitManager() {
        mDrawPoints = new ArrayList<IPoint>();
        mSplitters = new ArrayList<Splitter>();
        mGeosStack = new Stack<List<IGeometry>>();
        mDrawPointStack = new Stack<List<IPoint>>();
        // mCheckIndexStack = new Stack<Integer>();

        mGeos = new ArrayList<IGeometry>();
        mGeoVertexs = new ArrayList<>();

        temPolygons = new ArrayList<List<IPolygon>>();
        mSplitIndex = new ArrayList<Integer>();
    }

    public static GeoSplitManager instance() {
        if (mShearGeoManager == null) {
            mShearGeoManager = new GeoSplitManager();
        }
        return mShearGeoManager;
    }

    public void setMapcontrol(final MapView mapView) {
        this.mapView = mapView;
        this.mMap = mapView.getMap();
    }

    /**
     * 初始化设置需要剪切的IGeometry对象集合
     *
     * @param geos 要剪切的IGeometry对象集合
     */
    public void initGeometry(final List<IGeometry> geos) {
        if (geos != null && geos.size() > 0) {
            this.mShearGeometries = geos;
            reset();
        }
    }

    /**
     * 重新设置剪切器对象集合,并刷新显示
     */
    private void reset() {
        // 如果不为空，则先清空，再重新添加
        if (mSplitters.size() > 0) {
            mSplitters.clear();
        }
        // 重新设置Splitter
        for (int i = 0; i < mShearGeometries.size(); i++) {
            mSplitters.add(SplitterFactory.createSplitter(mShearGeometries.get(i)));
        }
        // 将要裁剪的IGeometry对象集合高亮显示
        try {
            resetGeoVertexs();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在mDrawPoints集合中增加一个采集点
     *
     * @param point 当前点击位置的IPoint对象
     */
    public void addPoint(final IPoint point) {
        // 第一次添加点
        if (mDrawPoints.size() == 0) {
            mDrawPointStack.push(null);
        } else {
            mDrawPointStack.push(new ArrayList<IPoint>(mDrawPoints));
        }
        mDrawPoints.add(point);
    }

    /**
     * 删除mDrawPoints集合中所有采集点
     *
     * @return 清除成功返回true，若已经为空，返回false
     */
    public boolean clearDrawLine() {
        if (mDrawPoints != null && mDrawPoints.size() > 0) {
            mDrawPoints.clear();
            currCheckIndex = -1;

            mDrawPointStack.clear();
            // 重新设置
            reset();
            return true;
        } else {
            return false;
        }
    }

    public boolean canSplit() {
        if (mDrawPoints.size() == 1) {
            currCheckIndex++;
            // 判断第一个点是否在Polygon内部，并设置状态
            for (Splitter splitter : mSplitters) {
                if (splitter.isPointInPolygon(mDrawPoints.get(0))) {
                    break;
                }
            }
            return false;
        } else {
            boolean canSplit = false;
            // mCheckIndexStack.push(currCheckIndex);
            for (Splitter splitter : mSplitters) {
                splitter.checkSeparable(mDrawPoints.get(currCheckIndex),
                        mDrawPoints.get(currCheckIndex + 1), currCheckIndex);
                canSplit = canSplit || splitter.isSeparable();
            }
            currCheckIndex++;
            return canSplit;
        }
    }

    public void split() {
        mGeosStack.push(new ArrayList<IGeometry>(mShearGeometries));

        for (int i = 0; i < mSplitters.size(); i++) {
            if (mSplitters.get(i).isSeparable()) {
                //mGeos.addAll(mSplitters.get(i).split(mDrawPoints));
                List<IGeometry> polygons = mSplitters.get(i).split(mDrawPoints);
                SplitterFactory.putInternalPolygons(mShearGeometries.get(i), polygons);
                mGeos.addAll(polygons);
            } else {
                mGeos.add(mShearGeometries.get(i));
            }
        }
        mShearGeometries.clear();
        mShearGeometries.addAll(mGeos);
        mGeos.clear();
    }

    /**
     * 地块切割撤销操作
     *
     * @return false, 表示撤销到了尽头;否则true;
     */
    public boolean undo() {
        if (mGeosStack.size() > 0) {
            mShearGeometries = mGeosStack.pop();
            reset();
            return true;
        } else {
            return false;
        }
    }

    public boolean canUndo() {
        if (mGeosStack.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 切割线点撤销操作
     *
     * @return
     * @throws IOException
     */
    public boolean revoke() throws IOException {
        if (mDrawPointStack.size() > 0) {
            List<IPoint> points = mDrawPointStack.pop();
            if (null == points) {
                mDrawPoints.remove(0);
                currCheckIndex = -1;
            } else {
                mDrawPoints.clear();
                mDrawPoints.addAll(points);

                // 如果不为空，则先清空，再重新添加
                if (mSplitters.size() > 0) {
                    mSplitters.clear();
                }
                // 重新设置Splitter
                for (int i = 0; i < mShearGeometries.size(); i++) {
                    mSplitters.add(SplitterFactory
                            .createSplitter(mShearGeometries.get(i)));
                }

                // 判断第一个点是否在Polygon内部，并设置状态
                for (Splitter splitter : mSplitters) {
                    if (splitter.isPointInPolygon(mDrawPoints.get(0))) {
                        break;
                    }
                }
                if (mDrawPoints.size() > 1) {
                    for (int i = 0; i < mDrawPoints.size() - 1; i++) {
                        for (Splitter splitter : mSplitters) {
                            splitter.checkSeparable(mDrawPoints.get(i),
                                    mDrawPoints.get(i + 1), i);
                        }
                    }
                }
                currCheckIndex = mDrawPoints.size() - 1;
            }
            refresh();
            return true;

        } else {
            return false;
        }
    }

    /**
     * 根据采集要素类型和所有的采集点，生成IGeometry对象
     *
     * @return 采集的IGeometry对象
     */
    public List<IGeometry> getShearedGeometries() {
        return mShearGeometries;
    }

    /**
     * 刷新显示
     *
     * @throws IOException
     * @throws Exception
     */
    public void refresh() throws IOException {
        // 清空所有元素
        mMap.getElementContainer().ClearElement();
        // 刷新裁剪几何对象(Geometry)显示
        refreshGeos();
        // 刷新剪裁几何对象(Geometry)顶点显示
        refreshGeoVertexs();
        // 刷新剪裁线显示
        refreshDrawLine();
        mapView.PartialRefresh();
    }

    /**
     * 清除所有Elements
     *
     * @throws IOException
     * @throws Exception
     */
    public void clearElements() throws IOException {
        if (mMap.getElementContainer().getElementCount() > 0) {
            mMap.getElementContainer().ClearElement();
        }
        mapView.PartialRefresh();
    }

    /**
     * 释放资源，建议垃圾回收器回收
     *
     * @throws IOException
     * @throws Exception
     */
    public void dispose() throws IOException {
        // 清空Elements,并刷新显示
        clearElements();
        mShearGeoManager = null;
    }

    /**
     * 根据所有的采集点，生成IGeometry对象
     *
     * @return 采集的IGeometry对象
     */
    private IGeometry getDrawGeometry() {
        if (mDrawPoints.size() > 1) {
            IPart part = new Part();
            for (int i = 0; i < mDrawPoints.size(); i++) {
                IPoint p = new Point(mDrawPoints.get(i).X(), mDrawPoints.get(i)
                        .Y());
                part.AddPoint(p);
            }
            IPolyline polyline = new Polyline();
            polyline.AddPart(part);
            return polyline;
        } else if (mDrawPoints.size() == 1) {
            return mDrawPoints.get(0);
        } else {
            return null;
        }
    }

    private void refreshDrawLine() throws IOException {
        // 增加PolylineElements
        if (mDrawPoints != null && mDrawPoints.size() > 0) {
            IElement element = null;
            IGeometry geo = getDrawGeometry();
            switch (geo.GeometryType()) {
                case Point:
                    element = new PointElement();
                    ((IPointElement) element)
                            .setSymbol(ElementStyles.FocusedPointStyle);
                    break;
                case Polyline:
                    element = new LineElement();
                    ((ILineElement) element).setSymbol(ElementStyles.LineStyle);
                    break;
                default:
                    break;
            }
            element.setGeometry(geo);
            this.mMap.getElementContainer().AddElement(element);

            // 增加PointElements
            if (geo.GeometryType() == srsGeometryType.Polyline) {
                IPointElement elementPt = new PointElement();
                for (int i = 0; i < mDrawPoints.size(); i++) {
                    elementPt.setSymbol(ElementStyles.NoFocusedPointStyle);
                    if (i == mDrawPoints.size() - 1) {
                        elementPt.setSymbol(ElementStyles.FocusedPointStyle);
                    }
                    elementPt.setGeometry(mDrawPoints.get(i));
                    this.mMap.getElementContainer().AddElement(elementPt);
                }
            }
        }
    }

    private void refreshGeos() throws IOException {
        // 增加PolygonElements,高亮显示正在裁剪的Polygons
        if (mShearGeometries != null && mShearGeometries.size() > 0) {
            for (IGeometry geo : mShearGeometries) {
                IFillElement element = new FillElement(true);
                element.setSymbol(ElementStyles.PolygonStyleHighlight);
                element.setGeometry(geo);
                this.mMap.getElementContainer().AddElement(element);
            }
        }
    }

    private void resetGeoVertexs() {
        this.mGeoVertexs.clear();
        //设置被剪裁几何对象的顶点，去除重复. 用于判断剪裁是否开始于顶点.
        if (mSplitters != null && mSplitters.size() > 0) {
            for (LinkedPoint linkedPoint : mSplitters.get(0).getLinkedGeoPoints()) {
                this.mGeoVertexs.add(linkedPoint.getPoint());
            }
        }
    }

    private void refreshGeoVertexs() throws IOException {
        // 增加PolygonElements,高亮显示正在裁剪的Polygons
        for (IPoint point : this.mGeoVertexs) {
            IPointElement element = new PointElement();
            element.setSymbol(ElementStyles.POINT_VERTEX_STYLE);
            element.setGeometry(point);
            this.mMap.getElementContainer().AddElement(element);
        }
    }

    public boolean isNearLastPoint(double x, double y, int range) {
        double limits = 0;
        if (range < 0) {
            limits = this.mMap.getScreenDisplay().ToMapDistance(rangeNearPoint);
        } else {
            limits = this.mMap.getScreenDisplay().ToMapDistance(range);
        }

        boolean flag = false;
        if (this.mDrawPoints.size() > 0) {
            IPoint currP = this.mDrawPoints.get(this.mDrawPoints.size() - 1);
            if (x > currP.X() - limits && x < currP.X() + limits && y > currP.Y() - limits && y < currP.Y() + limits) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isNearVertexs(double x, double y, int range) {
        double limits = 0;
        if (range < 0) {
            limits = this.mMap.getScreenDisplay().ToMapDistance(rangeNearPoint);
        } else {
            limits = this.mMap.getScreenDisplay().ToMapDistance(range);
        }

        boolean flag = false;
        if (this.mGeoVertexs.size() > 0) {
            for (int i = 0; i < this.mGeoVertexs.size(); i++) {
                IPoint currP = this.mGeoVertexs.get(i);
                if (x > currP.X() - limits && x < currP.X() + limits && y > currP.Y() - limits && y < currP.Y() + limits) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public int getPointsSize() {
        return this.mDrawPoints.size();
    }

    public void drawLineOnCanvas(Canvas canvas, float x, float y) {
        Path path = new Path();

        PointF startPt = mMap.FromMapPoint(mDrawPoints.get(mDrawPoints.size() - 1));
        path.moveTo(startPt.x, startPt.y);
        path.lineTo(x, y);

        double distance = NumberUtil.getDistance(startPt.x, startPt.y, x, y);
        canvas.drawPath(path, DrawPaintStyles.lineCollectingPaint);
        canvas.drawTextOnPath(NumberUtil.format(mMap.ToMapDistance(distance)), path, 0, 20.0f, DrawPaintStyles.textDistancePaint);

        Log.d(TAG, "draw shearing line");
    }
}
