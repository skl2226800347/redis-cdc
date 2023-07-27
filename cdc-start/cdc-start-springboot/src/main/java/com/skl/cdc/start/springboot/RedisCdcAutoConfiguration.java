package com.skl.cdc.start.springboot;

import com.skl.cdc.core.RedisSyncClient;
import com.skl.cdc.core.SyncClientConfig;
import com.skl.cdc.store.PsyncStore;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@EnableConfigurationProperties(RedisCdcConfigProperties.class)
@Configuration
@Import({RedisCdcWatchInit.class,InitApplicationListener.class})
public class RedisCdcAutoConfiguration {
    @Bean
    public RedisSyncClient redisSyncClient(RedisCdcConfigProperties redisCdcConfigProperties)throws IOException {
        SyncClientConfig config = new SyncClientConfig();
        PsyncStore psyncStore = new PsyncStore(redisCdcConfigProperties.getPsyncStorePath());
        BeanUtils.copyProperties(redisCdcConfigProperties,config);
        RedisSyncClient redisSyncClient = new RedisSyncClient(config);
        redisSyncClient.setRealParseRDB(redisCdcConfigProperties.isRealParseRDB());
        redisSyncClient.setPsyncStore(psyncStore);
        //redisSyncClient.connect();
        return redisSyncClient;
    }
}
