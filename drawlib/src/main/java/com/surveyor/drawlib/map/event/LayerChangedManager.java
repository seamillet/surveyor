package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by stg on 17/10/15.
 */
public class LayerChangedManager {
    private Collection<LayerChangedListener> listeners;

    public LayerChangedManager() {
    }

    public void addListener(LayerChangedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(LayerChangedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map, LayerChangedEventArgs sender) {
        if(this.listeners != null) {
            LayerChangedEvent event = new LayerChangedEvent(map, sender);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(LayerChangedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            LayerChangedListener listener = (LayerChangedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
