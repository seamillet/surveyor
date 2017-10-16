package com.surveyor.drawlib.map.event;

import java.util.EventListener;

/**
 * Created by stg on 17/10/15.
 */
public interface LayerAddedListener extends EventListener {
    void doEvent(LayerAddedEvent var1);
}
