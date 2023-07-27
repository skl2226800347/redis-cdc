package com.skl.cdc.core.enums;

public enum  DataTypeEnum {
    RDB((byte)1,"RDB"),
    WRITE((byte)2,"WRITE");
    private byte type;
    private String desc;
    DataTypeEnum(byte type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
