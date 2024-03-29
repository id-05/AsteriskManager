package com.asteriskmanager.fragments.configfragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ConfigFragmentEditor extends Fragment implements ConnectionCallback {

    private String filename;
    private static AsteriskTelnetClient asterTelnetClient;
    AsteriskServer currentServer;
    EditText outText;
    AmiState amiState = new AmiState();
    String backupStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filename = getArguments().getString("filename");
        }
        currentServer = AsteriskServerActivity.Server;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editor_file_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save_manager:
                print("save");
                saveChange(filename);
                return true;
            case R.id.restore_eb:
                print("restore");
                restoreBackup();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveChange(String filename){

    }

    private void restoreBackup(){
        outText.setText(configFileParser(backupStr));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_config_file, container, false);
        outText = fragmentView.findViewById(R.id.outTextEdit);
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
        print("buf  "+buf);
        if(buf.equals("open")){
            amistate.setAction("login");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("login")){
            amistate.setAction("mainaction");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("mainaction")){
            outText.setText(configFileParser(amistate.getDescription()));
            String str = amistate.getDescription();
            backupStr = str;
            print(str);
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

    private String configFileParser(String inStr){
        StringBuilder result;
        result = new StringBuilder();
        String[] words = inStr.split("\n");
            for (String word : words) {
                if(word.contains("Category")){
                    int i = word.indexOf(":");
                    result.append("\n" + "[").append(word.substring(i + 2, word.length())).append("]").append("\n");
                }
                if(word.contains("Line")){
                    int i = word.indexOf(":");
                    result.append(word.substring(i + 2, word.length())).append("\n");
                }
            }
        return result.toString();
    }
}