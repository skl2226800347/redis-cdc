package com.skl.cdc.core.event.deserialize.rdb;

import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.io.RedisInputStream;

import java.io.IOException;
import java.io.Serializable;

public class StringDeserialize extends Deserialize implements Serializable {
    private String value;
    private long expireTime;
    public StringDeserialize(byte dataType,int database,byte timestampType,long timestamp, String key){
        this.dataType = dataType;
        this.database = database;
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
    }

    public StringDeserialize(){

    }
    public String getValue() {
        return value;
    }

    @Override
    public void deserializeValue(RedisInputStream is) throws IOException {
        this.value =is.readString();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
