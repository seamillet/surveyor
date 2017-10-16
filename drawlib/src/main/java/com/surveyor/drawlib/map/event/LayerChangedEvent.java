package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */
public class LayerChangedEvent extends EventObject {
    private IMap _Map;
    private LayerChangedEventArgs _LayerChangedEventArgs;

    public LayerChangedEvent(Object source, LayerChangedEventArgs e) {
        super(source);
        this._Map = (IMap)source;
        this._LayerChangedEventArgs = e;
    }

    public LayerChangedEventArgs getLayerChangedEventArgs() {
        return this._LayerChangedEventArgs;
    }

    public IMap getMap() {
        return this._Map;
    }
}
