package com.surveyor.drawlib.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.surveyor.drawlib.common.Drawing;

import srs.Display.FromMapPointDelegate;
import srs.Display.Setting;
import srs.Display.Symbol.ISymbol;
import srs.Display.Symbol.ITextSymbol;
import srs.Display.Symbol.TextSymbol;
import srs.Geometry.Envelope;
import srs.Geometry.IGeometry;
import srs.Geometry.IPoint;
import srs.Geometry.Point;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public class TextElement extends Element implements ITextElement {
    private static final String TAG = TextElement.class.getSimpleName();

    private ITextSymbol mSymbol = new TextSymbol();
    private boolean mScaleText = true;
    private String mText = "文字";

    public TextElement() {
    }

    public final ITextSymbol getSymbol() {
        return this.mSymbol;
    }

    public final void setSymbol(ITextSymbol value) {
        if(this.mSymbol != value) {
            this.mSymbol = value;
            /*if(this.getGeometry() != null) {
                this.setGeometry(this.getGeometry().Extent().LowerLeft());
            }
            this.setGeometry((IGeometry)null);*/
        }
    }

    public final boolean getScaleText() {
        return this.mScaleText;
    }

    public final void setScaleText(boolean value) {
        this.mScaleText = value;
    }

    public final String getText() {
        return this.mText;
    }

    public final void setText(String value) {
        if(!this.mText.equals(value)) {
            this.mText = value;
            /*if(this.getGeometry() != null) {
                this.setGeometry(this.getGeometry().Extent().LowerLeft());
            }
            this.setGeometry((IGeometry)null);*/
        }
    }

    public IGeometry getGeometry() {
        return super.getGeometry();
    }

    public void setGeometry(IGeometry value) {
        super.setGeometry(value);

        /*if(super.getGeometry() == null) {
            super.setGeometry(value);
            this.setGeometry((IGeometry)null);
        } else if(super.getGeometry() != value && value != null) {
            this.SetTextSize(super.getGeometry(), value);
            super.setGeometry(value);
        }*/
    }

    public void draw(Bitmap canvas, FromMapPointDelegate Delegate) {
        try {
            if(this.getGeometry() == null) {
                throw new sRSException("1020");
            }

            if(this.mSymbol == null) {
                throw new sRSException("1021");
            }

//            if(!this.mScaleText) {
//                this.setGeometry(this.getGeometry().Extent().LowerLeft());
//                this.SetGeometry(Delegate);
//            } else {
//                this.setGeometry(this.getGeometry().Extent().LowerLeft());
//                this.SetGeometry((FromMapPointDelegate)null);
//            }

            Drawing e = new Drawing(new Canvas(canvas), Delegate);
            ITextSymbol textSymbol = (ITextSymbol)((TextSymbol)this.mSymbol).Clone();
            if(this.mScaleText) {
                PointF lt = Delegate.FromMapPoint(new Point(this.getGeometry().Extent().XMin(), this.getGeometry().Extent().YMax()));
                PointF rb = Delegate.FromMapPoint(new Point(this.getGeometry().Extent().XMax(), this.getGeometry().Extent().YMin()));
                float height = Math.abs(lt.y - rb.y);
                float oldHeight = (float)(this.getGeometry().Extent().YMax() - this.getGeometry().Extent().YMin());
                if(height > 0.0F && oldHeight > 0.0F) {
                    Typeface fomat = this.mSymbol.getFont();
                    textSymbol.setFont(fomat);
                    textSymbol.setSize(this.mSymbol.getSize());
                }
            }

            e.DrawText(this.mText, new Point(this.getGeometry().CenterPoint().X(), this.getGeometry().CenterPoint().Y()), textSymbol, Setting.TextRate);
        } catch (sRSException var10) {
            var10.printStackTrace();
        }
    }

    private void SetGeometry(FromMapPointDelegate Delegate) {
        if(this.getGeometry() != null && this.mSymbol != null) {
            Paint paint = new Paint();
            paint.setTypeface(this.mSymbol.getFont());
            paint.setTextSize(this.mSymbol.getSize());
            Rect bounds = new Rect();
            paint.getTextBounds(this.mText, 0, this.mText.length(), bounds);
            float boundWidth = paint.measureText(this.mText);
            float boundHeight = paint.measureText(this.mText);
            if(Delegate != null) {
                PointF point = Delegate.FromMapPoint(new Point(0.0D, 0.0D));
                PointF Xmin = Delegate.FromMapPoint(new Point(0.0D, 100.0D));
                float rate = 100.0F / Math.abs(Xmin.y - point.y);
                boundWidth *= rate;
                boundHeight *= rate;
            }

            IPoint point1 = this.getGeometry().CenterPoint();
            double Xmin1 = point1.X();
            double Xmax = point1.X() + (double)boundWidth;
            double Ymin = point1.Y();
            double Ymax = point1.Y() + (double)boundHeight;
            super.setGeometry(new Envelope(Xmin1, Ymin, Xmax, Ymax));
        }
    }

    private void SetTextSize(IGeometry oldGeo, IGeometry newGeo) {
        float oldHeight = (float)(oldGeo.Extent().YMax() - oldGeo.Extent().YMin());
        float newHeight = (float)(newGeo.Extent().YMax() - newGeo.Extent().YMin());
        Log.i(TAG, "SetTextSize---->old_height is " + oldHeight + ", new_height is " + newHeight);

        if(oldHeight > 0.0F && newHeight > 0.0F && this.mSymbol != null) {
            float rate = 1.0F;

            try {
                rate = newHeight / oldHeight;
            } catch (Exception var7) {
                Log.e("文字要素显示", "原始高度为0");
            }

            this.mSymbol.setSize(this.mSymbol.getSize() * rate);
        }

    }

    public IElement clone() {
        TextElement textElement = new TextElement();
        textElement.setName(this.getName());
        textElement.setScaleText(this.mScaleText);
        if(this.mSymbol instanceof ITextSymbol) {
            ISymbol tempVar = this.mSymbol.Clone();
            textElement.setSymbol((ITextSymbol)(tempVar instanceof ITextSymbol ?tempVar:null));
        }

        textElement.setText(this.mText);
        if(this.getGeometry() != null) {
            textElement.setGeometry(new Point(this.getGeometry().Extent().XMin(), this.getGeometry().Extent().YMin()));
            textElement.setGeometry(this.getGeometry().Clone());
        }
        return textElement;
    }
}
