package com.skl.cdc.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

public class BufferedSocketInputStream extends FilterInputStream {
    private static final int EOF =-1;
    private static final int BYTE_MAX=255;
    private AtomicLong totalReadOffset;
    private int offset;
    private int limit;
    private byte[] hb;

    public BufferedSocketInputStream(InputStream inputStream){
        super(inputStream);
        totalReadOffset = new AtomicLong(0);
        hb = new byte[999999];
    }
    @Override
    public int read() throws IOException {
        if(offset <limit){
            totalReadOffset.incrementAndGet();
            return hb[offset++]&BYTE_MAX;
        }
        offset=0;
        limit = in.read(hb);
        if(limit != EOF){
            totalReadOffset.incrementAndGet();
        }
        return this.limit != EOF ?  hb[offset++]&BYTE_MAX : EOF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(offset>=limit){
            if(len>=hb.length){
                int readLen= in.read(b,off,len);
                if(readLen>=0){
                    totalReadOffset.addAndGet(readLen);
                }
                return readLen;
            }
            offset=0;
            limit = in.read(hb,0,hb.length);
        }
        int bytesRemainingInBuffer= Math.min(len,this.limit-this.offset);
        if(bytesRemainingInBuffer ==-1){
            return bytesRemainingInBuffer;
        }
        try {
            System.arraycopy(hb, offset, b, off, bytesRemainingInBuffer);
        }catch (Exception e){
            e.printStackTrace();
        }
        offset+=bytesRemainingInBuffer;
        totalReadOffset.addAndGet(bytesRemainingInBuffer);
        return bytesRemainingInBuffer;
    }

    @Override
    public int available() throws IOException {
        return limit-offset+in.available();
    }


    public byte[] readRemain()throws IOException{
        if(offset>=limit){
            offset=0;
            limit = in.read(hb);
        }
        int remaining = limit-offset;
        if(remaining <=0){
           // throw new IOException("");
            return null;
        }
        byte[] bytes=new byte[remaining];
        System.arraycopy(hb,offset,bytes,0,remaining);
        limit=0;
        offset=0;
        return bytes;
    }

    public void load(byte[] bytes)throws IOException{
        this.offset = 0;
        this.limit = bytes.length;
        System.arraycopy(bytes,0,hb,0,bytes.length);
    }

    public long getTotalReadOffset(){
        return totalReadOffset.get();
    }
}
