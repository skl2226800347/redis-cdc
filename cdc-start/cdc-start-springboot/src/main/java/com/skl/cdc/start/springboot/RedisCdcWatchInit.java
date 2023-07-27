package com.skl.cdc.start.springboot;

import com.skl.cdc.core.RedisSyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class RedisCdcWatchInit {
    @Autowired
    private ApplicationContext applicationContext;
    @PostConstruct
    public void init(){
        System.out.println("..............init");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               start();
            }
        },"Redis-Slave-Thread");
        thread.setDaemon(true);
        thread.start();
    }
    private void start(){
        RedisSyncClient redisSyncClient = null;
        try {
            redisSyncClient = applicationContext.getBean(RedisSyncClient.class);
            if(redisSyncClient == null){
                return;
            }
            redisSyncClient.connect();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(redisSyncClient != null){
                try {
                    redisSyncClient.close();
                }catch (IOException e){
                    e.getMessage();
                }
            }
        }
    }
}
