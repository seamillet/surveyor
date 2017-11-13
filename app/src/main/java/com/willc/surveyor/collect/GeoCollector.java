/**
 * 
 */
package com.willc.surveyor.collect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.surveyor.drawlib.common.ElementStyles;
import com.surveyor.drawlib.elements.IElement;
import com.surveyor.drawlib.elements.IPointElement;
import com.surveyor.drawlib.elements.PointElement;
import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.mapview.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IPolyline;
import srs.Geometry.Point;

/**
 * @author keqian
 * 
 */
public abstract class GeoCollector {
	protected final int range = 30;
	protected final int rangeLastPoint = 60;

	//protected MapControl mMapControl = null;
	protected MapView mMapView = null;
	protected IMap mMap = null;

	protected List<IPoint> mPoints = null;
	protected List<IPoint> mMidPoints = null;
	protected int currentPointIndex = -1;
	protected int currentMidPointIndex = -1;

	protected Stack<IGeometry> historyGeos = null;
	protected Stack<String> historyActions = null;
	protected Stack<Integer> historyCurrIndex = null;
	protected Stack<List<IPoint>> historyMidPoints = null;

	protected GeoCollector() {
		mPoints = new ArrayList<IPoint>();
		historyGeos = new Stack<IGeometry>();
		historyActions = new Stack<String>();
		historyCurrIndex = new Stack<Integer>();
	}

	/**
	 * 设置对当前操作MapControl对象的引用
	 * 
	 * @param mapControl
	 *            当前操作的MapControl实例
	 */
//	public void setMapControl(MapControl mapControl) {
//		mMapControl = mapControl;
//		mMap = mMapControl.getMap();
//	}

	/**
	 * 设置对当前操作MapControl对象的引用
	 *
	 * @param mapView
	 *            当前操作的MapControl实例
	 */
	public void setMapControl(MapView mapView) {
		mMapView = mapView;
		mMap = mMapView.getMap();
	}

	/**
	 * 在点集合中增加一个采集点
	 * 
	 * @param point
	 */
	public abstract void addPoint(IPoint point);

	/**
	 * 更新当前选中采集点的实际地理坐标和屏幕坐标
	 * 
	 * @param point
	 *            地理坐标
	 * //@param pf
	 *            屏幕坐标
	 */
	public abstract void updatePoint(IPoint point);

	/**
	 * 删除所有采集点
	 * 
	 * @throws Exception
	 * 
	 */
	public abstract void clear() throws Exception;

	/**
	 * 删除指定位置的采集点
	 * 
	 * @return IGeometry 返回删除指定采集点后的IGeometry对象
	 * @throws Exception
	 */
	public abstract void delpt() throws Exception;

	/**
	 * 撤销操作
	 * 
	 * @throws Exception
	 */
	public abstract void undo() throws Exception;

	/**
	 * 根据采集要素类型和所有的采集点，生成IGeometry对象
	 * 
	 * @return 采集的IGeometry对象
	 */
	public abstract IGeometry getGeometry();

	/**
	 * 刷新地图
	 * 
	 * @throws Exception
	 */
	public abstract void refresh() throws Exception;

	/**
	 * 保存前，判断采集点的个数是否能够生成设置的采集类型的IGeometry对象
	 * 
	 * @param context
	 *            上下文信息，用于设置AlertDialog
	 * @return boolean true表示可以保存，false表示采集的信息无效
	 */
	public abstract boolean isGeometryValid(Context context);

	public abstract double getArea();

	public abstract double getLength();

	public abstract double[] getLastSideLength();

	public abstract double[] getEachSideLength();

	public abstract double[] getEachSideAngle();

	public abstract double getAngle();

	public abstract IPoint getPosition();

	/**
	 * 判断点击位置是否存在点
	 * 
	 * @param x
	 *            点击处实际地理坐标x
	 * @param y
	 *            点击处实际地理坐标y
	 * @return 如存在，返回True,否则False
	 */
	public abstract boolean isPointFocused(double x, double y);

