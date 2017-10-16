package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by stg on 17/10/15.
 */
public class LayerRemovedManager {
    private Collection<LayerRemovedListener> listeners;

    public LayerRemovedManager() {
    }

    public void addListener(LayerRemovedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(LayerRemovedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map, LayerEventArgs e) {
        if(this.listeners != null) {
            LayerRemovedEvent event = new LayerRemovedEvent(map, e);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(LayerRemovedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            LayerRemovedListener listener = (LayerRemovedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
