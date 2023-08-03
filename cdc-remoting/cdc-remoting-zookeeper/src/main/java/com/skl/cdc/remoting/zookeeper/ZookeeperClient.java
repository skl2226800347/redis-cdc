package com.skl.cdc.remoting.zookeeper;

public interface ZookeeperClient  extends ZookeeperService{
    boolean isClose();
    boolean isExists(String path);
    void create(String path, boolean ephemeral);

    byte[] getData(String path);
    void close();
}
