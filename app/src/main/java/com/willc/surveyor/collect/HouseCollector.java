/**
 *
 */
package com.willc.surveyor.collect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import com.surveyor.drawlib.elements.FillElement;
import com.surveyor.drawlib.elements.IElement;
import com.surveyor.drawlib.elements.IFillElement;
import com.surveyor.drawlib.elements.ILineElement;
import com.surveyor.drawlib.elements.IPointElement;
import com.surveyor.drawlib.elements.ITextElement;
import com.surveyor.drawlib.elements.LineElement;
import com.surveyor.drawlib.elements.PointElement;
import com.surveyor.drawlib.elements.TextElement;
import com.surveyor.drawlib.common.DrawPaintStyles;
import com.surveyor.drawlib.common.ElementStyles;
import com.surveyor.drawlib.utils.NumberUtil;
import com.willc.surveyor.interoperation.CollectInteroperator;

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
import srs.Geometry.Polygon;
import srs.Geometry.Polyline;
import srs.Geometry.srsGeometryType;

/**
 * @author keqian 面要素采集
 */
public class HouseCollector extends GeoCollector {
    private static final String TAG = HouseCollector.class.getSimpleName();

    private boolean equalsX = false;
    private boolean equalsY = false;

    public boolean isDraw = false;

    public HouseCollector() {
        super();
        mMidPoints = new ArrayList<IPoint>();
        historyMidPoints = new Stack<List<IPoint>>();
    }

