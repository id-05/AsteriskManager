package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import java.util.ArrayList;
import static com.asteriskmanager.MainActivity.print;

public class ManagerFragment extends Fragment implements ConnectionCallback {

    private String filename;
    private static AsteriskTelnetClient asterTelnetClient;
    AsteriskServer currentServer;
    EditText outText;
    AmiState amiState = new AmiState();
    String backupStr;
    private static final ArrayList<ManagerRecord> ManagerList = new ArrayList<>();
    RecyclerView recyclerView;
    ManagerRecordAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filename = "manager.conf";
        currentServer = AsteriskServerActivity.Server;
      //  setHasOptionsMenu(true);
       // recyclerView.setHasFixedSize(true);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                if(amistate.action.equals("mainaction")){
                    String com1 = "Action: GetConfig\n" +
                            "Filename: "+filename+"\n";
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
            amistate.setAction("mainaction");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("mainaction")){
            configFileParser(amistate.getDescription());
            adapter = new ManagerRecordAdapter(ManagerList);
            //adapter.setOnRecordClickListener(this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            String str = amistate.getDescription();
            backupStr = str;
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
        Boolean first = true;
        print(inStr);
        ManagerRecord bufManager = new ManagerRecord();
        StringBuilder result;
        result = new StringBuilder();
        String[] words = inStr.split("\n");
        for (String word : words) {
            if(word.contains("Category")){
                if(first&(!word.contains("general"))){
                    bufManager = new ManagerRecord();
                    int i = word.indexOf(":");
                    bufManager.setName(word.substring(i + 2, word.length()));
                    first = false;
                }else{
                    ManagerList.add(bufManager);
                    bufManager = new ManagerRecord();
                    int i = word.indexOf(":");
                    bufManager.setName(word.substring(i + 2, word.length()));
                }
            }

            if(word.contains("Line")){
                int i = word.indexOf(":");
                String buf = word.substring(i + 1, word.length());
                if(buf.contains("secret")){
                    int j = buf.indexOf("=");
                    bufManager.setSecret(buf.substring(j + 2, buf.length()).trim());
                }

                if(buf.contains("deny")){
                    int j = buf.indexOf("=");
                    bufManager.setDeny(buf.substring(j + 1, buf.length()).trim());
                }

                if(buf.contains("permit")){
                    int j = buf.indexOf("=");
                    bufManager.setPermit(buf.substring(j + 1, buf.length()).trim());
                }

                if(buf.contains("read")){
                    int j = buf.indexOf("=");
                    bufManager.setRead(buf.substring(j + 2, buf.length()).trim());
                }

                if(buf.contains("write")){
                    int j = buf.indexOf("=");
                    bufManager.setWrite(buf.substring(j + 2, buf.length()).trim());
                }

                if(buf.contains("writetimeout")){
                    int j = buf.indexOf("=");
                    bufManager.setTimeout(buf.substring(j + 2, buf.length()).trim());
                }
            }
        }
        result.toString();
    }
}