	/**
	 * 拖拽时，在Canvas上绘制采集点
	 * 
	 * @param canvas
	 * @param x
	 *            拖拽点x坐标
	 * @param y
	 *            拖拽点y坐标
	 */
	public abstract void drawPointsOnCanvas(Canvas canvas, float x, float y);

	/**
	 * 拖拽时，在Canvas上绘制拖动路径
	 * 
	 * @param canvas
	 * @param x
	 *            拖拽点x坐标
	 * @param y
	 *            拖拽点y坐标
	 */
	public void drawPathOnCanvas(Canvas canvas, float x, float y) {
		return;
	}

	public void drawLineOnCanvas(Canvas canvas, float x, float y) {
		return;
	}

	/**
	 * 判断点击位置是否存在点
	 * 
	 * @param x
	 *            点击处实际地理坐标x
	 * @param y
	 *            点击处实际地理坐标y
	 * @return 如存在，返回True,否则False
	 */
	public boolean isMidPointFocused(double x, double y) {
		return false;
	}

	public boolean isNearLastPoint(double x, double y, int range) {
		return false;
	}

	/**
	 * 当中点被选中时，在点集合中增加一个采集点
	 * 
	 * @param point
	 */
	public void addPointMid(IPoint point) {
		return;
	}

	public int getPointsSize() {
		return mPoints.size();
	}

	protected void geometryToPoints(IGeometry geo) {
		mPoints.clear();
		if (geo == null) {
			return;
		}
		IPart[] parts = null;
		switch (geo.GeometryType()) {
		case Point:
			IPoint point = geo.CenterPoint();
			mPoints.add(point);
			break;
		case Polyline:
			parts = ((IPolyline) geo).Parts();
			mPoints.addAll(java.util.Arrays.asList(parts[0].Points()));
			break;
		case Polygon:
			parts = ((IPolygon) geo).Parts();
			mPoints.addAll(java.util.Arrays.asList(parts[0].Points()));
			break;
		default:
			break;
		}
		currentPointIndex = 0;
	}

	public void setEditGeometry(IGeometry editGeometry) throws Exception {
		geometryToPoints(editGeometry);
		refresh();
	}

	/**
	 * 清除所有Elements
	 *
	 * @throws IOException
	 */
	public void clearElements() throws IOException {
		IMap map = mMapView.getMap();
		if (map.getElementContainer().getElementCount() > 0) {
			map.getElementContainer().ClearElement();
		}
		mMapView.PartialRefresh();
	}

	protected List<IElement> getEditPointElements() {
		List<IElement> elements = new ArrayList<IElement>();
		for (int i = 0; i < mPoints.size(); i++) {
			IElement element = new PointElement();
			((IPointElement) element).setSymbol(ElementStyles.NoFocusedPointStyle);
			if (i == currentPointIndex) {
				((IPointElement) element).setSymbol(ElementStyles.FocusedPointStyle);
			}
			element.setGeometry(mPoints.get(i));
			elements.add(element);
		}
		return elements;
	}

	protected List<IElement> getMidPointsElements() {
		List<IElement> elements = new ArrayList<IElement>();
		for (int i = 0; i < mMidPoints.size(); i++) {
			IElement element = new PointElement();
			((IPointElement) element)
					.setSymbol(ElementStyles.NoFocusedMidPointStyle);
			if (i == currentMidPointIndex) {
				((IPointElement) element)
						.setSymbol(ElementStyles.FocusedMidPointStyle);
			}
			element.setGeometry(mMidPoints.get(i));
			elements.add(element);
		}
		return elements;
	}

	/**
	 * 生成提示对话框
	 * 
	 * @param title
	 *            提示标题
	 * @param message
	 *            提示内容
	 * @return AlertDialog对象
	 */
	protected AlertDialog generateAlertDialog(Context context,
			CharSequence title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	protected IPoint calcMidPoint(IPoint p1, IPoint p2) {
		double x = (p1.X() + p2.X()) / 2;
		double y = (p1.Y() + p2.Y()) / 2;
		return new Point(x, y);
	}

	protected PointF calcMidPoint(float x1, float y1, PointF p2) {
		float x = (x1 + p2.x) / 2;
		float y = (y1 + p2.y) / 2;
		return new PointF(x, y);
	}
}
