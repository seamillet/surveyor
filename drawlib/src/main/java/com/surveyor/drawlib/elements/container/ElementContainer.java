package com.surveyor.drawlib.elements.container;

import com.surveyor.drawlib.elements.IElement;
import com.surveyor.drawlib.elements.ILineElement;
import com.surveyor.drawlib.elements.IPointElement;

import org.dom4j.Element;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import srs.Display.FromMapPointDelegate;
import srs.Display.IScreenDisplay;
import srs.Display.ScreenDisplay;
import srs.Geometry.IEnvelope;
import srs.Geometry.IGeometry;
import srs.Geometry.IPoint;
import srs.Geometry.IRelationalOperator;
import srs.Geometry.ISpatialOperator;
import srs.Geometry.srsGeometryType;
import srs.Layer.Event.ElementManager;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/28.
 */
public class ElementContainer implements IElementContainer {
    private IScreenDisplay mScreenDisplay;
    private List<IElement> mElements;
    private List<Integer> mSelectedElements;

    private ElementManager mElementChanged = new ElementManager();
    private ElementManager mElementselectedChanged = new ElementManager();

    public ElementManager getElementSelectedChanged() {
        return this.mElementselectedChanged != null?this.mElementselectedChanged:null;
    }

    public ElementManager getElementChanged() {
        return this.mElementChanged != null?this.mElementChanged:null;
    }

    public ElementContainer(IScreenDisplay screenDisplay) {
        this.mScreenDisplay = screenDisplay;
        this.mElements = new ArrayList();
        this.mSelectedElements = new ArrayList();
    }

    public final List<Integer> getSelectedElements() {
        if(this.mSelectedElements == null) {
            this.mSelectedElements = new ArrayList();
        }

        return this.mSelectedElements;
    }

