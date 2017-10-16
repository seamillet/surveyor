package com.surveyor.drawlib.map.event;

import srs.Layer.ILayer;

/**
 * Created by stg on 17/10/15.
 */
public class LayerEventArgs {
    private ILayer _layer;

    public final ILayer Layer() {
        return this._layer;
    }

    public final void Layer(ILayer value) {
        this._layer = value;
    }

    public LayerEventArgs(ILayer layer) {
        this._layer = layer;
    }
}
