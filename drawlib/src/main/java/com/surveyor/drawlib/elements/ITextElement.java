package com.surveyor.drawlib.elements;

import srs.Display.Symbol.ITextSymbol;

/**
 * Created by stg on 17/10/29.
 */
public interface ITextElement extends IElement {
    ITextSymbol getSymbol();

    void setSymbol(ITextSymbol symbol);

    boolean getScaleText();

    void setScaleText(boolean scaleText);

    String getText();

    void setText(String text);
}