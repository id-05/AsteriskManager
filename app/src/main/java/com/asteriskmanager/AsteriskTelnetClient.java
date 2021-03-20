package com.asteriskmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsteriskTelnetClient {
    private final TelnetConnection client;
    private final OutputStream outstream;
    //private org.apache.commons.net.telnet.TelnetClient rawConnection;
    private InputStream instream;

    public AsteriskTelnetClient(String ip, int port) throws IOException {
        client = new TelnetConnection(ip, port);
        client.connect();
        //rawConnection = client.getConnection();
        outstream = client.getOutput();
        instream = client.getReader();
    }

    public boolean sendCommand(String cmd) {
        if (client == null || !client.isConnected()) {
            return false;
        }

        String stringBuilder = cmd +
                "\n\r";
        byte[] cmdbyte = stringBuilder.getBytes();

        try {
            outstream.write(cmdbyte, 0, cmdbyte.length);
            outstream.flush();

            return true;
        } catch (Exception e1) {
            return false;
        }
    }

    public String getResponse(String cmd) throws IOException, InterruptedException {

        if (client == null || !client.isConnected()) {
            throw new IOException("Unable to send message to disconnected client");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cmd);
        stringBuilder.append("\n");

        byte[] cmdbyte = stringBuilder.toString().getBytes();

        outstream.write(cmdbyte, 0, cmdbyte.length);
        outstream.flush();

        InputStreamReader a = new InputStreamReader(instream);
        BufferedReader buf = new BufferedReader(a);

        while(buf.ready())
        {
            buf.read();
        }
        StringBuilder result = null;
        result = new StringBuilder();
        String bufstr;


        while((!(bufstr = buf.readLine()).equals(""))){
            result.append(bufstr+"\n");
        }
        return result.toString();
    }

    public String expectResponse(String cmd, String expected, int timeout) throws IOException, InterruptedException, TimeoutException, ExecutionException {

        if (client == null || !client.isConnected()) {
            throw new IOException("Unable to send message to disconnected client");
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(cmd);
        stringBuilder.append("\n");

        byte[] cmdbyte = stringBuilder.toString().getBytes();

        outstream.write(cmdbyte, 0, cmdbyte.length);
        outstream.flush();

        return readUntil(expected, timeout);
    }

    private String readUntil(String expected, int timeout) throws InterruptedException, TimeoutException, ExecutionException {
        final ExecutorService service;
        final Future<String> result;
        String buf = null;
        if (timeout == -1) {
            service = Executors.newFixedThreadPool(1);
            result = service.submit(new ReadUntil(expected));
            buf = result.get(5000, TimeUnit.SECONDS);
        } else {
            service = Executors.newFixedThreadPool(1);
            result = service.submit(new ReadUntil(expected));
            try {
                buf = result.get(timeout, TimeUnit.MILLISECONDS);
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    private class ReadUntil implements Callable<String> {
        String expected;

        public ReadUntil(String expect) {
            this.expected = expect;
        }

        @Override
        public String call() {
            try {
                InputStreamReader a = new InputStreamReader(instream);
                BufferedReader stream = new BufferedReader(a);

                String line;
                while ((line = stream.readLine()) != null){
                    MainActivity.print("line =   "+line);
                    if(line.contains(expected))
                        break;
                }
                return line;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

}

