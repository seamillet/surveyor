package com.surveyor.drawlib.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.surveyor.drawlib.map.event.ActiveLayerChangedManager;
import com.surveyor.drawlib.map.event.LayerAddedManager;
import com.surveyor.drawlib.map.event.LayerChangedEventArgs;
import com.surveyor.drawlib.map.event.LayerChangedManager;
import com.surveyor.drawlib.map.event.LayerClearedManager;
import com.surveyor.drawlib.map.event.LayerEventArgs;
import com.surveyor.drawlib.map.event.LayerRemovedManager;
import com.surveyor.drawlib.map.event.MapExtentChangedManager;
import com.surveyor.drawlib.view.MapView;

import org.dom4j.Element;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import srs.CoordinateSystem.ICoordinateSystem;
import srs.CoordinateSystem.ProjCSType;
import srs.Core.XmlFunction;
import srs.Display.FromMapPointDelegate;
import srs.Display.IScreenDisplay;
import srs.Display.ScreenDisplay;
import srs.Geometry.Envelope;
import srs.Geometry.IEnvelope;
import srs.Geometry.IGeometry;
import srs.Geometry.IPoint;
import srs.Geometry.ISpatialOperator;
import srs.Layer.ElementContainer;
import srs.Layer.Event.ElementListener;
import srs.Layer.Event.LayerActiveChangedEvent;
import srs.Layer.Event.LayerActiveChangedListener;
import srs.Layer.FeatureLayer;
import srs.Layer.GPSContainer;
import srs.Layer.IElementContainer;
import srs.Layer.IFeatureLayer;
import srs.Layer.IGPSContainer;
import srs.Layer.ILayer;
import srs.Layer.Layer;
import srs.Layer.TileLayer;
import srs.Layer.wmts.ImageDownLoader;
import srs.Operation.Event.SelectEventArgs;
import srs.Operation.Event.SelectionChangedEvent;
import srs.Operation.Event.SelectionChangedListener;
import srs.Operation.ISelectionSet;
import srs.Operation.SelectedFeatures;
import srs.Operation.SelectionSet;
import srs.Operation.Snap;
import srs.Operation.SnapSetting;
import srs.Utility.sRSException;
import srs.Utility.unitType;

/**
 * Created by stg on 17/10/14.
 */
public class Map implements IMap, LayerActiveChangedListener, SelectionChangedListener, ElementListener {
    private static final String TAG = Map.class.getSimpleName();

    private String mName;
    private IScreenDisplay mScreenDisplay;
    private IElementContainer mElementContainer;
    private IGPSContainer mGpsContainer;
    private ISelectionSet mSelectionSet;
    private ICoordinateSystem mCoordinateSystem;
    private ProjCSType mGeoProjectType;
    private ArrayList<ILayer> mLayers;
    private int mActiveLayerIndex;
    private IEnvelope mFullExtent;
    private IEnvelope newDeviceEnv;
    private IEnvelope newDisplayEnv;
    private ArrayList<IEnvelope> mAllExtents;
    private int mCurrentExtent;
    private boolean isPrivious;
    private boolean isNext;
    private String mMid;
    public static int INDEXDRAWLAYER = -1;
    private ActiveLayerChangedManager _ActiveLayerChanged;
    LayerChangedManager _LayerChanged;
    private LayerClearedManager _LayerCleared;
    private LayerAddedManager _LayerAdded;
    private LayerRemovedManager _LayerRemoved;
    private static MapExtentChangedManager _MapExtentChanged = new MapExtentChangedManager();

    public void dispose() throws Exception {
        this.mName = null;
        if(this.mScreenDisplay != null) {
            this.mScreenDisplay.dispose();
            this.mScreenDisplay = null;
        }

        this.mElementContainer.dispose();
        this.mElementContainer = null;
        this.mGpsContainer.getElementChanged().removeAllListener();
        this.mGpsContainer = null;
        this.mSelectionSet = null;
        this.mCoordinateSystem = null;
        this.mLayers = null;
        if(this.mFullExtent != null) {
            this.mFullExtent.dispose();
            this.mFullExtent = null;
        }

        if(this.newDeviceEnv != null) {
            this.newDeviceEnv.dispose();
            this.newDeviceEnv = null;
        }

        if(this.newDisplayEnv != null) {
            this.newDisplayEnv.dispose();
            this.newDisplayEnv = null;
        }

        this.mAllExtents = null;
        this.mMid = null;
    }

