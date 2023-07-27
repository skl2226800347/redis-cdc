package com.skl.cdc.core.event.deserialize.write;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.io.RedisInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteDeserialize extends Deserialize {
    private String type;
    private String key;
    private long expire;
    private Object value;
    private List<String> extendList;

    public WriteDeserialize(byte dataType,int database,byte timestampType,long timestamp, String key){
        this.dataType = dataType;
        this.database = database;
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
    }
    public WriteDeserialize(){

    }

    @Override
    public void deserializeValue(RedisInputStream is) throws IOException {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<String> getExtendList() {
        return extendList;
    }

    public void setExtendList(List<String> extendList) {
        this.extendList = extendList;
    }
}
