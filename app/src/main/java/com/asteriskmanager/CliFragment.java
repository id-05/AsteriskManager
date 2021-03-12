package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CliFragment extends Fragment implements ConnectionCallback {

    private static AsteriskTelnetClient asterTelnetClient;
    EditText commandText, outText;
    Button sendCommand;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();

    public CliFragment() {
        // Required empty public constructor
    }

    public static CliFragment newInstance(String param1, String param2) {
        CliFragment fragment = new CliFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentServer = AsteriskServerActivity.Server;
        amiState.setAction("open");
    }

    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(AsteriskServer server, final AmiState amistate) {
        new AbstractAsyncWorker<Boolean>(this, amistate) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected AmiState doAction() throws Exception {
                if(amistate.action.equals("open")){
                    asterTelnetClient = new AsteriskTelnetClient(server.getIpaddress(),Integer.parseInt(server.getPort()));
                    amistate.setResultOperation(asterTelnetClient.isConnected());
                }
                if(amistate.action.equals("login")){
                    String com1 = "Action: Login\n"+
                            "Events: off\n"+
                            "Username: "+server.getUsername()+"\n"+
                            "Secret: "+server.getSecret()+"\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setResultOperation(buf.contains("Success"));
                    amistate.setDescription(buf);
                }
                if(amistate.action.equals("corestatus")){
                    String com1 = "Action: CoreStatus\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setDescription(buf);
                }
                if(amistate.action.equals("exit")){
                    String com1 = "Action: Logoff\n";
                    asterTelnetClient.sendCommand(com1);
                    amistate.setResultOperation(true);
                    amistate.setDescription("");
                }
                return amistate;
            }
        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_cli, container, false);
        commandText = fragmentView.findViewById(R.id.commandText);
        outText = fragmentView.findViewById(R.id.outText);
        sendCommand = fragmentView.findViewById(R.id.sendCommand);
        sendCommand.setOnClickListener(sendClick);
        return fragmentView;
    }

    View.OnClickListener sendClick = v -> {
        doSomethingAsyncOperaion(currentServer,amiState);
    };

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(AmiState amistate) {

    }

    @Override
    public void onFailure(AmiState amiState) {

    }

    @Override
    public void onEnd() {

    }
}