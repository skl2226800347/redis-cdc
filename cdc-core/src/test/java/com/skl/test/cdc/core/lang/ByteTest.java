package com.skl.test.cdc.core.lang;
import com.skl.cdc.core.util.ByteUtil;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteTest {

    @Test
    public void toUnsignedInt(){
        byte b1=-3;
        int b2=Byte.toUnsignedInt(b1);
        System.out.println(b2);
        byte c1=4;
        int c2 = Byte.toUnsignedInt(c1);
        System.out.println(c2);

    }

    @Test
    public void toUnsigned(){
        byte b=-10;
        System.out.println(Byte.toUnsignedInt(b));
        System.out.println(Byte.toUnsignedLong(b));
    }

    @Test
    public void StringFlag(){
        byte[] bytes =new byte[1];
        //1100 0000 = 128+64 = 192;
        bytes[0]=(byte)192;
        System.out.println((bytes[0]>>6)&0x03);
    }

    @Test
    public void intFlag(){
        byte[] bytes =new byte[1];
        //1100 0000 = 128+64 = 192;
        bytes[0]=(byte)192;
        System.out.println((bytes[0]>>4)&0x0f);
    }
    @Test
    public void intFlag_2(){
        byte[] bytes =new byte[1];
        //1100 0000 = 128+64 = 192;
        bytes[0]=(byte)254;
        System.out.println(bytes[0]&0xff);
    }

    @Test
    public void intFlag_30(){
        byte[] bytes =new byte[1];
        //1100 0000 = 128+64 = 192;
        bytes[0]=(byte)252;
        System.out.println((bytes[0]>>4)&0x0f);
        System.out.println((bytes[0]&0x0f));
    }
    @Test
    public void intFlag_4(){
        byte[] bytes =new byte[1];
        //1100 0000 = 128+64 = 192;
        bytes[0]=-32;
        System.out.println(Byte.toUnsignedInt(bytes[0]));
        System.out.println(Integer.toBinaryString(224));
        System.out.println((bytes[0]>>4)&0x0f);
        System.out.println((bytes[0]&0x0f));
    }

    @Test
    public void t3(){
        long result =1;
        for(int i=0;i<14;i++){
            result*=2;
        }
        System.out.println(result -1);
    }

    @Test
    public void t(){
        //1111 1100
        int result = 128+64+32+16+8+4;
        System.out.println(result);
    }


    @Test
    public void toInt()throws IOException {
        byte[] bytes = new byte[8];
        bytes[0]=(byte)56;
        bytes[1]=(byte)5;
        bytes[2]=(byte)-34;
        bytes[3]=(byte)17;
        long l = ByteUtil.toIntByReverse(bytes);
        System.out.println(l);
        System.out.println(ByteBuffer.wrap(bytes).getInt());

        System.out.println("1688221856855".length());
        System.out.println("1688221910328".length());
        System.out.println("1688223206937".length());
    }
    @Test
    public void toLong()throws IOException {
        byte[] bytes = new byte[8];
        bytes[0]=(byte)56;
        bytes[1]=(byte)5;
        bytes[2]=(byte)-34;
        bytes[3]=(byte)17;
        bytes[4]=(byte)-119;
        bytes[5]=(byte)1;
        bytes[6]=0;
        bytes[7]=0;
        long l = ByteUtil.toLongByReverse(bytes);
        System.out.println(l);
        System.out.println(ByteBuffer.wrap(bytes).getLong());

        System.out.println("1688221856855".length());
        System.out.println("1688221910328".length());
        System.out.println("1688223206937".length());
    }

    @Test
    public void t43(){
        System.out.println(0xC2);
        System.out.println((byte)0xC2);
        byte b=(byte)-64;
        System.out.println(b&0xff);
        byte c=-61;
        System.out.println(b&0xff);
        System.out.println(c==b);
        System.out.println(Integer.toBinaryString(195));
        System.out.println(b&0x3f);
    }
    @Test
    public void short1(){
        byte b=-128;
        int c=b;
        if(b<=0){
            System.out.println("小于0="+b);
        }
    }

    @Test
    public void t1(){
        byte b='a';
        System.out.println(b);
    }
}
