package com.surveyor.drawlib.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.surveyor.drawlib.common.Drawing;

import java.math.BigDecimal;

import srs.Display.FromMapPointDelegate;
import srs.Display.Symbol.ILineSymbol;
import srs.Display.Symbol.ISymbol;
import srs.Display.Symbol.SimpleLineSymbol;
import srs.Geometry.IPolyline;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public class LineElement extends Element implements ILineElement {
    private ILineSymbol mSymbol = new SimpleLineSymbol();
    private boolean isDraw = false;

    public LineElement() {
    }

    public LineElement(boolean flg) {
        this.isDraw = flg;
    }

    public final ILineSymbol getSymbol() {
        return this.mSymbol;
    }

    public final void setSymbol(ILineSymbol value) {
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

        if(!(this.getGeometry() instanceof IPolyline)) {
            throw new sRSException("1022");
        }

        /*try {
            Drawing e = new Drawing(new Canvas(canvas), Delegate);
            e.DrawPolyline((IPolyline)this.getGeometry(), this.mSymbol);
        } catch (sRSException var4) {
            var4.printStackTrace();
        }*/
        Drawing.drawPolyline(new Canvas(canvas),(IPolyline)this.getGeometry(),this.mSymbol,delegate);
    }

    public BigDecimal reservedDecimal(double x) {
        BigDecimal bd = new BigDecimal(x);
        bd = bd.setScale(1, 4);
        return bd;
    }

    public IElement clone() {
        LineElement element = new LineElement();
        element.setName(this.getName());
        if(this.getGeometry() != null) {
            element.setGeometry(this.getGeometry().Clone());
        }

        if(this.mSymbol instanceof ILineSymbol) {
            ISymbol tempVar = this.mSymbol.Clone();
            element.setSymbol((ILineSymbol)(tempVar instanceof ILineSymbol ?tempVar:null));
        }

        return element;
    }
}
