package com.skl.cdc.core.support;

public class PsyncContinueResponse {
    private boolean isContinue;
    private byte[] hb;

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public byte[] getHb() {
        return hb;
    }

    public void setHb(byte[] hb) {
        this.hb = hb;
    }
}
