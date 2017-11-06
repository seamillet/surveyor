package com.surveyor.drawlib.elements;

import android.graphics.Bitmap;

import srs.Display.FromMapPointDelegate;
import srs.Geometry.IGeometry;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public interface IElement {
    IGeometry getGeometry();

    void setGeometry(IGeometry geometry);

    String getName();

    void setName(String name);

    void draw(Bitmap canvas, FromMapPointDelegate delegate) throws sRSException;

    IElement clone();
}
