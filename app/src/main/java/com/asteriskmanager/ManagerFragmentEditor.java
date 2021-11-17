package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.util.ConnectionCallback;

public class ManagerFragmentEditor extends Fragment implements ConnectionCallback {

    TextView mName, mSecret, mDeny, mPermit, mTimeout, mRead, mWrite;
    ManagerRecord record;
    private static AsteriskTelnetClient asterTelnetClient;
    String filename = "manager.conf.save";
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    int i = 0;
    String[] rulesName = {"system","call","log","verbose","command","agent","user","config","dtmf","reporting","cdr","dialplan","originate"};
    int rulesCount = 13;

    public ManagerFragmentEditor() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        setHasOptionsMenu(true);
        currentServer = AsteriskServerActivity.Server;
        if(arguments!=null){
            Log.d("asteriskmanager","name from managerlist "+ManagerFragment.ManagerList.get(arguments.getInt("filename")).getName());
            record = ManagerFragment.ManagerList.get(arguments.getInt("filename"));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manager_editor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_manager) {
            amiState.setAction("open");
            doSomethingAsyncOperaion(currentServer,amiState);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_manager_editor, container, false);
        mName = fragmentView.findViewById(R.id.managerNameEdit);
        mName.setText(record.getName());
        mSecret = fragmentView.findViewById(R.id.managerSecretEdit);
        mSecret.setText(record.getSecret());
        mDeny = fragmentView.findViewById(R.id.managerDenyEdit);
        mDeny.setText(record.getDeny());
        mPermit = fragmentView.findViewById(R.id.managerPermitEdit);
        mPermit.setText(record.getPermit());
        mRead = fragmentView.findViewById(R.id.managerReadEdit);
        mRead.setText(record.getRead());
        mRead.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose an rules:");
            builder.setNegativeButton("Cancel",null);
            boolean[] checkedItems = new boolean[rulesCount];
            for(int j = 0; j < rulesCount; j++){
                checkedItems[j] = mRead.getText().toString().contains(rulesName[j]);
            }

            builder.setMultiChoiceItems(rulesName, checkedItems, (dialog, which, isChecked) -> {

            });

            builder.setPositiveButton("Save", (dialog, which) -> {
                StringBuilder bufStr = new StringBuilder();
                for(int j = 0; j < rulesCount; j++){
                    if(checkedItems[j]){
                        bufStr.append(",").append(rulesName[j]);
                    }else{
                        checkedItems[j] = false;
                    }
                }
                String cutBuf = bufStr.toString().substring(1);
                mRead.setText(cutBuf);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        mWrite = fragmentView.findViewById(R.id.managerWriteEdit);
        mWrite.setText(record.getWrite());
        mWrite.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose an rules:");
            builder.setNegativeButton("Cancel",null);
            int i = 13;

            boolean[] checkedItems = new boolean[i];
            for(int j = 0; j < i; j++){
                checkedItems[j] = mWrite.getText().toString().contains(rulesName[j]);
            }

            builder.setMultiChoiceItems(rulesName, checkedItems, (dialog, which, isChecked) -> {

            });

            builder.setPositiveButton("Save", (dialog, which) -> {
                StringBuilder bufStr = new StringBuilder();
                for(int j = 0; j < i; j++){
                    if(checkedItems[j]){
                        bufStr.append(",").append(rulesName[j]);
                    }else{
                        checkedItems[j] = false;
                    }
                }
                String cutBuf = bufStr.toString().substring(1);
                mWrite.setText(cutBuf);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        mTimeout = fragmentView.findViewById(R.id.managerTimeoutEdit);
        mTimeout.setText(record.getTimeout());
        return fragmentView;
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

                if(amistate.getAction().equals("manager")){
                    String com1 = "Action: UpdateConfig\n" +
                                  "SrcFilename:"+filename+"\n" +
                                  "DstFilename:"+filename+"\n" +

                                   getStringForAmi(0, record.getName(),"secret", mSecret.getText().toString()) +
                                   getStringForAmi(1, record.getName(),"deny",   mDeny.getText().toString()) +
                                   getStringForAmi(2, record.getName(),"permit", mPermit.getText().toString()) +
                                   getStringForAmi(3, record.getName(),"read",   mRead.getText().toString()) +
                                   getStringForAmi(4, record.getName(),"write",  mWrite.getText().toString()) +
                                   getStringForAmi(5, record.getName(),"writetimeout",mTimeout.getText().toString());

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

    public String getStringForAmi(int i, String cateroryName, String varName, String varValue){
        String buf ="Action-00000"+i+": update\n"+
                "Cat-00000"+i+":"+cateroryName+ "\n"+
                "Var-00000"+i+":"+ varName+"\n"+
                "Value-00000"+i+":"+ varValue+"\n";
        return buf;
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
            amistate.setAction("manager");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("manager")){
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