package com.example.doyun.mylifelogger.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by doyun on 2016-12-06.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String F_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MyLifeLogger/";

    public DBHelper(Context context, String name) {
        super(context, F_path+name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE work ( _id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, mywork TEXT);");
        db.execSQL("CREATE TABLE event ( _id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, myevent TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
