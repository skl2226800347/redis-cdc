package com.skl.test.cdc.core.io;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.skl.cdc.core.enums.RedisCommandEnum;
import com.skl.cdc.core.event.deserialize.write.WriteDeserialize;
import com.skl.cdc.core.io.RedisInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileInputStream3Test {
    private static final WriteDeserialize PASS_DESERIALIZE=new WriteDeserialize();
    private static final WriteDeserialize END_DESERIALIZE=new WriteDeserialize();
    //FileInputStream fis =null;
    RedisInputStream inputStream;
    /**
     *    *星号
     */
    public static final byte  ASTERISK_SYMBOL=42;

    public static final byte BYTE_FF=-1;

    /**
     *  $符号
     */
    public static final byte DOLLAR_SYMBOL = 36;
    /**
     * 回车符
     */
    public static final byte R_SYMBOL = 13;

    /**
     * 换行符
     */
    public static final byte N_SYMBOL = 10;

    @Before
    public void init()throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("D:/tmp/writeCommand.txt");
        inputStream = new RedisInputStream(fis);
    }
    @Test
    public void print()throws IOException{
        byte[] bytes  = new byte[2048];
        inputStream.read(bytes);
        String str = new String(bytes);
        System.out.println(str);
    }

    @Test
    public void parse()throws Throwable {
        boolean cycleFlag = true;
        while(cycleFlag){
            //1-读取*相关内容
            int asteriskSymbol = readLineNumber();
            if(((byte)asteriskSymbol) == BYTE_FF){
                cycleFlag =false;
                break;
            }
            System.out.println("*后面数量="+asteriskSymbol);
            //2-读取$后面数字所代表的字符串
            String command = readCrossLineString();
            RedisCommandEnum redisCommand = RedisCommandEnum.getInstance(command);
            if(redisCommand == null){
                throw new RuntimeException("command:"+command+"为空");
            }
            switch (redisCommand){
                case SELECT:
                case DEL:
                case PING:
                case INCRBY:
                case EXPIRE:
                case SETNX:
                case SETEX:
                case HSET:
                    WriteDeserialize deserialize =parseObj(asteriskSymbol,redisCommand);
                    System.out.println("command:"+command+"     deserialize:"+JSONObject.toJSONString(deserialize));
                    if(deserialize == END_DESERIALIZE){
                        break;
                    }
                    break;
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
                    break;
                default:
                    System.out.println("未发现"+redisCommand);
                    cycleFlag=false;
                    throw new RuntimeException("redisCommand:"+redisCommand+" 未发现");
            }
        }
    }

    private WriteDeserialize parseObj(int  asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        RedisCommandEnum newRedisCommand = redisCommand;
        if(newRedisCommand == null) {
            //1-读取*相关内容
            asteriskSymbol = readLineNumber();
            if(((byte)asteriskSymbol)==BYTE_FF){
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
                return deserializeIncrbyithCommand(asteriskSymbol,newRedisCommand);
            case SETEX:
                return deserializeSetexWithCommand(asteriskSymbol, redisCommand);

            default:
                throw new RuntimeException("newRedisCommand:"+newRedisCommand+" 未发现");
        }
    }

    protected WriteDeserialize deserializeIncrbyithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize();
        deserialize.setKey(key);
        String strValue = readCrossLineString();
        deserialize.setValue(strValue);
        return deserialize;
    }
    private WriteDeserialize deserializeSetexWithCommand(int asteriskSymbol, RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize();
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
        WriteDeserialize deserialize = new WriteDeserialize();
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
        WriteDeserialize deserialize = new WriteDeserialize();
        deserialize.setKey(key);
        deserialize.setValue(value);
        deserialize.setType(redisCommand.getCommand());
        return deserialize;
    }

    private WriteDeserialize deserializeExpireWithCommand(RedisCommandEnum redisCommand)throws IOException{
        String key =readCrossLineString();
        String valueStr =readCrossLineString();
        WriteDeserialize deserialize = new WriteDeserialize();
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
        WriteDeserialize deserialize = new WriteDeserialize();
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
        WriteDeserialize deserialize = new WriteDeserialize();
        deserialize.setKey(key);
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

    private void skipRN()throws IOException{
        inputStream.read(new byte[2]);
    }

    private int readLineNumber()throws IOException{
        byte b =(byte)inputStream.read();
        if(BYTE_FF == b){
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
            if(BYTE_FF == b){
                break;
            }
            if(b == R_SYMBOL){
               byte next= (byte)inputStream.read();
               if(next == N_SYMBOL){
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


    @Test
    public void close()throws IOException{
        inputStream.close();
    }
}
