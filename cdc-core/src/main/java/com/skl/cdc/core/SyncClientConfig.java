package com.skl.cdc.core;

public class SyncClientConfig {
    String ip;
    int port;
    String username;
    String password;

    public SyncClientConfig(){

    }
    public SyncClientConfig(String ip,int port,String username,String password){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }
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
}
