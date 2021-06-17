package com.asteriskmanager.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.asteriskmanager.AsteriskServer;
import com.asteriskmanager.AsteriskServerActivity;
import com.asteriskmanager.R;
import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.util.ConnectionCallback;

import static com.asteriskmanager.MainActivity.print;

public class DashboardFragment extends Fragment implements ConnectionCallback {

    private static AsteriskTelnetClient asterTelnetClient;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    EditText edt1;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        amiState.setAction("open");
        doSomethingAsyncOperaion(currentServer,amiState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentServer = AsteriskServerActivity.Server;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        print("dashboard");
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
        final View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        edt1 = fragmentView.findViewById(R.id.editTextTextMultiLine);
        return fragmentView;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(AmiState amistate) {
        String buf = amistate.getAction();
        if(buf.equals("open")){
            amistate.setAction("login");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("login")){
            amistate.setAction("corestatus");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("corestatus")){
            edt1.setText(amistate.getDescription());
            String str = amistate.getDescription();
            String[] words = str.split("~");
            for (String word : words) {
                print(word);
            }
            amistate.setAction("exit");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("exit")){
            currentServer.setOnline(true);
            //adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(AmiState amiState) {
        print("failure");
        amiState.setAction("exit");
        doSomethingAsyncOperaion(currentServer,amiState);
    }

    @Override
    public void onEnd() {

    }
}