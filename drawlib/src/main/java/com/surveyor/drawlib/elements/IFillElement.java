package com.surveyor.drawlib.elements;

import srs.Display.Symbol.IFillSymbol;

/**
 * Created by stg on 17/10/29.
 */
public interface IFillElement extends IElement {
    IFillSymbol getSymbol();

    void setSymbol(IFillSymbol symbol);
}