package com.skl.test.cdc.core.network;

import com.skl.cdc.core.constants.NumberConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RedisSocketAuthTest {
    byte[] hb;
    private int count =0 ;
    @Before
    public void init(){
        hb=new byte[9000];
    }

    @Test
    public void one(){
        byte[] bytes ="AUTH".getBytes();
        System.out.println(bytes);
    }

    @Test
    public void auth(){
        hb[count++]= NumberConstants.ASTERISK_BYTE;
        writeInt(2);
        writeCtrl();

        List<String> argsList = Arrays.asList("AUTH","123");
        for(String arg : argsList){
            write(NumberConstants.DOLLAR_BYTE);
            writeInt(arg.getBytes().length);
            writeCtrl();
            write(arg.getBytes());
            writeCtrl();
        }
        System.out.println(new String(hb));
    }

    protected void write(byte value){
        hb[count++]=value;
    }

    private void writeCtrl(){
        hb[count++]='\r';
        hb[count++]='\n';
    }


    private void writeInt(int value){
        byte[] bytes = String.valueOf(value).getBytes();
        write(bytes);
    }

    private void write(byte[] bytes){
        int size = bytes.length;
        System.arraycopy(bytes,0,hb,count,size);
        count+=size;
    }
}
