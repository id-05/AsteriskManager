package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    AsteriskServer Server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asterisk_server);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            ServerId = arguments.getInt("serverid");
            Server = MainActivity.getServerById(ServerId);

        }
        setTitle(Server.getName());
    }
}