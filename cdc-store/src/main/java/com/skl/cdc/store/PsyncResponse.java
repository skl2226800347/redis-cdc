package com.skl.cdc.store;

public class PsyncResponse {
    private String type;
    private String rundId;
    private long firstOffset;
    private long offset;
    private long createTimeStamp;
    private long updateTimeStamp;
    private byte[]rdbStartHb;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRundId() {
        return rundId;
    }

    public void setRundId(String rundId) {
        this.rundId = rundId;
    }

    public long getFirstOffset() {
        return firstOffset;
    }

    public void setFirstOffset(long firstOffset) {
        this.firstOffset = firstOffset;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public long getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public byte[] getRdbStartHb() {
        return rdbStartHb;
    }

    public void setRdbStartHb(byte[] rdbStartHb) {
        this.rdbStartHb = rdbStartHb;
    }
}