    public Map(IEnvelope deviceExtent) {
        this.mGeoProjectType = ProjCSType.ProjCS_WGS1984_Albers_BJ;
        this._ActiveLayerChanged = new ActiveLayerChangedManager();
        this._LayerChanged = new LayerChangedManager();
        this._LayerCleared = new LayerClearedManager();
        this._LayerAdded = new LayerAddedManager();
        this._LayerRemoved = new LayerRemovedManager();
        this.mName = "";
        this.mScreenDisplay = new ScreenDisplay(deviceExtent);
        this.mFullExtent = new Envelope();
        this.mCoordinateSystem = null;
        this.mLayers = new ArrayList();
        this.mActiveLayerIndex = -1;
        this.mElementContainer = new ElementContainer(this.mScreenDisplay);
        this.mGpsContainer = GPSContainer.getInstance();
        this.mGpsContainer.setScreenDisplay(this.mScreenDisplay);
        this.mSelectionSet = new SelectionSet(this.mScreenDisplay);
        this.mAllExtents = new ArrayList();
        this.mAllExtents.add(this.mFullExtent);
        this.mCurrentExtent = 0;
        this.isPrivious = false;
        this.mElementContainer.getElementChanged().addListener(this);
        this.mGpsContainer.getElementChanged().addListener(this);
        Calendar cl = Calendar.getInstance();
        this.mMid = cl.getTime().toString() + cl.getTimeInMillis();
        Snap.Instance().SnapSet().clear();
        SnapSetting set = new SnapSetting();
        set.target = this.mElementContainer;
        set.buffer = 10.0D;
        Snap.Instance().SnapSet().add(set.clone());
        set = new SnapSetting();
        set.buffer = 10.0D;
        Snap.Instance().SnapSet().add(set.clone());
    }

    public final String getMid() {
        return this.mMid;
    }

    public ActiveLayerChangedManager getActiveLayerChanged() {
        return this._ActiveLayerChanged != null?this._ActiveLayerChanged:null;
    }

    public LayerChangedManager getLayerChanged() {
        return this._LayerChanged != null?this._LayerChanged:null;
    }

    public LayerClearedManager getLayerCleared() {
        return this._LayerCleared != null?this._LayerCleared:null;
    }

    public LayerAddedManager getLayerAdded() {
        return this._LayerAdded != null?this._LayerAdded:null;
    }

    public LayerRemovedManager getLayerRemoved() {
        return this._LayerRemoved != null?this._LayerRemoved:null;
    }

    public MapExtentChangedManager getMapExtentChanged() {
        return _MapExtentChanged != null?_MapExtentChanged:null;
    }

    public final String getName() {
        return this.mName;
    }

    public final void setName(String value) {
        this.mName = value;
    }

    public final int getBackColor() {
        return this.mScreenDisplay.getBackColor();
    }

    public final void setBackColor(int value) {
        this.mScreenDisplay.setBackColor(value);
    }

    public final IEnvelope getExtent() {
        return this.mScreenDisplay.getDisplayExtent();
    }

    public final void setExtent(IEnvelope value) {
        this.mScreenDisplay.setDisplayExtent(value);
        if(!this.isPrivious && !this.isNext) {
            this.mAllExtents.add(value);
            this.mCurrentExtent = this.mAllExtents.size() - 1;
            this.UpdateEditorList();
        } else {
            if(this.isPrivious) {
                this.isPrivious = false;
            }

            if(this.isNext) {
                this.isNext = false;
            }
        }

        this.OnMapExtentChanged(new Object());
    }

    public final IEnvelope getPreExtent() {
        if(this.mCurrentExtent > 0) {
            --this.mCurrentExtent;
            this.isPrivious = true;
            return (IEnvelope)this.mAllExtents.get(this.mCurrentExtent);
        } else {
            return null;
        }
    }

    public final IEnvelope getNextExtent() {
        if(this.mCurrentExtent < this.mAllExtents.size() - 1) {
            ++this.mCurrentExtent;
            this.isNext = true;
            return (IEnvelope)this.mAllExtents.get(this.mCurrentExtent);
        } else {
            return null;
        }
    }

    public final IEnvelope getFullExtent() {
        IGeometry tempVar = this.mFullExtent.Clone();
        return (IEnvelope)(tempVar instanceof IEnvelope?tempVar:null);
    }

