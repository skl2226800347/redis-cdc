package com.skl.cdc.remoting.zookeeper.param;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class PublishParam<K,V> implements Serializable {
    private static final long serialVersionUID = -7872617864111095445L;

    K key;
    V value;

    public byte[] toBytes(){
        return JSONObject.toJSONString(this).getBytes();
    }


    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
