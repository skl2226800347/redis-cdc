package com.skl.cdc.core.network;
import com.skl.cdc.core.constants.NumberConstants;
import com.skl.cdc.core.io.BufferedSocketInputStream;
import com.skl.cdc.core.io.RedisInputStream;
import com.skl.cdc.core.io.RedisOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
public class RedisSocket {

    private String ip;

    int port;
    private Socket socket;
    private RedisInputStream inputStream;
    private RedisOutputStream outputStream;
    public RedisSocket(String ip,int port){
        this.ip = ip;
        this.port = port;
        socket = new Socket();
    }

    public RedisInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(RedisInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void flush() throws IOException {
        getOutputStream().flush();
    }

    public RedisOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(RedisOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void connect()throws IOException {
        InetSocketAddress address = new InetSocketAddress(ip,port);
        socket.connect(address);
        this.inputStream = new RedisInputStream(new BufferedSocketInputStream(socket.getInputStream()));
        this.outputStream = new RedisOutputStream(socket.getOutputStream());
    }



    public void writeCtrl()throws IOException{
        outputStream.write(NumberConstants.R_BYTE);
        outputStream.write(NumberConstants.N_BYTE);
    }

    public void write(byte value)throws IOException{
        outputStream.write(value);
    }
    public void writeInt(int value)throws IOException{
        outputStream.writeInt(value);
    }

    public void writeIntCtrl(int value)throws IOException{
        outputStream.writeInt(value);
        writeCtrl();
    }

    public void write(byte[] bytes){
        outputStream.write(bytes);
    }


    public boolean isConnected(){
        return socket.isConnected();
    }

    public void close()throws IOException{
        socket.close();
        inputStream.close();
        outputStream.close();
    }
}
