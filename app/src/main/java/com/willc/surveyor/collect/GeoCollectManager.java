/**
 * 
 */
package com.willc.surveyor.collect;

import com.surveyor.drawlib.mapview.MapView;

import java.io.IOException;

import srs.Geometry.srsGeometryType;

/**
 * @author keqian
 * 
 */
public final class GeoCollectManager {

	private static GeoCollector mCollector = null;
	//private static MapControl mMapControl = null;
	private static MapView mMapView = null;
	private static srsGeometryType mType = null;

	public static GeoCollector getCollector() {
		if (mCollector == null) {
			/*switch (mType) {
			case Point:
				mCollector = new PointCollector();
				break;
			case Polyline:
				mCollector = new PolyLineCollector();
				break;
			case Polygon:
				mCollector = new PolygonCollector();
				break;
			default:
				break;
			}*/
			mCollector = new HouseCollector();
		}
		mCollector.setMapControl(mMapView);
		return mCollector;
	}

	/**
	 * 设置对当前操作MapControl对象的引用
	 * 
	 * @param mapControl
	 *            当前操作的MapControl实例
	 */
	/*public static void setMapControl(MapControl mapControl) {
		mMapControl = mapControl;
	}*/

	/**
	 * 设置对当前操作MapControl对象的引用
	 *
	 * @param mapView
	 *            当前操作的MapControl实例
	 */
	public static void setMapControl(MapView mapView) {
		mMapView = mapView;
	}

	/**
	 * 设置当前采集要素类型
	 * 
	 * @param geoType
	 *            当前采集要素类型
	 */
	public static void setGeometryType(srsGeometryType geoType) {
		mType = geoType;
	}

	/**
	 * 清空显示，并释放资源
	 * 
	 * @throws IOException
	 */
	public static void dispose() throws IOException {
		// 清空Elements,并刷新显示
		if (null != mCollector) {
			mCollector.clearElements();
			mCollector = null;
		}
		mType = null;
		mMapView = null;
	}
}
