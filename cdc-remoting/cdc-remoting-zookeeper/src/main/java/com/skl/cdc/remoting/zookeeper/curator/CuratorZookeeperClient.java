package com.skl.cdc.remoting.zookeeper.curator;

import com.skl.cdc.common.exception.RemotingException;
import com.skl.cdc.remoting.zookeeper.AbstractZookeeperClient;
import com.skl.cdc.remoting.zookeeper.event.DataEvent;
import com.skl.cdc.remoting.zookeeper.listener.DataListener;
import com.skl.cdc.remoting.zookeeper.param.PublishParam;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class CuratorZookeeperClient extends AbstractZookeeperClient {
    private static final Logger logger = LoggerFactory.getLogger(CuratorZookeeperClient.class);
    private CuratorFramework client;



    private List<TreeCache> treeCacheList = new ArrayList<>();

    public CuratorZookeeperClient(CuratorZookeeperConfig config){
        super(config);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        client = builder.retryPolicy(new RetryNTimes(1,config.getSleepMsBetweenRetries())).sessionTimeoutMs(config.getSessionTimeoutMs())
                .connectionTimeoutMs(config.getConnectionTimeoutMs())
                .connectString(config.getZkAddress()).build();
        client.start();
        boolean isConnected;
        try {
            isConnected = client.blockUntilConnected(config.getMaxWaitTime(), TimeUnit.MILLISECONDS);
        }catch (InterruptedException e){
            throw new RemotingException(e.getMessage());
        }
        if(!isConnected){
            throw new RemotingException("connect zk is failure");
        }
    }

    @Override
    protected void doPublish(String path, PublishParam remotingParam) {
        try {
            client.setData().forPath(path, remotingParam.toBytes());
        }catch (Exception e){
            logger.warn("doPublish",e);
        }
    }

    @Override
    protected void doSubscribe(String path, DataListener dataListener, Executor executor) {
        synchronized (this) {
            if(isSubscribe(path)){
                return;
            }
            TreeCacheListener treeCacheListener = new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    if(treeCacheEvent == null || treeCacheEvent.getData() == null || treeCacheEvent.getData().getData() == null){
                        return;
                    }
                    DataEvent dataEvent = new DataEvent();
                    dataEvent.setData(treeCacheEvent.getData().getData());
                    dataEvent.setPath(treeCacheEvent.getData().getPath());
                    dataListener.dataEvent(dataEvent);
                }
            };
            TreeCache treeCache = new TreeCache(client,path);
            treeCache.getListenable().addListener(treeCacheListener);
            try {
                treeCache.start();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            treeCacheList.add(treeCache);
            subscribes.add(path);
        }
    }


    @Override
    public boolean isExists(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void createEphemeral(String path) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        }catch(KeeperException.NodeExistsException e){
            logger.warn("createEphemeral path:{} is already exists e:{}",path,e);
        }catch (Exception e){
            logger.warn("createEphemeral e:{}",e);
        }
    }

    @Override
    protected void createPersistent(String path) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }catch (KeeperException.NodeExistsException e){
            logger.warn("createPersistent path:{} is already e:{}",path,e);
        }catch (Exception e){
            logger.warn("createPersistent e:{}",e);
        }
    }

    @Override
    protected void doDelete(String path) {
        try {
            client.delete().forPath(path);
        }catch (Exception e){
            logger.warn("doDelete e:",e);
        }
    }
}
