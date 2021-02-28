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
import android.view.View;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ConnectionCallback, RecordAdapter.OnRecordClickListener {

    public SharedPreferences sPref;
    public static DateBase dbHelper;
    RecyclerView recyclerView;
    private static ArrayList<AsteriskServer> ServerList = new ArrayList<>();
    public RecordAdapter adapter;

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
    }

    public static void GetServerList(ArrayList<AsteriskServer> serverList){
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("servers", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    //ifNoServerMes.setVisibility(View.INVISIBLE);
                    AsteriskServer server = new  AsteriskServer();
                    server.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    server.name = (cursor.getString(cursor.getColumnIndex("name")));
                    server.ipaddress = (cursor.getString(cursor.getColumnIndex("ip")));
                    server.port = (cursor.getString(cursor.getColumnIndex("port")));
                    server.username = (cursor.getString(cursor.getColumnIndex("login")));
                    server.secret = (cursor.getString(cursor.getColumnIndex("pass")));
                    //server.setConnect(false);
                    serverList.add(server);
                }
                while (cursor.moveToNext());
            }else {
              //  ifNoServerMes.setVisibility(View.VISIBLE);
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

    public static void serverUpdateBase(AsteriskServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.getName());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
    }

    public static void serverConnectionUpdateBase(AsteriskServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
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
            print("getorthancbyid error = "+ e);
        }
        return server;
    }

    public void saveConfig() {
        sPref = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        //ed.putInt("VIEWSTYLE", viewStyle);
        ed.commit();
    }

    public void loadConfig() {
        sPref = getSharedPreferences("config",MODE_PRIVATE);
        //viewStyle = sPref.getInt("VIEWSTYLE", 1);
    }

    public static void print(String str){
        Log.d("asteriskmanager",str);
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(AmiState amistate) {

    }

    @Override
    public void onFailure(AmiState amiState) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onRecordClick(int position) {

    }
}