package com.skl.cdc.core.event.deserialize.rdb;
import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.io.RedisInputStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetDeserialize extends Deserialize {
    private Set<String> set;
    public SetDeserialize(byte dataType,int database,byte timestampType,long timestamp, String key) throws IOException{
        this.dataType = dataType;
        this.database = database;
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
        set = new HashSet<>();
    }

    @Override
    public void deserializeValue(RedisInputStream is) throws IOException {
        int hashSize = is.readInt();
        for(int i=0;i<hashSize;i++) {
            String content = is.readString();
            set.add(content);
        }
        System.out.println(JSONObject.toJSONString(set));
    }
}
