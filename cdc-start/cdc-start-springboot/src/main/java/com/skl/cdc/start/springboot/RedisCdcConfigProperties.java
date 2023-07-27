package com.skl.cdc.start.springboot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.io.Serializable;
@ConfigurationProperties("redis.cdc")
public class RedisCdcConfigProperties implements Serializable {
    private String ip;
    private int port;
    private String username;
    private String password;
    private boolean realParseRDB;
    private String psyncStorePath;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRealParseRDB() {
        return realParseRDB;
    }

    public void setRealParseRDB(boolean realParseRDB) {
        this.realParseRDB = realParseRDB;
    }

    public String getPsyncStorePath() {
        return psyncStorePath;
    }

    public void setPsyncStorePath(String psyncStorePath) {
        this.psyncStorePath = psyncStorePath;
    }
}