    public final void setFullExtent(IEnvelope value) {
        this.mFullExtent = value;
    }

    public final IEnvelope getDeviceExtent() {
        return this.mScreenDisplay.getDeviceExtent();
    }

    public final void setDeviceExtent(IEnvelope value) {
        this.mScreenDisplay.setDeviceExtent(value);
        this.mGpsContainer.setScreenDisplay(this.mScreenDisplay);
    }

    public final double getScale() {
        return this.mScreenDisplay.getScale();
    }

    public final void setScale(double value) {
        this.mScreenDisplay.setScale(value);

        for(int i = this.mCurrentExtent; i < this.mAllExtents.size(); ++i) {
            this.mAllExtents.remove(this.mAllExtents.size() - 1);
        }

        this.mAllExtents.add(this.mScreenDisplay.getDisplayExtent());
        this.mCurrentExtent = this.mAllExtents.size() - 1;
        this.OnMapExtentChanged(new Object());
    }

    public final unitType MapUnits() {
        return this.mCoordinateSystem == null?unitType.Unknown:this.mCoordinateSystem.Unit();
    }

    public final ICoordinateSystem getCoordinateSystem() {
        return this.mCoordinateSystem;
    }

    public final void setCoordinateSystem(ICoordinateSystem value) {
        this.mCoordinateSystem = value;
    }

    public final int getLayerCount() {
        return this.mLayers.size();
    }

    public final ArrayList<ILayer> getLayers() {
        return this.mLayers;
    }

    public final ILayer getActiveLayer() {
        return this.mActiveLayerIndex >= 0 && this.mActiveLayerIndex < this.getLayerCount()?(ILayer)this.getLayers().get(this.mActiveLayerIndex):null;
    }

    public final void setActiveLayer(ILayer value) throws sRSException {
        if(this.mLayers.contains(value)) {
            this.mActiveLayerIndex = this.mLayers.indexOf(value);
            if(this._ActiveLayerChanged != null) {
                this._ActiveLayerChanged.fireListener(this, this.getActiveLayer());
            }

        } else {
            this.mActiveLayerIndex = -1;
            if(this._ActiveLayerChanged != null) {
                this._ActiveLayerChanged.fireListener(this, this.getActiveLayer());
            }

            throw new sRSException("00300001");
        }
    }

    public final IScreenDisplay getScreenDisplay() {
        return this.mScreenDisplay;
    }

    public final IElementContainer getElementContainer() {
        return this.mElementContainer;
    }

    public final ISelectionSet getSelectionSet() {
        return this.mSelectionSet;
    }

    public final double FromMapDistance(double mapDistance) {
        return this.mScreenDisplay.FromMapDistance(mapDistance);
    }

    public final double ToMapDistance(double deviceDistance) {
        return this.mScreenDisplay.ToMapDistance(deviceDistance);
    }

    public final PointF FromMapPoint(IPoint point) {
        return this.mScreenDisplay.FromMapPoint(point);
    }

    public final IPoint ToMapPoint(PointF point) {
        return this.mScreenDisplay.ToMapPoint(point);
    }

    public final boolean AddLayer(ILayer layer) throws IOException {
        if(layer != null) {
            this.mLayers.add(layer);
            if(layer.getUseAble()) {
                this.mAllExtents.add(this.CaculateFullExtent());
                this.mCurrentExtent = this.mAllExtents.size() - 1;
                this.OnLayerAdded(new LayerEventArgs(layer));
                this.OnLayerChanged(new LayerChangedEventArgs((ILayer[])this.mLayers.toArray(new ILayer[0])));
                if(layer instanceof IFeatureLayer) {
                    ((IFeatureLayer)(layer instanceof IFeatureLayer?layer:null)).getFeatureClass().getSelectionSetChanged().addListener(this);
                }

                layer.getLayerActiveChanged().addListener(this);
            }

            return true;
        } else {
            return false;
        }
    }

