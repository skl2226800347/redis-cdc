package com.skl.test.cdc.core.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileInputStream2Test {
    FileInputStream fis;
    @Before
    public void init()throws FileNotFoundException {
        fis = new FileInputStream("d:/tmp/header.rdb");
    }
    @Test
    public void read()throws IOException{
        byte[] data = new byte[1024];
        fis.read(data);
        String content = new String(data);
        System.out.println(content);
        String[] array =content.split("\n");
        for(String a : array){
            System.out.println("=="+a);
        }
        String str ="+OK\n" +
                "+FULLRESYNC 25b8fd5fa2ffd75c3cf72d18b2de561bcad218f2 278946344\n" +
                "$2429700";
        int len = str.length();
        System.out.println("len="+len);
    }
    @Test
    public void getRDBStart_auto()throws IOException{
        byte[] data = new byte[1024];
        int readLength =fis.read(data);
        String content = new String(data);
        //System.out.println(content);
        String[] array =content.split("\n");
        int rdbFileOffset =0;
        for(String a : array){
            if(a.startsWith("REDIS")){
                break;
            }else{
                rdbFileOffset+=a.getBytes().length+1;
            }
        }
        System.out.println("rdbFileOffset="+rdbFileOffset);
        fis.skip(-readLength+rdbFileOffset);
        byte[] bytes = new byte[256];
        fis.read(bytes);
        System.out.println(new String(bytes));

    }


    @Test
    public void getRDBStart_auto2()throws IOException{
        byte[] readBytes = new byte[1024];
        int readLength =fis.read(readBytes);
        String content = new String(readBytes);
        //System.out.println(content);
        String[] array =content.split("\n");
        int rdbFileOffset =0;
        for(String a : array){
            if(a.startsWith("REDIS")){
                break;
            }else{
                rdbFileOffset+=a.getBytes().length+1;
            }
        }
        System.out.println("rdbFileOffset="+rdbFileOffset);
        int remain = readLength-rdbFileOffset;
        byte[] bytes = new byte[remain];
        System.arraycopy(readBytes,rdbFileOffset,bytes,0,remain);
        System.out.println("bytes="+new String(bytes));

    }

    @Test
    public void getRDBStart()throws IOException{
        byte[] data = new byte[1024];
        String str ="+OK\n" +
                "+FULLRESYNC 25b8fd5fa2ffd75c3cf72d18b2de561bcad218f2 278946344\n" +
                "$2429700";
        int len = str.length();
        System.out.println("len="+len);
        fis.skip(79);
        fis.read(data);
        System.out.println(new String(data));
    }

    @After
    public void destroy()throws IOException {
        fis.close();
    }
}
