package com.surveyor.drawlib.map.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */
public interface ContentChangedListener extends EventListener {
    void doEvent(EventObject var1);
}
