package com.surveyor.drawlib.map.event;

import java.util.ArrayList;
import java.util.Arrays;

import srs.Layer.ILayer;

/**
 * Created by stg on 17/10/15.
 */

public class LayerChangedEventArgs {
    private ArrayList<ILayer> _layers = new ArrayList();

    public final ILayer[] Layers() {
        return (ILayer[])this._layers.toArray(new ILayer[0]);
    }

    public final void Layers(ILayer[] value) {
        this._layers.clear();
        this._layers.addAll(Arrays.asList(value));
    }

    public LayerChangedEventArgs(ILayer[] layer) {
        this._layers.addAll(Arrays.asList(layer));
    }
}
