/**
 * 
 */
package com.willc.surveyor.split;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IRelationalOperator;
import srs.Geometry.Polygon;

/**
 * @author keqian Splitter工厂类，用于生成具体类型的Splitter
 */
public class SplitterFactory {

	private SplitterFactory() {
	}

	public static Splitter createSplitter(IGeometry geometry) {
		List<LinkedPoint> geoPoints = getGeoPoints(geometry);

		Splitter splitter = null;
		switch (getPolygonType(geoPoints)) {
		case Convex:
			splitter = new ConvexSplitter();
			break;
		case Concave:
			splitter = new ConcaveSplitter();
			break;
		}
		splitter.setLinkedGeoPoints(geoPoints);
		return splitter;
	}

	/**将原多边形的洞分配到切割后的多边形中
	 * @param geo 原多边形
	 * @param polygons 分割后的多边形
	 */
	public static List<IGeometry> putInternalPolygons(IGeometry geo, List<IGeometry> polygons){
		if(!(geo instanceof Polygon)){
			return polygons;
		}
		IPart[] parts = ((Polygon)geo).Parts(); //原多边形的各PART
		IPolygon polygonC = null; //切割后的某个多边形
		IPolygon pcIn = null; //原多边形的某个其他环
		
		try {
			for(int i=1;i<parts.length;i++){
				//从多边形的第2个环开始判断，因为被分割的就是第一个环
				pcIn = new Polygon(parts[i]);
				for(int j=0;j<polygons.size();j++){
					polygonC = (IPolygon)polygons.get(j);
					if (((IRelationalOperator)polygonC).Contains(pcIn)
							/*||((IRelationalOperator) polygonC).Within(pcIn)*/){
						//若被切割后的多边形包围，则将part设为其内环，并跳出内层循环
						((IPolygon)polygons.get(j)).AddPart(parts[i], false);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return polygons;
	}

	/**
	 * 根据Geometry对象的点集合，获取封装后的LinkedPoint集合
	 */
	private static List<LinkedPoint> getGeoPoints(IGeometry geometry) {
		List<LinkedPoint> geoPoints = new ArrayList<LinkedPoint>();
		if (geometry != null) {
			IPoint[] geoPts = ((IPolygon) geometry).Parts()[0].Points();
			// 除去最后一个点
			int lastIndex = geoPts.length - 2;
			for (int k = 0; k <= lastIndex; k++) {
				LinkedPoint lp = new LinkedPoint();
				lp.setPoint(geoPts[k]);
				if (k == 0) {
					lp.setPre(lastIndex);
					lp.setNext(k + 1);
				} else if (k == lastIndex) {
					lp.setPre(k - 1);
					lp.setNext(0);
				} else {
					lp.setPre(k - 1);
					lp.setNext(k + 1);
				}
				geoPoints.add(lp);
			}
		}
		return geoPoints;
	}

	/**
	 * 利用向量叉乘，判定多边形的凸凹类型 描述：当前点和相邻前后两点的向量外积；
	 * 公式：向量a(x1,y1)和向量b(x2,y2)的叉乘等于x1y2-x2y1;若结果大于0,表示凸多边形;小于0，表示凹多边形
	 */
	private static PolygonType getPolygonType(List<LinkedPoint> geoPoints) {
		PolygonType type = PolygonType.Convex;
		for (int i = 0; i < geoPoints.size(); i++) {
			LinkedPoint lp1 = geoPoints.get(i);
			LinkedPoint lp2 = geoPoints.get(lp1.getNext());
			LinkedPoint lp3 = geoPoints.get(lp2.getNext());

			IPoint p1 = lp1.getPoint();
			IPoint p2 = lp2.getPoint();
			IPoint p3 = lp3.getPoint();

			double x1 = p2.X() - p1.X();
			double y1 = p2.Y() - p1.Y();
			double x2 = p3.X() - p2.X();
			double y2 = p3.Y() - p2.Y();
			double result = x1 * y2 - x2 * y1;
			if (result > 0) {
				type = PolygonType.Concave;
				break;
			}
		}
		return type;
	}
}
