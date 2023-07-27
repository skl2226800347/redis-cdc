package com.skl.test.cdc.core.support;

import com.skl.cdc.core.util.ByteUtil;
import org.junit.Test;

import java.nio.ByteBuffer;

public class ByteUtilTest {

    @Test
    public void toInt(){
        byte[] bytes = new byte[4];
        bytes[0]=0;
        bytes[1]=18;
        bytes[2]=51;
        bytes[3]=26;
        System.out.println(ByteUtil.toInt(bytes));
    }
    @Test
    public void toInt_2(){
        byte[] bytes = new byte[4];
        bytes[0]=0;
        bytes[1]=116;
        bytes[2]=59;
        bytes[3]=-100;
        System.out.println(ByteUtil.toInt(bytes));
    }
    @Test
    public void t1(){
        byte b ='a';
        System.out.println( 0x61);
        System.out.println(b);
    }
    @Test
    public void t2(){
        byte[] bytes=new byte[4];
        bytes[0]=(byte)0x1F;
        bytes[1]=(byte)0xFF;
        bytes[2]=(byte)0xFF;
        bytes[3]=(byte)0xFF;
        int result =ByteBuffer.wrap(bytes).getInt();
        System.out.println(result);
    }

    @Test
    public void t(){
        //1100 0001
        //
        byte value =(byte)0xC1;
        System.out.println(value&0x3f);
    }

    @Test
    public void readHighBits(){
        //1111 1111 = 128+ 64+32+16+8+4+2+1=
        byte value = (byte)255;
        int hight =ByteUtil.readHighBits(value);
        System.out.println(hight);
    }

    @Test
    public void readLowBits(){
        //0011 1111 =32+16+8+4+2+1=63
        byte value = (byte)255;
        int low =ByteUtil.readLowBits(value);
        System.out.println(low);
    }

    @Test
    public void oper_1(){
        byte[] bytes=new byte[2];
        bytes[0]=(byte)0x4F;
        System.out.println(bytes[0]);
        bytes[1]=(byte)0xFF;
        //01001111  11111111
        //00001111  11111111
        // 2048+1024+512+256  + 128+64+32+16+8+4+2+1
        //  3840+255
        //4095
        //01001111
        //00001111   8+4+2+1 = 15
        System.out.println((((bytes[0]<<2)&0xff)>>2)&0xff);
        System.out.println((((bytes[0]<<2)>>2)&0xff));
        //0001 0011 1100 = 256+ 32+16+8+4=
        System.out.println(bytes[0]<<2);
    }

    @Test
    public void getLong(){
        byte[] bytes = new byte[8];
        bytes[0]=1;
        bytes[1]=1;
        bytes[2]=1;
        bytes[3]=1;
        bytes[4]=1;
        bytes[5]=1;
        bytes[6]=1;
        bytes[7]=1;
        long value =ByteUtil.makeLong(bytes[0],bytes[1],bytes[2],bytes[3],bytes[4],bytes[5],bytes[6],bytes[7]);
        System.out.println(value);
        long value2 =ByteUtil.makeLongV2(bytes[0],bytes[1],bytes[2],bytes[3],bytes[4],bytes[5],bytes[6],bytes[7]);
        System.out.println(value2);
        System.out.println(ByteBuffer.wrap(bytes).getLong());
    }

    @Test
    public void t11(){
        int i=1;
        int c =i<<5;
        System.out.println(c);
    }
    @Test
    public void t13(){
        int i=128;
        int c =i>>5;
        System.out.println(c);
    }
}
