package com.skl.test.cdc.remoting.zookeeper;

import com.skl.cdc.remoting.zookeeper.ZookeeperClient;
import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;
import com.skl.cdc.remoting.zookeeper.curator.CuratorZookeeperClientBuilder;
import com.skl.cdc.remoting.zookeeper.param.PublishParam;

public class CuratorZookeeperClientCreateEphemeralTest extends AbstractZookeeperTest{
    public static void main(String[]args) throws Exception{
        ZookeeperConfig config = AbstractZookeeperTest.getZookeeperConfig();
        CuratorZookeeperClientBuilder builder = CuratorZookeeperClientBuilder.createCuratorZookeeperClientBuilder();
        builder.config(config);
        ZookeeperClient zookeeperClient = builder.builderClient();
        String zkPath="/redis-cdc/ephemeral/05";
        PublishParam publishParam = new PublishParam();
        publishParam.setValue("nihao");
        zookeeperClient.publish(zkPath,publishParam);
        System.out.println("创建"+zkPath+"  完成了");
        byte[] bytes =zookeeperClient.getData(zkPath);
        System.out.println(new String(bytes));
        Thread.sleep(800000000);

    }
}
