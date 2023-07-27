package com.skl.cdc.core.enums;
/**
 * # 0 =  "String Encoding"
 * # 1 =  "List Encoding"
 * # 2 =  "Set Encoding"
 * # 3 =  "Sorted Set Encoding"
 * # 4 =  "HashDeserialize Encoding"
 * # 9 =  "Zipmap Encoding"
 * # 10 = "Ziplist Encoding"
 * # 11 = "Intset Encoding"
 * # 12 = "Sorted Set in Ziplist Encoding"
 * # 13 = "Hashmap in Ziplist Encoding"
 */

public enum ValueTypeEnum {
    String_Encoding((byte)0,"String Encoding"),
    List_Encoding((byte)1,"List Encoding"),
    Set_Encoding((byte)2,"Set Encoding"),
    Sorted_Set_Encoding((byte)3,"Sorted Set Encoding"),
    Hash_Encoding((byte)4,"HashDeserialize Encoding"),
    Zipmap_Encoding((byte)9,"Zipmap Encoding"),
    Ziplist_Encoding((byte)10,"Ziplist Encoding"),
    Intset_Encoding((byte)11,"Intset Encoding"),
    Sorted_Set_in_Ziplist_Encoding((byte)12,"Sorted Set in Ziplist Encoding"),
    Hashmap_in_Ziplist_Encoding((byte)13,"Hashmap in Ziplist Encoding"),
    Quicklist_Encoding((byte)14,"Quicklist Encoding");

    private byte type;
    private String desc;
    ValueTypeEnum(byte type, String desc){
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

    public static final ValueTypeEnum getInstance(byte type){
        for(ValueTypeEnum valueType : values()){
            if(valueType.type == type){
                return valueType;
            }
        }
        return null;
    }
}
