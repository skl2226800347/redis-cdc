package com.skl.cdc.core.enums;
/**
 * 高位bit
 * @author skl
 */
public enum HighBitType {
    NUMBER_HIGHT_BITS_ZERO(0x00,"0"),
    NUMBER_HIGHT_BITS_ONE(0x01,"1"),
    NUMBER_HIGHT_BITS_TWO(0x02,"2"),
    NUMBER_HIGHT_BITS_THREE(0x03,"3")
    ;
    private int type;
    private String desc;

    HighBitType(int type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
