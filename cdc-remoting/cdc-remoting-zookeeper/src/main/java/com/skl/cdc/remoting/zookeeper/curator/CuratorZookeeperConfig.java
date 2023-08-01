package com.skl.cdc.remoting.zookeeper.curator;

import com.skl.cdc.remoting.zookeeper.ZookeeperConfig;

public class CuratorZookeeperConfig extends ZookeeperConfig {
    private static final int DEFAULT_SLEEP_MS_BETWEEN_RETRIES=10000;
    private int sleepMsBetweenRetries =DEFAULT_SLEEP_MS_BETWEEN_RETRIES;

    public int getSleepMsBetweenRetries() {
        return sleepMsBetweenRetries;
    }

    public void setSleepMsBetweenRetries(int sleepMsBetweenRetries) {
        this.sleepMsBetweenRetries = sleepMsBetweenRetries;
    }
}
