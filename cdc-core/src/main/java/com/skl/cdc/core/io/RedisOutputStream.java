package com.skl.cdc.core.io;

import java.io.IOException;
import java.io.OutputStream;

public class RedisOutputStream extends OutputStream {
    private OutputStream outputStream;
    private byte[] hb;
    private int count;
    public RedisOutputStream(OutputStream outputStream){
        this.outputStream = outputStream;
        this.hb = new byte[8192];
    }
    @Override
    public void write(int b) throws IOException {
        writeInt(b);
    }



    @Override
    public void flush() throws IOException {
        flushBuffer();
        outputStream.flush();
    }

    private void flushBuffer()throws IOException{
        if(this.count>0){
            outputStream.write(hb,0,this.count);
            this.count=0;
        }
    }

    public void write(byte value){
        hb[count++]=value;
    }
    public void writeInt(int value){
        byte[] bytes = String.valueOf(value).getBytes();
        write(bytes);
    }

    @Override
    public void write(byte[] bytes){
        int size = bytes.length;
        System.arraycopy(bytes,0,hb,count,size);
        count+=size;
    }
}
