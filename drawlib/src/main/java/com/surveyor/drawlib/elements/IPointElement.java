package com.surveyor.drawlib.elements;

import srs.Display.Symbol.IPointSymbol;

/**
 * Created by stg on 17/10/29.
 */
public interface IPointElement extends IElement {
    IPointSymbol getSymbol();

    void setSymbol(IPointSymbol symbol);
}
