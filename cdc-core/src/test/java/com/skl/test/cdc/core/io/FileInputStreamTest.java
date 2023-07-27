package com.skl.test.cdc.core.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileInputStreamTest {
    FileInputStream fis;
    @Before
    public void init()throws FileNotFoundException {
        fis = new FileInputStream("d:/tmp/redis.rdb");
    }
    @Test
    public void count()throws IOException{
        byte[] bytes =new byte[102400];
        int count=0;
        while(true){
            int read =fis.read(bytes);
            if(read>=0){
                count+=read;
            }else{
                break;
            }
        }
        //1482370
        System.out.println("count="+count);
    }


    @Test
    public void readFileEof()throws IOException{
        byte[] bytes =new byte[1748446];
        int read =fis.read(bytes);
        System.out.println("read="+read);
        int countNum=1;
        for(int i=(read-1);i>=0;i--){
            System.out.println("bytes["+i+"]="+bytes[i]);
            if(countNum++>10){
                break;
            }
        }
        byte[] bytesS = new byte[10];
        int readS = fis.read(bytesS);
        System.out.println("readS="+readS);
        System.out.println("readS="+readS);
    }

    @After
    public void destroy()throws IOException {
        fis.close();
    }
}
