package com.skl.cdc.common.exception;

public class RemotingException extends RuntimeException{
    private String msg;
    public RemotingException(String msg){
        super(msg);
        this.msg = msg;
    }
}
