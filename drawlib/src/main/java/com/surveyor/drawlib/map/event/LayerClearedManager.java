package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by stg on 17/10/15.
 */
public class LayerClearedManager {
    private Collection<LayerClearedListener> listeners;

    public LayerClearedManager() {
    }

    public void addListener(LayerClearedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(LayerClearedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map) {
        if(this.listeners != null) {
            LayerClearedEvent event = new LayerClearedEvent(map);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(LayerClearedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            LayerClearedListener listener = (LayerClearedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
