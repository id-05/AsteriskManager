package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    AsteriskServer Server;
    Button bExit, bCLI, bPeers, bOriginate;
    public static ViewPager viewPager;

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

        //viewPager = findViewById(R.id.viewPage);
        //AsteriskServerItemAdapter serverItemAdapter = new AsteriskServerItemAdapter(getSupportFragmentManager(),0);
        //viewPager.setAdapter(serverItemAdapter);
        //viewPager.addOnPageChangeListener(new );
        bExit = findViewById(R.id.but_exit);
        bExit.setOnClickListener(exit);
        bCLI = findViewById(R.id.but_cli);
        //bCLI.setOnClickListener(cli);
        bPeers = findViewById(R.id.but_peers);
        bOriginate = findViewById(R.id.but_originate);
    }

    //View.OnClickListener cli = v -> viewPager.setCurrentItem(0);
    View.OnClickListener exit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    
}