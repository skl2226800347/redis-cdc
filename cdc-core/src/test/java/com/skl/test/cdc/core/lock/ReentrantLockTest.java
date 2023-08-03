package com.skl.test.cdc.core.lock;

import org.junit.Test;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * ReetrantLock锁，可以重入，但是lock有计数功能，有几次lock，就要有几次unlock。
 */
public class ReentrantLockTest {
    @Test
    public void lock()throws InterruptedException{
        Lock lock  = new ReentrantLock();
        lock.lock();
        lock.lock();
        System.out.println("当前线程:"+Thread.currentThread().getName()+"  acquire lock");
        lock.unlock();
        lock.unlock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("当前线程:"+Thread.currentThread().getName()+"   开始");
                lock.lock();
                System.out.println("当前线程:"+Thread.currentThread().getName()+"   获取锁");
                lock.unlock();
                System.out.println("当前线程:"+Thread.currentThread().getName()+"   释放锁");
            }
        });
        thread.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
