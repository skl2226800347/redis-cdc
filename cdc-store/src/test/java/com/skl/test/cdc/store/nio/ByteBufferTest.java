package com.skl.test.cdc.store.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

public class ByteBufferTest {
    @Test
    public void wrap(){
        ByteBuffer byteBuffer = ByteBuffer.wrap("ddddd".getBytes());
        System.out.println(byteBuffer);
    }


}
