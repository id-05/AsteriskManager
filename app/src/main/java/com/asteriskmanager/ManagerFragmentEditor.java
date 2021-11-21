package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.util.ConnectionCallback;

import java.util.Objects;

import static com.asteriskmanager.AsteriskServerActivity.Server;

public class ManagerFragmentEditor extends Fragment implements ConnectionCallback {

    TextView mName, mSecret, mDeny, mPermit, mTimeout, mRead, mWrite;
    ManagerRecord record;
    private static AsteriskTelnetClient asterTelnetClient;
    String filename = "manager_custom.conf";
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
    String[] rulesName = {"system","call","log","verbose","command","agent","user","config","dtmf","reporting","cdr","dialplan","originate"};
    int rulesCount = 13;
    boolean delAction = false;

    public ManagerFragmentEditor() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        setHasOptionsMenu(true);
        currentServer = Server;
        if(arguments!=null){
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

        if (id == R.id.delete_manager) {
            delAction = true;
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
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Action: UpdateConfig\n").append("SrcFilename:").append(filename).append("\n").append("DstFilename:").append(filename).append("\n");
                    if(delAction){
                        stringBuilder.append(getStringForAmi(0, "delcat", record.getName(), "", ""));
                    }
                    else {
                        stringBuilder.append(getStringForAmi(0, "update", record.getName(), "secret", mSecret.getText().toString()));
                        stringBuilder.append(getStringForAmi(1, "update", record.getName(), "deny", mDeny.getText().toString()));
                        stringBuilder.append(getStringForAmi(2, "update", record.getName(), "permit", mPermit.getText().toString()));
                        stringBuilder.append(getStringForAmi(3, "update", record.getName(), "read", mRead.getText().toString()));
                        stringBuilder.append(getStringForAmi(4, "update", record.getName(), "write", mWrite.getText().toString()));
                        stringBuilder.append(getStringForAmi(5, "update", record.getName(), "writetimeout", mTimeout.getText().toString()));
                        stringBuilder.append(getStringForAmi(6, "renamecat", record.getName(), "", mName.getText().toString()));
                    }
                    String buf = asterTelnetClient.getResponse(stringBuilder.toString());
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

    public String getStringForAmi(int i, String action,String cateroryName, String varName, String varValue){
        StringBuilder buf = new StringBuilder();
        buf.append("Action-00000").append(i).append(":").append(action).append("\n");
        buf.append("Cat-00000").append(i).append(":").append(cateroryName).append("\n");
        if(!varName.equals("")){
            buf.append("Var-00000").append(i).append(":").append(varName).append("\n");
        }
        if(!varValue.equals("")) {
            buf.append("Value-00000").append(i).append(":").append(varValue).append("\n");
        }
        return buf.toString();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(AmiState amistate) {
        String buf = amistate.getAction();
        Log.d("asteriskmanager","answer = "+amistate.getDescription());
        if(buf.equals("open")){
            amistate.setAction("login");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("login")){
            amistate.setAction("manager");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("manager")){
            if(delAction){
                delAction = false;
                Objects.requireNonNull(getActivity()).setTitle(Server.getName()+" : "+"Manager");
                AsteriskServerActivity.fragmentTransaction  = AsteriskServerActivity.fragmentManager.beginTransaction();
                AsteriskServerActivity.fragmentTransaction.replace(R.id.container, new ManagerFragment());
                AsteriskServerActivity.fragmentTransaction.commit();
                Toast toast = Toast.makeText(getContext(),
                        "DELETE SUCCESSFULLY", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Toast toast = Toast.makeText(getContext(),
                        "SAVED SUCCESSFULLY", Toast.LENGTH_SHORT);
                toast.show();
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