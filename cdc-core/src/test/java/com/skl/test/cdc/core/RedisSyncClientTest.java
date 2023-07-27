package com.skl.test.cdc.core;
import com.skl.cdc.core.RedisSyncClient;
import com.skl.cdc.core.SyncClientConfig;
import com.skl.cdc.store.PsyncStore;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

public class RedisSyncClientTest extends AbstractTest{

    @Before
    public void init(){
        File file = new File("D:/tmp/psync.log");
        file.delete();
    }

    @Test
    public void connect() throws Exception{
        PsyncStore psyncStore = new PsyncStore("D:/tmp/psync.log");
        SyncClientConfig syncClientConfig = new SyncClientConfig(ip,port,null,password);
        RedisSyncClient redisSocketClient = new RedisSyncClient(syncClientConfig);
        redisSocketClient.setPsyncStore(psyncStore);
        redisSocketClient.setRealParseRDB(true);
        /*redisSocketClient.registerDataListener(new DataListener() {
            @Override
            public void onChanage(Deserialize deserialize) {
                System.out.println("onChange ---->"+JSONObject.toJSONString(deserialize));
            }
        });*/
        redisSocketClient.connect();
    }
}
