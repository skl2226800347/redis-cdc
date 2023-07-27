package com.skl.cdc.core.parser;

import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.common.util.CollectionUtil;
import com.skl.cdc.common.util.DateUtil;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.enums.DataTypeEnum;
import com.skl.cdc.core.enums.HighBitType;
import com.skl.cdc.core.enums.ValueTypeEnum;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.event.deserialize.rdb.*;
import com.skl.cdc.core.io.RedisInputStream;
import com.skl.cdc.core.listener.DataListener;
import com.skl.cdc.core.support.RDBHeader;
import com.skl.cdc.core.util.ByteUtil;
import com.skl.cdc.core.util.CRC64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

public abstract class RedisRdbParser extends RedisWriteParser{

    /**
     * 默认全量解析
     */
    protected boolean isIncrementPsync = false;


    public RedisRdbParser(){

    }
    public RedisRdbParser(RedisInputStream inputStream){
        this.inputStream = inputStream;
    }

    abstract public  void parse() throws IOException;



    protected RDBHeader parseHeader()throws IOException {
        RDBHeader rdbHeader = new RDBHeader();
        //魔数
        String magic = inputStream.readString(5);
        rdbHeader.setMagic(magic);
        System.out.println("魔数:" + magic);
        //RDB版本
        String version = inputStream.readString(4);
        rdbHeader.setVersion(version);
        System.out.println("version:" + version);

        byte[] bytes = inputStream.read(1);
        if(NumberConstants.BYTE_FA != bytes[0]){
            throw new RuntimeException("解析错误");
        }
        //reids版本
        //String redisVer = inputStream.readString(10);
        String redisVersionKey = inputStream.readString();
        System.out.println("redisVersionKey="+redisVersionKey);
        //redis版本号
        String redisVersionValue = inputStream.readString();
        System.out.println("redisVersionValue="+redisVersionValue);

        inputStream.skip(1);
        if(NumberConstants.BYTE_FA != bytes[0]){
            throw new RuntimeException("解析错误");
        }

        String redisBits = inputStream.readString();
        System.out.println("redisBits:"+redisBits);

        String sixtyFour = inputStream.readString();
        System.out.println("sixtyFour:"+sixtyFour);
        inputStream.skip(1);
        String rdbCreateTime = inputStream.readString();
        System.out.println("rdbCreateTime:"+rdbCreateTime);
        String rdbCreateTimeV2 =inputStream.readString();
        System.out.println("rdbCreateTimeV2:"+rdbCreateTimeV2);
        System.out.println(DateUtil.dateStr(new Date(Long.parseLong(rdbCreateTimeV2)),DateUtil.DEFAULT_FORMAT));
        inputStream.skip(1);
        String dump = inputStream.readString();
        System.out.println("dumpStr    "+dump);

        String dumpSize=inputStream.readString();
        System.out.println("dumpSize="+dumpSize);
        return rdbHeader;
    }

    protected void parseBody()throws IOException{
        boolean cycleFlag = true;
        boolean hasNextDatabase=false;
        while(cycleFlag) {
            if(!hasNextDatabase) {
                byte[] bytes = inputStream.read(1);
                if(bytes[0]== NumberConstants.BYTE_FE){
                    byte parseResult =parseDb();
                    if(NumberConstants.BYTE_FE == parseResult){
                        hasNextDatabase=true;
                        continue;
                    }else if(NumberConstants.BYTE_FF==parseResult){
                        break;
                    }
                } else if (NumberConstants.BYTE_FF == bytes[0]) {
                    break;
                }
            }else{
                byte parseResult =parseDb();
                if(NumberConstants.BYTE_FE == parseResult){
                    hasNextDatabase=true;
                }else if(NumberConstants.BYTE_FF==parseResult){
                    break;
                }else{
                    hasNextDatabase=false;
                }
                continue;
            }
        }
    }

