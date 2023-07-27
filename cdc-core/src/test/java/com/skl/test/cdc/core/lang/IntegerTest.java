package com.skl.test.cdc.core.lang;

import org.junit.Test;

import java.nio.ByteBuffer;

public class IntegerTest {
    @Test
    public void toUnsignedLong(){
        int i=-3;
        System.out.println(Integer.toUnsignedLong(i));
        String bstr=Integer.toBinaryString(-3);
        System.out.println("长度:"+bstr.length()+"   二进制:"+bstr);
    }

    @Test
    public void readInt_1(){
        ByteBuffer byteBuffer =ByteBuffer.allocate(4);
        byteBuffer.putInt(-3555);
        byte[] bytes = byteBuffer.array();
        int result =0;
        int len = bytes.length;
        for(int i=0;i<bytes.length;i++){
            result |=((bytes[i]&0xff)<<(8*(len-i-1)));
        }
        System.out.println(result);
    }

    @Test
    public void readInt_2(){
        byte[] bytes = new byte[4];
        bytes[0] = -112;
        bytes[1] =1;
        int result =0;
        int len = bytes.length;
        System.out.println(Byte.toUnsignedInt(bytes[0]));
        System.out.println(ByteBuffer.wrap(bytes).getInt());
        for(int i=0;i<bytes.length;i++){
            result |=((bytes[i]&0xff)<<(8*(len-i-1)));
        }
        System.out.println(result);
    }


    @Test
    public void readInt_5(){
        byte[] bytes = new byte[2];
        System.out.println(Byte.toUnsignedInt((byte)-112));
        System.out.println(bytes[1] & 0x00ff);
        //1001 0000     =128+16=144
        //<<8位
        //0000 0001     =128+16=144
        //<<不移动。
        //1001 0000  0000 0001   =144+1=145;
        bytes[0] =(byte)-112;
        bytes[1]=(byte)1;
        int value = ((bytes[1] & 0xff) << 8) | (bytes[0] & 0x00ff);
        System.out.println(value);
    }

    @Test
    public void readInt_6(){
        byte[] bytes = new byte[2];
        bytes[0] =(byte)-112;
        bytes[1]=(byte)1;
        int len = bytes.length;
        int result =0;
        for(int i=(bytes.length-1);i>=0;i--){
            result |=((bytes[i]&0xff)<<(8*(i)));
        }
        System.out.println(result);
    }
    @Test
    public void t(){
        System.out.println(Short.MAX_VALUE);
    }

    @Test
    public void t3(){
        byte flag =-48;
        System.out.println(Byte.toUnsignedInt(flag));
        System.out.println(flag &0xff);
        System.out.println(Integer.toBinaryString(208));
    }
    @Test
    public void t4(){
        System.out.println("2147483647".length());
        System.out.println("2147483647".length());
    }

    @Test
    public void t6(){
        System.out.println(Byte.toUnsignedInt((byte)-61));
        System.out.println(Integer.toBinaryString(195));
    }

    @Test
    public void t7(){
        System.out.println(Byte.toUnsignedInt((byte)-61));
    }
}
