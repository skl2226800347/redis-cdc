package com.skl.cdc.core.util;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.support.HeaderType;
import com.skl.cdc.core.support.PsyncContinueResponse;
import com.skl.cdc.store.PsyncResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author skl
 */
public class RdbParseUtil {
    public static final PsyncResponse parsePsyncResponse(byte[] src){
        if(src == null || src.length<40){
            return  null;
        }
        int readLength = src.length;
        String content = new String(src);
        String[] array =content.split("\n");
        int srcPos =0;
        boolean isOk=false;
        boolean isFullResync=false;
        boolean isBeforeRdb=false;
        PsyncResponse response= null;
        for(String line : array){
            if((!isOk) && line.startsWith("+OK")){
                isOk=true;
            }else if((!isFullResync) && line.startsWith("+FULLRESYNC")){
                if(response == null) {
                    response = getPsyncResponse(line);
                }
                isFullResync=true;
            }else if((!isBeforeRdb) &&line.startsWith("$")){
                if(response == null) {
                    response = new PsyncResponse();
                }
                isBeforeRdb = true;
            }
            if(line.startsWith("REDIS")){
                break;
            }else{
                srcPos+=line.getBytes().length+1;
            }
        }
        System.out.println("srcPos="+srcPos);
        int remain = readLength-srcPos;
        if(remain<=5){
            return response;
        }
        byte[] dest = new byte[remain];
        System.arraycopy(src,srcPos,dest,0,remain);
        byte[] magicBytes =new byte[5];
        System.arraycopy(dest,0,magicBytes,0,5);
        String maic = new String(magicBytes);
        if(maic.startsWith("REDIS") && (isFullResync || isBeforeRdb)){
            response.setRdbStartHb(dest);
            return response;
        }
        return response;
    }

    public static final PsyncContinueResponse parsePsyncContinueResponse(byte[] src){
        String content = new String(src);
        String[] array =content.split("\n");
        if(array == null || array.length<= NumberConstants.ZERO){
            return null;
        }
        int loopCount=2;
        boolean isOk=false;
        boolean isContinue = false;
        AtomicInteger offset = new AtomicInteger(0);
        for(int i=0;i<array.length;i++){
            if(i>=loopCount){
                break;
            }
            String line = array[i];
            if(line == null){
                continue;
            }
            if(line.startsWith("+OK")){
                isOk = true;
            }
            if(line.startsWith("+CONTINUE")){
                isContinue=true;
                offset.addAndGet(line.length()+2);
                break;
            }else{
                offset.addAndGet(line.length()+1);
            }
        }

        if(isContinue){
            PsyncContinueResponse psyncContinueResponse = new PsyncContinueResponse();
            psyncContinueResponse.setContinue(true);
            int remain = src.length-offset.get();
            if(remain>NumberConstants.ZERO){
                byte[] desc = new byte[remain];
                System.arraycopy(src,offset.get(),desc,0,remain);
                psyncContinueResponse.setHb(desc);
                return psyncContinueResponse;
            }else{
                //throw new RuntimeException(" 数组长度:"+src.length+"  offset:"+offset.get()+"  remain:"+remain+"   src:"+new String(src));
                System.out.println(" 数组长度:"+src.length+"  offset:"+offset.get()+"  remain:"+remain+"   src:"+new String(src));
            }
        }

        return null;
    }



    public static final PsyncResponse getPsyncResponse(String str){
        String[] array =str.split("\\s+");
        if(array == null || array.length<3){
            return null;
        }
        if(HeaderType.getInstance(array[0]) == null){
            return null;
        }
        PsyncResponse header = new PsyncResponse();
        header.setType(array[0]);
        header.setRundId(array[1]);
        header.setFirstOffset(Long.parseLong(array[2]));
        return header;
    }


    @Deprecated
    public static final void parseBody(byte[] bytes)throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        byte[] bytes1 = new byte[5];
        bais.read(bytes1);
        System.out.println(new String(bytes1));

        byte[] bytes2 = new byte[4];
        bais.read(bytes2);
        System.out.println(new String(bytes2));

        byte[] bytes3 = new byte[1];
        bais.read(bytes3);
        System.out.println(new String(bytes3,"GBK"));
    }
}