    public final void setSelectedElements(List<Integer> value) {
        this.mSelectedElements = value;
        if(this.mElementselectedChanged != null) {
            try {
                this.mElementselectedChanged.fireListener();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }
    }

    public void setSelectedElement(Integer value) {
        if(this.mSelectedElements != null) {
            this.mSelectedElements.clear();
        } else {
            this.mSelectedElements = new ArrayList();
        }

        this.mSelectedElements.add(value);
        if(this.mElementselectedChanged != null) {
            try {
                this.mElementselectedChanged.fireListener();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }
    }

    public final int getElementCount() {
        return this.mElements.size();
    }

    public final List<IElement> getElements() {
        return this.mElements;
    }

    public final IEnvelope getExtent() throws IOException {
        IEnvelope envelope = null;
        if(this.mElements.size() > 0) {
            envelope = ((IElement)this.mElements.get(0)).getGeometry().Extent();

            for(int i = 1; i < this.mElements.size(); ++i) {
                IEnvelope tempVar = ((IElement)this.mElements.get(i)).getGeometry().Extent();
                IGeometry tempVar2 = ((ISpatialOperator)(tempVar instanceof ISpatialOperator ?tempVar:null)).Union(envelope);
                envelope = (IEnvelope)(tempVar2 instanceof IEnvelope ?tempVar2:null);
            }
        }
        return envelope;
    }

    public final void AddElement(IElement element) throws IOException {
        this.mElements.add(element);
        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public final void AddElements(IElement[] elements) throws IOException {
        this.mElements.addAll(Arrays.asList(elements));
        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public final void AddElements(List<IElement> elements) throws IOException {
        this.mElements.addAll(elements);
        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public final void RemoveElementsE(List<IElement> elements) throws IOException {
        Iterator var3 = elements.iterator();

        while(var3.hasNext()) {
            IElement ele = (IElement)var3.next();
            if(this.mElements.contains(ele)) {
                this.mElements.remove(ele);
            }
        }

        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public final void RemoveElementsI(List<Integer> ids) throws IOException {
        ArrayList elements = new ArrayList();

        for(int i = 0; i < ids.size(); ++i) {
            elements.add((IElement)this.mElements.get(((Integer)ids.get(i)).intValue()));
        }

        this.RemoveElementsE(elements);
    }

    public void RemoveElement(IElement elements) throws IOException {
        if(this.mElements.contains(elements)) {
            this.mElements.remove(elements);
        }

        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public void RemoveElement(Integer id) throws IOException {
        ArrayList elements = new ArrayList();
        elements.add((IElement)this.mElements.get(id.intValue()));
        this.RemoveElementsE(elements);
    }

    public final void ClearElement() throws IOException {
        this.mElements.clear();
        this.ClearSelectedElement();
        if(this.mElementChanged != null) {
            this.mElementChanged.fireListener();
        }
    }

    public final void ClearSelectedElement() {
        this.mSelectedElements.clear();
    }

    public final void Refresh() {
        this.Refresh(new FromMapPointDelegate((ScreenDisplay)this.mScreenDisplay));
    }

    public final void Refresh(FromMapPointDelegate Delegate) {
        try {
            for(int i = 0; i < this.mElements.size(); ++i) {

                ((IElement)this.mElements.get(i)).draw(this.mScreenDisplay.getCanvas(), Delegate);
                /*if(this.mSelectedElements != null && this.mSelectedElements.contains(Integer.valueOf(i))) {
                    ((IElement)this.mElements.get(i)).DrawSelected(this.mScreenDisplay.getCanvas(), Delegate);
                }*/
            }
        } catch (sRSException e) {
            e.printStackTrace();
        }
    }

    public void dispose() throws Exception {
        this.mScreenDisplay = null;
        this.mElements = null;
        this.mSelectedElements = null;
        this.mElementChanged = null;
        this.mElementselectedChanged = null;
    }

    public final List<Integer> SelectPoint(IGeometry geometry, boolean isMulti) throws IOException {
        ArrayList ids = new ArrayList();
        int i;
        double buffer;
        IGeometry tempVar3;
        IEnvelope env;
        if(geometry.GeometryType() == srsGeometryType.Point) {
            for(i = 0; i < this.mElements.size(); ++i) {
                if(((IElement)this.mElements.get(i)).getGeometry().GeometryType() == srsGeometryType.Point) {
                    buffer = this.mScreenDisplay.ToMapDistance((double)(((IPointElement)(this.mElements.get(i) instanceof IPointElement?(IElement)this.mElements.get(i):null)).getSymbol().getSize() / 2.0F));
                    tempVar3 = ((IElement)this.mElements.get(i)).getGeometry();
                    env = ((IPoint)(tempVar3 instanceof IPoint ?tempVar3:null)).ExpandEnvelope(buffer);
                    if(((IRelationalOperator)(env instanceof IRelationalOperator ?env:null)).Contains(geometry)) {
                        ids.add(Integer.valueOf(i));
                    }
                }
            }

            if(!isMulti && ids.size() > 0) {
                ArrayList var9 = new ArrayList();
                var9.add((Integer)ids.get(ids.size() - 1));
                return var9;
            }
        } else {
            for(i = 0; i < this.mElements.size(); ++i) {
                if(((IElement)this.mElements.get(i)).getGeometry().GeometryType() == srsGeometryType.Point) {
                    buffer = this.mScreenDisplay.ToMapDistance((double)(((IPointElement)(this.mElements.get(i) instanceof IPointElement?(IElement)this.mElements.get(i):null)).getSymbol().getSize() / 2.0F));
                    tempVar3 = ((IElement)this.mElements.get(i)).getGeometry();
                    env = ((IPoint)(tempVar3 instanceof IPoint ?tempVar3:null)).ExpandEnvelope(buffer);
                    if(((IRelationalOperator)(geometry instanceof IRelationalOperator ?geometry:null)).Intersects(env)) {
                        ids.add(Integer.valueOf(i));
                    }
                }
            }
        }

        return ids;
    }

    public final List<Integer> Select(IGeometry geometry, boolean isMulti) throws IOException {
        ArrayList ids = new ArrayList();
        int i;
        double tempVar4;
        IGeometry tempVar3;
        IEnvelope env;
        IGeometry var10;
        if(geometry.GeometryType() == srsGeometryType.Point) {
            for(i = 0; i < this.mElements.size(); ++i) {
                if(((IElement)this.mElements.get(i)).getGeometry().GeometryType() == srsGeometryType.Point) {
                    tempVar4 = this.mScreenDisplay.ToMapDistance((double)(((IPointElement)(this.mElements.get(i) instanceof IPointElement?(IElement)this.mElements.get(i):null)).getSymbol().getSize() / 2.0F));
                    tempVar3 = ((IElement)this.mElements.get(i)).getGeometry();
                    env = ((IPoint)(tempVar3 instanceof IPoint ?tempVar3:null)).ExpandEnvelope(tempVar4);
                    if(((IRelationalOperator)(env instanceof IRelationalOperator ?env:null)).Contains(geometry)) {
                        ids.add(Integer.valueOf(i));
                    }
                } else if(((IElement)this.mElements.get(i)).getGeometry().GeometryType() == srsGeometryType.Polyline) {
                    tempVar4 = this.mScreenDisplay.ToMapDistance((double)(((ILineElement)(this.mElements.get(i) instanceof ILineElement?(IElement)this.mElements.get(i):null)).getSymbol().getWidth() / 2.0F));
                    IEnvelope var11 = ((IPoint)(geometry instanceof IPoint ?geometry:null)).ExpandEnvelope(tempVar4);
                    if(((IRelationalOperator)(var11 instanceof IRelationalOperator ?var11:null)).Intersects(((IElement)this.mElements.get(i)).getGeometry())) {
                        ids.add(Integer.valueOf(i));
                    }
                } else {
                    var10 = ((IElement)this.mElements.get(i)).getGeometry();
                    if(((IRelationalOperator)(var10 instanceof IRelationalOperator ?var10:null)).Contains(geometry)) {
                        ids.add(Integer.valueOf(i));
                    }
                }
            }

            if(!isMulti && ids.size() > 0) {
                ArrayList var9 = new ArrayList();
                var9.add((Integer)ids.get(ids.size() - 1));
                return var9;
            }
        } else {
            for(i = 0; i < this.mElements.size(); ++i) {
                if(((IElement)this.mElements.get(i)).getGeometry().GeometryType() == srsGeometryType.Point) {
                    tempVar4 = this.mScreenDisplay.ToMapDistance((double)(((IPointElement)(this.mElements.get(i) instanceof IPointElement?(IElement)this.mElements.get(i):null)).getSymbol().getSize() / 2.0F));
                    tempVar3 = ((IElement)this.mElements.get(i)).getGeometry();
                    env = ((IPoint)(tempVar3 instanceof IPoint ?tempVar3:null)).ExpandEnvelope(tempVar4);
                    if(((IRelationalOperator)(geometry instanceof IRelationalOperator ?geometry:null)).Intersects(env)) {
                        ids.add(Integer.valueOf(i));
                    }
                } else {
                    var10 = ((IElement)this.mElements.get(i)).getGeometry();
                    if(((IRelationalOperator)(var10 instanceof IRelationalOperator ?var10:null)).Intersects(geometry)) {
                        ids.add(Integer.valueOf(i));
                    }
                }
            }
        }

        return ids;
    }

    public final void LoadXMLData(Element node) throws SecurityException, IllegalArgumentException, ClassNotFoundException, sRSException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        /*if(node != null) {
            this.mElements.clear();
            this.mSelectedElements.clear();
            if(node.attribute("SelectElements") != null) {
                String elementsNode = node.attributeValue("SelectElements");
                String[] nodeList = elementsNode.split("[,]", -1);
                if(nodeList != null && nodeList.length > 0) {
                    for(int elementNode = 0; elementNode < nodeList.length; ++elementNode) {
                        boolean element = true;
                        int var9 = Integer.parseInt(nodeList[elementNode]);
                        if(var9 >= 0) {
                            this.mSelectedElements.add(Integer.valueOf(var9));
                        }
                    }
                }
            }

            Element var6 = node.element("Elements");
            if(var6 != null) {
                Iterator var7 = var6.elementIterator();

                while(var7.hasNext()) {
                    Element var8 = (Element)var7.next();
                    if(var8.getName().equals("Element")) {
                        IElement var10 = XmlFunction.LoadElementXML(var8);
                        if(var10 != null) {
                            this.mElements.add(var10);
                        }
                    }
                }
            }

        }*/
    }

    public final void SaveXMLData(Element node) {
        /*if(node != null) {
            Element elementsNode = node.getDocument().addElement("Elements");
            String selectValues = "";
            if(this.mSelectedElements.size() > 0) {
                selectValues = selectValues + ((Integer)this.mSelectedElements.get(0)).toString();

                for(int element = 1; element < this.mSelectedElements.size(); ++element) {
                    selectValues = selectValues + "," + ((Integer)this.mSelectedElements.get(element)).toString();
                }
            }

            XmlFunction.AppendAttribute(node, "SelectElements", selectValues);
            Iterator var5 = this.mElements.iterator();

            while(var5.hasNext()) {
                IElement var7 = (IElement)var5.next();
                Element elementNode = node.getDocument().addElement("Element");
                XmlFunction.SaveElementXML(elementNode, var7);
                elementsNode.add(elementNode);
            }

            node.add(elementsNode);
        }*/
    }
}
