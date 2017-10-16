package com.surveyor.drawlib.map.event;

import com.surveyor.drawlib.map.IMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by stg on 17/10/15.
 */
public class MapExtentChangedManager {
    private Collection<MapExtentChangedListener> listeners;

    public MapExtentChangedManager() {
    }

    public void addListener(MapExtentChangedListener listener) {
        if(this.listeners == null) {
            this.listeners = new HashSet();
        }

        this.listeners.add(listener);
    }

    public void removeListener(MapExtentChangedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireListener(IMap map, Object e) {
        if(this.listeners != null) {
            MapExtentChangedEvent event = new MapExtentChangedEvent(map, e);
            this.notifyListeners(event);
        }
    }

    private void notifyListeners(MapExtentChangedEvent event) {
        Iterator iter = this.listeners.iterator();

        while(iter.hasNext()) {
            MapExtentChangedListener listener = (MapExtentChangedListener)iter.next();
            listener.doEvent(event);
        }

    }
}
