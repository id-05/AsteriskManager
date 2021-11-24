package com.asteriskmanager.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.asteriskmanager.ManagerFragmentEditor;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.AsteriskServer;
import com.asteriskmanager.AsteriskServerActivity;
import com.asteriskmanager.util.ConnectionCallback;
import com.asteriskmanager.R;
import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;

import java.text.ParseException;
import java.util.Objects;

import static com.asteriskmanager.MainActivity.print;

public class ChannelFragment extends Fragment implements ConnectionCallback {

    private static AsteriskTelnetClient asterTelnetClient;
    EditText  outText;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    public SipManager sipManager = null;
    public SipProfile sipProfile = null;


    public ChannelFragment() {
    }

//    public static ChannelFragment newInstance(String param1, String param2) {
//        ChannelFragment fragment = new ChannelFragment();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentServer = AsteriskServerActivity.Server;

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.USE_SIP)
                == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.USE_SIP}, 0);
        }

        if (sipManager == null) {
            sipManager = SipManager.newInstance(getContext());
        }

        SipProfile.Builder builder = null;
        try {
            builder = new SipProfile.Builder("404", "188.75.221.200");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert builder != null;
        builder.setPassword("pr09ramm1$t");
        sipProfile = builder.build();

        try {
            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {

                        public void onRegistering(String localProfileUri) {
                            Log.d("asteriskmanager","Registering with SIP Server...");
                        }

                        public void onRegistrationDone(String localProfileUri, long expiryTime) {
                            Log.d("asteriskmanager","Ready");
                        }

                        public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                         String errorMessage) {
                            Log.d("asteriskmanager","Registration failed.  Please check settings.");
                        }
                    });
        } catch (SipException e) {
            Log.d("asteriskmanager",e.getMessage().toString());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_channel, container, false);
        outText = fragmentView.findViewById(R.id.outText);
        outText.setKeyListener(null);
        setHasOptionsMenu(true);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        amiState.setAction("open");
        doSomethingAsyncOperaion(currentServer,amiState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.channel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sip_status) {
            Log.d("asteriskmanager","sip status");

            Intent intent = new Intent();
            intent.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, Intent.FILL_IN_DATA);
            try {
                sipManager.open(sipProfile, pendingIntent, null);
            } catch (SipException e) {
                e.printStackTrace();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(AsteriskServer server, final AmiState amistate) {
        new AbstractAsyncWorker(this, amistate) {
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
                    String com1 = "Action: CoreShowChannels\n";
                    String com2 = "Event: CoreShowChannelsComplete";
                    String buf = asterTelnetClient.getUntilResponse(com1,com2);
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
            outText.setText(amistate.getDescription());
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
        }
    }

    @Override
    public void onFailure(AmiState amiState) {

    }

    @Override
    public void onEnd() {

    }
}