package com.skl.cdc.remoting.zookeeper.listener;

import com.skl.cdc.remoting.zookeeper.event.DataEvent;

/**
 * @author skl
 */
public interface DataListener {
    /**
     * data changed listener
     * @param dataEvent data
     */
    void dataEvent(DataEvent dataEvent);
}
