package com.skl.test.cdc.core.support;
import com.skl.cdc.core.util.ByteUtil;
import com.skl.cdc.core.util.RedisLzfCompressUtil;
import org.junit.Test;

public class RedisLzfCompressUtilTest {


    @Test
    public void deCompress(){
        //29,123,34,64,116,121,112,101,34,58,34,99,111,109,46,121,117,97,110,116,117,46,73,77,46,101,110,116,105,116,121,32,9,13,67,111,110,102,105,103,34,44,34,97,112,112,73,100,32,39,9,49,52,48,48,55,57,56,51,56,57,-128,20,2,75,101,121,32,21,31,52,51,100,99,49,51,102,52,55,50,100,50,57,49,100,57,53,99,49,54,48,98,49,55,53,52,49,48,48,54,49,55,8,51,98,56,97,97,99,53,57,49,32,36,19,99,102,57,56,99,48,97,53,98,101,54,99,51,52,101,57,99,49,56,48,32,75,2,99,111,114,96,97,21,57,49,54,44,34,100,101,102,97,108,117,116,34,58,48,44,34,101,120,112,105,114,64,-94,5,50,53,57,50,48,48,64,43,7,103,109,116,67,114,101,97,116,96,21,18,48,50,51,45,48,52,45,49,48,32,49,49,58,50,51,58,51,50,46,-96,35,4,77,111,100,105,102,64,-98,-32,15,35,0,105,32,-47,1,56,52,32,7,6,115,68,101,108,101,116,101,32,14,0,34,64,59,4,117,110,105,111,110,64,-18,4,51,52,52,50,125
        String str="29,123,34,64,116,121,112,101,34,58,34,99,111,109,46,121,117,97,110,116,117,46,73,77,46,101,110,116,105,116,121,32,9,13,67,111,110,102,105,103,34,44,34,97,112,112,73,100,32,39,9,49,52,48,48,55,57,56,51,56,57,-128,20,2,75,101,121,32,21,31,52,51,100,99,49,51,102,52,55,50,100,50,57,49,100,57,53,99,49,54,48,98,49,55,53,52,49,48,48,54,49,55,8,51,98,56,97,97,99,53,57,49,32,36,19,99,102,57,56,99,48,97,53,98,101,54,99,51,52,101,57,99,49,56,48,32,75,2,99,111,114,96,97,21,57,49,54,44,34,100,101,102,97,108,117,116,34,58,48,44,34,101,120,112,105,114,64,-94,5,50,53,57,50,48,48,64,43,7,103,109,116,67,114,101,97,116,96,21,18,48,50,51,45,48,52,45,49,48,32,49,49,58,50,51,58,51,50,46,-96,35,4,77,111,100,105,102,64,-98,-32,15,35,0,105,32,-47,1,56,52,32,7,6,115,68,101,108,101,116,101,32,14,0,34,64,59,4,117,110,105,111,110,64,-18,4,51,52,52,50,125";
        byte[] inData = ByteUtil.stringToByte(str);
        byte[] outData = new byte[293];
         RedisLzfCompressUtil.deCompress(inData,258,outData,293);
        System.out.println("outData="+new String(outData));
    }

    @Test
    public void intDistInt(){
        String str="29,123,34,64,116,121,112,101,34,58,34,99,111,109,46,121,117,97,110,116,117,46,73,77,46,101,110,116,105,116,121,32,9,13,67,111,110,102,105,103,34,44,34,97,112,112,73,100,32,39,9,49,52,48,48,55,57,56,51,56,57,-128,20,2,75,101,121,32,21,31,52,51,100,99,49,51,102,52,55,50,100,50,57,49,100,57,53,99,49,54,48,98,49,55,53,52,49,48,48,54,49,55,8,51,98,56,97,97,99,53,57,49,32,36,19,99,102,57,56,99,48,97,53,98,101,54,99,51,52,101,57,99,49,56,48,32,75,2,99,111,114,96,97,21,57,49,54,44,34,100,101,102,97,108,117,116,34,58,48,44,34,101,120,112,105,114,64,-94,5,50,53,57,50,48,48,64,43,7,103,109,116,67,114,101,97,116,96,21,18,48,50,51,45,48,52,45,49,48,32,49,49,58,50,51,58,51,50,46,-96,35,4,77,111,100,105,102,64,-98,-32,15,35,0,105,32,-47,1,56,52,32,7,6,115,68,101,108,101,116,101,32,14,0,34,64,59,4,117,110,105,111,110,64,-18,4,51,52,52,50,125";
        byte[] inData = ByteUtil.stringToByte(str);
        for(byte b: inData){
            int c=b;
            int d =(b&0xff);
            if(c != d){
                System.out.println("b:"+b+"   c:"+c+"    d:"+d);
            }
        }
    }
    @Test
    public void strToByte(){
        String s="-63";
        byte b = Byte.parseByte(s);
        System.out.println(b);
    }
}
