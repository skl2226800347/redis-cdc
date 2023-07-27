package com.skl.cdc.core.event.deserialize.rdb;
import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.io.RedisInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class HashDeserialize extends Deserialize {
    private Map<String,String> entryMap;
    public HashDeserialize(byte dataType,int database,byte timestampType,long timestamp, String key) throws IOException{
        this.dataType = dataType;
        this.database = database;
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
        entryMap = new HashMap<>();
    }

    @Override
    public void deserializeValue(RedisInputStream is) throws IOException {
        int hashSize = is.readInt();
        for(int i=0;i<hashSize;i++) {
            String key = is.readString();
            String value = is.readString();
            entryMap.put(key,value);
        }
        System.out.println(JSONObject.toJSONString(entryMap));
    }
}
