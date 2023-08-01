package com.skl.cdc.remoting.zookeeper.event;

import java.io.Serializable;

public class DataEvent implements Serializable {
    private String path;
    private byte[] data;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
