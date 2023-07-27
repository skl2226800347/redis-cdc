package com.skl.cdc.core;

import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.listener.DataListener;
import com.skl.cdc.core.network.RedisSocket;
import com.skl.cdc.core.parser.RedisFileRdbParser;
import com.skl.cdc.core.parser.RedisRdbParser;
import com.skl.cdc.core.parser.RedisSocketRdbParser;
import com.skl.cdc.core.support.PsyncContinueResponse;
import com.skl.cdc.core.util.RdbParseUtil;
import com.skl.cdc.store.PsyncResponse;
import com.skl.cdc.store.PsyncStore;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
/**
 * redis socket 客户端
 * 参考：http://redis.io/topics/replication
 *       https://github.com/sripathikrishnan/redis-rdb-tools/blob/master/docs/RDB_File_Format.textile
 *       https://zhuanlan.zhihu.com/p/311523487?utm_id=0
 *       https://github.com/dalei2019/redis-study/blob/main/docs/redis-rdb-format.md
 *       Redis设计和实现
 *
 *       https://blog.csdn.net/u012319493/article/details/83653860
 *       https://github.com/huangz1990/redis-3.0-annotated.git
 *       https://github.com/ganghuawang/java-redis-rdb
 *       http://m.tuiyu.com/shouyou/article/129100.html(C语言开发工具)
 *       ZipList参考:https://blog.csdn.net/qq_59776041/article/details/126646673
 *       RDB:https://www.cnblogs.com/510602159-Yano/p/15914839.html
 *       RDB各种类型结构：https://heapdump.cn/article/4709651
 *      java Jedis客户端写不同类型数据:https://blog.csdn.net/w464960660/article/details/108084848
 *      redis RDB文件格式(官网):https://rdb.fnordig.de/file_format.html
 *      @author skl
 */
public class RedisSyncClient {
    private String ip;
    private int port;
    private String username;
    private String password;
    private SyncClientConfig syncClientConfig;
    private RedisSocket socket;
    private boolean isRealParseRDB=false;
    private String rdbStorePath;
    private RedisRdbParser rdbParser;
    private PsyncStore psyncStore;
    private List<DataListener> dataListeners= new LinkedList<DataListener>();
    public RedisSyncClient(SyncClientConfig syncClientConfig){
        Objects.requireNonNull(syncClientConfig,"syncClientConfig cannot be null");
        this.syncClientConfig = syncClientConfig;
        socket = new RedisSocket(syncClientConfig.getIp(),syncClientConfig.getPort());
    }
    public void connect()throws IOException {
        socket.connect();
        if(isRealParseRDB){
            rdbParser = new RedisSocketRdbParser(this,socket.getInputStream());
        }else{
            rdbParser = new RedisFileRdbParser(this.rdbStorePath);
        }
        //auth
        auth();
        //从Master同步数据
        psyncForMulti();
    }

