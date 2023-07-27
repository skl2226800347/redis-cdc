package com.skl.cdc.common.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {
    private static final int ZERO = 0;

    public static final boolean isEmpty(Collection<?> collection){
        if(collection == null || collection.size() <=ZERO){
            return true;
        }
        return false;
    }
    public static final boolean isEmpty(Map map){
        if(map == null || map.size() <=ZERO){
            return true;
        }
        return false;
    }
    public static final boolean isNotEmpty(Collection<?> collection){
        if(collection != null && collection.size() >ZERO){
            return true;
        }
        return false;
    }
    public static final boolean isNotEmpty(Map map){
        if(map != null && map.size() >ZERO){
            return true;
        }
        return false;
    }
}
