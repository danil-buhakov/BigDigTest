package com.bigdig.dan.bigdigappa;

import android.database.Cursor;

import java.util.ArrayList;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;

public class Link {
    private String mUrl;
    private int mStatus;
    private Date mDate;

    public Link(String url, int status, Date time){
        mUrl = url;
        mStatus = status;
        mDate = time;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getStatus() {
        return mStatus;
    }

    public Date getDate() {
        return mDate;
    }

    public static List<Link> getLinksFromCursor(Cursor cursor){
        List<Link> result = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String url = cursor.getString(cursor.getColumnIndex(DbOpenHelper.URL));
            int status = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.STATUS));
            long time = cursor.getLong(cursor.getColumnIndex(DbOpenHelper.TIME));
            Date date = new Date(time);
            Link new_link = new Link(url,status,date);
            result.add(new_link);
            cursor.moveToNext();
        }
        return result;
    }

    public static final Comparator<Link> COMPARE_BY_DATE = new Comparator<Link>() {
        @Override
        public int compare(Link o1, Link o2) {
            return (int)(o2.getDate().getTime()-o1.getDate().getTime());
        }
    };

    public static final Comparator<Link> COMPARE_BY_STATUS = new Comparator<Link>() {
        @Override
        public int compare(Link o1, Link o2) {
            return o1.getStatus()-o2.getStatus();
        }
    };
}