    public void reConnect()throws IOException {
        this.socket.close();
        this.socket = new RedisSocket(ip,port);
        this.socket.connect();
        if(isRealParseRDB){
            rdbParser.setInputStream(socket.getInputStream());
        }
        //auth
        auth();
    }
    public void psyncForMulti()throws IOException{
        boolean executeFlag;
        boolean isNeedAuth=true;
        do{
            executeFlag =psync(isNeedAuth);
            try{
                System.out.println("执行一次又一次");
                Thread.sleep(1000);
                reConnect();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            isNeedAuth=true;
            System.out.println("excuteFlag="+executeFlag);
        }while(true);
    }
    public boolean psync(boolean isNeedAuth)throws IOException{
        StringBuilder command= new StringBuilder();
        PsyncResponse prePsyncResonse =psyncStore.getPysncResponse();
        if(isIncrementPsync(prePsyncResonse)){
            long offset =prePsyncResonse.getFirstOffset();
            command.append("psync ").append(prePsyncResonse.getRundId()).append(" ").append(offset);
        }else{
            command.append("psync ? -1");
        }
        //command.append("psync ? -1");
        return doPsync(command.toString(),prePsyncResonse,isNeedAuth);
    }


    public boolean doPsync(String command, PsyncResponse redisPsyncResponse,boolean isNeedAuth)throws IOException{
        if(isNeedAuth) {
            socket.write(command.getBytes());
            socket.writeCtrl();
            socket.flush();
        }
        final boolean isIncrementPsync = isIncrementPsync(redisPsyncResponse);
        boolean isIncrementPsyncTemp = isIncrementPsync;
        FileOutputStream fos = null;
        long totalNum=0;
        try {
            int parseFileStartNum=0;
            boolean cycle=true;
            boolean parseHeader = false;
            boolean parseFileStart=false;
            if(!isRealParseRDB) {
                Objects.requireNonNull(this.rdbStorePath,"RDB文件存储地址不能为null");
                File file = createFile(rdbStorePath);
                fos = new FileOutputStream(file);
            }
            while(cycle) {
                byte[] readData = socket.getInputStream().readRemain();
                if(readData == null){
                    System.out.println(isConnected());
                    break;
                }
                String content= new String(readData);
                System.out.println("content="+content);
                if(content.contains("SELECT") && content.contains("$")){
                    System.out.println("发现特殊符合");
                }
                if(isIncrementPsyncTemp){
                    PsyncContinueResponse psyncContinueResponse =RdbParseUtil.parsePsyncContinueResponse(readData);
                    if(psyncContinueResponse != null && psyncContinueResponse.isContinue()){
                        isIncrementPsyncTemp = false;
                        parseHeader=true;
                        parseFileStart = true;
                        String str1=new String(readData);
                        System.out.println("str1="+str1);
                        rdbParser.setIncrementPsync(true);
                        if(psyncContinueResponse.getHb() != null && psyncContinueResponse.getHb().length>NumberConstants.ZERO) {
                            readData = psyncContinueResponse.getHb();
                            String str2=new String(readData);
                            System.out.println(str2);
                            System.out.println("重新加载");
                            socket.getInputStream().load(readData);
                        }else{
                            continue;
                        }
                    }
                }
                if(!parseHeader) {
                    redisPsyncResponse = RdbParseUtil.parsePsyncResponse(readData);
                    if (redisPsyncResponse != null && redisPsyncResponse.getRundId() != null) {
                        parseHeader = true;
                        if (redisPsyncResponse.getRdbStartHb() != null) {
                            readData = redisPsyncResponse.getRdbStartHb();
                        }
                    }
                }else{
                    if(!parseFileStart){
                        PsyncResponse psyncResponse = RdbParseUtil.parsePsyncResponse(readData);
                        if(psyncResponse != null && psyncResponse.getRdbStartHb() != null){
                            readData = psyncResponse.getRdbStartHb();
                        }
                    }
                }
                String readDataStr= new String(readData);
                System.out.println("==================");
                if(readDataStr.startsWith("REDIS")){
                    parseFileStart=true;
                    if(isRealParseRDB()){
                        System.out.println("重新加载");
                        socket.getInputStream().load(readData);
                    }
                }
                if(!parseFileStart){
                    if((++parseFileStartNum)>=NumberConstants.PARSE_FILE_START_MAX_NUM){
                        throw new RuntimeException("解析经过"+parseFileStartNum+"次，仍然没有解析成功RDB文件");
                    }
                    continue;
                }
                if(readData != null && readData.length>0) {
                    totalNum+=readData.length;
                    if(isRealParseRDB()){
                        rdbParser.parse();
                        System.out.println("重复循环");
                        return true;
                        //break;
                    }else{
                        fos.write(readData);
                        fos.flush();
                    }
                }else{
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            storePysncResonse(redisPsyncResponse);
            if(fos != null){
                fos.flush();
                fos.close();
            }
            System.out.println("存储多少个totalNum="+totalNum);
        }
        return false;
    }
    private void storePysncResonse(PsyncResponse redisPsyncResponse){
        if(redisPsyncResponse != null) {
            redisPsyncResponse.setOffset(socket.getInputStream().getTotalReadOffset());
            redisPsyncResponse.setUpdateTimeStamp(System.currentTimeMillis());
            psyncStore.putPysncResponse(redisPsyncResponse);
        }
    }
    public void registerDataListener(DataListener dataListener){
        synchronized (dataListeners){
            dataListeners.add(dataListener);
        }
    }

    private boolean isIncrementPsync(PsyncResponse prePsyncResonse){
        if(prePsyncResonse != null && StringUtils.isNotEmpty(prePsyncResonse.getRundId())){
            return true;
        }else{
            return false;
        }
    }

    private void writeRdbHeader(byte[] bytes) throws IOException{
        File file = new File("d:/tmp/header.rdb");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    private static  File createFile(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        return file;
    }

    public void auth()throws IOException{
        socket.write(NumberConstants.ASTERISK_BYTE);
        socket.writeInt(2);
        socket.writeCtrl();

        List<String> argsList = Arrays.asList("AUTH",this.syncClientConfig.getPassword());
        for(String arg : argsList){
            socket.write(NumberConstants.DOLLAR_BYTE);
            socket.writeIntCtrl(arg.getBytes().length);
            socket.write(arg.getBytes());
            socket.writeCtrl();
        }
        socket.flush();
    }


    public SyncClientConfig getSyncClientConfig() {
        return syncClientConfig;
    }

    public void setSyncClientConfig(SyncClientConfig syncClientConfig) {
        this.syncClientConfig = syncClientConfig;
    }

    public boolean isRealParseRDB() {
        return isRealParseRDB;
    }
    public void setRealParseRDB(boolean realParseRDB) {
        isRealParseRDB = realParseRDB;
    }

    public String getRdbStorePath() {
        return rdbStorePath;
    }

    public void setRdbStorePath(String rdbStorePath) {
        this.rdbStorePath = rdbStorePath;
    }

    public boolean isConnected(){
        return socket.isConnected();
    }

    public List<DataListener> getDataListeners() {
        return dataListeners;
    }

    public PsyncStore getPsyncStore() {
        return psyncStore;
    }

    public void setPsyncStore(PsyncStore psyncStore) {
        this.psyncStore = psyncStore;
    }

    public void close()throws IOException{
        if(socket != null){
            socket.close();
        }
    }
}
