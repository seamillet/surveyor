package com.surveyor.drawlib.map.event;

import java.util.EventListener;

/**
 * Created by stg on 17/10/15.
 */
public interface LayerChangedListener extends EventListener {
    void doEvent(LayerChangedEvent var1);
}
