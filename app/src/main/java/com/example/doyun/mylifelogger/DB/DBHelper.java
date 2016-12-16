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

    public static final String TABLE_WORK = "work";
    public static final String TABLE_EVENT = "event";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_WORK = "mywork";
    public static final String COLUMN_NAME_EVENT = "myevent";

    public DBHelper(Context context, String name) {
        super(context, F_path+name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_WORK+" ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NAME_DATE +" TEXT, "+COLUMN_NAME_TIME+" TEXT, "+COLUMN_NAME_WORK+" TEXT);");
        db.execSQL("CREATE TABLE "+TABLE_EVENT+" ( _id INTEGER PRIMARY KEY AUTOINCREMENT,  "+ COLUMN_NAME_DATE +"  TEXT, "+COLUMN_NAME_TIME+" TEXT, "+COLUMN_NAME_EVENT+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
