package com.skl.cdc.remoting.zookeeper;

import java.util.List;

public class ZookeeperConfig {
    private int connectionTimeoutMs;
    private int sessionTimeoutMs;
    private int maxWaitTime;
    private String zkAddress;
    private static final String SUBSCRIBE_ROOT="/redis-cdc";
    private String subscribeRoot = SUBSCRIBE_ROOT;
    private List<String> subscribePathList ;

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getSubscribeRoot() {
        return subscribeRoot;
    }

    public void setSubscribeRoot(String subscribeRoot) {
        this.subscribeRoot = subscribeRoot;
    }

    public List<String> getSubscribePathList() {
        return subscribePathList;
    }

    public void setSubscribePathList(List<String> subscribePathList) {
        this.subscribePathList = subscribePathList;
    }


}
