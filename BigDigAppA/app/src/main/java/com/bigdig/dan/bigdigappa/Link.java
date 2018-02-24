package com.bigdig.dan.bigdigappa;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Link {
    private String mUrl;
    private int mStatus;
    private long mTime;

    public Link(String url, int status, long time){
        mUrl = url;
        mStatus = status;
        mTime = time;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getStatus() {
        return mStatus;
    }

    public long getTime() {
        return mTime;
    }

    public static List<Link> getLinksFromCursor(Cursor cursor){
        List<Link> result = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String url = cursor.getString(cursor.getColumnIndex(DbOpenHelper.URL));
            int status = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.STATUS));
            long time = cursor.getLong(cursor.getColumnIndex(DbOpenHelper.TIME));
            Link new_link = new Link(url,status,time);
            result.add(new_link);
            cursor.moveToNext();
        }
        return result;
    }
}
