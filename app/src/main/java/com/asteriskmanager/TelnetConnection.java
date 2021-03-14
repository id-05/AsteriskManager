package com.asteriskmanager;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.SocketException;
import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetConnection {
    private TelnetClient client = null;
    private final String SERVER_IP;
    private final int SERVERPORT;

    public TelnetConnection(String ip, int port) throws IOException{
        SERVER_IP = ip;
        SERVERPORT = port;
        client = new TelnetClient();

    }

    public void connect() throws IOException{
        try {
            client.connect(SERVER_IP, SERVERPORT);
        } catch (SocketException ex) {
            Log.d("aster",ex.toString());
        }
    }

    public BufferedInputStream getReader(){
        return new BufferedInputStream(client.getInputStream(),1000000);
    }

    public OutputStream getOutput(){
        return client.getOutputStream();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public TelnetClient getConnection(){
        return client;
    }
}

