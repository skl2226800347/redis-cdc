package com.skl.cdc.core.constants;

public class NumberConstants {
    /**
     *    *星号
     */
    public static final byte  ASTERISK_SYMBOL=42;
    /**
     *  $符号
     */
    public static final byte DOLLAR_SYMBOL = 36;
    /**
     * 回车符
     */
    public static final byte R_SYMBOL = 13;

    /**
     * 换行符
     */
    public static final byte N_SYMBOL = 10;

    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';

    public static final byte R_BYTE='\r';
    public static final byte N_BYTE='\n';



    public static final byte BYTE_SIEX= -6;

    public static final byte BYTE_FF=-1;
    public static final byte BYTE_FE=-2;
    public static final byte BYTE_FD=-3;
    public static final byte BYTE_FC=-4;
    public static final byte BYTE_FB=-5;
    public static final byte BYTE_FA=-6;


    public static final byte BYTE_DEFAULT_ERROR=-11;


    public static final int ZERO=0;

    //1100 0000
    public static final int NUMBER_LOW_BITS_ZERO =0x00;
    //1100 0001
    public static final int NUMBER_LOW_BITS_ONE =0x01;
    //1100 0010
    public static final int NUMBER_LOW_BITS_TWO =0x02;
    //1100 0011
    public static final int NUMBER_LOW_BITS_THREE =0x03;


    public static final int PARSE_FILE_START_MAX_NUM=10;
}
