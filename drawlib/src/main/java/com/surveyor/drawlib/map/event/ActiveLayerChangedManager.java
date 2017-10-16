package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import srs.Layer.ILayer;

/**
 * Created by stg on 17/10/15.
 */
public class ActiveLayerChangedManager {
    private Collection<ActiveLayerChangedListener> listeners;

    public ActiveLayerChangedManager() {
    }

    public void addListener(ActiveLayerChangedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(ActiveLayerChangedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map, ILayer sender) {
        if(this.listeners != null) {
            ActiveLayerChangedEvent event = new ActiveLayerChangedEvent(this, map, sender);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(ActiveLayerChangedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            ActiveLayerChangedListener listener = (ActiveLayerChangedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
