package com.skl.cdc.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MappedFile {
    private String path;
    private RandomAccessFile file;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;
    private long fileSize = 1024*1024*1;
    private AtomicInteger writeOffset = null;
    private AtomicInteger readOffset = null;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MappedFile(String path){
        this.path = path;
        try {
            this.file = new RandomAccessFile(this.path, "rw");
            fileChannel =  file.getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
            writeOffset= new AtomicInteger(0);
            readOffset = new AtomicInteger(0);
        }catch (FileNotFoundException e){
            throw new RuntimeException(e.getMessage());
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void doAppend(byte[] data){
        readWriteLock.writeLock().tryLock();
        try{
            int currentPostion=writeOffset.get();
            ByteBuffer byteBuffer = mappedByteBuffer.slice();
            byteBuffer.position(currentPostion);
            byteBuffer.put(data);
            writeOffset.addAndGet(data.length);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public byte[] select(int pos){
        readWriteLock.readLock().tryLock();
        try{
            ByteBuffer byteBuffer = mappedByteBuffer.slice();
            byteBuffer.position(pos);
            ByteBuffer newByteBuffer = byteBuffer.slice();
            int totalData = newByteBuffer.getInt();
            if(totalData<=0){
                return null;
            }
            byte[] bytes = new byte[totalData];
            newByteBuffer.get(bytes);
            return bytes;
        }finally {
            readWriteLock.readLock().unlock();
        }
    }
    public void close(){
        try {
            mappedByteBuffer.force();
            fileChannel.close();
            file.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
