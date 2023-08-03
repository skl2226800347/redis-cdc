package com.skl.test.cdc.remoting.zookeeper;

import com.skl.cdc.common.util.DateUtil;
import com.skl.cdc.remoting.zookeeper.ZookeeperClient;
import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;
import com.skl.cdc.remoting.zookeeper.curator.CuratorZookeeperClientBuilder;
import com.skl.cdc.remoting.zookeeper.event.DataEvent;
import com.skl.cdc.remoting.zookeeper.listener.DataListener;

import java.util.Date;

public class CuratorZookeeperClientSubscribeTest {
    public static final void main(String[] args)throws Exception{
        ZookeeperConfig config = AbstractZookeeperTest.getZookeeperConfig();
        CuratorZookeeperClientBuilder builder = CuratorZookeeperClientBuilder.createCuratorZookeeperClientBuilder();
        builder.config(config);
        ZookeeperClient zookeeperClient = builder.builderClient();
        String zkPath="/redis-cdc/ephemeral/05";
        zookeeperClient.subscribe(zkPath, new DataListener() {
            @Override
            public void dataEvent(DataEvent dataEvent) {
                System.out.println("当前线程:"+Thread.currentThread().getName()+"  时间:"+DateUtil.dateStr(new Date(),DateUtil.DEFAULT_FORMAT) +"   path="+dataEvent.getPath()+"  data:" +new String(dataEvent.getData()));
            }
        });
        Thread.sleep(800000000);
    }
}
