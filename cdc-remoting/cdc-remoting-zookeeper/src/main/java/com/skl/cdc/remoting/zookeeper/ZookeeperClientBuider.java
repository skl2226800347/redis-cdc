package com.skl.cdc.remoting.zookeeper;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class ZookeeperClientBuider{
    protected ZookeeperConfig config;

    protected ConcurrentMap<String, ZookeeperClient> zookeeperClientMap = new ConcurrentHashMap<>();

    public ZookeeperClient builderClient() {
        Objects.requireNonNull(this.getConfig(),"config  not null");
        Objects.requireNonNull(this.getConfig().getZkAddress(),"address param not null");
        String zkAddress = getConfig().getZkAddress();
        ZookeeperClient zookeeperClient = fetchZookeeperClientAndUpdateCache(zkAddress);
        if (zookeeperClient != null){
            return zookeeperClient;
        }
        synchronized (this){
            zookeeperClient = fetchZookeeperClientAndUpdateCache(zkAddress);
            if (zookeeperClient != null){
                return zookeeperClient;
            }
            zookeeperClient = createZookeeperClient(config);
            writeToCache(zkAddress,zookeeperClient);
        }
        return zookeeperClient;
    }


    public ZookeeperConfig getConfig() {
        if(this.config == null){
            this.config = new ZookeeperConfig();
        }
        return this.config;
    }


    private ZookeeperClient fetchZookeeperClientAndUpdateCache(String address){
        ZookeeperClient zookeeperClient = zookeeperClientMap.get(address);
        if(zookeeperClient != null && !zookeeperClient.isClose()){
            writeToCache(address,zookeeperClient);
            return zookeeperClient;
        }
        return null;
    }
    private void writeToCache(String address ,ZookeeperClient zookeeperClient){
        zookeeperClientMap.put(address,zookeeperClient);
    }
    protected abstract ZookeeperClient createZookeeperClient(ZookeeperConfig config);


    public List<String> subscribePaths() {
        ZookeeperConfig zookeeperConfig =  (ZookeeperConfig)getConfig();
        return zookeeperConfig.getSubscribePathList();
    }
}
