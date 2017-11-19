/**
 *
 */
package com.surveyor.drawlib.common;

import android.graphics.Color;
import android.graphics.Typeface;

import srs.Display.Symbol.ISimpleFillSymbol;
import srs.Display.Symbol.ISimpleLineSymbol;
import srs.Display.Symbol.ISimplePointSymbol;
import srs.Display.Symbol.ITextSymbol;
import srs.Display.Symbol.SimpleFillStyle;
import srs.Display.Symbol.SimpleFillSymbol;
import srs.Display.Symbol.SimpleLineStyle;
import srs.Display.Symbol.SimpleLineSymbol;
import srs.Display.Symbol.SimplePointStyle;
import srs.Display.Symbol.SimplePointSymbol;
import srs.Display.Symbol.TextSymbol;

/**
 * @author keqian Elements默认显示样式
 */
public final class ElementStyles {
    public final static ISimplePointSymbol NoFocusedPointStyle = new SimplePointSymbol(Color.WHITE, 14, SimplePointStyle.Square);
    public final static ISimplePointSymbol FocusedPointStyle = new SimplePointSymbol(Color.RED, 14, SimplePointStyle.Square);
    public final static ISimplePointSymbol POINT_LAST_STYLE = new SimplePointSymbol(Color.RED, 20, SimplePointStyle.Circle);
    public final static ISimplePointSymbol POINT_VERTEX_STYLE = new SimplePointSymbol(Color.RED, 16, SimplePointStyle.Square);
    public final static ISimplePointSymbol NoFocusedMidPointStyle = new SimplePointSymbol(Color.rgb(64, 200, 255), 9, SimplePointStyle.Circle);
    public final static ISimplePointSymbol FocusedMidPointStyle = new SimplePointSymbol(Color.RED, 9, SimplePointStyle.Square);
    public final static ISimpleLineSymbol LineStyle = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineStyle.Solid);
    public final static ISimpleFillSymbol PolygonStyle = new SimpleFillSymbol(Color.argb(120, 242, 240, 26), LineStyle, SimpleFillStyle.Soild);
    public final static ISimpleLineSymbol LineStyleHighlight = new SimpleLineSymbol(Color.argb(255, 0, 255, 255), 3, SimpleLineStyle.Solid);
    public final static ISimpleFillSymbol PolygonStyleHighlight = new SimpleFillSymbol(Color.argb(64, 0, 64, 240), LineStyleHighlight, SimpleFillStyle.Soild);
    public static ITextSymbol Text_DISTANCE_STYLE = null;

    static{
        Text_DISTANCE_STYLE = generateTextSymbol(null, Color.rgb(255, 140, 0), 24.0f);
    }

    private static ITextSymbol generateTextSymbol(Typeface typeface, int color, float textSize) {
        ITextSymbol symbol = new TextSymbol();
        if (typeface != null) {
            symbol.setFont(typeface);
        }
        symbol.setColor(color);
        symbol.setSize(textSize);
        return symbol;
    }
}
