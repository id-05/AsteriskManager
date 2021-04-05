package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.asteriskmanager.configfragment.ConfigFragment;
import com.google.android.material.navigation.NavigationView;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    public static AsteriskServer Server;
    Button bExit, bCLI, bChannels, bConfig, bDashboard, bQueue;
    NavigationView naviViewLeft;
    DrawerLayout drawerLayout;
    public static FragmentTransaction fragmentTransaction;
    public static FragmentManager fragmentManager;
    public static Boolean configfileactive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asterisk_server);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            ServerId = arguments.getInt("serverid");
            Server = MainActivity.getServerById(ServerId);

        }
        setTitle(Server.getName()+" : "+"DASHBOARD");

        drawerLayout = findViewById(R.id.drawer_layout);
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
        bQueue = findViewById(R.id.but_queue);
        bQueue.setOnClickListener(queue);

        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        DashboardFragment firstFragment = new DashboardFragment();
        fragmentTransaction.add(R.id.container, firstFragment);
        fragmentTransaction.commit();
    }

    View.OnClickListener dashboard = v -> {
        setTitle(Server.getName()+" : "+"DASHBOARD");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new DashboardFragment());
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener cli = v -> {
        setTitle(Server.getName()+" : "+"CLI");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new CliFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener channels = v -> {
        setTitle(Server.getName()+" : "+"Channels");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ChannelFragment());
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener config = v -> {
        setTitle(Server.getName()+" : "+"Config");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ConfigFragment());
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener queue = v -> {
        setTitle(Server.getName()+" : "+"Queues");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new QueueFragment());
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener exit = v -> finish();

    @Override
    public void onBackPressed() {
        if(configfileactive)
        {
            setTitle(Server.getName()+" : "+"Config");
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new ConfigFragment());
            fragmentTransaction.commit();
            configfileactive = false;
        }else{
            super.onBackPressed();
        }

    }
}