package com.surveyor.drawlib.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import srs.Display.FromMapPointDelegate;
import srs.Display.IFromMapPointDelegate;
import srs.Display.Symbol.IFillSymbol;
import srs.Display.Symbol.ILineSymbol;
import srs.Display.Symbol.IPointSymbol;
import srs.Display.Symbol.ISimpleFillSymbol;
import srs.Display.Symbol.ISimpleLineSymbol;
import srs.Display.Symbol.ISimplePointSymbol;
import srs.Display.Symbol.ITextSymbol;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IPolyline;
import srs.Geometry.Part;
import srs.Geometry.Point;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/28.
 */
public class Drawing {
    public static float HollowLineWidth = 2.0F;

    private static Paint defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private IFromMapPointDelegate mFromMapPointDelegate;
    private Canvas mCanvas;
    private float mtextrate;

    public Drawing(Canvas canvas, FromMapPointDelegate delegate) throws sRSException {
        this(canvas);
        if (delegate == null) {
            throw new sRSException("00300001");
        } else {
            this.mFromMapPointDelegate = delegate;
        }
    }

    public Drawing(Canvas vaule) {
        this.mtextrate = 1.0F;
        this.mCanvas = vaule;
    }

    public Canvas getCanvas() {
        return this.mCanvas;
    }

    //Point
    public static void drawPoint(Canvas canvas, IPoint point, IPointSymbol symbol, FromMapPointDelegate delegate) {
        drawPoint(canvas, delegate.FromMapPoint(point), symbol);
    }

