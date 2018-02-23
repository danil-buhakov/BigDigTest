package com.bigdig.dan.bigdigappa;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class DbContentProvider extends ContentProvider {
    static final String AUTHORITY = "com.bigdig.dan.provider.History";
    static final String PATH = "history";
    static final String TAG = "DbContentProvider";

    public static final Uri HISTORY_CONTENT_URI = Uri.parse(
            "content://"+AUTHORITY+"/"+PATH);

    private SQLiteDatabase mDatabase;
    private DbOpenHelper mDbOpenHelper;

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new DbOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(!uri.equals(HISTORY_CONTENT_URI)) {
           Log.i(TAG, "Wrong URI: " + uri);
            return null;
        }
        mDatabase = mDbOpenHelper.getWritableDatabase();
        long rowID = mDatabase.insert(DbOpenHelper.TABLE_NAME,null,values);
        int i =mDatabase.query(
                DbOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null)
                .getCount();
        Log.i(TAG, "insert " + i);
        return ContentUris.withAppendedId(HISTORY_CONTENT_URI, rowID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "delete");
        if(uri!=HISTORY_CONTENT_URI) {
            Log.i(TAG, "Wrong URI: " + uri);
            return 0;
        }
        mDatabase = mDbOpenHelper.getWritableDatabase();
        return mDatabase.delete(DbOpenHelper.TABLE_NAME,selection,selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "update");
        if(uri!=HISTORY_CONTENT_URI) {
            Log.i(TAG, "Wrong URI: " + uri);
            return 0;
        }
        mDatabase = mDbOpenHelper.getWritableDatabase();
        return mDatabase.update(DbOpenHelper.TABLE_NAME,values,selection,selectionArgs);
    }
}
