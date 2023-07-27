package com.skl.cdc.core.io;

import com.skl.cdc.core.enums.HighBitType;
import com.skl.cdc.core.util.ByteUtil;
import com.skl.cdc.core.util.RedisLzfCompressUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
public class RedisInputStream extends InputStream {
    private static int BYTE_MAX=255;

    private InputStream inputStream;
    private Integer peek;
    public RedisInputStream(InputStream inputStream){
        this.inputStream = inputStream;
    }


    @Override
    public int read() throws IOException {
        int result;
        if(peek == null){
            result= readWithBlockBoundaries();
        }else{
            result =peek;
            peek=null;
        }
        if(result ==-1){
            //throw new IOException("-1");
        }
        return result;
    }


    public byte[] read(int length) throws IOException{
        try {
            byte[] bytes = new byte[length];
            fill(bytes,0,bytes.length);
            return bytes;
        }catch (Throwable e){
            e.printStackTrace();
        }
        return  null;
    }

    public void fill(byte[] bytes,int offset,int length) throws IOException{
        int remaining = length;
        while(remaining != 0){
            int read = inputStream.read(bytes,offset+length-remaining,remaining);
            if(read ==-1){
                throw new EOFException();
            }
            remaining-=read;
        }
    }

    public int readInt()throws IOException {
        byte value = (byte) read();
        return readInt(value);
    }

    public int readInt(byte byteFlag)throws IOException{
        int highBit = ByteUtil.readHighBits(byteFlag);
        if(HighBitType.NUMBER_HIGHT_BITS_ZERO.getType() == highBit){
            return byteFlag&0xFF;
        }else if(HighBitType.NUMBER_HIGHT_BITS_ONE.getType()==highBit){
            byte[] bytes =read(1);
            int result = (((((byteFlag<<2)&0xff)>>2)&0xff)<<8)|
                    (bytes[0]&0xff);
            return result;
        }else if(HighBitType.NUMBER_HIGHT_BITS_TWO.getType() == highBit){
            byte[] bytes = read(4);
            return ByteUtil.makeInt(bytes[0],bytes[1],bytes[2],bytes[3]);
        }else if(HighBitType.NUMBER_HIGHT_BITS_THREE.getType() == highBit){
            throw new UnsupportedOperationException("特殊编码格式不能直接返回字符串长度");
        }else{
            throw new UnsupportedOperationException("不支持");
        }
    }


    /**
     * 获取int整数，len表示几个字节，最多4个
     * 数组的高位存储整数的高位
     * @param  len 数组数量
     * @return 整数
     */
    public int readInt(int len)throws IOException {
        Objects.requireNonNull(len>4 || len<=0,"len小于0或大于4");
        byte[] bytes = read(len);
        int result =ByteUtil.toInt(bytes);
        return result;
    }

    /**
     * 获取int整数，len表示几个字节，最多4个
     * 数组的高位存储整数的高位
     * @param  len 数组数量
     * @return 整数
     */
    public int readIntByReverse(int len)throws IOException {
        Objects.requireNonNull(len>4 || len<=0,"len小于0或大于4");
        byte[] bytes = read(len);
        int result = ByteUtil.toIntByReverse(bytes);
        return result;
    }



    /**
     * 获取int整数，len表示几个字节，最多4个
     * 数组的高位存储整数的高位
     * @param  len 数组数量
     * @return 整数
     */
    public long readLongByReverse(int len)throws IOException {
        Objects.requireNonNull(len>8 || len<=0,"len小于0或大于8");
        byte[] bytes = read(len);
        long result =ByteUtil.toLongByReverse(bytes);
        return result;
    }

    public long readExpireTimestampByMillisecond()throws IOException{
        byte[] bytes = read(8);
        long expire = ((long)(bytes[7] & 0x00ff) << 56)
                + ((long)(bytes[6] & 0x00ff) << 48) + ((long)(bytes[5] & 0x00ff) << 40)
                + ((long)(bytes[4] & 0x00ff) << 32) + ((long)(bytes[3] & 0x00ff) << 24)
                + ((long)(bytes[2] & 0x00ff) << 16) + ((long)(bytes[1] & 0x00ff) << 8)
                + ((long)(bytes[0] & 0x00ff));
        return expire;
    }

    public long readExpireTimestampBySecond()throws IOException{
        byte[] bytes = read(4);
        int expire = ((bytes[3] & 0x00ff) << 24)
                + ((bytes[2] & 0x00ff) << 16) + ((bytes[1] & 0x00ff) << 8)
                + ((bytes[0] & 0x00ff));
        return Integer.toUnsignedLong(expire);
    }

    public long readLong(int length)throws IOException {
        byte[] bytes = read(length);
        return ByteUtil.makeLong(bytes[0],bytes[1],bytes[2],bytes[3],bytes[4],bytes[5],bytes[6],bytes[7]);
    }

    /**
     * 直接从流中获取指定长度字节，然后转换成字符串
     * @param len 长度
     * @return 字符串
     */
    public String readString(int len)throws IOException{
        return new String(read(len));
    }


    /**
     * 从流中获取字符串(该方法会自动获取字符串长度)
     * @return 字符串
     */
    public String readString()throws IOException{
        //value相关
        byte byteFlag = (byte) read();
        return readString(byteFlag);
    }

    public String readString(byte byteFlag)throws IOException{
        int high = ByteUtil.readHighBits(byteFlag);
        int low =ByteUtil.readLowBits(byteFlag);
        if(high ==0 || high ==1 || high ==2){
            int valueLength = readInt(byteFlag);
            String value = readString(valueLength);
            return value;
        }else if(high==3 && low==0 ){
            Integer value = readIntByReverse(1);
            return value+"";
        }else if(high ==3 && low==1){
            Integer value = readIntByReverse(2);
            return value+"";
        }else if(high == 3 && low ==2){
            Integer value = readIntByReverse(4);
            System.out.println("value:"+value);
            return value+"";
        }else if (high == 3 &&  low == 3) {
            byte[] outData = deCompress();
            String value = new String(outData,"UTF-8");
            return value;
        }else {
            throw new UnsupportedOperationException("not supported");
        }
    }

    public byte[] deCompress()throws IOException{
        int inLen = readInt();
        int outLen = readInt();
        System.out.println("inLen=" + inLen + "    outLen =" + outLen);
        byte[] inData = read(inLen);
        byte[] outData = new byte[outLen];
        RedisLzfCompressUtil.deCompress(inData,inLen,outData,outLen);
        return outData;
    }

    public int readWithBlockBoundaries()throws IOException{
        return inputStream.read();
    }

    public byte[] readRemain()throws IOException{
        if(inputStream instanceof BufferedSocketInputStream){
            return ((BufferedSocketInputStream) inputStream).readRemain();
        }
        throw new UnsupportedOperationException("不支持！");
    }


    public void load(byte[] bytes)throws IOException{
        if(inputStream instanceof BufferedSocketInputStream){
            ((BufferedSocketInputStream) inputStream).load(bytes);
            return;
        }
        throw new UnsupportedOperationException("不支持！");
    }

    public long getTotalReadOffset(){
        if(inputStream instanceof BufferedSocketInputStream){
            return ((BufferedSocketInputStream) inputStream).getTotalReadOffset();
        }else if(inputStream instanceof BufferedFileInputStream ){
            return ((BufferedFileInputStream)inputStream).getTotalReadOffset();
        }
        throw new UnsupportedOperationException("不支持！");
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void close() throws IOException {
        if(inputStream != null){
            inputStream.close();
        }
    }

}
