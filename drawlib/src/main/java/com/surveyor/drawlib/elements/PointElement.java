package com.surveyor.drawlib.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.surveyor.drawlib.common.Drawing;

import srs.Display.FromMapPointDelegate;
import srs.Display.Symbol.IPointSymbol;
import srs.Display.Symbol.SimplePointSymbol;
import srs.Geometry.IPoint;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public class PointElement extends Element implements IPointElement {
    private IPointSymbol mSymbol;

    public PointElement() {
        this.mSymbol = new SimplePointSymbol();
    }

    public final IPointSymbol getSymbol() {
        return this.mSymbol;
    }

    public final void setSymbol(IPointSymbol value) {
        if(this.mSymbol != value) {
            this.mSymbol = value;
        }
    }

    @Override
    public void draw(Bitmap canvas, FromMapPointDelegate delegate) throws sRSException {

        if (this.getGeometry() == null) {
            throw new sRSException("1020");
        }

        if (this.mSymbol == null) {
            throw new sRSException("1021");
        }

        if (!(this.getGeometry() instanceof IPoint)) {
            throw new sRSException("1022");
        }

        Drawing.drawPoint(new Canvas(canvas), (IPoint) this.getGeometry(), this.getSymbol(), delegate);
    }

    @Override
    public IElement clone() {
        PointElement element = new PointElement();
        element.setName(this.getName());

        if(this.getGeometry() != null) {
            element.setGeometry(this.getGeometry().Clone());
        }

        if (this.getSymbol() != null) {
            element.setSymbol((IPointSymbol) this.getSymbol().Clone());
        }
        return element;
    }
}
