package com.skl.cdc.core.parser;

import com.skl.cdc.core.RedisSyncClient;
import com.skl.cdc.core.io.RedisInputStream;

import java.io.IOException;

public class RedisSocketRdbParser extends RedisRdbParser {

    public RedisSocketRdbParser(RedisSyncClient syncClient, RedisInputStream inputStream){
        this.syncClient = syncClient;
        this.inputStream = inputStream;
    }
    @Override
    public void parse()throws IOException{
        if(!isIncrementPsync) {
            //解析头
            parseHeader();
        }
        if(!isIncrementPsync) {
            //解析数据区
            parseBody();
        }
        if(!isIncrementPsync) {
            //解析为尾部
            parseEnd();
        }
        //解析命令
        parseCommand();
    }
}
