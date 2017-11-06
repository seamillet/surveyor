package com.surveyor.drawlib.elements;

import srs.Display.Symbol.ILineSymbol;

/**
 * Created by stg on 17/10/29.
 */
public interface ILineElement extends IElement {
    ILineSymbol getSymbol();

    void setSymbol(ILineSymbol symbol);
}
