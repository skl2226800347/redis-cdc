package com.skl.test.cdc.remoting.zookeeper;

import com.skl.cdc.remoting.zookeeper.ZookeeperClient;
import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;
import com.skl.cdc.remoting.zookeeper.curator.CuratorZookeeperClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorZookeeperClientTest extends AbstractZookeeperTest{
    ZookeeperClient zookeeperClient;
    @Before
    public void init(){
        ZookeeperConfig config = getZookeeperConfig();
        CuratorZookeeperClientBuilder builder = CuratorZookeeperClientBuilder.createCuratorZookeeperClientBuilder();
        builder.config(config);
        zookeeperClient = builder.builderClient();
    }

    @Test
    public void createPersistent(){
        String zkPath="/redis-cdc/persistent/01";
        zookeeperClient.create(zkPath,false);
        boolean exist =zookeeperClient.isExists(zkPath);
        System.out.println(exist);
    }

    @Test
    public void exitst_persistent(){
        String zkPath="/redis-cdc/persistent/01";
        boolean exist = zookeeperClient.isExists(zkPath);
        System.out.println(exist);
    }

    @Test
    public void createEphemeral(){
        String zkPath="/redis-cdc/ephemeral/01";
        zookeeperClient.create(zkPath,true);
        boolean exist =zookeeperClient.isExists(zkPath);
        System.out.println(exist);
    }
    @Test
    public void exitst_ephermeral(){
        String zkPath="/redis-cdc/ephemeral/01";
        boolean exist = zookeeperClient.isExists(zkPath);
        System.out.println(exist);
    }


    @After
    public void close(){
        zookeeperClient.close();
    }
}
