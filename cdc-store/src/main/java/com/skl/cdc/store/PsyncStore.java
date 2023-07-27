package com.skl.cdc.store;


import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * psync同步的头数数据存储到磁盘中
 * @author skl
 */
public class PsyncStore {
    private String path;
    private MappedFile mappedFile;
    public PsyncStore(String path){
        Objects.requireNonNull(path == null,"path cannot be null");
        this.path = path;
        this.mappedFile = new MappedFile(path);
    }
    public PsyncResponse getPysncResponse(){
        byte[] data = mappedFile.select(0);
        if(data == null ||data.length<=0){
            return null;
        }
        PsyncResponse response = new PsyncResponse();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        //runId长度
        int runIdLen = byteBuffer.getInt();
        byte[] runIdBytes = new byte[runIdLen];
        byteBuffer.get(runIdBytes);
        //runId
        String runId = new String(runIdBytes);
        //firstOffset
        long firstOffset = byteBuffer.getLong();
        //offset
        long offset = byteBuffer.getLong();
        //createTimeStamp
        long createTimeStamp = byteBuffer.getLong();
        //updateTimeStamp
        long updateTimeStamp = byteBuffer.getLong();

        //setter
        response.setRundId(runId);
        response.setFirstOffset(firstOffset);
        response.setOffset(offset);
        response.setCreateTimeStamp(createTimeStamp);
        response.setUpdateTimeStamp(updateTimeStamp);
        return response;
    }
    public void putPysncResponse(PsyncResponse redisPsyncResponse){
        int totalSize= 4   //总长度
                +4    //runId长度
                +redisPsyncResponse.getRundId().length()  // runId
                +8   //firstOffset
                +8   //offset
                +8   //createTimestap
                +8;   //updateTimestap
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
        //总长度
        byteBuffer.putInt(totalSize);
        //runId长度
        byteBuffer.putInt(redisPsyncResponse.getRundId().length());
        //runId内容
        byteBuffer.put(redisPsyncResponse.getRundId().getBytes());
        //firstOffset
        byteBuffer.putLong(redisPsyncResponse.getFirstOffset());
        //offset
        byteBuffer.putLong(redisPsyncResponse.getOffset());
        //createtimestamp
        byteBuffer.putLong(redisPsyncResponse.getCreateTimeStamp());
        //updatetimestamp
        byteBuffer.putLong(redisPsyncResponse.getUpdateTimeStamp());
        mappedFile.doAppend(byteBuffer.array());
    }

    public void close(){
        mappedFile.close();
    }
}