    public final boolean AddLayers(ArrayList<ILayer> layers) throws IOException {
        if(layers != null) {
            ILayer layer = null;

            for(int i = 0; i < layers.size(); ++i) {
                layer = (ILayer)layers.get(i);
                this.mLayers.add(layer);
                if(layer.getUseAble()) {
                    this.mAllExtents.add(this.CaculateFullExtent());
                    this.mCurrentExtent = this.mAllExtents.size() - 1;
                    this.OnLayerAdded(new LayerEventArgs(layer));
                    this.OnLayerChanged(new LayerChangedEventArgs((ILayer[])this.mLayers.toArray(new ILayer[0])));
                    if(layer instanceof IFeatureLayer) {
                        ((IFeatureLayer)(layer instanceof IFeatureLayer?layer:null)).getFeatureClass().getSelectionSetChanged().addListener(this);
                    }

                    layer.getLayerActiveChanged().addListener(this);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void doEvent(LayerActiveChangedEvent event) {
        try {
            this.setActiveLayer(event.getLayer());
        } catch (sRSException var3) {
            var3.printStackTrace();
        }

        if(this._ActiveLayerChanged != null) {
            this._ActiveLayerChanged.fireListener(this, this.getActiveLayer());
        }

    }

    public final ILayer GetLayer(int index) throws sRSException {
        if(index >= 0 && index < this.mLayers.size()) {
            return (ILayer)this.mLayers.get(index);
        } else {
            throw new sRSException("00300001");
        }
    }

    public final ILayer GetLayer(String name) {
        for(int i = 0; i < this.mLayers.size(); ++i) {
            if(((ILayer)this.mLayers.get(i)).getName().equals(name)) {
                return (ILayer)this.mLayers.get(i);
            }
        }

        return null;
    }

    public final void MoveLayer(int fromIndex, int toIndex) throws sRSException {
        if(fromIndex >= 0 && fromIndex < this.mLayers.size()) {
            if(toIndex >= 0 && toIndex < this.mLayers.size()) {
                ILayer layer = (ILayer)this.mLayers.get(fromIndex);
                this.mLayers.remove(fromIndex);
                this.mLayers.add(toIndex, layer);
                layer = null;
            } else {
                throw new sRSException("00300001");
            }
        } else {
            throw new sRSException("00300001");
        }
    }

    public final void MoveLayer(ILayer layer, int toIndex) throws sRSException {
        if(toIndex >= 0 && toIndex < this.mLayers.size()) {
            if(this.mLayers.contains(layer)) {
                this.MoveLayer(this.mLayers.indexOf(layer), toIndex);
            } else {
                throw new sRSException("00300001");
            }
        } else {
            throw new sRSException("00300001");
        }
    }

    public final void RemoveLayer(int index) throws sRSException, IOException {
        if(this.mActiveLayerIndex == index) {
            this.mActiveLayerIndex = -1;
        }

        ILayer layer = (ILayer)this.mLayers.get(index);
        this.mLayers.remove(index);
        this.mAllExtents.add(this.CaculateFullExtent());
        if(layer instanceof IFeatureLayer) {
            SelectedFeatures[] var6;
            int var5 = (var6 = this.getSelectionSet().getSelectedFeatures()).length;

            for(int var4 = 0; var4 < var5; ++var4) {
                SelectedFeatures sf = var6[var4];
                if(sf.FeatureClass.getFid().equals(((IFeatureLayer)(layer instanceof IFeatureLayer?layer:null)).getFeatureClass().getFid())) {
                    this.getSelectionSet().RemoveSelectedFeatures(sf.clone());
                }
            }
        }

        this.OnLayerRemoved(new LayerEventArgs(layer));
        this.OnLayerChanged(new LayerChangedEventArgs((ILayer[])this.mLayers.toArray(new ILayer[0])));
        layer = null;
    }

    public final void RemoveLayer(ILayer layer) throws sRSException, IOException {
        if(this.mLayers.contains(layer)) {
            this.RemoveLayer(this.mLayers.indexOf(layer));
        } else {
            throw new sRSException("00300001");
        }
    }

    public final void ClearLayer() throws IOException {
        for(int i = 0; i < this.mLayers.size(); ++i) {
            this.mLayers.set(i, null);
        }

        this.mActiveLayerIndex = -1;
        this.mLayers.clear();
        this.mScreenDisplay.setDisplayExtent(this.CaculateFullExtent());
        this.getSelectionSet().ClearSelection();
        this.OnLayerCleared();
        this.OnLayerChanged(new LayerChangedEventArgs((ILayer[])this.mLayers.toArray(new ILayer[0])));
    }

    public final Bitmap ExportMap(boolean IsEdit) {
        return this.mScreenDisplay.getCanvas();
    }

    public final Bitmap ExportMapLayer() {
        return this.mScreenDisplay.getCache();
    }

    public final void PartialRefresh() throws IOException {
        try {
            Log.w("LEVEL-ROW-COLUMN", "Map.PartialRefresh 将图层缓存绘制于画布地图上，准备绘制临时要素");
            this.mScreenDisplay.ResetPartCaches();
            this.mElementContainer.Refresh();
            this.mSelectionSet.Refresh();
            this.mGpsContainer.Refresh();
        } catch (Exception var2) {
            Log.e("地图渲染错误", var2.getMessage());
        }
    }

    public void drawLayer(Handler handler) {
        if(INDEXDRAWLAYER < this.mLayers.size() && !ImageDownLoader.IsStop()) {
            ILayer message1 = (ILayer)this.mLayers.get(INDEXDRAWLAYER);
            if(message1.getVisible() && message1.getUseAble() && this.mScreenDisplay.getScale() > message1.getMinimumScale() && this.mScreenDisplay.getScale() < message1.getMaximumScale()) {
                try {
                    ((Layer)(message1 instanceof Layer?message1:null)).DrawLayer(this.mScreenDisplay, handler);
                } catch (Exception var8) {
                    Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer:图层 815  " + var8.getMessage() + message1.getName() + " ");
                    var8.printStackTrace();
                }
            } else {
                Log.e("LEVEL-ROW-COLUMN", message1.getName() + "不显示");
                Message e = new Message();
                e.arg1 = 6;
                handler.sendMessage(e);

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException var7) {
                    Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer:" + message1.getName() + " at InterruptedException " + var7.getMessage());
                    var7.printStackTrace();
                }
            }
        } else {
            Message message;
            if(INDEXDRAWLAYER < this.mLayers.size() || ImageDownLoader.IsStop()) {
                Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer 完成");
                message = new Message();
                message.arg1 = 1;
                handler.sendMessage(message);

                try {
                    Thread.sleep(2L);
                } catch (InterruptedException var9) {
                    Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer 完成 at InterruptedException " + var9.getMessage());
                    var9.printStackTrace();
                }

                return;
            }

            try {
                this.PartialRefresh();
            } catch (IOException var6) {
                Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer Element等要素绘制" + var6.getMessage());
                var6.printStackTrace();
            }

            message = new Message();
            message.arg1 = 1;
            handler.sendMessage(message);

            try {
                Thread.sleep(2L);
            } catch (InterruptedException var5) {
                Log.e("LEVEL-ROW-COLUMN", "Map.drawLayer at InterruptedException" + var5.getMessage());
                var5.printStackTrace();
            }
        }

    }

    public final void Refresh(Handler handler, Bitmap bitmap) throws Exception {
        this.mScreenDisplay.ResetCaches(bitmap);
        INDEXDRAWLAYER = 0;
        ImageDownLoader.StartThread();
        this.drawLayer(handler);
    }

    public final void Refresh(Bitmap bitmap) throws Exception {
        this.mScreenDisplay.ResetCaches(bitmap);
        int layersCount = this.mLayers.size();

        for(int i = 0; i < layersCount; ++i) {
            if(((ILayer)this.mLayers.get(i)).getVisible()) {
                ((Layer)(this.mLayers.get(i) instanceof Layer?(ILayer)this.mLayers.get(i):null)).DrawLayer(this.mScreenDisplay, (Handler)null);
            }
        }

        this.mScreenDisplay.ResetPartCaches();
        this.mElementContainer.Refresh();
        this.mSelectionSet.Refresh();
    }

    public final void Refresh(IEnvelope deviceExtent, Bitmap bitmap) throws Exception {
        IPoint tl = this.mScreenDisplay.ToMapPoint(new PointF((float)deviceExtent.XMin(), (float)deviceExtent.YMax()));
        IPoint br = this.mScreenDisplay.ToMapPoint(new PointF((float)deviceExtent.XMax(), (float)deviceExtent.YMin()));
        Envelope env = new Envelope(tl.X(), tl.Y(), br.X(), br.Y());
        this.newDeviceEnv = deviceExtent;
        this.newDisplayEnv = env;
        FromMapPointDelegate Delegate = new FromMapPointDelegate((ScreenDisplay)this.mScreenDisplay);
        this.mScreenDisplay.ResetCaches(deviceExtent, bitmap);

        for(int i = 0; i < this.mLayers.size(); ++i) {
            if(((ILayer)this.mLayers.get(i)).getVisible()) {
                ((Layer)(this.mLayers.get(i) instanceof Layer?(ILayer)this.mLayers.get(i):null)).DrawLayer(this.mScreenDisplay, this.mScreenDisplay.getCache(), env, Delegate, (Handler)null);
            }
        }

        this.mElementContainer.Refresh(Delegate);
        this.mSelectionSet.Refresh(Delegate);
        this.mScreenDisplay.getCanvas();
    }

    private PointF FromNewMapPoint(IPoint point) throws sRSException {
        if(this.newDeviceEnv != null && this.newDisplayEnv != null) {
            if(point == null) {
                throw new sRSException("00300001");
            } else {
                double _Rate1 = 1.0D;
                double _Left = 0.0D;
                double _Top = 0.0D;
                double wRate = (this.newDisplayEnv.XMax() - this.newDisplayEnv.XMin()) / (this.newDeviceEnv.XMax() - this.newDeviceEnv.XMin());
                double hRate = (this.newDisplayEnv.YMax() - this.newDisplayEnv.YMin()) / (this.newDeviceEnv.YMax() - this.newDeviceEnv.YMin());
                if(Math.abs(wRate) >= 1.401298464324817E-45D && Math.abs(hRate) >= 1.401298464324817E-45D) {
                    double CX1 = (this.newDisplayEnv.XMax() + this.newDisplayEnv.XMin()) / 2.0D;
                    double CY = (this.newDisplayEnv.YMax() + this.newDisplayEnv.YMin()) / 2.0D;
                    if(wRate >= hRate) {
                        _Left = this.newDisplayEnv.XMin();
                        _Top = CY + wRate * (this.newDeviceEnv.YMax() - this.newDeviceEnv.YMin()) / 2.0D;
                        _Rate1 = wRate;
                    } else {
                        _Left = CX1 - hRate * (this.newDeviceEnv.XMax() - this.newDeviceEnv.XMin()) / 2.0D;
                        _Top = this.newDisplayEnv.YMax();
                        _Rate1 = hRate;
                    }

                    float screenX = (float)((point.X() - _Left) / _Rate1);
                    float screenY = (float)((_Top - point.Y()) / _Rate1);
                    return new PointF(screenX, screenY);
                } else {
                    Object CX = null;
                    return (PointF)CX;
                }
            }
        } else {
            Object _Rate = null;
            return (PointF)_Rate;
        }
    }

    private IEnvelope CaculateFullExtent() throws IOException {
        if(this.mLayers.isEmpty()) {
            if(this.mElementContainer.getElementCount() > 0) {
                this.mFullExtent = this.mElementContainer.getExtent();
            } else {
                this.mFullExtent = new Envelope(this.getDeviceExtent().XMin(), this.getDeviceExtent().YMin(), this.getDeviceExtent().YMax(), this.getDeviceExtent().YMax());
            }
        } else {
            boolean first = true;

            for(int i = 0; i < this.mLayers.size(); ++i) {
                if(first) {
                    this.mFullExtent = ((ILayer)this.mLayers.get(i)).getExtent();
                    first = false;
                    if(this.mAllExtents.size() == 1) {
                        this.mScreenDisplay.setDisplayExtent(this.mFullExtent);
                    } else {
                        this.mScreenDisplay.setDisplayExtent((IEnvelope)this.mAllExtents.get(this.mCurrentExtent));
                    }

                    this.mCoordinateSystem = ((ILayer)this.mLayers.get(i)).getCoordinateSystem();
                } else {
                    IEnvelope layerExtent = this.mFullExtent.ConvertToPolygon().Extent();
                    IGeometry tempVar = ((ISpatialOperator)(layerExtent instanceof ISpatialOperator?layerExtent:null)).Union(((ILayer)this.mLayers.get(i)).getExtent());
                    this.mFullExtent = (IEnvelope)(tempVar instanceof IEnvelope?tempVar:null);
                }
            }
        }

        return this.mFullExtent;
    }

    public void doEvent(SelectionChangedEvent event, SelectEventArgs e) {
        this.getSelectionSet().AddFeatures(e.SelectedFeatures());
    }

    public void doEvent() throws IOException {
        this.mFullExtent = this.CaculateFullExtent();
    }

    private void OnLayerChanged(LayerChangedEventArgs e) {
        if(this._LayerChanged != null) {
            this._LayerChanged.fireListener(this, e);
        }

    }

    private void OnLayerCleared() {
        if(this._LayerCleared != null) {
            this._LayerCleared.fireListener(this);
        }

    }

    private void OnLayerAdded(LayerEventArgs e) {
        if(this._LayerAdded != null) {
            this._LayerAdded.fireListener(this, e);
        }

    }

    private void OnLayerRemoved(LayerEventArgs e) {
        if(this._LayerRemoved != null) {
            this._LayerRemoved.fireListener(this, e);
        }

    }

    private void OnMapExtentChanged(Object e) {
        if(_MapExtentChanged != null) {
            _MapExtentChanged.fireListener(this, e);
        }

    }

    private void UpdateEditorList() {
        if(this.mAllExtents.size() > 500) {
            this.mAllExtents.remove(0);
            --this.mCurrentExtent;
            if(this.mCurrentExtent < 0) {
                this.mCurrentExtent = 0;
            }
        }

    }

    public final void LoadXMLData(Element node) throws SecurityException, IllegalArgumentException, ClassNotFoundException, sRSException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        if(node != null) {
            this.mName = node.attributeValue("Name");
            this.mScreenDisplay.setBackColor(Integer.parseInt(node.attributeValue("BackColor")));
            Element childNode = node.element("Layers");
            Element elementNode;
            if(childNode != null) {
                Iterator envNode = childNode.elementIterator();

                while(envNode.hasNext()) {
                    elementNode = (Element)envNode.next();
                    if(elementNode.getName().equals("Layer")) {
                        ILayer layer = XmlFunction.LoadLayerXML(elementNode);
                        if(layer != null) {
                            this.AddLayer(layer);
                        }

                        if(layer instanceof IFeatureLayer) {
                            ((FeatureLayer)(layer instanceof FeatureLayer?layer:null)).getFeatureClass().setSelectionSet(((FeatureLayer)(layer instanceof FeatureLayer?layer:null)).getFeatureClass().getSelectionSet());
                        }
                    }
                }
            }

            Element envNode1 = node.element("Extent");
            if(envNode1 != null) {
                IGeometry elementNode1 = srs.Geometry.XmlFunction.LoadGeometryXML(envNode1);
                if(elementNode1 instanceof IEnvelope) {
                    this.mScreenDisplay.setDisplayExtent((IEnvelope)elementNode1);
                }
            }

            elementNode = node.element("ElementContainer");
            if(elementNode != null) {
                ((ElementContainer)(this.mElementContainer instanceof ElementContainer?this.mElementContainer:null)).LoadXMLData(elementNode);
            }

        }
    }

    @SuppressLint({"UseValueOf"})
    public final void SaveXMLData(Element node) {
        if(node != null) {
            XmlFunction.AppendAttribute(node, "Name", this.mName);
            XmlFunction.AppendAttribute(node, "BackColor", String.valueOf(this.mScreenDisplay.getBackColor()));
            Element envNode = node.getDocument().addElement("Extent");
            srs.Geometry.XmlFunction.SaveGeometryXML(envNode, this.mScreenDisplay.getDisplayExtent());
            node.add(envNode);
            Element layersNode = node.getDocument().addElement("Layers");
            XmlFunction.AppendAttribute(layersNode, "ActiveLayerIndex", (new Integer(this.mActiveLayerIndex)).toString());
            Iterator var5 = this.mLayers.iterator();

            while(var5.hasNext()) {
                ILayer elementsNode = (ILayer)var5.next();
                Element layerNode = node.getDocument().addElement("Layer");
                XmlFunction.SaveLayerXML(layerNode, elementsNode);
                layersNode.add(layerNode);
            }

            node.add(layersNode);
            Element elementsNode1 = node.getDocument().addElement("ElementContainer");
            ((ElementContainer)(this.mElementContainer instanceof ElementContainer?this.mElementContainer:null)).SaveXMLData(elementsNode1);
            node.add(elementsNode1);
        }
    }

    public IGPSContainer getGPSContainer() {
        return this.mGpsContainer;
    }

    public boolean getHasWMTSBUTTOM() {
        boolean ISWMTSBUTTOM = false;
        if(this.mLayers.size() > 0 && this.mLayers.get(0) instanceof TileLayer) {
            ISWMTSBUTTOM = true;
        }

        return ISWMTSBUTTOM;
    }

    public ProjCSType getGeoProjectType() {
        return this.mGeoProjectType;
    }

    public void setGeoProjectType(ProjCSType value) {
        this.mGeoProjectType = value;
    }
}
