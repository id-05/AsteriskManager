package com.asteriskmanager.telnet;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.SocketException;
import android.util.Log;
import org.apache.commons.net.telnet.TelnetClient;

public class TelnetConnection {
    private final TelnetClient client;
    private final String SERVER_IP;
    private final int SERVERPORT;

    public TelnetConnection(String ip, int port){
        this.SERVER_IP = ip;
        this.SERVERPORT = port;
        client = new TelnetClient();
    }

    public void connect() throws IOException{
        try {
            client.connect(SERVER_IP, SERVERPORT);
        } catch (SocketException ex) {
            Log.d("asteriskmanager",ex.toString());
        }
    }

    public BufferedInputStream getReader(){
        return new BufferedInputStream(client.getInputStream());
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

