package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;

import java.util.EventObject;

import srs.Layer.ILayer;

/**
 * Created by stg on 17/10/15.
 */
public class ActiveLayerChangedEvent extends EventObject {
    private IMap _map;
    private ILayer _layer;

    public ActiveLayerChangedEvent(Object source, IMap map, ILayer layer) {
        super(source);
        this._map = map;
        this._layer = layer;
    }

    public IMap getMap() {
        return this._map;
    }

    public ILayer getLayer() {
        return this._layer;
    }
}
