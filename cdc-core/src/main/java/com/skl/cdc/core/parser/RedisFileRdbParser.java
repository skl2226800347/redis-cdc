package com.skl.cdc.core.parser;
import com.skl.cdc.core.io.BufferedFileInputStream;
import com.skl.cdc.core.io.RedisInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
/**
 * https://www.lmlphp.com/user/1414/article/item/8225746/
 */
public class RedisFileRdbParser extends RedisRdbParser {

    public RedisFileRdbParser(String rdbFilePath){
        Objects.requireNonNull(rdbFilePath,"rdbFilePath:"+rdbFilePath+" 为null");
        File file = new File(rdbFilePath);
        if(!file.exists()){
            throw new IllegalArgumentException("rdbFile:"+rdbFilePath+"不存在");
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedFileInputStream bfis = new BufferedFileInputStream(fis);
            inputStream = new RedisInputStream(bfis);
        }catch (FileNotFoundException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public void parse()throws IOException {
        if(isIncrementPsync) {
            //解析头
            parseHeader();
        }
        //解析数据区
        parseBody();
        //解析为尾部
        parseEnd();
    }
}
