package com.surveyor.drawlib.map;

import android.annotation.SuppressLint;

import com.surveyor.drawlib.map.event.ContentChangedManager;
import com.surveyor.drawlib.map.event.FocusMapChangedManager;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import srs.Core.XmlFunction;
import srs.Geometry.Envelope;

/**
 * Created by stg on 17/10/14.
 */
public class ActiveView implements IActiveView{
    private boolean isRelativePath = true;
    private IMap mMap;
    private ContentChangedManager mContentChanged = new ContentChangedManager();
    private FocusMapChangedManager mFocusMapChanged = new FocusMapChangedManager();

    public ActiveView() {
    }

    public ContentChangedManager getContentChanged() {
        return this.mContentChanged;
    }

    public FocusMapChangedManager getFocusMapChanged() {
        return this.mFocusMapChanged;
    }

    public final IMap FocusMap() {
        if(this.mMap == null) {
            this.mMap = new Map(new Envelope(0.0D, 0.0D, 60.0D, 60.0D));
        }

        return this.mMap;
    }

    public final void FocusMap(IMap value) {
        if(this.mMap == null) {
            this.mMap = value;
        } else if(!this.mMap.equals(value)) {
            this.mMap = value;
        }

    }

    public final boolean IsRelativePath() {
        return this.isRelativePath;
    }

    public final void IsRelativePath(boolean value) {
        this.isRelativePath = value;
    }

    @SuppressLint({"DefaultLocale"})
    public final void LoadFromFile(String filePath) throws DocumentException {
        if(!filePath.equals("")) {
            Document doc = (new SAXReader()).read(filePath).getDocument();
            Element parentNode = doc.getRootElement();
            if(parentNode.getName() != "ActiveView") {
                parentNode = parentNode.element("ActiveView");
            }

            if(parentNode.attribute("IsRelativePath") != null && parentNode.attributeValue("IsRelativePath").toUpperCase().equals("TRUE")) {
                File file = new File(filePath);
                this.LoadFromRelativePath(parentNode, file.getParent());
            }

            this.LoadXMLData(parentNode);
        }

        if(this.getContentChanged() != null) {
            this.getContentChanged().fireListener();
        }

    }

    private void LoadFromRelativePath(Element node, String workSpace) {
        try {
            Iterator frameNodeList = node.element("PageLayout").element("MapFrames").elementIterator("MapFrame");

            while(frameNodeList.hasNext()) {
                Element frameNode = (Element)frameNodeList.next();
                Iterator layerNodeList = frameNode.element("Map").element("Layers").elementIterator("Layer");

                while(layerNodeList.hasNext()) {
                    Element layerNode = (Element)layerNodeList.next();
                    if(layerNode.attribute("Source") != null) {
                        String oldFile = layerNode.attributeValue("Source");
                        if(oldFile.startsWith("\\") || oldFile.startsWith("/")) {
                            oldFile = oldFile.substring(1, oldFile.length() - 1);
                        }

                        String fileName = workSpace + oldFile;
                        layerNode.addAttribute("Source", fileName);
                    }
                }
            }
        } catch (Exception var9) {
            ;
        }

    }

    public final void SaveToFile(String filePath) throws IOException {
        Document doc = null;
        doc = DocumentHelper.createDocument();
        doc.addDocType("1.0", (String)null, (String)null);
        Element parentNode = doc.addElement("ActiveView");
        this.SaveXMLData(parentNode);
        if(this.isRelativePath) {
            File format = new File(filePath);
            this.SaveToRelativePath(parentNode, format.getParent());
        }

        doc.add(parentNode);
        OutputFormat format1 = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format1);
        writer.write(doc);
        writer.close();
    }

    private void SaveToRelativePath(Element node, String workSpace) {
        try {
            Iterator frameNodeList = node.element("PageLayout").element("MapFrames").elementIterator("MapFrame");

            while(frameNodeList.hasNext()) {
                Element frameNode = (Element)frameNodeList.next();
                Iterator layerNodeList = frameNode.element("Map").element("Layers").elementIterator("Layer");

                while(layerNodeList.hasNext()) {
                    Element layerNode = (Element)layerNodeList.next();
                    if(layerNode.attribute("Source") != null) {
                        String fileName = layerNode.attributeValue("Source").replace(workSpace, "");
                        layerNode.addAttribute("Source", fileName);
                    }
                }
            }
        } catch (Exception var8) {
            ;
        }

    }

    public final void LoadXMLData(Element node) {
        if(node != null) {
            if(node.element("IsRelativePath") != null) {
                this.isRelativePath = Boolean.parseBoolean(node.attributeValue("IsRelativePath"));
            }

            Element pageNode = node.element("PageLayout");
            if(pageNode == null) {
                Element mapNode = node.element("Map");
                if(mapNode != null) {
                    Envelope env = new Envelope(0.0D, 0.0D, 100.0D, 100.0D);
                    Map map = new Map(env);
                    this.FocusMap(map);
                }
            }
        }
    }

    @SuppressLint({"UseValueOf"})
    public final void SaveXMLData(Element node) {
        if(node != null) {
            XmlFunction.AppendAttribute(node, "IsRelativePath", (new Boolean(this.isRelativePath)).toString());
        }
    }

    public void dispose() {
        this.mMap = null;
        this.mFocusMapChanged = null;
        this.mContentChanged = null;
    }
}
