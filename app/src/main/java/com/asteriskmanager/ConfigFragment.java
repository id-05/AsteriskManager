package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import static com.asteriskmanager.MainActivity.print;

public class ConfigFragment extends Fragment implements ConnectionCallback, ConfigFileAdapter.OnRecordClickListener {

    private static AsteriskTelnetClient asterTelnetClient;
    EditText  outText;
    RecyclerView recyclerView;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    ArrayList<ConfigFileRecord> filesList = new ArrayList<>();

    public ConfigFragment() {
        // Required empty public constructor
    }

    public static ChannelFragment newInstance(String param1, String param2) {
        ChannelFragment fragment = new ChannelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentServer = AsteriskServerActivity.Server;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_config, container, false);
        //outText = fragmentView.findViewById(R.id.outText);
        //outText.setKeyListener(null);
        recyclerView = fragmentView.findViewById(R.id.RecyclerConfigFiles);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        amiState.setAction("open");
        String[] filesname = getResources().getStringArray(R.array.ConfigFile);
        String[] filesdescription = getResources().getStringArray(R.array.ConfigFileDescription);
        filesList.clear();
        for(int i=0; i<filesname.length;i++){
            ConfigFileRecord BufRecord = new ConfigFileRecord();
            BufRecord.setFilename(filesname[i]);
            BufRecord.setDescription(filesdescription[i]);
            switch (filesname[i]){
                case "adtranvofr.conf":{BufRecord.setCategory("Channels");break;}
                case "adsi.conf":{BufRecord.setCategory("Analog Display Services Interface");break;}
                case "extensions.conf":{BufRecord.setCategory("Extensions");break;}
                case "alarmreceiver.conf":{BufRecord.setCategory("Comands Extensions");break;}
                case "amd.conf":{BufRecord.setCategory("Others");break;}
            }
            filesList.add(BufRecord);
        }
        ConfigFileAdapter recyclerViewAdapter = new ConfigFileAdapter(filesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnRecordClickListener(this);
        recyclerViewAdapter.notifyDataSetChanged();
     //   doSomethingAsyncOperaion(currentServer,amiState);
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
                    String com1 = "Action: GetConfig\n" +
                            "Filename: asterisk.conf\n";
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

    @Override
    public void onRecordClick(int position) {
        getActivity().setTitle(getActivity().getTitle()+" / "+filesList.get(position).filename);
        AsteriskServerActivity.fragmentTransaction = AsteriskServerActivity.fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("filename", filesList.get(position).filename);
        EditConfigFileFragment fragment = new EditConfigFileFragment();
        fragment.setArguments(bundle);
        AsteriskServerActivity.fragmentTransaction.replace(R.id.container, fragment);
        AsteriskServerActivity.fragmentTransaction.commit();
        AsteriskServerActivity.configfileactive = true;
    }
}