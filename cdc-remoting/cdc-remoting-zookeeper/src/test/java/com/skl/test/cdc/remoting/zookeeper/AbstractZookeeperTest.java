package com.skl.test.cdc.remoting.zookeeper;

import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;
import com.skl.cdc.remoting.zookeeper.curator.CuratorZookeeperConfig;

public class AbstractZookeeperTest {
    public static ZookeeperConfig getZookeeperConfig(){
        CuratorZookeeperConfig config = new CuratorZookeeperConfig();
        config.setZkAddress("127.0.0.1:2181");
        config.setConnectionTimeoutMs(10000);
        config.setSessionTimeoutMs(10000);
        config.setSleepMsBetweenRetries(100000);
        config.setMaxWaitTime(10000);
        return config;
    }
}
