 package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.util.ConnectionCallback;

import java.util.ArrayList;
import java.util.Objects;

import static com.asteriskmanager.MainActivity.print;

public class ManagerFragment extends Fragment implements ConnectionCallback,  ManagerRecordAdapter.OnManagerClickListener {

    private String filename;
    private static AsteriskTelnetClient asterTelnetClient;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    String backupStr;
    public static final ArrayList<ManagerRecord> ManagerList = new ArrayList<>();
    RecyclerView recyclerView;
    ManagerRecordAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filename = "manager.conf.save";
        currentServer = AsteriskServerActivity.Server;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manager_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addmanagerbut) {
            print("add press");
            //saveChange(filename);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_manager, container, false);
        recyclerView = fragmentView.findViewById(R.id.recyclerViewManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        amiState.setAction("open");
        doSomethingAsyncOperaion(currentServer,amiState);
    }

    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(AsteriskServer server, final AmiState amistate) {
        new AbstractAsyncWorker(this, amistate) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected AmiState doAction() throws Exception {
                if(amistate.getAction().equals("open")){
                    asterTelnetClient = new AsteriskTelnetClient(server.getIpaddress(),Integer.parseInt(server.getPort()));
                    amistate.setResultOperation(asterTelnetClient.isConnected());
                }
                if(amistate.getAction().equals("login")){
                    String com1 = "Action: Login\n"+
                            "Events: off\n"+
                            "Username: "+server.getUsername()+"\n"+
                            "Secret: "+server.getSecret()+"\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setResultOperation(buf.contains("Success"));
                    amistate.setDescription(buf);
                }
                if(amistate.getAction().equals("mainaction")){
                    String com1 = "Action: GetConfig\n" +
                            "Filename: "+filename+"\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setDescription(buf);
                }
                if(amistate.getAction().equals("exit")){
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
        Log.d("asteriskmanager","buf = "+buf);
        if(buf.equals("open")){
            amistate.setAction("login");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("login")){
            amistate.setAction("mainaction");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("mainaction")){

            configFileParser(amistate.getDescription());
            adapter = new ManagerRecordAdapter(ManagerList);
            adapter.setOnManagerClickListener(this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            backupStr = amistate.getDescription();
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

    private void configFileParser(String inStr){
        boolean first = true;
        print(inStr);
        ManagerRecord bufManager = new ManagerRecord();
        String[] words = inStr.split("\n");
        for (String word : words) {
            if(word.contains("Category")) {
                if(!word.contains("general"))
                {
                    if (first) {
                        bufManager = new ManagerRecord();
                        int i = word.indexOf(":");
                        bufManager.setName(word.substring(i + 2));
                        first = false;
                    } else {
                        ManagerList.add(bufManager);
                        bufManager = new ManagerRecord();
                        int i = word.indexOf(":");
                        bufManager.setName(word.substring(i + 2));
                    }
                }
            }
            if(word.contains("Line")){
                int i = word.indexOf(":");
                String buf = word.substring(i + 1);
                int j = buf.indexOf("=");
                if(buf.contains("secret=")){
                    bufManager.setSecret(buf.substring(j + 1).trim());
                }

                if(buf.contains("deny=")){
                    bufManager.setDeny(buf.substring(j + 1).trim());
                }

                if(buf.contains("permit=")){
                    bufManager.setPermit(buf.substring(j + 1).trim());
                }

                if(buf.contains("read=")){
                    bufManager.setRead(buf.substring(j + 1).trim());
                }

                if(buf.contains("write=")){
                    bufManager.setWrite(buf.substring(j + 1).trim());
                }

                if(buf.contains("writetimeout=")){
                    bufManager.setTimeout(buf.substring(j + 1).trim());
                }
            }
        }
        ManagerList.add(bufManager);
    }

    @Override
    public void onManagerClick(int position) {
        Objects.requireNonNull(getActivity()).setTitle(getActivity().getTitle()+" / " + ManagerList.get(position).getName());
        AsteriskServerActivity.fragmentTransaction = AsteriskServerActivity.fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("filename", position);
        ManagerFragmentEditor fragment = new ManagerFragmentEditor();
        fragment.setArguments(bundle);
        AsteriskServerActivity.fragmentTransaction.replace(R.id.container, fragment);
        AsteriskServerActivity.fragmentTransaction.commit();
        AsteriskServerActivity.subFragment = "manager";
    }
}