package com.skl.cdc.core.parser;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.skl.cdc.common.util.CollectionUtil;
import com.skl.cdc.core.RedisSyncClient;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.enums.DataTypeEnum;
import com.skl.cdc.core.enums.RedisCommandEnum;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.event.deserialize.write.WriteDeserialize;
import com.skl.cdc.core.io.RedisInputStream;
import com.skl.cdc.core.listener.DataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class RedisWriteParser {
    protected static final Logger log = LoggerFactory.getLogger(RedisWriteParser.class);
    private static final WriteDeserialize PASS_DESERIALIZE=new WriteDeserialize();
    private static final WriteDeserialize END_DESERIALIZE=new WriteDeserialize();
    protected RedisInputStream inputStream;
    protected RedisSyncClient syncClient;

    protected RedisWriteParser(){

    }

    public void parseCommand()throws IOException {
        boolean cycleFlag = true;
        while(cycleFlag){
            //1-读取*相关内容
            int asteriskSymbol = readLineNumber();
            if(((byte)asteriskSymbol) == NumberConstants.BYTE_FF){
                //cycleFlag =false
                waitFor(1000);
                break;
            }
            System.out.println("*后面数量="+asteriskSymbol);
            //2-读取$后面数字所代表的字符串
            String command = readCrossLineString();
            RedisCommandEnum redisCommand = RedisCommandEnum.getInstance(command);
            if(redisCommand == null){
                System.out.println(new String(inputStream.read(1000)));
                throw new RuntimeException("command:"+command+"为空");
            }

            WriteDeserialize writeDeserialize = doParseCommand(redisCommand,asteriskSymbol);
            notifyChange(writeDeserialize);
        }
    }

    protected WriteDeserialize doParseCommand(RedisCommandEnum redisCommand,int asteriskSymbol)throws IOException{
        switch (redisCommand){
            case SELECT:
            case DEL:
            case PING:
            case INCRBY:
            case EXPIRE:
            case SETNX:
            case SETEX:
            case HSET:
            case PSETEX:
            case PEXPIRE:
                WriteDeserialize deserialize =parseObj(asteriskSymbol,redisCommand);
                System.out.println("command:"+redisCommand+"     deserialize:"+ JSONObject.toJSONString(deserialize));
                if(deserialize == END_DESERIALIZE){
                    break;
                }
                return deserialize;
            case SET:
                WriteDeserialize setDeserialize = parseObj(asteriskSymbol,redisCommand);
                if(setDeserialize == null){
                    break;
                }
                if(setDeserialize == END_DESERIALIZE){
                    break;
                }
                WriteDeserialize expireWithSetDeserialize = parseObj(asteriskSymbol,null);
                if(expireWithSetDeserialize == null){
                    break;
                }
                if(expireWithSetDeserialize == END_DESERIALIZE){
                    break;
                }
                if(setDeserialize.getKey().equals(expireWithSetDeserialize.getKey()) && RedisCommandEnum.EXPIRE.getCommand().equals(expireWithSetDeserialize.getType())){
                    System.out.println("keyValueWithSet="+ JSONObject.toJSONString(setDeserialize));
                    System.out.println("expireWithSet="+ JSONObject.toJSONString(expireWithSetDeserialize));
                    setDeserialize.setExpire(Long.parseLong(expireWithSetDeserialize.getValue()+""));
                }
                return setDeserialize;
            default:
                System.out.println("未发现"+redisCommand);
                throw new RuntimeException("redisCommand:"+redisCommand+" 未发现");
        }
        return null;
    }
    private void waitFor(int millis){
        try {
            System.out.println("waitFor millis="+millis+"  前 是否连接成功:"+syncClient.isConnected());
            Thread.sleep(millis);
            System.out.println("waitFor millis="+millis+"  后 是否连接成功:"+syncClient.isConnected());
        }catch (InterruptedException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private WriteDeserialize parseObj(int  asteriskSymbol, RedisCommandEnum redisCommand)throws IOException {
        RedisCommandEnum newRedisCommand = redisCommand;
        if(newRedisCommand == null) {
            //1-读取*相关内容
            asteriskSymbol = readLineNumber();
            if(((byte)asteriskSymbol)==NumberConstants.BYTE_FF){
                return END_DESERIALIZE;
            }
            System.out.println("*后面数量=" + asteriskSymbol);
            String key = null;
            //2-读取$后面数字所代表的字符串
            String command = readCrossLineString();
            newRedisCommand= RedisCommandEnum.getInstance(command);
        }
        switch (newRedisCommand){
            case PING:
                return PASS_DESERIALIZE;
            case SELECT:
                readCrossLineString();
                return PASS_DESERIALIZE;
            case DEL:
                return deserializeDelWithCommand(newRedisCommand);
            case SET:
                return deserializeSetWithCommand(asteriskSymbol,newRedisCommand);
            case SETNX:
                return deserializeSetnxWithCommand(asteriskSymbol,newRedisCommand);
            case EXPIRE:
                return deserializeExpireWithCommand(newRedisCommand);
            case HSET:
                return deserializeHsetWithCommand(asteriskSymbol,newRedisCommand);
            case INCRBY:
                return deserializeIncrbyWithCommand(asteriskSymbol,newRedisCommand);
            case SETEX:
                return deserializeSetexWithCommand(asteriskSymbol, redisCommand);
            case PEXPIRE:
                return deserializePexpireWithCommand(asteriskSymbol,redisCommand);
            case PSETEX:
                return deserializePsetexWithCommand(asteriskSymbol,redisCommand);
            default:
                throw new RuntimeException("newRedisCommand:"+newRedisCommand+" 未发现");
        }
    }

    private WriteDeserialize deserializePsetexWithCommand(int asteriskSymbol,RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        List<String> valueList = new LinkedList<>();
        String valueStr1 =readCrossLineString();
        valueList.add(valueStr1);
        String valueStr2 =readCrossLineString();
        valueList.add(valueStr2);
        deserialize.setValue(valueList);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }

    private WriteDeserialize deserializePexpireWithCommand(int asteriskSymbol,RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        String valueStr =readCrossLineString();
        deserialize.setValue(valueStr);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }

    protected WriteDeserialize deserializeIncrbyWithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        String strValue = readCrossLineString();
        deserialize.setValue(strValue);
        return deserialize;
    }
    private WriteDeserialize deserializeSetexWithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        int remaing = asteriskSymbol -2;
        List<String> list = new LinkedList<>();
        for(int i=0;i<remaing;i++){
            String value = readCrossLineString();
            list.add(value);
        }
        deserialize.setValue(list);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }
    private WriteDeserialize deserializeHsetWithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        int remaing = asteriskSymbol -2;
        Set<String> set = new LinkedHashSet<>();
        for(int i=0;i<remaing;i++){
            String value = readCrossLineString();
            set.add(value);
        }
        deserialize.setValue(set);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }
    private WriteDeserialize deserializeSetnxWithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        int valueLen = readLineNumber();
        byte[] value = readBytes(valueLen);
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        deserialize.setValue(value);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }

    private WriteDeserialize deserializeExpireWithCommand(RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        String valueStr =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        deserialize.setValue(Integer.parseInt(valueStr));
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }
    private WriteDeserialize deserializeSetWithCommand(int  asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        //1-key
        String key =readCrossLineString();
        System.out.println("key="+key);
        if(key.equals("new2-corp-charge-733")){
            System.out.println("");
        }
        //2-value
        int valueLen = readLineNumber();
        byte[] value = readBytes(valueLen);
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setKey(key);
        deserialize.setValue(value);
        deserialize.setType(redisCommand.getCommand());
        if(asteriskSymbol == 3){
            return deserialize;
        }
        int remaining = asteriskSymbol -3;
        List<String> extendList = Lists.newArrayList();
        for(int i=0;i<remaining;i++){
            String extend = readCrossLineString();
            extendList.add(extend);
        }
        deserialize.setExtendList(extendList);
        return deserialize;
    }

    private WriteDeserialize deserializeDelWithCommand(RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize(DataTypeEnum.WRITE.getType(),-1,(byte)0,0,key);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }
    private byte[] readBytes(int len)throws IOException{
        byte[] bytes = new byte[len];
        inputStream.read(bytes);
        skipRN();
        return bytes;
    }
    private String readCrossLineString()throws IOException{
        int number = readLineNumber();
        //3-读取命令
        byte[] bytes = new byte[number];
        inputStream.read(bytes);
        String value = new String(bytes);
        skipRN();
        return value;
    }

    protected void notifyChange(Deserialize deserialize){
        if(deserialize == null){
            return;
        }
        if(CollectionUtil.isNotEmpty(syncClient.getDataListeners())){
            try{
                for(DataListener dataListener : syncClient.getDataListeners()){
                    dataListener.onChanage(deserialize);
                }
            }catch (Throwable e){

            }
        }
    }

    private void skipRN()throws IOException{
        inputStream.read(new byte[2]);
    }

    private int readLineNumber()throws IOException{
        byte b =(byte)inputStream.read();
        if(NumberConstants.BYTE_FF == b){
            return b;
        }
        return readNumber();
    }
    private int readNumber()throws IOException{
        final int maxLen = 64;
        ByteBuffer byteBuffer =ByteBuffer.allocate(maxLen);
        int count=0;
        while((count++)<maxLen){
            byte b =(byte)inputStream.read();
            if(NumberConstants.BYTE_FF == b){
                break;
            }
            if(b == NumberConstants.R_SYMBOL){
                byte next= (byte)inputStream.read();
                if(next == NumberConstants.N_SYMBOL){
                    break;
                }
            }else{
                byteBuffer.put(b);
            }
        }
        int len = byteBuffer.position();
        byte[] bytes = new byte[len];
        System.arraycopy(byteBuffer.array(),0,bytes,0,len);
        return Integer.parseInt(new String(bytes));
    }














    public RedisInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(RedisInputStream inputStream) {
        this.inputStream = inputStream;
    }
}
