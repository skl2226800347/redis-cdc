package com.skl.test.cdc.store;
import com.alibaba.fastjson.JSONObject;
import com.skl.cdc.store.PsyncResponse;
import com.skl.cdc.store.PsyncStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PsyncStoreTest {
    private PsyncStore psyncStore;
    @Before
    public void init(){
        String path ="D:/tmp/psync.log";
        psyncStore = new PsyncStore(path);
    }

    @Test
    public void putPysncResponse(){
        PsyncResponse pysncResponse = new PsyncResponse();
        pysncResponse.setOffset(333333);
        pysncResponse.setRundId("DDR333DDDDDDDDDDDD");
        psyncStore.putPysncResponse(pysncResponse);
    }
    @Test
    public void getPysncResponse(){
        PsyncResponse pysncResponse =psyncStore.getPysncResponse();
        System.out.println(JSONObject.toJSONString(pysncResponse));
    }

    @Test
    public void putAndGetPysncResponse(){
        PsyncResponse writePysncResponse = new PsyncResponse();
        writePysncResponse.setRundId("run_id00001");
        writePysncResponse.setFirstOffset(1111);
        writePysncResponse.setOffset(33333333);
        writePysncResponse.setCreateTimeStamp(System.currentTimeMillis());
        writePysncResponse.setUpdateTimeStamp(System.currentTimeMillis()+1);
        System.out.println("writePysncResponse:"+JSONObject.toJSONString(writePysncResponse));
        psyncStore.putPysncResponse(writePysncResponse);
        PsyncResponse getRedisPsyncResponse =psyncStore.getPysncResponse();
        System.out.println("getRedisPsyncResponse="+JSONObject.toJSONString(getRedisPsyncResponse));
    }

    @After
    public void after(){
        //psyncStore.close();
    }
}
