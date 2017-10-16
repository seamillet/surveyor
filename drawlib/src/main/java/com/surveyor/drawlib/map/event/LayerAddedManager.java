package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by stg on 17/10/15.
 */
public class LayerAddedManager {
    private Collection<LayerAddedListener> listeners;

    public LayerAddedManager() {
    }

    public void addListener(LayerAddedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(LayerAddedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map, LayerEventArgs e) {
        if(this.listeners != null) {
            LayerAddedEvent event = new LayerAddedEvent(map, e);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(LayerAddedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            LayerAddedListener listener = (LayerAddedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
