package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewServer extends AppCompatActivity {

    AsteriskServer server = new AsteriskServer();
    String method;
    EditText ipaddressEdit,portEdit,usernameEdit,secretEdit;
    Integer id;
    DateBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_server);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            method = arguments.getString("method");
            ipaddressEdit.setText("");
            portEdit.setText("");
            usernameEdit.setText("");
            secretEdit.setText("");
            switch (method){
                case "edit":
                {
                    id = arguments.getInt("serverid");
                    try {
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
                        ipaddressEdit.setText(server.ipaddress);
                        portEdit.setText(server.getPort());
                        usernameEdit.setText(server.getUsername());
                        secretEdit.setText(server.getSecret());
                        setTitle(R.string.editAsterServer);
                    }catch (Exception e){
                        MainActivity.print("addserver oncreate "+e.toString());
                    }
                }
                break;
                case "new":
                {
                    setTitle(R.string.titleAddNewServer);
                    ipaddressEdit.setText("");
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

    final View.OnClickListener pressSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if((!ipaddressEdit.getText().toString().equals("")) & (!portEdit.getText().toString().equals("")) & (!usernameEdit.getText().toString().equals("")) & (!secretEdit.getText().toString().equals("")))
            {
                switch (method) {
                    case "new":{
                        server.setIpaddress(ipaddressEdit.getText().toString());
                        server.setPort(portEdit.getText().toString());
                        server.setUsername(usernameEdit.getText().toString());
                        server.setSecret(secretEdit.getText().toString());
                        //savePressed = true;
                        MainActivity.serverAddBase(server);
                        finish();
                    }
                    break;
                    case "edit":{
                        server.setIpaddress(ipaddressEdit.getText().toString());
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
                if(ipaddressEdit.getText().toString().equals("")){
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


}