    private byte parseDb()throws IOException{
        byte[] bytes =inputStream.read(1);
        int database =inputStream.readInt(bytes[0]);
        if(database ==96){
            System.out.println("database == 12");
        }
        System.out.println("database:"+database);
        bytes =inputStream.read(2);
        if(bytes[0]!= NumberConstants.BYTE_FB){
            throw new RuntimeException("FB未解析成功");
        }
        if(bytes[0] == NumberConstants.BYTE_FE){
            throw new RuntimeException("FE未解析成功");
        }
        int keyCount =inputStream.readInt(bytes[1]);
        bytes =inputStream.read(1);
        int expireKeyCount =inputStream.readInt(bytes[0]);
        System.out.println("数据库db:"+database+"   key总量="+keyCount+"   expireKeyCount="+expireKeyCount);
        boolean isNextDb=false;
        while(!isNextDb){
            bytes =inputStream.read(1);
            //如果是新的database开头，跳出循环，重新执行
            if(bytes[0] == NumberConstants.BYTE_FE){
                isNextDb=true;
                break;
            }
            if(bytes[0] == NumberConstants.BYTE_FF){
                return NumberConstants.BYTE_FF;
            }
            //1-key 过期时间
            long timestamp = 0;
            byte timestampType = 0;
            if(NumberConstants.BYTE_FC == bytes[0]){
                timestampType = bytes[0];
                timestamp =inputStream.readExpireTimestampByMillisecond();
                String expireDate =DateUtil.dateStr(new Date(timestamp),DateUtil.DEFAULT_FORMAT);
                System.out.println("FC  过期时间:"+expireDate);
                if(expireDate.trim().equals("2023-07-03 12:02:26".trim())){
                    System.out.println("特殊关注");
                }
                bytes = inputStream.read(1);
            }else if(NumberConstants.BYTE_FD == bytes[0]){
                timestampType = bytes[0];
                timestamp =inputStream.readExpireTimestampBySecond();
                String expireDate =DateUtil.dateStr(new Date(timestamp),DateUtil.DEFAULT_FORMAT);
                System.out.println("FD  过期时间:"+expireDate);
                bytes = inputStream.read(1);
            }
            //2-value 存储类型
            byte valueType = bytes[0];
            //3-key
            String key = inputStream.readString();
            System.out.println("valueType:"+valueType+"  key="+key);
            if("skl_33".equals(key)){
                key=key;
            }
            Deserialize deserialize = deserializeValue(DataTypeEnum.RDB.getType(),database,timestampType,timestamp,key,valueType);
            System.out.println("database="+database+"  valueType="+valueType+"  key="+key+"  --->  deserialize="+JSONObject.toJSONString(deserialize));
            notifyChange(deserialize);
        }
        if(isNextDb){
            //parseString();
            return NumberConstants.BYTE_FE;
        }
        return NumberConstants.BYTE_DEFAULT_ERROR;
    }



    protected Deserialize deserializeValue(byte dataType,int database,byte timestampType,long timestamp, String key, byte valueType)throws IOException{
        //4-value
        ValueTypeEnum valueTypeEnum= ValueTypeEnum.getInstance(valueType);
        if(valueTypeEnum == null){
            throw new NullPointerException("valueType not match");
        }
        Deserialize value;
        switch (valueTypeEnum){
            case String_Encoding:
                value =parseString(dataType,database,timestampType,timestamp,key);
                break;
            case List_Encoding:
                value =parseList(dataType,database,timestampType,timestamp,key);
                break;
            case Set_Encoding:
                value =parseSet(dataType,database,timestampType,timestamp,key);
                break;
            case Sorted_Set_Encoding:
                value = parseSortedSet(dataType,database,timestampType,timestamp,key);
                break;
            case Hash_Encoding:
                value =parseHash(dataType,database,timestampType,timestamp,key);
                break;
            case Zipmap_Encoding:
                value =parseZipmap(dataType,database,timestampType,timestamp,key);
                break;
            case Ziplist_Encoding:
                value =parseZipList(dataType,database,timestampType,timestamp,key);
                break;
            case Intset_Encoding:
                value =parseIntset(dataType,database,timestampType,timestamp,key);
                break;
            case Sorted_Set_in_Ziplist_Encoding:
                value = parseSortedSetInZiplist(dataType,database,timestampType,timestamp,key);
                break;
            case Hashmap_in_Ziplist_Encoding:
                value = parseHashmapInZiplist(dataType,database,timestampType,timestamp,key);
                break;
            case Quicklist_Encoding:
                value = parseQuicklistEncoding(dataType,database,timestampType,timestamp,key);
                break;
            default:
                throw new UnsupportedOperationException("valueType:"+valueType+" 不支持");
        }
        return value;
    }

    protected Deserialize parseString(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        StringDeserialize deserialize = new StringDeserialize(dataType,database,timestampType,timestamp,key);
        deserialize.deserializeValue(inputStream);
        return deserialize;
    }

    protected Deserialize parseList(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
       /* int len = inputStream.readInt();
        int len2 = inputStream.readInt();
        String size = inputStream.readString(23);*/
        throw new UnsupportedOperationException("not support");
    }
    protected SetDeserialize parseSet(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        SetDeserialize setDeserialize = new SetDeserialize(dataType,database,timestampType,timestamp,key);
        setDeserialize.deserializeValue(inputStream);
        return setDeserialize;
    }
    protected Deserialize parseSortedSet(byte dataType,int database,byte timestampType,long timestamp, String key){
        throw new UnsupportedOperationException("not support");
    }
    protected HashDeserialize parseHash(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        HashDeserialize hashDeserialize = new HashDeserialize(dataType,database,timestampType,timestamp,key);
        hashDeserialize.deserializeValue(inputStream);
        return hashDeserialize;
    }
    protected Deserialize parseZipmap(byte dataType,int database,byte timestampType,long timestamp, String key){
        throw new UnsupportedOperationException("not support");
    }

