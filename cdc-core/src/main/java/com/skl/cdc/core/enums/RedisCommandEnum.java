package com.skl.cdc.core.enums;

public enum RedisCommandEnum {

    SELECT("SELECT","SELECT"),
    DEL("DEL","DEL"),
    SET("SET","SET"),
    HSET("HSET","HSET"),
    SETNX("SETNX","SETNX"),
    SETEX("SETEX","SETEX"),
    EXPIRE("EXPIRE","EXPIRE"),
    INCRBY("INCRBY","INCRBY"),
    PEXPIRE("PEXPIRE","PEXPIRE"),
    PSETEX("PSETEX","PSETEX"),
    PING("PING","PING")
    ;
    String command;
    String desc;
    RedisCommandEnum(String command, String desc){
        this.command = command;
        this.desc = desc;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static final RedisCommandEnum getInstance(String command){
        if(command == null){
            return null;
        }
        for(RedisCommandEnum redisCommand : values()){
            if(redisCommand.getCommand().equals(command)){
                return redisCommand;
            }
        }
        return null;
    }
}
