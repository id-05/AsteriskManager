package com.asteriskmanager.fragments.channelfragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.AsteriskServer;
import com.asteriskmanager.AsteriskServerActivity;
import com.asteriskmanager.util.ConnectionCallback;
import com.asteriskmanager.R;
import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import static com.asteriskmanager.MainActivity.print;

public class ChannelFragment extends Fragment implements ConnectionCallback, ChannelRecordAdapter.OnChannelClickListener {

    private static AsteriskTelnetClient asterTelnetClient;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    public SipManager sipManager = null;
    public SipProfile sipProfile = null;
    SipAudioCall call = null;
    String sipServer = "188.75.221.200";
    boolean callActive = false;
    String spyChNum = "";
    ChannelRecord currentChannel;
    int curPosition;

    static ArrayList<ChannelRecord> ChannelList = new ArrayList<>();
    ChannelRecordAdapter adapter;
    RecyclerView recyclerView;


    public ChannelFragment() {
    }

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
            builder = new SipProfile.Builder("404", sipServer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert builder != null;
        builder.setPassword("pr09ramm1$t");
        sipProfile = builder.build();

//        try {
//            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
//
//                        public void onRegistering(String localProfileUri) {
//                            Log.d("asteriskmanager","Registering with SIP Server...");
//                        }
//
//                        public void onRegistrationDone(String localProfileUri, long expiryTime) {
//                            Log.d("asteriskmanager","Ready");
//                        }
//
//                        public void onRegistrationFailed(String localProfileUri, int errorCode,
//                                                         String errorMessage) {
//                            Log.d("asteriskmanager","Registration failed.  Please check settings.");
//                        }
//                    });
//        } catch (SipException e) {
//            Log.d("asteriskmanager",e.getMessage());
//        }


    }

    SipAudioCall.Listener listener = new SipAudioCall.Listener() {

        @Override
        public void onCallEstablished(SipAudioCall call) {
            Log.d("asteriskmanager","start make call");
            call.startAudio();
            call.setSpeakerMode(true);
            call.toggleMute();
        }

        @Override

        public void onCallEnded(SipAudioCall call) {
            Log.d("asteriskmanager",call.getState()+"");
        }
    };

    SipRegistrationListener sipRegistration = new SipRegistrationListener() {
        @Override
        public void onRegistering(String localProfileUri) {
            Log.d("asteriskmanager","onRegistering");
        }

        @Override
        public void onRegistrationDone(String localProfileUri, long expiryTime) {
            Log.d("asteriskmanager","onRegistrationDone");
//            try {
//                call = sipManager.makeAudioCall(sipServer,sipProfile.getUriString(), listener, 30);
//            } catch (SipException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
            Log.d("asteriskmanager","onRegistrationFailed");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_channel, container, false);
        recyclerView = fragmentView.findViewById(R.id.recyclerViewChannel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
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
                sipManager.register(sipProfile,500,sipRegistration);
            } catch (SipException e) {
                Log.d("asteriskmanager",e.getMessage());
            }
//            SipProfile.Builder builder2 = null;
//            try {
//                builder2 = new SipProfile.
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            assert builder2 != null;
//            builder2.setPassword("pr09ramm1$t");
//            peerProfile = builder2.build();
            // sipManager.makeAudioCall(sipProfile,peerProfile,listener);

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

                if(amistate.action.equals("channels")){
                    String buf = "";
                    if(callActive){
                        String com1 = "Action: Originate\n"+
                                "Channel: SIP/404\n"+
                                "Application: ChanSpy\n"+
                                "Data: "+currentChannel.getChannelName()+",qx\n"+
                                "Callerid: "+currentChannel.getChannelName()+" <"+currentChannel.getChannelName()+">\n";
                        Log.d("asteriskmanager",com1);
                        buf = asterTelnetClient.getResponse(com1);
                        amistate.setResultOperation(true);
                        amistate.setDescription("ok");
                    }else {
                        String com1 = "Action: CoreShowChannels\n";
                        String com2 = "Event: CoreShowChannelsComplete";
                        buf = asterTelnetClient.getUntilResponse(com1, com2);
                        amistate.setResultOperation(true);
                        amistate.setDescription(buf);
                    }


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
            amistate.setAction("channels");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("channels")){
            if(callActive){
                Log.d("asteriskmanager","success call");
                callActive = false;
                Log.d("asteriskmanager","curPosition on success= "+curPosition);
                ChannelList.get(curPosition).setActive(false);
                adapter.notifyDataSetChanged();
            }else {
                channelParser(amistate.getDescription());
                if (ChannelList.size() > 0) {
                    adapter = new ChannelRecordAdapter(ChannelList);
                    adapter.setOnChannelClickListener(this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
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

    public void channelParser(String buf){
        ChannelList.clear();
        ChannelRecord bufChannel = null;
        String[] words = buf.split("\n");
        for (String word : words) {
            if(word.contains("Channel:")){
                 bufChannel = new ChannelRecord();
                 bufChannel.setChannelName(word.substring(word.indexOf(":")+2));
            }

            if(word.contains("CallerIDNum:")){
                bufChannel.setCallerIDNum(word.substring(word.indexOf(":")+2));
            }

            if(word.contains("ConnectedLineNum:")){
                bufChannel.setConnectedLineNum(word.substring(word.indexOf(":")+2));
            }

            if(word.contains("Duration:")){
                bufChannel.setDuration(word.substring(word.indexOf(":")+2));
            }

            if(word.contains("BridgeId:")){
                ChannelList.add(bufChannel);
            }
        }
//        Event: CoreShowChannel
//        Channel: SIP/505-00001248
//        ChannelState: 6
//        ChannelStateDesc: Up
//        CallerIDNum: 505
//        CallerIDName: Reg-Chkalova5
//        ConnectedLineNum: 83022315615
//        ConnectedLineName: 83022315615
//        Language: ru
//        AccountCode:
//        Context: macro-dial-one
//        Exten: s
//        Priority: 1
//        Uniqueid: 1638235720.64806
//        Linkedid: 1638235421.63361
//        Application: AppDial
//        ApplicationData: (Outgoing Line)
//        Duration: 00:00:15
//        BridgeId: f5c961d6-a3e9-4947-b1af-b519f0e5f034
    }

    @Override
    public void onChannelClick(int position) {
        //disaViewHolder.contactLayout.setBackgroundColor(Color.GRAY);
        this.curPosition = position;
        ChannelList.get(curPosition).setActive(true);
        adapter.notifyDataSetChanged();
        Log.d("asteriskmanager","curPosition = "+curPosition);

        callActive = true;
        amiState.setAction("open");
        currentChannel = ChannelList.get(position);
        spyChNum = ChannelList.get(position).getCallerIDNum();
        Log.d("asteriskmanager",spyChNum);
        doSomethingAsyncOperaion(currentServer,amiState);
    }
}