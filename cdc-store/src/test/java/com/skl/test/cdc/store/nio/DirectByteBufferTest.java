package com.skl.test.cdc.store.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

public class DirectByteBufferTest {
    @Test
    public void slice(){
        ByteBuffer firstByteBuffer = ByteBuffer.allocateDirect(1000);
        System.out.println("firstByteBuffer1:"+firstByteBuffer);
        ByteBuffer byteBuffer =firstByteBuffer.slice();
        System.out.println("     byteBuffer2:"+byteBuffer);
        byteBuffer.put("abc".getBytes());
        System.out.println("firstByteBuffer3:"+firstByteBuffer);
        System.out.println("     byteBuffer4:"+byteBuffer);
    }

    @Test
    public void putAndGet(){
        ByteBuffer firstByteBuffer = ByteBuffer.allocateDirect(1000);
        ByteBuffer.allocate(1024);
        System.out.println("firstByteBuffer:"+firstByteBuffer);
        ByteBuffer writeByteBuffer =firstByteBuffer.slice();
        byte[] bytes ="abc".getBytes();
        //write
        System.out.println("writeByteBuffer:"+writeByteBuffer);
        writeByteBuffer.putInt(bytes.length);
        writeByteBuffer.put(bytes);
        System.out.println("writeByteBuffer:"+writeByteBuffer);
        System.out.println("firstByteBuffer:"+firstByteBuffer);
        //read
        ByteBuffer readByteBuffer = firstByteBuffer.slice();
        System.out.println(readByteBuffer);
        int len =readByteBuffer.getInt();
        byte[] readBytes = new byte[len];
        readByteBuffer.get(readBytes);
        System.out.println("len="+len+"     readBytes:"+new String(readBytes));
    }

    @Test
    public void putAndGet_v2(){
        ByteBuffer firstByteBuffer = ByteBuffer.allocateDirect(1000);
        System.out.println("firstByteBuffer:"+firstByteBuffer);
        ByteBuffer writeByteBuffer =firstByteBuffer.slice();
        byte[] bytes ="abc".getBytes();
        //write
        System.out.println("writeByteBuffer:"+writeByteBuffer);
        writeByteBuffer.putInt(bytes.length);
        writeByteBuffer.put(bytes);
        System.out.println("writeByteBuffer:"+writeByteBuffer);
        System.out.println("firstByteBuffer:"+firstByteBuffer);
        //read
        ByteBuffer readByteBuffer = firstByteBuffer.slice();
        System.out.println("readByteBuffer="+readByteBuffer);
        readByteBuffer.position(5);
        ByteBuffer newReadByteBuffer = readByteBuffer.slice();
        System.out.println("newReadByteBuffer="+newReadByteBuffer);
        byte[] readBytes = new byte[bytes.length];
        newReadByteBuffer.get(readBytes);
        System.out.println("  readBytes:"+new String(readBytes));
    }

}
