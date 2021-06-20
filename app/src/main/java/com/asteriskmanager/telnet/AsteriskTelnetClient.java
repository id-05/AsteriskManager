package com.asteriskmanager.telnet;

import org.apache.commons.io.input.TeeInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class AsteriskTelnetClient {
        private final TelnetConnection client;
        private final OutputStream outstream;
        private final InputStream instream;
        private PipedInputStream spyReader;

        public AsteriskTelnetClient(String ip, int port) throws IOException {
            client = new TelnetConnection(ip, port);
            client.connect();
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

        public String getResponse(String cmd) throws IOException {

            if (client == null || !client.isConnected()) {
                throw new IOException("Unable to send message to disconnected client");
            }

            String stringBuilder = cmd +
                    "\n";
            byte[] cmdbyte = stringBuilder.getBytes();

            InputStreamReader a = spawnSpy();
            BufferedReader buf = new BufferedReader(a);
            outstream.write(cmdbyte, 0, cmdbyte.length);
            outstream.flush();
            while(buf.ready())
            {
                buf.read();
            }
            StringBuilder result;
            result = new StringBuilder();
            String bufstr;
            while((!(bufstr = buf.readLine()).equals(""))){
                result.append(bufstr);
            }
            return result.toString();
        }
        public InputStreamReader spawnSpy() throws IOException {
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream();
            in.connect(out);
            if(spyReader!=null) {
                return spawnSpy(spyReader, out);
            } else {
                spyReader = in;
                return spawnSpy(instream, out);
            }
        }

        private InputStreamReader spawnSpy(InputStream in, PipedOutputStream pipeout) {
            return new InputStreamReader(new TeeInputStream(in,pipeout));
        }

        public boolean isConnected() {
            return client.isConnected();
        }

    public String getUntilResponse(String cmd, String response) throws IOException, InterruptedException {

        if (client == null || !client.isConnected()) {
            throw new IOException("Unable to send message to disconnected client");
        }

        String stringBuilder = cmd +
                "\n";
        byte[] cmdbyte = stringBuilder.getBytes();

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


        while((!(bufstr = buf.readLine()).equals(response))){
            result.append(bufstr).append("\n");
        }
        return result.toString();
    }

    }