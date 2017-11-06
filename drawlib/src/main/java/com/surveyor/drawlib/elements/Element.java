package com.surveyor.drawlib.elements;

import srs.Geometry.IGeometry;

/**
 * Created by stg on 17/10/28.
 */
public abstract class Element implements IElement {
    private IGeometry mGeometry;
    private String mName = "";

    protected Element() {
    }

    public IGeometry getGeometry() {
        return this.mGeometry;
    }

    public void setGeometry(IGeometry value) {
        this.mGeometry = value;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String value) {
        this.mName = value;
    }

    /*public void DrawSelected(Bitmap canvas, FromMapPointDelegate Delegate) {
        try {
            if(this.mGeometry == null) {
                throw new sRSException("1020");
            }

            if(Setting.SelectElementStyle == null) {
                throw new sRSException("1021");
            }

            Drawing e = new Drawing(new Canvas(canvas), Delegate);
            if(this.mGeometry.GeometryType() == srsGeometryType.Point) {
                if(((IPointElement)(this instanceof IPointElement?this:null)).getSymbol() == null) {
                    throw new sRSException("1021");
                }

                PointF pointF = Delegate.FromMapPoint((IPoint)(this.mGeometry instanceof IPoint?this.mGeometry:null));
                IPointSymbol symbol = ((IPointElement)(this instanceof IPointElement?this:null)).getSymbol();
                PointF TLPoint = new PointF(pointF.x - symbol.getSize() / 2.0F, pointF.y - symbol.getSize() / 2.0F);
                PointF BRPoint = new PointF(pointF.x + symbol.getSize() / 2.0F, pointF.y + symbol.getSize() / 2.0F);
                e.DrawRectangle(TLPoint, BRPoint, Setting.SelectElementStyle);
            } else {
                e.DrawRectangle(this.mGeometry.Extent(), Setting.SelectElementStyle);
            }
        } catch (sRSException var8) {
            var8.printStackTrace();
        }
    }*/

    public IElement clone() {
        return null;
    }
}
