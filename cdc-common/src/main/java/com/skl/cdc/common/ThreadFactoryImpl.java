package com.skl.cdc.common;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryImpl implements ThreadFactory {
    private AtomicInteger index = new AtomicInteger(0);
    private String prefix;

    public ThreadFactoryImpl(String prefix){
        this.prefix = prefix;
    }
    @Override
    public Thread newThread(Runnable r) {
        index.incrementAndGet();
        StringBuilder name = new StringBuilder(prefix).append("-");
        name.append(index.incrementAndGet());
        return new Thread(r,name.toString());
    }
}
