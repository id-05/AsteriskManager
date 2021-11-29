package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.asteriskmanager.fragments.configfragment.ConfigFragment;
import com.asteriskmanager.fragments.channelfragment.ChannelFragment;
import com.asteriskmanager.fragments.CliFragment;
import com.asteriskmanager.fragments.DashboardFragment;
import com.asteriskmanager.fragments.QueueFragment;
import com.asteriskmanager.fragments.managerfragment.ManagerFragment;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;

public class AsteriskServerActivity extends AppCompatActivity {

    Integer ServerId;
    public static AsteriskServer Server;
    Button bExit, bCLI, bChannels, bConfig, bDashboard, bQueue, bManager;
    NavigationView naviViewLeft;
    DrawerLayout drawerLayout;
    public static FragmentTransaction fragmentTransaction;
    public static FragmentManager fragmentManager;
    public static String subFragment = "";
    public SipManager sipManager = null;
    public SipProfile sipProfile = null;

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
        bManager = findViewById(R.id.but_manager);
        bManager.setOnClickListener(manager);

        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        DashboardFragment firstFragment = new DashboardFragment();
        fragmentTransaction.add(R.id.container, firstFragment);
        fragmentTransaction.commit();

        if (sipManager == null) {
            sipManager = SipManager.newInstance(this);
        }

        SipProfile.Builder builder = null;
        try {
            builder = new SipProfile.Builder("404", "188.75.221.200");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        builder.setPassword("pr09ramm1$t");
        sipProfile = builder.build();

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

    View.OnClickListener manager = v -> {
        setTitle(Server.getName()+" : "+"Managers");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ManagerFragment());
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    };

    View.OnClickListener exit = v -> finish();

    @Override
    public void onBackPressed() {
        switch (subFragment){
            case("config"):
                setTitle(Server.getName()+" : "+"Config");
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ConfigFragment());
                fragmentTransaction.commit();
                subFragment = "";
                break;

            case ("manager"):
                setTitle(Server.getName()+" : "+"Manager");
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ManagerFragment());
                fragmentTransaction.commit();
                subFragment = "";
                break;

                default:
            super.onBackPressed();
        }

    }
}