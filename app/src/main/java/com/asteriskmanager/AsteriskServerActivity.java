package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    static AsteriskServer Server;
    Button bExit, bCLI, bChannels, bConfig, bDashboard;
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
        bChannels = findViewById(R.id.but_channels);
        bChannels.setOnClickListener(channels);
        bConfig = findViewById(R.id.but_config);
        bConfig.setOnClickListener(config);
    }

    View.OnClickListener dashboard = v -> {
        viewPager.setCurrentItem(0);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener cli = v -> {
        viewPager.setCurrentItem(1);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener channels = v -> {
        viewPager.setCurrentItem(2);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener config = v -> {
        viewPager.setCurrentItem(3);
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener exit = v -> finish();

    
}