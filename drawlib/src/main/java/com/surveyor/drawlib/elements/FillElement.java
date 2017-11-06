package com.surveyor.drawlib.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.surveyor.drawlib.common.Drawing;

import java.math.BigDecimal;

import srs.Display.FromMapPointDelegate;
import srs.Display.Symbol.IFillSymbol;
import srs.Display.Symbol.ISymbol;
import srs.Display.Symbol.SimpleFillSymbol;
import srs.Display.Symbol.TextSymbol;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public class FillElement extends Element implements IFillElement {
    private IFillSymbol mSymbol;
    private boolean mIsDraw = false;

    public FillElement() {
        this.mSymbol = new SimpleFillSymbol();
    }

    public FillElement(boolean isDraw) {
        this.mIsDraw = isDraw;
        this.mSymbol = new SimpleFillSymbol();
    }

    public final IFillSymbol getSymbol() {
        return this.mSymbol;
    }

    public final void setSymbol(IFillSymbol value) {
        if(this.mSymbol != value) {
            this.mSymbol = value;
        }
    }

    public void draw(Bitmap canvas, FromMapPointDelegate delegate) throws sRSException {
        if(this.getGeometry() == null) {
            throw new sRSException("1020");
        }

        if(this.mSymbol == null) {
            throw new sRSException("1021");
        }

        if(!(this.getGeometry() instanceof IEnvelope) && !(this.getGeometry() instanceof IPolygon)) {
            throw new sRSException("1022");
        }

        try {
            Drawing e = new Drawing(new Canvas(canvas), delegate);
            if(this.getGeometry() instanceof IEnvelope) {
                Drawing.drawRectangle(new Canvas(canvas), (IEnvelope) this.getGeometry(), this.getSymbol(),delegate);
                //e.DrawRectangle((IEnvelope)this.getGeometry(), this.mSymbol);
            } else {
                if(this.mIsDraw) {
                    double areaValue = ((IPolygon)this.getGeometry()).Area();
                    IPoint iPoint = this.getGeometry().CenterPoint();
                    BigDecimal bd = (new BigDecimal(areaValue / 666.666D)).setScale(4, 4);
                    e.DrawText(bd + "(亩)", iPoint, new TextSymbol(), 2.0F);
                }
                //e.DrawPolygon((IPolygon)this.getGeometry(), this.mSymbol);
                Drawing.drawPolygon(new Canvas(canvas), (IPolygon) this.getGeometry(), this.getSymbol(),delegate);
            }
        } catch (sRSException var8) {
            var8.printStackTrace();
        }
    }

    public IElement clone() {
        FillElement element = new FillElement();
        element.setName(this.getName());
        if(this.getGeometry() != null) {
            element.setGeometry(this.getGeometry().Clone());
        }

        if(element.getSymbol() instanceof IFillSymbol) {
            ISymbol tempVar = this.mSymbol.Clone();
            element.setSymbol((IFillSymbol)(tempVar instanceof IFillSymbol ?tempVar:null));
        }

        return element;
    }
}
