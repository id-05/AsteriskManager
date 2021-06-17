package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asteriskmanager.telnet.AmiState;
import com.asteriskmanager.telnet.AsteriskTelnetClient;
import com.asteriskmanager.util.AbstractAsyncWorker;
import com.asteriskmanager.util.ConnectionCallback;
import com.asteriskmanager.util.DateBase;
import com.google.android.material.snackbar.Snackbar;
import static com.asteriskmanager.MainActivity.print;

public class AddNewServer extends AppCompatActivity implements ConnectionCallback {

    AsteriskServer server = new AsteriskServer();
    String method;
    EditText ipEdit,portEdit,usernameEdit,secretEdit,nameEdit;
    Button saveBut, cancelBut,testBut;
    Integer id;
    DateBase dbHelper;
    @SuppressLint("StaticFieldLeak")
    static LinearLayout settinglayout;
    private static AsteriskTelnetClient asterTelnetClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_server);
        nameEdit = findViewById(R.id.name);
        ipEdit = findViewById(R.id.ipaddress);
        portEdit = findViewById(R.id.asterport);
        usernameEdit = findViewById(R.id.amiusername);
        secretEdit = findViewById(R.id.amiusersecret);
        testBut = findViewById(R.id.testcon);
        saveBut = findViewById(R.id.savebutton);
        cancelBut = findViewById(R.id.cancelbutton);
        saveBut.setOnClickListener(pressSave);
        cancelBut.setOnClickListener(cancelClick);
        testBut.setOnClickListener(testConnection);
        settinglayout = findViewById(R.id.settinglayout);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            method = arguments.getString("method");
            ipEdit.setText("");
            portEdit.setText("");
            usernameEdit.setText("");
            secretEdit.setText("");
            switch (method){
                case "edit":
                {
                    id = arguments.getInt("serverid");
                    try {
                        dbHelper = new DateBase(this);
                        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
                        String selection = "id = ?";
                        String[] selectionArgs = new String[]{String.valueOf(id)};
                        Cursor cursor = userDB.query("servers", null, selection, selectionArgs, null, null, null);
                        if (cursor.moveToFirst()) {
                            server.setId(id);
                            server.name = (cursor.getString(cursor.getColumnIndex("name")));
                            server.ipaddress = (cursor.getString(cursor.getColumnIndex("ip")));
                            server.port = (cursor.getString(cursor.getColumnIndex("port")));
                            server.username = (cursor.getString(cursor.getColumnIndex("login")));
                            server.secret = (cursor.getString(cursor.getColumnIndex("pass")));
                        }
                        cursor.close();
                        ipEdit.setText(server.ipaddress);
                        portEdit.setText(server.getPort());
                        usernameEdit.setText(server.getUsername());
                        secretEdit.setText(server.getSecret());
                        nameEdit.setText(server.getName());
                        setTitle(R.string.editAsterServer);
                    }catch (Exception e){
                        print("addserver oncreate "+e.toString());
                    }
                }
                break;
                case "new":
                {
                    setTitle(R.string.titleAddNewServer);
                    ipEdit.setText("");
                    portEdit.setText("");
                    usernameEdit.setText("");
                    secretEdit.setText("");
                }
                break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    final View.OnClickListener pressSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if((!ipEdit.getText().toString().equals("")) & (!portEdit.getText().toString().equals("")) &
                    (!usernameEdit.getText().toString().equals("")) & (!secretEdit.getText().toString().equals("")))
            {
                switch (method) {
                    case "new":{
                        server.setName(nameEdit.getText().toString());
                        server.setIpaddress(ipEdit.getText().toString());
                        server.setPort(portEdit.getText().toString());
                        server.setUsername(usernameEdit.getText().toString());
                        server.setSecret(secretEdit.getText().toString());
                        MainActivity.serverAddBase(server);
                        finish();
                    }
                    break;
                    case "edit":{
                        server.setName(nameEdit.getText().toString());
                        server.setIpaddress(ipEdit.getText().toString());
                        server.setPort(portEdit.getText().toString());
                        server.setUsername(usernameEdit.getText().toString());
                        server.setSecret(secretEdit.getText().toString());
                        MainActivity.serverConnectionUpdateBase(server);
                        finish();
                    }
                    break;
                    default:
                        break;
                }
            }else{
                if(ipEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failureIP, Toast.LENGTH_SHORT); toast.show();
                }
                if(portEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failurePORT, Toast.LENGTH_SHORT); toast.show();
                }
                if(usernameEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failureUSERNAME, Toast.LENGTH_SHORT); toast.show();
                }
                if(secretEdit.getText().toString().equals("")){
                    Toast toast = Toast.makeText(AddNewServer.this, R.string.failureSECRET, Toast.LENGTH_SHORT); toast.show();
                }
            }
        }
    };

    View.OnClickListener cancelClick = v -> finish();

    View.OnClickListener testConnection = v -> {
        AmiState amistate = new AmiState();
        amistate.action="open";
        doSomethingAsyncOperaion(amistate);
    };


    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(final AmiState amistate) {
        new AbstractAsyncWorker<Boolean>(this, amistate) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected AmiState doAction() throws Exception {
                if(amistate.action.equals("open")){
                    asterTelnetClient = new AsteriskTelnetClient(ipEdit.getText().toString(),Integer.parseInt(portEdit.getText().toString()));
                    amistate.setResultOperation(asterTelnetClient.isConnected());
                }
                if(amistate.action.equals("login")){
                    String com1 = "Action: Login\n"+
                            "Events: off\n"+
                            "Username: "+usernameEdit.getText().toString()+"\n"+
                            "Secret: "+secretEdit.getText().toString()+"\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setResultOperation(buf.contains("Response: SuccessMessage: Authentication accepted"));
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
            doSomethingAsyncOperaion(amistate);
        }
        if(buf.equals("login")){
            amistate.setAction("exit");
            doSomethingAsyncOperaion(amistate);
        }
        if(buf.equals("exit")){
            Snackbar.make(settinglayout,
                    R.string.SUCCESS,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(AmiState amistate) {
        Snackbar.make(settinglayout,
                R.string.FAILURE,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onEnd() {

    }

}