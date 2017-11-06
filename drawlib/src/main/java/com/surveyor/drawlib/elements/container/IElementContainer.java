package com.surveyor.drawlib.elements.container;

import com.surveyor.drawlib.elements.IElement;

import java.io.IOException;
import java.util.List;

import srs.Display.FromMapPointDelegate;
import srs.Geometry.IEnvelope;
import srs.Geometry.IGeometry;
import srs.Layer.Event.ElementManager;

/**
 * Created by stg on 17/10/28.
 */
public interface IElementContainer {
    int getElementCount();

    List<IElement> getElements();

    List<Integer> getSelectedElements();

    void setSelectedElements(List<Integer> var1);

    void setSelectedElement(Integer var1);

    IEnvelope getExtent() throws IOException;

    ElementManager getElementChanged();

    ElementManager getElementSelectedChanged();

    void AddElement(IElement var1) throws IOException;

    void AddElements(IElement[] var1) throws IOException;

    void AddElements(List<IElement> var1) throws IOException;

    void RemoveElementsE(List<IElement> var1) throws IOException;

    void RemoveElementsI(List<Integer> var1) throws IOException;

    void RemoveElement(IElement var1) throws IOException;

    void RemoveElement(Integer var1) throws IOException;

    void ClearElement() throws IOException;

    void ClearSelectedElement();

    List<Integer> Select(IGeometry var1, boolean var2) throws IOException;

    List<Integer> SelectPoint(IGeometry var1, boolean var2) throws IOException;

    void Refresh();

    void Refresh(FromMapPointDelegate var1);

    void dispose() throws Exception;
}
