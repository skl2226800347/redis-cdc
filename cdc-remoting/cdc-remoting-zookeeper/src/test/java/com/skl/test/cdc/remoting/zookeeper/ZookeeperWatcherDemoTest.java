package com.skl.test.cdc.remoting.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZookeeperWatcherDemoTest {
    public static void main(String[]args)throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 40000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(Watcher.Event.KeeperState.SyncConnected == event.getState()){
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        System.out.println("zookeeper链接成功");
        String zkPath="/redis";

       /* Stat temp = zooKeeper.exists(zkPath,false);
        System.out.println(temp);*/
        //创建
        String result =zooKeeper.create(zkPath,"2".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        System.out.println(result);
       // zooKeeper.exists(zkPath,true);
        final Stat stat =zooKeeper.exists(zkPath, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                StringBuilder info = new StringBuilder();
                info.append("[process] 当前线程:").append(Thread.currentThread().getName()).append("  ").append(event.getPath())
                        .append(" ").append(event.getState());
                info.append("  :"+JSONObject.toJSON(event));
                System.out.println(info.toString());
                try {
                    zooKeeper.exists(event.getPath(), true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("stat:"+stat);
        //触发Watcher
        Stat stat2 =zooKeeper.setData(zkPath,"hello world".getBytes(),stat.getVersion());
        stat2 =zooKeeper.setData(zkPath,"hello".getBytes(),stat2.getVersion());

        Thread.sleep(10000);
        zooKeeper.delete(zkPath,stat2.getVersion());
        zooKeeper.close();
        System.out.println("删除完成。。。。");
    }
}
