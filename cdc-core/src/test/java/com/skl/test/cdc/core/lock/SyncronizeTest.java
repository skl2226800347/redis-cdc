package com.skl.test.cdc.core.lock;

import org.junit.Test;
/**
 * synchronized 锁可以重入
 */
public class SyncronizeTest {
    @Test
    public void locks() throws InterruptedException{
        Object object = new Object();
        synchronized (object){
            synchronized (object){
                System.out.println("当前线程:"+Thread.currentThread().getName()+"  获取锁");
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object){
                    System.out.println("当前线程:"+Thread.currentThread().getName());
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
    }
}