    @Override
    public void addPoint(IPoint point) {
        historyCurrIndex.push(currentPointIndex);
        historyActions.push("ADD");
        historyGeos.push(getGeometry());
        historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));

        // 添加采集点
        if (currentPointIndex == mPoints.size() - 1) {
            if (equalsX) {
                mPoints.add(currentPointIndex + 1, new Point(mPoints.get(currentPointIndex).X(), point.Y()));
                this.equalsX = false;
            } else if (equalsY) {
                mPoints.add(currentPointIndex + 1, new Point(point.X(), mPoints.get(currentPointIndex).Y()));
                this.equalsY = false;
            } else {
                mPoints.add(point);
            }
        } else {
            mPoints.add(currentPointIndex + 1, point);
        }
        currentPointIndex++;

        // 添加中点
        /*if (mPoints.size() > 1) {

			if (mPoints.size() == 2) {
				mMidPoints.add(calcMidPoint(mPoints.get(1), mPoints.get(0)));
			} else if (mPoints.size() == 3) {
				mMidPoints.add(calcMidPoint(mPoints.get(2), mPoints.get(1)));
				mMidPoints.add(calcMidPoint(mPoints.get(2), mPoints.get(0)));
			} else if (mPoints.size() > 3) {
				IPoint midPointModified = calcMidPoint(
						mPoints.get(currentPointIndex),
						mPoints.get(currentPointIndex - 1));
				IPoint midPointAdd = calcMidPoint(
						mPoints.get(currentPointIndex),
						mPoints.get((currentPointIndex + 1) % mPoints.size()));
				mMidPoints.remove(currentPointIndex - 1);
				mMidPoints.add(currentPointIndex - 1, midPointModified);
				mMidPoints.add(currentPointIndex, midPointAdd);
			}
		}*/
    }

    @Override
    public void addPointMid(IPoint point) {
        historyCurrIndex.push(currentPointIndex);
        historyActions.push("ADD");
        historyGeos.push(getGeometry());
        historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));

        if (mPoints.size() == 2) {
            // 添加采集点
            currentPointIndex = 2;
            mPoints.add(currentPointIndex, point);
            // 添加中点
            IPoint midPointAdd1 = calcMidPoint(mPoints.get(2), mPoints.get(1));
            IPoint midPointAdd2 = calcMidPoint(mPoints.get(2), mPoints.get(0));
            mMidPoints.add(1, midPointAdd1);
            mMidPoints.add(2, midPointAdd2);
        } else {
            // 添加采集点
            currentPointIndex = currentMidPointIndex + 1;
            mPoints.add(currentPointIndex, point);
            // 添加中点
            IPoint midPointModified = calcMidPoint(
                    mPoints.get(currentPointIndex),
                    mPoints.get(currentPointIndex - 1));
            IPoint midPointAdd = calcMidPoint(mPoints.get(currentPointIndex),
                    mPoints.get((currentPointIndex + 1) % mPoints.size()));
            mMidPoints.remove(currentPointIndex - 1);
            mMidPoints.add(currentPointIndex - 1, midPointModified);
            mMidPoints.add(currentPointIndex, midPointAdd);
        }
        currentMidPointIndex = -1;
    }

    @Override
    public void updatePoint(IPoint point) {
        historyCurrIndex.push(currentPointIndex);
        historyActions.push("UPDATE");
        historyGeos.push(getGeometry());

        mPoints.remove(currentPointIndex);
        mPoints.add(currentPointIndex, point);

        // 修改中点的值
        updateMidPoints();
    }

    private void updateMidPoints() {
        historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));
        // 起始点编辑，更改起始点和起始点后一点的中点位置
        if (currentPointIndex == 0) {
            mMidPoints.remove(0);
            mMidPoints.add(0, calcMidPoint(mPoints.get(0), mPoints.get(1)));
            mMidPoints.remove(mMidPoints.size() - 1);
            mMidPoints.add(calcMidPoint(mPoints.get(0),
                    mPoints.get(mPoints.size() - 1)));
        }
        // 末尾点编辑，更改末尾点和末尾点前一点的中点位置
        else if (currentPointIndex == mPoints.size() - 1) {
            mMidPoints.remove(currentPointIndex);
            mMidPoints.add(calcMidPoint(mPoints.get(currentPointIndex),
                    mPoints.get((currentPointIndex + 1) % mPoints.size())));
            mMidPoints.remove(currentPointIndex - 1);
            mMidPoints.add(
                    currentPointIndex - 1,
                    calcMidPoint(mPoints.get(currentPointIndex),
                            mPoints.get(currentPointIndex - 1)));
        }
        // 中间点编辑，更改该点和前一点中点以及该点和后一点中点位置
        else {
            IPoint midPointPre = calcMidPoint(mPoints.get(currentPointIndex),
                    mPoints.get(currentPointIndex - 1));
            IPoint midPointLast = calcMidPoint(mPoints.get(currentPointIndex),
                    mPoints.get(currentPointIndex + 1));
            mMidPoints.remove(currentPointIndex - 1);
            mMidPoints.add(currentPointIndex - 1, midPointPre);
            mMidPoints.remove(currentPointIndex);
            mMidPoints.add(currentPointIndex, midPointLast);
        }
    }

    @Override
    public void clear() throws Exception {
        if (mPoints != null && mPoints.size() > 0) {
            historyCurrIndex.push(currentPointIndex);
            historyActions.push("CLEAR");
            historyGeos.push(getGeometry());
            historyMidPoints.push(historyMidPoints.push(new ArrayList<IPoint>(
                    mMidPoints)));

            mPoints.clear();
            mMidPoints.clear();
            currentPointIndex = -1;
            currentMidPointIndex = -1;
        }
        refresh();
    }

    @Override
    public void delpt() throws Exception {
        // 表示中点被选中或没有采集节点，不进行操作
        if (currentPointIndex != -1) {
            historyCurrIndex.push(currentPointIndex);
            historyActions.push("DELETE");
            historyGeos.push(getGeometry());
            historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));

            if (mPoints.size() == 1) {
                // 删除节点
                mPoints.remove(currentPointIndex);
                currentPointIndex--;
            } else if (mPoints.size() == 2) {
                if (currentPointIndex == 0) {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    mMidPoints.remove(0);
                } else {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    currentPointIndex--;
                    mMidPoints.remove(0);
                }
            } else if (mPoints.size() == 3) {
                if (currentPointIndex == 0) {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    // 删除与该节点相邻的两个中点
                    mMidPoints.remove(0);
                    mMidPoints.remove(1);
                } else {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    currentPointIndex--;
                    // 删除与该节点相邻的两个中点
                    mMidPoints.remove(currentPointIndex);
                    mMidPoints.remove(currentPointIndex);
                }
            } else if (mPoints.size() > 3) {
                if (currentPointIndex == 0) {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    // 计算节点删除后的中点位置
                    IPoint midPoint = calcMidPoint(
                            mPoints.get(currentPointIndex),
                            mPoints.get(mPoints.size() - 1));
                    // 删除与该节点相邻的两个中点
                    mMidPoints.remove(0);
                    mMidPoints.remove(mMidPoints.size() - 1);
                    // 添加上面计算的中点
                    mMidPoints.add(midPoint);

                } else {
                    // 删除节点
                    mPoints.remove(currentPointIndex);
                    currentPointIndex--;
                    // 计算节点删除后的中点位置
                    IPoint midPoint = calcMidPoint(
                            mPoints.get(currentPointIndex),
                            mPoints.get((currentPointIndex + 1)
                                    % mPoints.size()));
                    // 删除与该节点相邻的两个中点
                    mMidPoints.remove(currentPointIndex);
                    mMidPoints.remove(currentPointIndex);
                    // 添加上面计算的中点
                    mMidPoints.add(currentPointIndex, midPoint);
                }
            }
        }
        refresh();
    }

    /**
     * 撤销
     *
     * @throws Exception
     */
    @Override
    public void undo() throws Exception {
        if (historyGeos.size() > 0) {
            CharSequence action = historyActions.pop();
            if (historyMidPoints.size() > 0) {
                if (action == "CLEAR") {
                    historyMidPoints.pop();
                }
                mMidPoints.clear();
                mMidPoints.addAll(historyMidPoints.pop());
            }
            geometryToPoints(historyGeos.pop());
            int index = historyCurrIndex.pop();
            if (action == "ADD" && index < currentPointIndex) {
                currentPointIndex--;
            } else {
                currentPointIndex = index;
            }
        }
        refresh();
    }

    @Override
    public void setEditGeometry(IGeometry editGeometry) throws Exception {
        // 设置采集点
        geometryToPoints(editGeometry);
        mPoints.remove(mPoints.size() - 1);
        // 设置当前选中点为末尾点
        currentPointIndex = mPoints.size() - 1;
        // 设置中点
        calcMidPoints();
        refresh();
    }

    private void calcMidPoints() {
        for (int i = 0; i < mPoints.size() - 1; i++) {
            mMidPoints.add(calcMidPoint(mPoints.get(i), mPoints.get(i + 1)));
        }
        // 起点到尾点的中点
        mMidPoints.add(calcMidPoint(mPoints.get(0), mPoints.get(currentPointIndex)));
    }

    @Override
    public boolean isPointFocused(double x, double y) {
        double limits = mMap.getScreenDisplay().ToMapDistance(range);
        boolean flag = false;
        if (mPoints.size() > 0) {
            for (int i = 0; i < mPoints.size(); i++) {
                IPoint currP = mPoints.get(i);
                if (x > currP.X() - limits && x < currP.X() + limits
                        && y > currP.Y() - limits && y < currP.Y() + limits) {
                    currentPointIndex = i;
                    currentMidPointIndex = -1;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean isMidPointFocused(double x, double y) {
        double limits = mMap.getScreenDisplay().ToMapDistance(range);
        boolean flag = false;
        if (mMidPoints.size() > 0) {
            for (int i = 0; i < mMidPoints.size(); i++) {
                IPoint currP = mMidPoints.get(i);
                if (x > currP.X() - limits && x < currP.X() + limits
                        && y > currP.Y() - limits && y < currP.Y() + limits) {
                    currentMidPointIndex = i;
                    currentPointIndex = -1;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean isNearLastPoint(double x, double y, int range) {
        double limits = 0;
        if (range < 0) {
            limits = mMap.getScreenDisplay().ToMapDistance(rangeLastPoint);
        } else {
            limits = mMap.getScreenDisplay().ToMapDistance(range);
        }

        boolean flag = false;
        if (mPoints.size() > 0) {
            IPoint currP = mPoints.get(currentPointIndex);
            if (x > currP.X() - limits && x < currP.X() + limits && y > currP.Y() - limits && y < currP.Y() + limits) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void drawLineOnCanvas(Canvas canvas, float x, float y) {
        Path path = new Path();

        PointF startPt = mMap.FromMapPoint(mPoints.get(currentPointIndex));
        float hDistance = Math.abs(startPt.x - x);
        float vDistance = Math.abs(startPt.y - y);

        if (hDistance > vDistance) {
            Log.d(TAG, "draw collecting line --> hDistance > vDistance");
            path.moveTo(startPt.x, startPt.y);

            double angle = Math.atan(vDistance / hDistance) * 180 / Math.PI;
            double distance = 0;
            Log.d(TAG, "draw collecting line --> angle is " + angle);
            if (Double.compare(angle, 10.0) <= 0) {
                //与x轴夹角,小于等于10度.
                Log.d(TAG, "draw collecting line --> x <= 10°");
                path.lineTo(x, startPt.y);
                distance = hDistance;
                this.equalsY = true;  //在添加点时addPoint(), y值和起点相同
            } else {
                Log.d(TAG, "draw collecting line --> x > 10°");
                path.lineTo(x, y);
                distance = NumberUtil.getDistance(startPt.x, startPt.y, x, y);
            }
            canvas.drawPath(path, DrawPaintStyles.lineCollectingPaint);
            canvas.drawTextOnPath(NumberUtil.format(mMap.ToMapDistance(distance)), path, 0, 20.0f, DrawPaintStyles.textDistancePaint);
        } else {
            Log.d(TAG, "draw collecting line --> vDistance > hDistance");
            path.moveTo(startPt.x, startPt.y);

            double angle = Math.atan(hDistance / vDistance) * 180 / Math.PI;
            double distance = 0;
            Log.d(TAG, "draw collecting line --> angle is " + angle);
            if (Double.compare(angle, 10.0) <= 0) {
                //与y轴夹角,小于等于10度.
                Log.d(TAG, "draw collecting line --> y <= 10°");
                path.lineTo(startPt.x, y);
                distance = vDistance;
                this.equalsX = true;  //在添加点时addPoint(), x值和起点相同
            } else {
                Log.d(TAG, "draw collecting line --> y > 10°");
                path.lineTo(x, y);
                distance = NumberUtil.getDistance(startPt.x, startPt.y, x, y);
            }
            canvas.drawPath(path, DrawPaintStyles.lineCollectingPaint);
            canvas.drawTextOnPath(NumberUtil.format(mMap.ToMapDistance(distance)), path, 0, 20.0f, DrawPaintStyles.textDistancePaint);
        }
    }

    @Override
    public void drawPathOnCanvas(Canvas canvas, float x, float y) {
        Path path = new Path();
        if (currentPointIndex == 0) {
            path.moveTo(x, y);
        } else {
            PointF pf = mMap.FromMapPoint(mPoints.get(0));
            path.moveTo(pf.x, pf.y);
        }
        for (int i = 1; i < mPoints.size(); i++) {
            if (currentPointIndex == i) {
                path.lineTo(x, y);
            } else {
                PointF pointf = mMap.FromMapPoint(mPoints.get(i));
                path.lineTo(pointf.x, pointf.y);
            }
        }
        path.close();
        //canvas.drawPath(path, DrawPaintStyles.polygonPaint);
        canvas.drawPath(path, DrawPaintStyles.linePaintPaint);
    }

    @Override
    public void drawPointsOnCanvas(Canvas canvas, float x, float y) {
        // 采集点
        for (int i = 0; i < mPoints.size(); i++) {
            if (i == currentPointIndex) {
                canvas.drawCircle(x, y, 7, DrawPaintStyles.pointFocusedPaint);
            } else {
                PointF pf = mMap.FromMapPoint(mPoints.get(i));
                canvas.drawCircle(pf.x, pf.y, 7, DrawPaintStyles.pointNoFocusedPaint);
            }
        }
        // 计算动态中点，并将中点绘制到Canvas
        if (currentPointIndex == 0) {
            PointF cMidForeword = calcMidPoint(x, y, mMap.FromMapPoint(mPoints.get(currentPointIndex + 1)));
            PointF cMidBackword = calcMidPoint(x, y, mMap.FromMapPoint(mPoints.get(mPoints.size() - 1)));
            canvas.drawCircle(cMidForeword.x, cMidForeword.y, 4, DrawPaintStyles.midPointPaint);
            canvas.drawCircle(cMidBackword.x, cMidBackword.y, 4, DrawPaintStyles.midPointPaint);
            for (int i = 1; i < mMidPoints.size() - 1; i++) {
                PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
                canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
            }
        }
        // 末尾点编辑，更改末尾点和末尾点前一点的中点位置
        else if (currentPointIndex == mPoints.size() - 1) {
            PointF cMidForeword = calcMidPoint(x, y,
                    mMap.FromMapPoint(mPoints.get(currentPointIndex - 1)));
            PointF cMidBackword = calcMidPoint(
                    x,
                    y,
                    mMap.FromMapPoint(mPoints.get((currentPointIndex + 1)
                            % mPoints.size())));
            canvas.drawCircle(cMidForeword.x, cMidForeword.y, 4,
                    DrawPaintStyles.midPointPaint);
            canvas.drawCircle(cMidBackword.x, cMidBackword.y, 4,
                    DrawPaintStyles.midPointPaint);
            for (int i = 0; i < mMidPoints.size() - 2; i++) {
                PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
                canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
            }
        }
        // 中间点编辑，更改该点和前一点中点以及该点和后一点中点位置
        else {
            for (int i = 0; i < mMidPoints.size(); i++) {
                if (i == currentPointIndex - 1) {
                    PointF cMidPt = calcMidPoint(x, y,
                            mMap.FromMapPoint(mPoints
                                    .get(currentPointIndex - 1)));
                    canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
                            DrawPaintStyles.midPointPaint);
                    continue;
                }
                if (i == currentPointIndex) {
                    PointF cMidPt = calcMidPoint(x, y,
                            mMap.FromMapPoint(mPoints
                                    .get(currentPointIndex + 1)));
                    canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
                            DrawPaintStyles.midPointPaint);
                    continue;
                }
                PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
                canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
            }
        }
    }

    @Override
    public IGeometry getGeometry() {
        if (mPoints.size() > 2) {
            IPart part = new Part();
            for (int i = 0; i < mPoints.size(); i++) {
                part.AddPoint(mPoints.get(i));
            }
            if (isDraw) {

            }
            part.AddPoint(mPoints.get(0));
            IPolygon polygon = new Polygon();
            polygon.AddPart(part, true);
            return polygon;
        } else if (mPoints.size() == 2) {
            IPart part = new Part();
            for (int i = 0; i < mPoints.size(); i++) {
                part.AddPoint(mPoints.get(i));
            }
            IPolyline polyline = new Polyline();
            polyline.AddPart(part);
            return polyline;
        } else if (mPoints.size() == 1) {
            return mPoints.get(0);
        }
        return null;
    }

    @Override
    public void refresh() throws Exception {
        mMap.getElementContainer().ClearElement();

        IGeometry geo = getGeometry();
        if (null != geo) {
            IElement element = null;
            switch (geo.GeometryType()) {
                case Point:
                    element = new PointElement();
                    ((IPointElement) element).setSymbol(ElementStyles.POINT_LAST_STYLE);
                    break;
                case Polyline:
                    element = new LineElement();
                    ((ILineElement) element).setSymbol(ElementStyles.LineStyle);
                    break;
                case Polygon:
                    element = new FillElement(true);
                    ((IFillElement) element).setSymbol(ElementStyles.PolygonStyle);
                    break;
                default:
                    break;
            }

            CollectInteroperator.mCollectedGeometry = geo;

            element.setGeometry(geo);
            mMap.getElementContainer().AddElement(element);
            if (mPoints.size() > 1) {
                //List<IElement> elements = new ArrayList<IElement>();
                //elements.addAll(getEditPointElements());
                //elements.addAll(getMidPointsElements());
                //mMap.getElementContainer().AddElements(elements);
                IPointElement lastPoint = new PointElement();
                lastPoint.setGeometry(mPoints.get(currentPointIndex));
                lastPoint.setSymbol(ElementStyles.POINT_LAST_STYLE);
                mMap.getElementContainer().AddElement(lastPoint);

                List<IElement> elements = new ArrayList<>();
                for (int i = 0; i < mPoints.size() - 1; i++) {
                    ITextElement textElement = new TextElement();

                    double distance = NumberUtil.getDistance(mPoints.get(i).X(), mPoints.get(i).Y(), mPoints.get(i + 1).X(), mPoints.get(i + 1).Y());
                    textElement.setGeometry(NumberUtil.getMidPoint(mPoints.get(i), mPoints.get(i + 1)));
                    textElement.setText(NumberUtil.format(distance));
                    textElement.setScaleText(true);
                    textElement.setSymbol(ElementStyles.Text_DISTANCE_STYLE);
                    elements.add(textElement);
                }
                mMap.getElementContainer().AddElements(elements);
            }
        }
        mMapView.PartialRefresh();
    }

    @Override
    public boolean isGeometryValid(Context context) {
        // 面有效性判断
        if (mPoints.size() < 3) {
            generateAlertDialog(context, "需要有效的面", "通过单击地图或使用当前位置来采集面").show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double getArea() {
        IGeometry geo = getGeometry();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {
            return ((IPolygon) geo).Area();
        }
        return -1;
    }

    @Override
    public double getLength() {
        IGeometry geo = getGeometry();
        //int i = ((IPolygon)geo).PartCount();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {
            return ((IPolygon) geo).Length();
        } else if (geo != null && geo.GeometryType() == srsGeometryType.Polyline) {
            return ((IPolyline) geo).Length();
        }
        return -1;
    }

    @Override
    public double[] getLastSideLength() {
        IGeometry geo = getGeometry();
        //int i = ((IPolygon)geo).PartCount();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {

            return ((IPolygon) geo).LastSideLength();
        }
        return new double[0];
    }

    @Override
    public double[] getEachSideLength() {
        IGeometry geo = getGeometry();
        //int i = ((IPolygon)geo).PartCount();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {

            return ((IPolygon) geo).EachSideLength();
        }
        return new double[0];
    }

    @Override
    public double[] getEachSideAngle() {
        IGeometry geo = getGeometry();
        //int i = ((IPolygon)geo).PartCount();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {

            return ((IPolygon) geo).EachSideAngle();
        }
        return new double[0];
    }

    @Override
    public double getAngle() {
        IGeometry geo = getGeometry();
        //int i = ((IPolygon)geo).PartCount();
        if (geo != null && geo.GeometryType() == srsGeometryType.Polygon) {

            return ((IPolygon) geo).Angle();
        }
        return -1;
    }

    @Override
    public IPoint getPosition() {
        /*IGeometry geo = getGeometry();
		if(geo!=null)
		{
			return geo.CenterPoint();
		}*/
        if (mPoints.size() > 0) {
            return mPoints.get(mPoints.size() - 1);
        }
        return null;
    }


}
