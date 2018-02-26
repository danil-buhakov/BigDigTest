package com.bigdig.dan.bigdigappa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "historyBase.db";
    public final static String TABLE_NAME = "history";
    public final static String URL = "url";
    public final static String STATUS = "status";
    public final static String TIME = "time";
    public final static String ID = "_id";

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "( " +
                ID + " integer primary key autoincrement, " +
                URL + ", " +
                STATUS + ", " +
                TIME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
