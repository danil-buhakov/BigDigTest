package com.bigdig.dan.bigdigappb;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MainActivity";

    final Uri CONTACT_URI = Uri
            .parse("content://com.bigdig.dan.provider.History/history");

    private final static String URL = "url";
    private final static String STATUS = "status";
    private final static String TIME = "time";
    private final static String ID = "_id";

    private String urls;
    private ImageView loadedImage;
    private TextView infoTextView;
    private int mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mStatus = getIntent().getIntExtra("Status", -1);
        if (mStatus == -1)
            calledFromTest();
        else
            calledFromHistory();
    }

    private void init() {
        urls = getIntent().getStringExtra("String");
        loadedImage = (ImageView) findViewById(R.id.loaded_image);
        infoTextView = (TextView) findViewById(R.id.txt_info);
    }

    private void calledFromTest() {
        if (urls == null) {
            //todo: implement timer
        } else
            tryLoadImage();
    }

    private void tryLoadImage() {
        if (hasConnection(this)) {
            loadImage();
        } else {
            setNoInternetInfoMessage();
        }
    }

    private void calledFromHistory() {
        Toast.makeText(this, "calledFromHistory", Toast.LENGTH_SHORT).show();
        if (mStatus == 1) {
            //todo: make exit on 15 seconds here
        } else {
            tryLoadImage();
        }
    }

    private void loadImage() {
        loadedImage.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        Picasso.with(this).load(urls).placeholder(android.R.drawable.stat_sys_download).into(loadedImage, new Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                insertOrUpdate(1);
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                insertOrUpdate(2);
            }
        });
    }

    private void setNoInternetInfoMessage() {
        loadedImage.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(getString(R.string.info_no_connection));
        insertOrUpdate(3);
    }

    private void insertOrUpdate(int status) {
        if (mStatus == -1)
            insertLink(status, Calendar.getInstance().getTimeInMillis());
        else if (mStatus != status)
            updateLink(status, Calendar.getInstance().getTimeInMillis());
    }

    private void insertLink(int status, long time) {
        ContentValues cv = new ContentValues();
        cv.put(URL, urls);
        cv.put(STATUS, status);
        cv.put(TIME, time);
        getContentResolver().insert(CONTACT_URI, cv);
    }

    private void updateLink(int status, long time) {
        Toast.makeText(this, "updateLink", Toast.LENGTH_SHORT).show();
        ContentValues cv = new ContentValues();
        cv.put(URL, urls);
        cv.put(STATUS, status);
        cv.put(TIME, time);
        int result = getContentResolver().update(CONTACT_URI, cv, ID + "= ?", new String[]{String.valueOf(getIntent().getIntExtra("Id", 0))});
        Toast.makeText(this, "result = " + result, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
