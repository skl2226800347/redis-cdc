package com.skl.cdc.remoting.zookeeper.curator;

import com.skl.cdc.remoting.zookeeper.ZookeeperClient;
import com.skl.cdc.remoting.zookeeper.ZookeeperClientBuider;
import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;

public class CuratorZookeeperClientBuilder extends ZookeeperClientBuider {
    public static final CuratorZookeeperClientBuilder createCuratorZookeeperClientBuilder(){
        CuratorZookeeperClientBuilder curatorZookeeperClientBuilder = new CuratorZookeeperClientBuilder();
        return curatorZookeeperClientBuilder;
    }
    @Override
    protected ZookeeperClient createZookeeperClient(ZookeeperConfig config) {
        return new CuratorZookeeperClient((CuratorZookeeperConfig)config);
    }

    public CuratorZookeeperClientBuilder config(ZookeeperConfig config){
        this.config = config;
        return this;
    }

    @Override
    public CuratorZookeeperConfig getConfig() {
        if(this.config == null){
            this.config = new CuratorZookeeperConfig();
        }
        return (CuratorZookeeperConfig)config;
    }

}
