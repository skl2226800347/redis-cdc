package com.skl.test.cdc.core.util;

import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.common.util.DateUtil;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.support.PsyncContinueResponse;
import com.skl.cdc.core.util.ByteUtil;
import com.skl.cdc.core.util.RdbParseUtil;
import com.skl.cdc.store.PsyncResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class RdbParseUtilTest {
    FileInputStream fis;
    @Before
    public void init()throws FileNotFoundException {
        fis = new FileInputStream("d:/tmp/header.rdb");
    }
    @Test
    public void parsePsyncResponse()throws IOException{
        byte[] readBytes = new byte[1024];
        int readLength =fis.read(readBytes);
        PsyncResponse redisPsyncResponse = RdbParseUtil.parsePsyncResponse(readBytes);
        System.out.println(JSONObject.toJSONString(redisPsyncResponse));
    }
    @Test
    public void getPsyncResponse() {
        PsyncResponse header = RdbParseUtil.getPsyncResponse("+FULLRESYNC 25b8fd5fa2ffd75c3cf72d18b2de561bcad218f2 5335609");
        System.out.println(JSONObject.toJSONString(header));
        if (header != null && header.getRundId() != null) {
            System.out.println("runId:" + header.getRundId().length());
        }
    }

    @Test
    public void parsePsyncContinueResponse(){
        String str="+OK\n" +
                "+CONTINUE\n" +
                "\n" +
                "*2\n" +
                "$6\n" +
                "SELECT\n" +
                "$1\n" +
                "0\n" +
                "*2\n" +
                "$3\n" +
                "DEL\n" +
                "$45\n" +
                "01202010020230712085849367298940_isHealthyPay\n";
        PsyncContinueResponse psyncContinueResponse = RdbParseUtil.parsePsyncContinueResponse(str.getBytes());
        System.out.println(JSONObject.toJSONString(psyncContinueResponse));
    }

    @Test
    public void t() {
        byte l =(byte)0xFD;
        System.out.println(l);
        System.out.println((byte)0xFD);
        System.out.println((byte)0xFC);

    }

    @Test
    public void parseBody() throws Throwable {
        File file = new File("d:/tmp/redis.rdb");
        FileInputStream fis = new FileInputStream(file);

        //魔数
        String magic = new String(read(5, fis));
        System.out.println("魔数:" + magic);
        //RDB版本
        String version = new String(read(4, fis));
        System.out.println("version:" + version);

        fis.skip(1);
        //reids版本
        String redisVer = new String(read(10, fis));
        System.out.println(redisVer);

        //redis版本号
        String redisVer2 = new String(read(7, fis));
        System.out.println(redisVer2);

        fis.skip(1);

        String redisBits = new String(read(11,fis));
        System.out.println("redisBits:"+redisBits);

        fis.skip(1);
        String sixtyFour = new String(read(1,fis));
        System.out.println("sixtyFour:"+sixtyFour);

        fis.skip(2);
        String rdbCreateTime = new String(read(5,fis));
        System.out.println("rdbCreateTime:"+rdbCreateTime);

        fis.skip(1);

        String rdbCreateTimeV2 = new String(read(4,fis));
        System.out.println("rdbCreateTimeV2:"+rdbCreateTimeV2);
        Integer timestamp = ByteBuffer.wrap(rdbCreateTimeV2.getBytes()).getInt();
        System.out.println(DateUtil.dateStr(new Date(timestamp),DateUtil.DEFAULT_FORMAT));


        fis.skip(1);

        String dump = new String(read(9,fis));
        System.out.println("dumpStr    "+dump);

        fis.skip(1);

        String dumpSize = new String(read(4,fis));
        System.out.println("dumpSize="+dumpSize+"  "+getInt(dumpSize.getBytes()));


        boolean cycleFlag = true;
        while(cycleFlag) {
            byte[] bytes =read(1,fis);
            //db开头
            if(bytes[0]== NumberConstants.BYTE_FE){
                parseDb(fis);
                break;
            }else if(bytes[0]==NumberConstants.BYTE_FF){
                cycleFlag=false;
            }
        }

        fis.close();
    }

    private void parseDb(FileInputStream fis)throws Exception{
        byte[] bytes =read(1,fis);
        int database =bytes[0];
        System.out.println("database:"+database);
        fis.skip(1);
        bytes =read(1,fis);
        int highBit =ByteUtil.readHighBits(bytes[0]);
        System.out.println(highBit);
    }

    private Integer getInt(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }
    private byte[] read(int length, FileInputStream fis) throws IOException {
        byte[] bytes = new byte[length];
        fis.read(bytes);
        return bytes;
    }

    @Test
    public void parseBodyV2() throws Throwable {
        File file = new File("d:/tmp/redis.rdb");
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        fis.read(bytes);
        System.out.println("xx");
    }

    @After
    public void destroy()throws IOException {
        fis.close();
    }
}
