package com.surveyor.drawlib.map;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;

import com.surveyor.drawlib.map.event.ActiveLayerChangedManager;
import com.surveyor.drawlib.map.event.LayerAddedManager;
import com.surveyor.drawlib.map.event.LayerChangedManager;
import com.surveyor.drawlib.map.event.LayerClearedManager;
import com.surveyor.drawlib.map.event.LayerRemovedManager;
import com.surveyor.drawlib.map.event.MapExtentChangedManager;

import java.io.IOException;
import java.util.ArrayList;

import srs.CoordinateSystem.ICoordinateSystem;
import srs.CoordinateSystem.ProjCSType;
import srs.Display.IScreenDisplay;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.Layer.Event.LayerActiveChangedEvent;
import srs.Layer.IElementContainer;
import srs.Layer.IGPSContainer;
import srs.Layer.ILayer;
import srs.Operation.ISelectionSet;
import srs.Utility.sRSException;
import srs.Utility.unitType;


/**
 * Created by stg on 17/10/14.
 */
public interface IMap {
    boolean getHasWMTSBUTTOM();

    String getName();

    void setName(String var1);

    String getMid();

    int getBackColor();

    void setBackColor(int var1);

    IEnvelope getFullExtent();

    void setFullExtent(IEnvelope var1);

    IEnvelope getDeviceExtent();

    void setDeviceExtent(IEnvelope var1);

    IEnvelope getExtent();

    void setExtent(IEnvelope var1);

    IEnvelope getPreExtent();

    IEnvelope getNextExtent();

    double getScale();

    void setScale(double var1);

    unitType MapUnits();

    ICoordinateSystem getCoordinateSystem();

    void setCoordinateSystem(ICoordinateSystem var1);

    ProjCSType getGeoProjectType();

    void setGeoProjectType(ProjCSType var1);

    int getLayerCount();

    ArrayList<ILayer> getLayers();

    ILayer getActiveLayer();

    void setActiveLayer(ILayer var1) throws sRSException;

    boolean AddLayer(ILayer var1) throws IOException;

    boolean AddLayers(ArrayList<ILayer> var1) throws IOException;

    ILayer GetLayer(int var1) throws sRSException;

    ILayer GetLayer(String var1);

    void MoveLayer(int var1, int var2) throws sRSException;

    void MoveLayer(ILayer var1, int var2) throws sRSException;

    void RemoveLayer(int var1) throws sRSException, IOException;

    void RemoveLayer(ILayer var1) throws sRSException, IOException;

    void ClearLayer() throws IOException;

    IScreenDisplay getScreenDisplay();

    IElementContainer getElementContainer();

    IGPSContainer getGPSContainer();

    ISelectionSet getSelectionSet();

    Bitmap ExportMap(boolean var1);

    Bitmap ExportMapLayer();

    void PartialRefresh() throws IOException;

    void Refresh(Bitmap var1) throws sRSException, Exception;

    void Refresh(Handler var1, Bitmap var2) throws sRSException, Exception;

    void drawLayer(Handler var1);

    void Refresh(IEnvelope var1, Bitmap var2) throws Exception;

    PointF FromMapPoint(IPoint var1);

    IPoint ToMapPoint(PointF var1);

    double FromMapDistance(double var1);

    double ToMapDistance(double var1);

    void doEvent(LayerActiveChangedEvent var1);

    void dispose() throws Exception;

    ActiveLayerChangedManager getActiveLayerChanged();

    LayerChangedManager getLayerChanged();

    LayerAddedManager getLayerAdded();

    LayerRemovedManager getLayerRemoved();

    LayerClearedManager getLayerCleared();

    MapExtentChangedManager getMapExtentChanged();
}

