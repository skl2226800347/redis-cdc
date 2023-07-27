package com.skl.test.cdc.core.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamTest {
    @Test
    public void read(){
        byte[] initBytes = "abc".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(initBytes);
        byte[] bytes = new byte[3];
        bais.read(bytes,1,2);
        System.out.println(new String(bytes));
    }
}
