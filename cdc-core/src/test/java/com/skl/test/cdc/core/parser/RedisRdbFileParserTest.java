package com.skl.test.cdc.core.parser;

import com.skl.cdc.core.parser.RedisFileRdbParser;
import org.junit.Before;
import org.junit.Test;

public class RedisRdbFileParserTest {
    RedisFileRdbParser redisRdbFileParser;
    @Before
    public void before(){
        redisRdbFileParser = new RedisFileRdbParser("d:/tmp/redis.rdb");
    }


    @Test
    public void parse()throws Throwable{
        //2638482
        redisRdbFileParser.parse();
    }

    @Test
    public void after()throws Throwable{
        redisRdbFileParser.destroy();
    }
}
