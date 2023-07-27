package com.skl.test.cdc.core.lang;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StrTest {

    @Test
    public void o1(){
        System.out.println("25b8fd5fa2ffd75c3cf72d18b2de561bcad218f2".length());
    }


    @Test
    public void tow2(){
        String l=null;
        //62
        System.out.println("+FULLRESYNC 25b8fd5fa2ffd75c3cf72d18b2de561bcad218f2 2280115\n".length());
    }

    @Test
    public void t(){
        //0100 0100   = 64+4=68
        //1011 1100 =-68 =0xBC
       byte[] bytes = new byte[1];
       bytes[0] = -68;
       System.out.println(new String(bytes));
    }
    @Test
    public void t2(){
        System.out.println(0xff);
    }
    @Test
    public void t3(){
        byte b=-3;
        int c = -3;
        System.out.println(b==c);
    }

    @Test
    public void t4(){
        byte[] readBytes= null;
        if(readBytes == null || readBytes.length<=0){
            System.out.println("空");
        }
    }
    @Test
    public void disFromFile()throws Exception{
        File file = new File("D:/tmp/value.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String result = null;
        while((result = br.readLine())!= null){
            sb.append(result.trim().replaceAll("\n","").replaceAll("\r",""));
        }
        System.out.println("长度:"+sb.length()+"  "+sb);

    }
    @Test
    public void t6(){
        System.out.println("abc".getBytes().length);
        System.out.println("abc".toCharArray().length);
    }
    @Test
    public void t8(){
        byte b ='{';
        System.out.println(b);

        char c='{';
        System.out.println(c);
    }

    @Test
    public void t9(){
        System.out.println(0x24);
    }

    @Test
    public void t10(){
        byte b=-64;
        System.out.println(Byte.toUnsignedInt(b));
    }

    @Test
    public void t31(){
        System.out.println("user:config:detail:name:-1_-1_billPayRecord".length());
    }
}
