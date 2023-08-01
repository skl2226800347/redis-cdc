package com.skl.cdc.remoting.zookeeper;

import com.skl.cdc.remoting.zookeeper.listener.DataListener;
import com.skl.cdc.remoting.zookeeper.param.PublishParam;

import java.util.concurrent.Executor;

public interface ZookeeperService {
    void publish(String path,PublishParam publishParam);
    void subscribe(String path, DataListener dataListener);
    void subscribe(String path, DataListener dataListener, Executor executor);
    void delete(String path);
}