    public static void drawPoint(Canvas canvas, PointF pointF, IPointSymbol symbol) {
        if (symbol instanceof ISimplePointSymbol) {
            defaultPaint.reset();
            defaultPaint.setAntiAlias(true);
            //defaultPaint.setAlpha(symbol.getTransparent());
            defaultPaint.setColor(symbol.getColor());
            defaultPaint.setStyle(Paint.Style.FILL);

            switch (((ISimplePointSymbol) symbol).getStyle()) {
                case Circle:
                    drawPointCircle(canvas, pointF, symbol, defaultPaint);
                    break;
                case Square:
                    drawPointSquare(canvas, pointF, symbol, defaultPaint);
                    break;
                case Cross:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(symbol.getSize() / 2.0f);
                    drawPointCross(canvas, pointF, symbol, defaultPaint);
                    break;
                case X:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(symbol.getSize() / 2.0f);
                    drawPointX(canvas, pointF, symbol, defaultPaint);
                    break;
                case Diamond:
                    drawPointDiamond(canvas, pointF, symbol, defaultPaint);
                    break;
                case Triangle:
                    drawPointTriangle(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowCircle:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointCircle(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowSquare:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointSquare(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowDiamond:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointDiamond(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowTriangle:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointTriangle(canvas, pointF, symbol, defaultPaint);
                    break;
            }
        }
    }

    private static void drawPointCircle(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        canvas.drawCircle(pointF.x, pointF.y, symbol.getSize(), paint);
    }

    private static void drawPointSquare(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        canvas.drawRect(pointF.x - offset, pointF.y - offset, pointF.x + offset, pointF.y + offset, paint);
    }

    private static void drawPointCross(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pLeft = new PointF(pointF.x - offset, pointF.y);
        PointF pRight = new PointF(pointF.x + offset, pointF.y);
        PointF pTop = new PointF(pointF.x, pointF.y - offset);
        PointF pBottom = new PointF(pointF.x, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pLeft.x, pLeft.y); //左-->右
        path.lineTo(pRight.x, pRight.y);
        path.moveTo(pTop.x, pTop.y);   //上-->下
        path.lineTo(pBottom.x, pBottom.y);

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointX(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pTL = new PointF(pointF.x - offset, pointF.y - offset);
        PointF pTR = new PointF(pointF.x + offset, pointF.y - offset);
        PointF pBL = new PointF(pointF.x - offset, pointF.y + offset);
        PointF pBR = new PointF(pointF.x + offset, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pTL.x, pTL.y);
        path.lineTo(pBR.x, pBR.y); //左上-->右下
        path.moveTo(pTR.x, pTR.y);
        path.lineTo(pBL.x, pBL.y); //右上-->左下

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointDiamond(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pL = new PointF(pointF.x - offset, pointF.y);
        PointF pR = new PointF(pointF.x + offset, pointF.y);
        PointF pT = new PointF(pointF.x, pointF.y - offset);
        PointF pB = new PointF(pointF.x, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pL.x, pL.y);
        path.lineTo(pB.x, pB.y); //左-->下
        path.lineTo(pR.x, pR.y); //下-->右
        path.lineTo(pT.x, pT.y); //右-->上
        path.close();

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointTriangle(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pTop = new PointF(pointF.x - offset, pointF.y);
        PointF pBL = new PointF(pointF.x - offset, pointF.y + offset);
        PointF pBR = new PointF(pointF.x + offset, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pTop.x, pTop.y);
        path.lineTo(pBL.x, pBL.y); //上-->左下
        path.lineTo(pBR.x, pBR.y); //左下-->右下
        path.close();

        canvas.drawPath(path, paint);
        path = null;
    }


    //Line
    public static void drawPolyline(Canvas canvas, IPolyline polyline, ILineSymbol symbol, FromMapPointDelegate delegate) {
        for(int i = 0; i < polyline.PartCount(); i++) {
            IPart part = polyline.Parts()[i];
            IPoint[] iPoints = part.Points();

            if(iPoints.length > 1) {
                PointF[] points = new PointF[iPoints.length];
                for(int j = 0; j < iPoints.length; j++) {
                    points[j] = delegate.FromMapPoint(iPoints[j]);
                }

                drawPolyline(canvas, points, symbol);
            }
        }
    }

    public static void drawPolyline(Canvas canvas, PointF[] pts, ILineSymbol symbol) {
        if(symbol instanceof ISimpleLineSymbol) {
            defaultPaint.reset();
            defaultPaint.setAntiAlias(true);
            //defaultPaint.setAlpha(symbol.getTransparent());
            defaultPaint.setColor(symbol.getColor());
            defaultPaint.setStyle(Paint.Style.STROKE);
            defaultPaint.setStrokeWidth(((ISimpleLineSymbol)symbol).getWidth());

            DashPathEffect effects = null;
            switch(((ISimpleLineSymbol)symbol).getStyle()) {
                case Solid:
                    break;
                case Dash:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F}, 0.0F);
                    break;
                case DashDot:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case DashDotDot:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case Dot:
                    effects = new DashPathEffect(new float[]{2.0F, 3.0F}, 0.0F);
                    break;
                default:
                    break;
            }

            if(effects != null) {
                defaultPaint.setPathEffect(effects);
            }

            Path path = new Path();
            path.moveTo(pts[0].x, pts[0].y);
            for (int i = 1; i < pts.length; i++) {
                path.lineTo(pts[i].x, pts[i].y);
            }

            canvas.drawPath(path, defaultPaint);
            path = null;
            effects = null;
        }
    }

    public static void drawLine(Canvas canvas, IPoint startPoint, IPoint endPoint, ILineSymbol symbol, FromMapPointDelegate delegate) {
        PointF[] points = new PointF[2];
        points[0] = delegate.FromMapPoint(startPoint);
        points[1] = delegate.FromMapPoint(endPoint);

        drawPolyline(canvas, points, symbol);
    }

    public static void drawLine(Canvas canvas, PointF startPoint, PointF endPoint, ILineSymbol symbol) throws sRSException {
        drawPolyline(canvas, new PointF[]{startPoint, endPoint}, symbol);
    }


    //Polygon
    public static void drawPolygon(Canvas canvas, IPolygon polygon, IFillSymbol symbol, FromMapPointDelegate delegate) {
        if (polygon != null && polygon.PartCount() != 0) {
            Path gp = new Path();
            Integer[] indexes = polygon.ExteriorRingIndex();

            Path pF;
            IPoint[] ipoints;
            IPart part;
            int index;
            PointF pC;
            int j;
            int k;
            for (index = 0; index < indexes.length - 1; ++index) {
                part = polygon.Parts()[indexes[index].intValue()];
                ipoints = part.Points();
                pF = new Path();
                pC = delegate.FromMapPoint(ipoints[0]);
                pF.moveTo(pC.x, pC.y);

                for (j = 1; j < ipoints.length; ++j) {
                    pC = delegate.FromMapPoint(ipoints[j]);
                    pF.lineTo(pC.x, pC.y);
                }

                pF.close();
                if (ipoints.length >= 3) {
                    gp.addPath(pF);
                }

                for (j = indexes[index].intValue() + 1; j < indexes[index + 1].intValue() - 1; ++j) {
                    part = polygon.Parts()[j];
                    ipoints = part.Points();
                    pF = new Path();
                    pC = delegate.FromMapPoint(ipoints[ipoints.length - 1]);
                    pF.moveTo(pC.x, pC.y);

                    for (k = ipoints.length - 2; k >= 0; --k) {
                        pC = delegate.FromMapPoint(ipoints[k]);
                        pF.lineTo(pC.x, pC.y);
                    }

                    pF.close();
                    if (ipoints.length >= 3) {
                        gp.addPath(pF);
                    }
                }

                pC = null;
            }

            index = indexes[indexes.length - 1].intValue();
            part = polygon.Parts()[index];
            ipoints = part.Points();
            pF = new Path();
            pC = delegate.FromMapPoint(ipoints[0]);
            pF.moveTo(pC.x, pC.y);

            for (j = 1; j < ipoints.length; ++j) {
                pC = delegate.FromMapPoint(ipoints[j]);
                pF.lineTo(pC.x, pC.y);
            }

            pF.close();
            if (ipoints.length >= 3) {
                gp.addPath(pF);
            }

            for (j = index + 1; j < polygon.PartCount(); ++j) {
                part = polygon.Parts()[j];
                ipoints = part.Points();
                pF = new Path();
                pC = delegate.FromMapPoint(ipoints[0]);
                pF.moveTo(pC.x, pC.y);

                for (k = 1; k < ipoints.length; ++k) {
                    pC = delegate.FromMapPoint(ipoints[k]);
                    pF.lineTo(pC.x, pC.y);
                }

                pF.close();
                if (ipoints.length >= 3) {
                    gp.addPath(pF);
                }
            }

            drawPolygon(canvas, gp, symbol);
            gp = null;
        }
    }

    public static void drawRectangle(Canvas canvas, IEnvelope rectangle, IFillSymbol symbol, FromMapPointDelegate delegate) {
        drawPolygon(canvas, rectangle.ConvertToPolygon(), symbol,delegate);
    }

    public static void drawPolygon(Canvas canvas, PointF[] points, IFillSymbol symbol) {
        Path gp = new Path();
        Path p2D = new Path();
        PointF pC = points[0];
        p2D.moveTo(pC.x, pC.y);

        for(int part = 1; part < points.length; ++part) {
            pC = points[part];
            p2D.lineTo(pC.x, pC.y);
        }

        p2D.close();
        gp.addPath(p2D);
        Part var12 = new Part();
        PointF[] var10 = points;
        int var9 = points.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            PointF point = var10[var8];
            Point p = new Point((double)point.x, (double)point.y);
            var12.AddPoint(p);
        }

        drawPolygon(canvas, gp, symbol);
        gp = null;
    }

    public static void drawRectangle(Canvas canvas, PointF TLPoint, PointF BRPoint, IFillSymbol symbol) {
        Path path = new Path();
        path.moveTo(TLPoint.x, TLPoint.y);
        path.lineTo(TLPoint.x, BRPoint.y);
        path.lineTo(BRPoint.x, BRPoint.y);
        path.lineTo(BRPoint.x, TLPoint.y);
        path.lineTo(TLPoint.x, TLPoint.y);
        path.close();
        drawPolygon(canvas, path, symbol);
    }

    private static void drawPolygon(Canvas canvas, Path path, IFillSymbol symbol) {

        if(symbol instanceof ISimpleFillSymbol) {

            Paint paintOutLine = null;
            DashPathEffect effects = null;

            if(symbol.getOutLineSymbol() != null) {
                paintOutLine = new Paint();
                paintOutLine.setAntiAlias(true);
                //paintOutLine.setAlpha(symbol.getOutLineSymbol().getTransparent());
                paintOutLine.setColor(symbol.getOutLineSymbol().getColor());
                paintOutLine.setStyle(Paint.Style.STROKE);
                paintOutLine.setStrokeWidth(symbol.getOutLineSymbol().getWidth());

                switch(((ISimpleLineSymbol)symbol.getOutLineSymbol()).getStyle()) {
                    case Solid:
                        break;
                    case Dash:
                        effects = new DashPathEffect(new float[]{10.0F, 3.0F}, 0.0F);
                        break;
                    case DashDot:
                        effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                        break;
                    case DashDotDot:
                        effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                        break;
                    case Dot:
                        effects = new DashPathEffect(new float[]{2.0F, 3.0F}, 0.0F);
                        break;
                    default:
                        break;
                }

                if(effects != null) {
                    paintOutLine.setPathEffect(effects);
                }
            }


            switch(((ISimpleFillSymbol) symbol).getStyle()) {
                case Soild:
                    defaultPaint.reset();
                    defaultPaint.setAntiAlias(true);
                    defaultPaint.setColor(((ISimpleFillSymbol)symbol).getColor());
                    defaultPaint.setStyle(Paint.Style.FILL);
                    canvas.drawPath(path, defaultPaint);
                    if(paintOutLine != null) {
                        canvas.drawPath(path, paintOutLine);
                    }
                    break;
                case Hollow:
                    if(paintOutLine != null) {
                        canvas.drawPath(path, paintOutLine);
                    }
                    break;
                default:
                    defaultPaint.reset();
                    defaultPaint.setAntiAlias(true);
                    defaultPaint.setColor(((ISimpleFillSymbol)(symbol instanceof ISimpleFillSymbol ?symbol:null)).getForeColor());
                    canvas.drawPaint(defaultPaint);
                    if(paintOutLine != null) {
                        canvas.drawPath(path, paintOutLine);
                    }
            }

            paintOutLine = null;
            effects = null;
        }
    }


    //Text
    public final void DrawText(String text, IPoint point, ITextSymbol symbol, float rate) {
        this.DrawText(text, this.mFromMapPointDelegate.FromMapPoint(point), symbol, rate);
    }

    public final void DrawText(String text, PointF pointF, ITextSymbol symbol, float size) {
        this.mtextrate = size;
        Paint paint;
        Rect bounds;
        int boundWidth;
        int boundHeight;
        if(!symbol.getVertical()) {
            paint = new Paint();
            paint.setTypeface(symbol.getFont());
            paint.setColor(symbol.getColor());
            paint.setTextSize(symbol.getSize() * this.mtextrate);
            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            boundWidth = bounds.width();
            boundHeight = bounds.height();
            this.mCanvas.drawText(text, pointF.x - (float)(boundWidth * 3 / 4), pointF.y + (float)boundHeight, paint);
        } else {
            paint = new Paint();
            paint.setTypeface(symbol.getFont());
            paint.setColor(symbol.getColor());
            paint.setTextSize(symbol.getSize() * this.mtextrate);
            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            boundWidth = bounds.width();
            boundHeight = bounds.height();
            this.mCanvas.drawText(text, pointF.x - (float)(boundWidth * 3 / 4), pointF.y + (float)(boundHeight * 4 / 3), paint);
        }
    }

    public final void DrawHighlightText(String text, PointF pointF, ITextSymbol symbol) {
        Paint paint = new Paint();
        paint.setColor(symbol.getColor());
        paint.setTypeface(symbol.getFont());
        this.mCanvas.drawText(text, pointF.x, pointF.y, paint);
    }

    //Image
    public final void DrawImage(Bitmap image, IEnvelope extent) {
        PointF TL = this.mFromMapPointDelegate.FromMapPoint(new Point(extent.XMin(), extent.YMax()));
        PointF BR = this.mFromMapPointDelegate.FromMapPoint(new Point(extent.XMax(), extent.YMin()));
        RectF rectangle = new RectF(TL.x, TL.y, BR.x - TL.x, BR.y - TL.y);
        this.DrawImage(image, rectangle);
    }

    public final void DrawImage(Bitmap image, RectF rectangle) {
        this.mCanvas.drawBitmap(image, (Rect)null, rectangle, (Paint)null);
    }

    public final void DrawImage(Bitmap image, PointF point) {
        this.mCanvas.drawBitmap(image, point.x, point.y, (Paint)null);
    }

    public final void DrawColor(int color) {
        this.mCanvas.drawColor(color);
    }


    public final void DrawAngle(IPoint iPoint, double angle, ILineSymbol symbol) {
        Paint p = new Paint();
        p.setColor(symbol.getColor());
        RectF oval = new RectF(100.0F, 100.0F, 100.0F, 100.0F);
        this.mCanvas.drawArc(oval, 90.0F, 90.0F, false, p);
    }
}
