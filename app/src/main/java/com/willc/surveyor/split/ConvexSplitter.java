/**
 * 
 */
package com.willc.surveyor.split;

import java.util.ArrayList;
import java.util.List;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.Part;
import srs.Geometry.Polygon;

/**
 * @author keqian 凸多边形裁剪器
 */
public class ConvexSplitter extends Splitter {

	public ConvexSplitter() {
		super();
	}

	@Override
	public List<IGeometry> split(List<IPoint> linePoints) {
		List<IGeometry> geometries = new ArrayList<IGeometry>();
		Polygon geo1 = new Polygon();
		Polygon geo2 = new Polygon();
		IPart part1 = new Part();
		IPart part2 = new Part();

		if (isFstPtInPolygon) {
			// 构造geo1——以第一个Geometry点为起始点,逆时针构造
			// add geo point
			int nextIntersect = addGeoPts(part1, 0, -1);
			// add line point
			int nextGeo = addLinePts(part1, nextIntersect, linePoints);
			// add last geo points
			addGeoPts(part1, nextGeo, 0);
			// add part to geometry
			geo1.AddPart(part1, true);
			geometries.add(geo1);

			if (!isOutSameEdge) {
				// 接着构造下一个geo——以构造Geo1的nextIntersect为基础,逆时针构造
				int secStart = nextIntersect + 1;
				// add geo point
				int nextIntersect2 = addGeoPts(part2, secStart, -1);
				// add line point
				int nextGeo2 = addLinePts(part2, nextIntersect2, linePoints);
				// add last geo points
				addGeoPts(part2, nextGeo2, secStart);
			} else {
				LinkedPoint lp = mGeoPoints.get(nextIntersect);
				LinkedPoint startLPt = mInterPoints.get(lp
						.getNextIntsectIndex());
				LinkedPoint endLPt = null;
				if (lp.getNextIntsectIndex() == mInterPoints.size() - 1) {
					endLPt = mInterPoints.get(lp.getNextIntsectIndex() - 1);
				} else {
					endLPt = mInterPoints.get(lp.getNextIntsectIndex() + 1);
				}
				// 折线切割
				part2.AddPoint(startLPt.getPoint());
				if (startLPt.getIndex() > endLPt.getIndex()) {
					for (int i = startLPt.getIndex(); i >= endLPt.getIndex() + 1; i--) {
						part2.AddPoint(linePoints.get(i));
					}
				} else {
					for (int i = startLPt.getIndex() + 1; i <= endLPt
							.getIndex(); i++) {
						part2.AddPoint(linePoints.get(i));
					}
				}
				part2.AddPoint(endLPt.getPoint());
				part2.AddPoint(startLPt.getPoint());
			}
			// add part to geometry
			geo2.AddPart(part2, true);
			geometries.add(geo2);
		} else {
			// 构造geo1——以第一个Geometry点为起始点,逆时针构造
			// add geo point
			int nextIntersect = addGeoPts(part1, 0, -1);
			// add line point
			int nextGeo = addLinePts(part1, nextIntersect, linePoints);
			// add last geo points
			addGeoPts(part1, nextGeo, 0);
			// add part to geometry
			geo1.AddPart(part1, true);
			geometries.add(geo1);

			if (!isOutSameEdge) {
				// 接着构造下一个geo——以构造Geo1的nextIntersect为基础,逆时针构造
				int secStart = nextIntersect + 1;
				// add geo point
				int nextIntersect2 = addGeoPts(part2, secStart, -1);
				// add line point
				int nextGeo2 = addLinePts(part2, nextIntersect2, linePoints);
				// add last geo points
				addGeoPts(part2, nextGeo2, secStart);
			} else {
				LinkedPoint lp = mGeoPoints.get(nextIntersect);
				LinkedPoint startLPt = mInterPoints.get(lp
						.getNextIntsectIndex());
				LinkedPoint endLPt = null;
				if (lp.getNextIntsectIndex() == mInterPoints.size() - 1) {
					endLPt = mInterPoints.get(lp.getNextIntsectIndex() - 1);
				} else {
					endLPt = mInterPoints.get(lp.getNextIntsectIndex() + 1);
				}
				// 折线切割
				part2.AddPoint(startLPt.getPoint());
				if (startLPt.getIndex() > endLPt.getIndex()) {
					for (int i = startLPt.getIndex(); i >= endLPt.getIndex() + 1; i--) {
						part2.AddPoint(linePoints.get(i));
					}
				} else {
					for (int i = startLPt.getIndex() + 1; i <= endLPt
							.getIndex(); i++) {
						part2.AddPoint(linePoints.get(i));
					}
				}
				part2.AddPoint(endLPt.getPoint());
				part2.AddPoint(startLPt.getPoint());
			}
			// add part to geometry
			geo2.AddPart(part2, true);
			geometries.add(geo2);
		}
		return geometries;
	}

}
