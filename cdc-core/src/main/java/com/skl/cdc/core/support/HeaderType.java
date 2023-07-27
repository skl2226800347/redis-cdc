package com.skl.cdc.core.support;

public enum HeaderType {
    FULLRESYNC("+FULLRESYNC")
    ;
    private String type;
    HeaderType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static final HeaderType getInstance(String type){
        for(HeaderType headerType : values()){
            if(headerType.getType().equals(type)){
                return headerType;
            }
        }
        return null;
    }
}
