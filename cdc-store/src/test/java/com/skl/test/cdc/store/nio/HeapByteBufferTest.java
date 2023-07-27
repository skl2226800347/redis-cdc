package com.skl.test.cdc.store.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

public class HeapByteBufferTest {
    @Test
    public void putAndGet(){
        ByteBuffer firstByteBuffer = ByteBuffer.allocate(1024);
        System.out.println("firstByteBuffer114:"+firstByteBuffer);
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
}
