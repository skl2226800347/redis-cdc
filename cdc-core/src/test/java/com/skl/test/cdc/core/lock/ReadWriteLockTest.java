package com.skl.test.cdc.core.lock;

import org.junit.Test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * ReetrantReadWriteLock可重入。
 */
public class ReadWriteLockTest {
    @Test
    public void lock()throws InterruptedException{
        ReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.WriteLock writeLock = ((ReentrantReadWriteLock) lock).writeLock();

        writeLock.lock();
        writeLock.lock();
        System.out.println("当前线程:"+Thread.currentThread().getName());
        writeLock.unlock();
        writeLock.unlock();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeLock.lock();
                System.out.println("当前线程:"+Thread.currentThread().getName());
                writeLock.unlock();
            }
        });
        thread.start();
        Thread.sleep(100000);
    }
}
