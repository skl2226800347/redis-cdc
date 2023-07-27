package com.skl.test.cdc.core.redis;
import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.common.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.*;

public class JedisPoolTest {
    private JedisPool redisPool;
    final String ip="127.0.0.1";
    final int port=6379;
    final int timeout = 10000;
    final String username=null;
    final String password="123";
    final int database = 96;

    @Before
    public void initialize(){
        JedisPoolConfig config = new JedisPoolConfig();
        redisPool  = new JedisPool(config,ip,port,timeout,username,password,database);
    }
    @Test
    public void set(){
        Jedis jedis =redisPool.getResource();
        jedis.set("skl_1".getBytes(),"123".getBytes());
        jedis.set("skl_2".getBytes(),"America".getBytes());
        jedis.set("skl_6".getBytes(),"abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg".getBytes());
    }

    @Test
    public void get(){
        Jedis jedis = redisPool.getResource();
        String value =jedis.get("fastjson:docts:1402");
        System.out.println("value="+value);
        byte[] bytes = value.getBytes();
        //目前实际长度value.length():6800596
        //压缩长度:1192730
        //解压后长度:7617436
        System.out.println("长度="+value.length());
    }

    @Test
    public void setAndGet(){
        Jedis jedis =redisPool.getResource();
        jedis.set("skl_1".getBytes(),"118".getBytes());
        System.out.println(new String(jedis.get("skl_1".getBytes())));
        jedis.set("skl_2".getBytes(),"400".getBytes());
        System.out.println(new String(jedis.get("skl_2".getBytes())));
        jedis.set("skl_3".getBytes(),"400000000".getBytes());
        System.out.println(new String(jedis.get("skl_3".getBytes())));
        jedis.set("skl_4".getBytes(),"America".getBytes());
        System.out.println(new String(jedis.get("skl_4".getBytes())));
        jedis.set("skl_5".getBytes(),"中国人民万岁".getBytes());
        System.out.println(new String(jedis.get("skl_5".getBytes())));
        StringBuffer value = new StringBuffer();
        for(int i=0;i<20007;i++){
            value.append("ac");
        }
        //jedis.set("skl_6".getBytes(),"abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg,abcdefg".getBytes());
        jedis.set("skl_6".getBytes(),value.toString().getBytes());
        String skl_6_value =new String(jedis.get("skl_6".getBytes()));
        System.out.println("skl_6_value:"+skl_6_value.length()+"    "+skl_6_value);
        setAndGet_setExpireSecond();
        setAndGet_setExpireMillis();
    }
    @Test
    public void setAndGet_V2() {
        Jedis jedis = redisPool.getResource();
        //1-String
        //jedis.setnx("skl_31","中国");
        String stringValue = jedis.get("skl_31");
        System.out.println("stringValue="+stringValue);
        //2-map
        Map<String,String> map =new HashMap();
        map.put("name","张三");
        //jedis.hset("skl_32",map);
        String hValue =jedis.hget("skl_32","name");
        System.out.println("hValue:"+hValue);
        //list
        //jedis.lpush("skl_33","aa","bb","cc");
        List<String> lpush_value = jedis.lrange("skl_33",0,3);
        System.out.println("lpush_value="+JSONObject.toJSONString(lpush_value));
        //set
        //jedis.sadd("skl_34","第一行");
        Set<String> set =jedis.smembers("skl_34");
        System.out.println("set="+JSONObject.toJSONString(set));
        //SortSet
        Map<String, Double> scoreMembers = new HashMap<>();
        scoreMembers.put("money1",3d);
        scoreMembers.put("money2",10d);
        //jedis.zadd("skl_36",scoreMembers);
        Set<String> sortSet =jedis.zrange("skl_36",0,3);
        System.out.println("sortSet="+JSONObject.toJSONString(sortSet));


        jedis.lpush("skl_40","dd","ee","ff");
        List<String> lpush_valueV2 = jedis.lrange("skl_40",0,3);
        System.out.println("lpush_valueV2="+JSONObject.toJSONString(lpush_valueV2));


    }

    @Test
    public void setAndGet_setExpireSecond(){
        Jedis jedis =redisPool.getResource();
        jedis.set("skl_12".getBytes(),DateUtil.dateStr(new Date(),DateUtil.DEFAULT_FORMAT).getBytes(), SetParams.setParams().ex(7200));
        System.out.println(new String(jedis.get("skl_12".getBytes())));
    }

    @Test
    public void setAndGet_setExpireMillis(){
        Jedis jedis =redisPool.getResource();
        jedis.set("skl_22".getBytes(),DateUtil.dateStr(new Date(),DateUtil.DEFAULT_FORMAT).getBytes(), SetParams.setParams().px(10800000L));
        System.out.println(new String(jedis.get("skl_22".getBytes())));
    }

    @Test
    public void keys(){
        Jedis jedis =redisPool.getResource();
        Set<String> keys = jedis.keys("*");
        System.out.println(keys.size());
    }
    @Test
    public void deleteKey(){
        Jedis jedis =redisPool.getResource();
        Set<String> keys = jedis.keys("*");
        for(String key: keys) {
            jedis.del(key);
        }
    }
}
