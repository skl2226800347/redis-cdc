package com.skl.cdc.core.util;

public class RedisLzfCompressUtil {

    public static final void deCompress(byte[] inData,int inLen,byte[] outData,int outLen){
        int inIndex =0;
        int outIndex=0;
        do{
            int ctrl = inData[inIndex++]&0xff;
            //int ctrl = Byte.toUnsignedInt(inData[inIndex++]);
            if(ctrl<(1<<5)){
                ctrl++;
               /* do{
                    outData[outIndex++]=inData[inIndex++];
                }while((--ctrl)>0);*/
                System.arraycopy(inData,inIndex,outData,outIndex,ctrl);
                inIndex+=ctrl;
                outIndex+=ctrl;
            }else{
                int len = ctrl>>5;
                if(len == 7){
                    len +=inData[inIndex++]&255;
                }
                len+=2;

                ctrl =-((ctrl & 0x1f) << 8) -1;
                ctrl-=inData[inIndex++]&0xff;

                ctrl+=outIndex;
                if(outIndex+len>outLen){
                    throw new ArrayIndexOutOfBoundsException();
                }
                for(int i=0;i<len;i++){
                    outData[outIndex++]=outData[ctrl++];
                }
            }
        }while(inIndex<inLen);
    }
}
