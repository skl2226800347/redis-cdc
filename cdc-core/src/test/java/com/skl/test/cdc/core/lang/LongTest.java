package com.skl.test.cdc.core.lang;
import com.skl.cdc.common.util.DateUtil;
import org.junit.Test;
import java.nio.ByteBuffer;
import java.util.Date;
public class LongTest {

    @Test
    public void readLong(){
        byte[] bytes = new byte[8];
        bytes[0]=-8;
        bytes[1]=65;
        bytes[2]=120;
        bytes[3]=6;
        bytes[4]=-119;
        bytes[5]=1;
        bytes[6]=0;
        bytes[7]=0;
        long result =0;
        for(int i=(bytes.length-1);i>=0;i--){
            result |=((bytes[i]&0xff)<<(8*(i)));
        }
        System.out.println("result="+result);
        System.out.println("getLong="+ByteBuffer.wrap(bytes).getLong());

        long expire = ((long)(bytes[7] & 0x00ff) << 56)
                + ((long)(bytes[6] & 0x00ff) << 48) + ((long)(bytes[5] & 0x00ff) << 40)
                + ((long)(bytes[4] & 0x00ff) << 32) + ((long)(bytes[3] & 0x00ff) << 24)
                + ((long)(bytes[2] & 0x00ff) << 16) + ((long)(bytes[1] & 0x00ff) << 8)
                + ((long)(bytes[0] & 0x00ff));
        //key:skl_12    2023-06-29 15:24:52 +7200秒=2023-06-29 17:24:51
        System.out.println("expire="+expire+"  "+ DateUtil.dateStr(new Date(expire),DateUtil.DEFAULT_FORMAT));
        System.out.println(3600*3);
    }


    @Test
    public void toUnsignedLong(){
        byte[] bytes = new byte[4];
        bytes[0] =3;
        bytes[1]=4;
        bytes[2]=3;
        bytes[3]=10;
        int expire = ((bytes[3] & 0x00ff) << 24)
                + ((bytes[2] & 0x00ff) << 16) + ((bytes[1] & 0x00ff) << 8)
                + ((bytes[0] & 0x00ff));
        System.out.println(Integer.toUnsignedLong(expire));
        System.out.println(Integer.toUnsignedLong(333333));
        System.out.println(333333);
    }

    @Test
    public void t12(){
        System.out.println(0xff);
    }
}