    protected ZipListDeserialize parseHashmapInZiplist(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        ZipListDeserialize zipList= new ZipListDeserialize(dataType,database,timestampType,timestamp,key);
        byte byteFlag = (byte) inputStream.read();
        int highBit = ByteUtil.readHighBits(byteFlag);
        if(HighBitType.NUMBER_HIGHT_BITS_THREE.getType() == highBit){
            int high = ByteUtil.readHighBits(byteFlag);
            int low =ByteUtil.readLowBits(byteFlag);
            if(high == 3 && low ==3) {
                byte[] bytes = inputStream.deCompress();
                RedisInputStream is =new RedisInputStream(new ByteArrayInputStream(bytes));
                zipList.deserializeValue(is);
                System.out.println("zipList:" + JSONObject.toJSONString(zipList));
            }else{
                throw new UnsupportedOperationException("不支持");
            }
        }else{
            int zlbytiesLength= inputStream.readInt(byteFlag);
            System.out.println(zlbytiesLength);
            byte[] bytes = inputStream.read(zlbytiesLength);
            RedisInputStream is =new RedisInputStream(new ByteArrayInputStream(bytes));
            zipList.deserializeValue(is);
            System.out.println("zipList:"+JSONObject.toJSONString(zipList));
        }
        return zipList;
    }
    protected Deserialize  parseZipList(byte dataType,int database,byte timestampType,long timestamp, String key){
        throw new UnsupportedOperationException("not support");
    }
    protected Deserialize parseIntset(byte dataType,int database,byte timestampType,long timestamp, String key){
        throw new UnsupportedOperationException("not support");
    }

    protected ZipListDeserialize parseSortedSetInZiplist(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        ZipListDeserialize zipList = new ZipListDeserialize(dataType,database,timestampType,timestamp,key);
        byte byteFlag = (byte) inputStream.read();
        int highBit = ByteUtil.readHighBits(byteFlag);
        if(HighBitType.NUMBER_HIGHT_BITS_THREE.getType() == highBit){
            int high = ByteUtil.readHighBits(byteFlag);
            int low =ByteUtil.readLowBits(byteFlag);
            if(high == 3 && low ==3) {
                byte[] bytes = inputStream.deCompress();
                RedisInputStream is =new RedisInputStream(new ByteArrayInputStream(bytes));
                zipList.deserializeValue(is);
            }else{
                throw new UnsupportedOperationException("不支持");
            }
        }else{
            int zlbytiesLength= inputStream.readInt(byteFlag);
            System.out.println(zlbytiesLength);
            byte[] bytes = inputStream.read(zlbytiesLength);
            RedisInputStream is =new RedisInputStream(new ByteArrayInputStream(bytes));
            zipList.deserializeValue(is);
        }
        return zipList;
    }

    protected Deserialize parseQuicklistEncoding(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException{
        byte byteFlag = (byte)inputStream.read();
        int zlbytiesLength= inputStream.readInt(byteFlag);
        System.out.println(zlbytiesLength);
        int len = inputStream.readInt();
        byte[] bytes = inputStream.read(len);
        QuicklistDeserialize quicklist = new QuicklistDeserialize(dataType,database,timestampType,timestamp,key);
        RedisInputStream is =new RedisInputStream(new ByteArrayInputStream(bytes));
        quicklist.deserializeValue(is);
        System.out.println("quicklist:"+JSONObject.toJSONString(quicklist));
        return quicklist;
    }

    protected void parseEnd()throws IOException{
        byte[] bytes =inputStream.read(8);
        long crc = CRC64Util.digest(bytes);
        System.out.println("RDB CRC:"+crc);
        byte[] bytes2 = inputStream.read(2000);
        String str2 = new String(bytes2);
        writeCommand(bytes2);
        System.out.println(str2);
    }

    protected void writeCommand(byte[] bytes) {
        try {
            File file = new File("D:/tmp/writeCommand.txt");
            if(file.exists()){
               file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        }catch (Throwable e){
            throw new RuntimeException(e.getMessage());
        }

    }

    public void destroy() throws IOException{
        inputStream.close();
    }

    public boolean isIncrementPsync() {
        return isIncrementPsync;
    }

    public void setIncrementPsync(boolean incrementPsync) {
        isIncrementPsync = incrementPsync;
    }
}
