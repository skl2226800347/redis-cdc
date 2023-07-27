package com.skl.cdc.core.event.deserialize.rdb;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.io.RedisInputStream;
import com.skl.cdc.core.util.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuicklistDeserialize extends Deserialize {
    private int zlbyties;
    private int zltail;
    private int zllen;
    private List<Object> list;
    public QuicklistDeserialize(byte dataType,int database,byte timestampType,long timestamp, String key)throws IOException {
        this.dataType = dataType;
        this.database = database;
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
        this.list = new ArrayList<>();
    }
    @Override
    public void deserializeValue(RedisInputStream is)throws IOException {
        System.out.println("key:" + key);
        try {
            byte[] bytes = new byte[4];
            is.read(bytes);
            this.zlbyties = ByteUtil.toIntByReverse(bytes);
            System.out.println("zlbytes:" + zlbyties);

            bytes = new byte[4];
            is.read(bytes);
            this.zltail = ByteUtil.toIntByReverse(bytes);
            System.out.println("zltail:" + zltail);

            bytes = new byte[2];
            is.read(bytes);
            this.zllen = ByteUtil.toIntByReverse(bytes);
            System.out.println("zllen:" + zllen);
            for(int i=0;i<zllen;i++) {
                int keyPrevEntryLength = is.readInt();
                System.out.println("keyPrevEntryLength:" + keyPrevEntryLength);
                Object entryKey = parseEntry(is);
                list.add(entryKey);
            }
            bytes = new byte[1];
            is.read(bytes);
            if(bytes[0] != NumberConstants.BYTE_FF){
                throw new RuntimeException("0xff");
            }
        }finally {
            if(is != null){
                is.close();
            }
        }
    }

    public Object parseEntry(RedisInputStream is)throws IOException{
        byte[] bytes = is.read(1);
        byte byteFlag = bytes[0];
        int flag =(byteFlag>>6)&0x03;
        //00pppppp
        if(flag ==0){
            int len =byteFlag&0x3f;
            String entryContent =is.readString(len);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(flag == 1){//01pppppp
            bytes = is.read(1);
            int len =((byteFlag&0x3f)|bytes[0]);
            String entryContent =is.readString(len);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(flag ==2){
            bytes = is.read(4);
            int len =ByteUtil.toInt(bytes);
            String entryContent =is.readString(len);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(((byteFlag>>4)&0x0f) ==12){//1100____
            bytes = is.read(2);
            int entryContent =ByteUtil.toInt(bytes);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(((byteFlag>>4)&0x0f) ==13){//1101____
            bytes = is.read(4);
            int entryContent =ByteUtil.toIntByReverse(bytes);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(((byteFlag>>4)&0x0f) ==14){//1110____
            bytes = is.read(8);
            long entryContent =ByteUtil.toLongByReverse(bytes);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if(((byteFlag>>4)&0xff) ==240){//11110000
            bytes = is.read(3);
            int len =ByteUtil.toInt(bytes);
            String entryContent =is.readString(len);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if((byteFlag&0xff) ==254){//11111110
            bytes = is.read(1);
            int entryContent =ByteUtil.toInt(bytes);
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else if((((byteFlag>>4)&0x0f) ==15) && ((byteFlag&0x0f)>=1 && (byteFlag&0x0f) <=13)){
            int entryContent =(byteFlag&0x0f)-1;
            System.out.println("entryContent="+entryContent);
            return entryContent;
        }else {
            throw new RuntimeException("未知byteFlag");
        }
    }


    public int getZlbyties() {
        return zlbyties;
    }

    public void setZlbyties(int zlbyties) {
        this.zlbyties = zlbyties;
    }

    public int getZltail() {
        return zltail;
    }

    public void setZltail(int zltail) {
        this.zltail = zltail;
    }

    public int getZllen() {
        return zllen;
    }

    public void setZllen(int zllen) {
        this.zllen = zllen;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }
}
