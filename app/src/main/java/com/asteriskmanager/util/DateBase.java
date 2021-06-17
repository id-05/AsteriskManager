package com.asteriskmanager.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DateBase extends SQLiteOpenHelper {

    public DateBase(Context context) {
        super(context, "asteriskmanager", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase asteriskmanagerDB) {
        String SQL =
                "create table servers ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "date integer,"
                        + "ip text,"
                        + "port text,"
                        + "login text,"
                        + "pass text,"
                        + "comment text" + ");";
        asteriskmanagerDB.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase asteriskmanagerDB, int i, int i1) {

    }
}
