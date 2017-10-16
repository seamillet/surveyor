package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;

import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */
public class LayerAddedEvent extends EventObject {
    private IMap _Map;
    private LayerEventArgs _LayerEventArgs;

    public LayerAddedEvent(Object source, LayerEventArgs e) {
        super(source);
        this._Map = (IMap)source;
        this._LayerEventArgs = e;
    }

    public LayerEventArgs getLayerEventArgs() {
        return this._LayerEventArgs;
    }

    public IMap getMap() {
        return this._Map;
    }
}
