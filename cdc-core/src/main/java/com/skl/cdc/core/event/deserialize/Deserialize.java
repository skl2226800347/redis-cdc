package com.skl.cdc.core.event.deserialize;

import com.skl.cdc.core.io.RedisInputStream;

import java.io.IOException;

public abstract class Deserialize {
    protected byte dataType;
    protected Integer database;
    protected String key;
    protected byte timestampType;
    protected long timestamp;
    abstract public void deserializeValue(RedisInputStream is)throws IOException;

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte getTimestampType() {
        return timestampType;
    }

    public void setTimestampType(byte timestampType) {
        this.timestampType = timestampType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
