package com.skl.test.cdc.core;
import com.skl.cdc.core.RedisSyncClient;
import com.skl.cdc.core.SyncClientConfig;
import com.skl.cdc.core.event.deserialize.Deserialize;
import com.skl.cdc.core.listener.DataListener;
import com.skl.cdc.store.PsyncStore;

public class RedisSyncClientV2Test extends AbstractTest{

    public static void main(String[]args)throws Exception{
        PsyncStore psyncStore = new PsyncStore("D:/tmp/psync.log");
        SyncClientConfig syncClientConfig = new SyncClientConfig(ip,port,null,password);

        RedisSyncClient redisSocketClient = new RedisSyncClient(syncClientConfig);
        redisSocketClient.setPsyncStore(psyncStore);
        redisSocketClient.setRdbStorePath("d:/tmp/redis.rdb");
        redisSocketClient.registerDataListener(new DataListener() {
            @Override
            public void onChanage(Deserialize deserialize) {
                //System.out.println(JSONObject.toJSONString(deserialize));
            }
        });
        redisSocketClient.connect();
    }
}
