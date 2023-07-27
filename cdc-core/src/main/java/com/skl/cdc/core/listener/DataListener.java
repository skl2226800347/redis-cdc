package com.skl.cdc.core.listener;

import com.skl.cdc.core.event.deserialize.Deserialize;

public interface DataListener {
    void onChanage(Deserialize deserialize);
}
