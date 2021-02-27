package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements ConnectionCallback {

    public SharedPreferences sPref;
    public static DateBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


    }

    @SuppressLint("StaticFieldLeak")
    public void doSomethingAsyncOperaion(final String comand, final String number) {
        new AbstractAsyncWorker<Boolean>(this, comand,number) {
            //@SuppressLint("StaticFieldLeak")
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Boolean doAction() throws Exception {

                return true;
            }
        }.execute();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess(String data, String param) {

    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onEnd() {

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
}