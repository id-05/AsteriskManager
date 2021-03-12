package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    static AsteriskServer Server;
    Button bExit, bCLI, bPeers, bOriginate, bDashboard;
    public static ViewPager viewPager;
    NavigationView naviViewLeft;
    DrawerLayout drawerLayout;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPage);
        AsteriskServerItemAdapter serverItemAdapter = new AsteriskServerItemAdapter(getSupportFragmentManager(),0);
        viewPager.setAdapter(serverItemAdapter);
        //viewPager.addOnPageChangeListener(new );
        naviViewLeft = findViewById(R.id.naviViewLeft);
        naviViewLeft.bringToFront();

        bDashboard = findViewById(R.id.but_dashboard);
        bDashboard.setOnClickListener(dashboard);
        bExit = findViewById(R.id.but_exit);
        bExit.setOnClickListener(exit);
        bCLI = findViewById(R.id.but_cli);
        bCLI.setOnClickListener(cli);
        bPeers = findViewById(R.id.but_peers);
        bOriginate = findViewById(R.id.but_originate);
    }

    View.OnClickListener dashboard = v -> {
        viewPager.setCurrentItem(0);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener cli = v -> {
        viewPager.setCurrentItem(1);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener exit = v -> finish();

    
}