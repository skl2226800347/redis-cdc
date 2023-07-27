package com.skl.cdc.core.util;
import java.io.IOException;
import java.util.Objects;
public class ByteUtil {

    public static final int toInt(byte[] bytes){
        checkParams(bytes,4);
        int len = bytes.length;
        int result =0;
        for(int i=0;i<bytes.length;i++){
            if(i==0){
                result |=((bytes[i])<<(8*(len-i-1)));
            }else{
                result |=((bytes[i]&0xff)<<(8*(len-i-1)));
            }

        }
        return result;
    }

    public static final int toIntByReverse(byte[] bytes){
        checkParams(bytes,4);
        int result =0;
        int start = bytes.length-1;
        for(int i=start;i>=0;i--){
            if(i==start){
                result |=(bytes[i]<<(8*(i)));
            }else{
                result |=((bytes[i]&0xff)<<(8*(i)));
            }
        }
        return result;
    }

    public static int makeInt(byte byte0,byte byte1,byte byte2,byte byte3){
        int result = (((byte0)<<24) |
                ((byte1&0xff)<<16) |
                ((byte2&0xff)<<8) |
                byte3&0xff);
        return result;
    }


    public static long toLong(byte[] bytes)throws IOException {
        checkParams(bytes,8);
        int len = bytes.length;
        long result =0;
        for(int i=0;i<len;i++){
            if(i==0){
                result |=((long)bytes[i]<<(8*(len-1-i)));
            }else{
                result |=(((long)bytes[i]&0xff)<<(8*(len-1-i)));
            }

        }
        return result;
    }
    public static long toLongByReverse(byte[] bytes){
        checkParams(bytes,8);
        long result =0;
        int start = bytes.length-1;
        for(int i=start;i>=0;i--){
            if(i==start){
                result |=((long)bytes[i]<<(8*(i)));
            }else{
                result |=(((long)bytes[i]&0xff)<<(8*(i)));
            }
        }
        return result;
    }



    public static long makeLong(byte byte0,byte byte1,byte byte2,byte byte3,byte byte4,byte byte5,byte byte6,
        byte byte7){
        long result = ((((long)byte0)<<56) |
                (((long)byte1&0xff)<<48) |
                (((long)byte2&0xff)<<40) |
                (((long)byte3&0xff)<<32) |
                (((long)byte4&0xff)<<24) |
                (((long)byte5&0xff)<<16) |
                (((long)byte6&0xff)<<8) |
                (long)byte7&0xff);
        return result;
    }

    static public long makeLongV2(byte b7, byte b6, byte b5, byte b4,
                                 byte b3, byte b2, byte b1, byte b0)
    {
        return ((((long)b7       ) << 56) |
                (((long)b6 & 0xff) << 48) |
                (((long)b5 & 0xff) << 40) |
                (((long)b4 & 0xff) << 32) |
                (((long)b3 & 0xff) << 24) |
                (((long)b2 & 0xff) << 16) |
                (((long)b1 & 0xff) <<  8) |
                (((long)b0 & 0xff)      ));
    }



    public static final int readHighBits(byte value){
        return ((value>>6) & 0x03);
    }

    public static final int readLowBits(byte value){
        return (value&0x3f);
    }

    public static short makeShort(byte byte0,byte byte1){
        int result = (((byte0)<<8) |
                byte1&0xff);
        return (short)result;
    }


    public static final String byteToString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for(byte b:bytes){
            sb.append(b).append(",");
        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static final byte[] stringToByte(String str){
        String[] subs = str.split(",");
        byte[] bytes = new byte[subs.length];
        for(int i=0;i<bytes.length;i++){
            byte b=Byte.parseByte(subs[i]);
            bytes[i] = b;
        }
        return bytes;
    }

    private static final void checkParams(byte[] bytes,int max){
        Objects.requireNonNull(bytes==null,"bytes not null");
        Objects.requireNonNull(bytes.length>max || bytes.length<=0,"len小于0或大于8");
    }
}
