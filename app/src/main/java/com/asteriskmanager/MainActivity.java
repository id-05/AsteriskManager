package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.asteriskmanager.telnet.AsteriskTelnetClient;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ConnectionCallback, RecordAdapter.OnRecordClickListener {

    public SharedPreferences sPref;
    public static DateBase dbHelper;
    RecyclerView recyclerView;
    private static ArrayList<AsteriskServer> ServerList = new ArrayList<>();
    public RecordAdapter adapter;
    private static AsteriskTelnetClient asterTelnetClient;
    AmiState amiState = new AmiState();
    AsteriskServer currentServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DateBase(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.AddServer:
                Intent i = new Intent(MainActivity.this, AddNewServer.class);
                i.putExtra("method","new");
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadConfig();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecordAdapter(ServerList,this);
        adapter.setOnRecordClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerList.clear();
        GetServerList(ServerList);
        for (final AsteriskServer server:ServerList){
            try{
                currentServer = server;
                amiState.setAction("open");
                doSomethingAsyncOperaion(currentServer,amiState);
            }catch (Exception e){
                print("error resume /system "+e.toString());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(AsteriskServer server, final AmiState amistate) {
        new AbstractAsyncWorker<Boolean>(this, amistate) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected AmiState doAction() throws Exception {
                if(amistate.action.equals("open")){
                    asterTelnetClient = new AsteriskTelnetClient(server.getIpaddress(),Integer.parseInt(server.getPort()));
                    amistate.setResultOperation(asterTelnetClient.isConnected());
                }
                if(amistate.action.equals("login")){
                    String com1 = "Action: Login\n"+
                            "Events: off\n"+
                            "Username: "+server.getUsername()+"\n"+
                            "Secret: "+server.getSecret()+"\n";
                    String buf = asterTelnetClient.getResponse(com1);
                    amistate.setResultOperation(true);
                    amistate.setResultOperation(buf.contains("Success"));
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

    public static void GetServerList(ArrayList<AsteriskServer> serverList){
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("servers", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    AsteriskServer server = new  AsteriskServer();
                    server.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    server.name = (cursor.getString(cursor.getColumnIndex("name")));
                    server.ipaddress = (cursor.getString(cursor.getColumnIndex("ip")));
                    server.port = (cursor.getString(cursor.getColumnIndex("port")));
                    server.username = (cursor.getString(cursor.getColumnIndex("login")));
                    server.secret = (cursor.getString(cursor.getColumnIndex("pass")));
                    server.setOnline(false);
                    serverList.add(server);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            MainActivity.print(e.toString());
        }

    }

    public static void serverAddBase(AsteriskServer server){
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.name);
        newValues.put("ip",server.ipaddress);
        newValues.put("port",server.port);
        newValues.put("login",server.username);
        newValues.put("pass",server.secret);
        newValues.put("comment","test");
        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            userDB.insertOrThrow("servers", null, newValues);
            userDB.close();
        }catch (SQLException e){
            print("error add to base "+e.toString());
        }
    }

    public static void serverDelBase(AsteriskServer server) {
        int id = server.getId();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.delete("servers","id = " + id, null);
    }

    public static void serverConnectionUpdateBase(AsteriskServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.getName());
        newValues.put("ip",server.getIpaddress());
        newValues.put("port",server.getPort());
        newValues.put("login",server.getUsername());
        newValues.put("pass",server.getSecret());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
    }

    public static AsteriskServer getServerById(int id) {
        AsteriskServer server = new AsteriskServer();
        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            Cursor cursor = userDB.query("servers", null, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                server.name = (cursor.getString(cursor.getColumnIndex("name")));
                server.ipaddress = (cursor.getString(cursor.getColumnIndex("ip")));
                server.port = (cursor.getString(cursor.getColumnIndex("port")));
                server.username = (cursor.getString(cursor.getColumnIndex("login")));
                server.secret = (cursor.getString(cursor.getColumnIndex("pass")));
            }
            cursor.close();
        }catch (Exception e){
            print("error = "+ e);
        }
        return server;
    }

    public void saveConfig() {
        sPref = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.commit();
    }

    public void loadConfig() {
        sPref = getSharedPreferences("config",MODE_PRIVATE);
    }

    public static void print(String str){
        Log.d("asteriskmanager",str);
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
            amistate.setAction("exit");
            doSomethingAsyncOperaion(currentServer,amistate);
        }
        if(buf.equals("exit")){
            currentServer.setOnline(true);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onFailure(AmiState amiState) {
        currentServer.setOnline(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onRecordClick(int position) {
        print("click");
        Intent i = new Intent(MainActivity.this, AsteriskServerActivity.class);
        i.putExtra("serverid",ServerList.get(position).getId());
        startActivity(i);
    }
}