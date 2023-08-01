package com.skl.test.cdc.remoting.zookeeper;
import com.skl.cdc.remoting.zookeeper.ZookeeperClient;
import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;
import com.skl.cdc.remoting.zookeeper.curator.CuratorZookeeperClientBuilder;
import org.junit.Test;
public class CuratorZookeeperClientBuilderTest extends AbstractZookeeperTest{
    @Test
    public void buildClient(){
        ZookeeperConfig config = getZookeeperConfig();
        CuratorZookeeperClientBuilder builder = CuratorZookeeperClientBuilder.createCuratorZookeeperClientBuilder();
        builder.config(config);
        ZookeeperClient zookeeperClient = builder.builderClient();
        boolean exist = zookeeperClient.isExists("/rdc-redis");
        System.out.println(exist);
    }
}
