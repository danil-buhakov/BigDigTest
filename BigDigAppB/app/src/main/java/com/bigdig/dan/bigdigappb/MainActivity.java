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

    private String urls;
    private ImageView loadedImage;
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urls = getIntent().getStringExtra("String");
        loadedImage = (ImageView) findViewById(R.id.loaded_image);
        infoTextView = (TextView) findViewById(R.id.txt_info);
        if (urls == null)
            urls = "";
        else if (hasConnection(this)) {
            loadImage();
        } else{
            setNoInternetInfoMessage();
        }
    }

    private void loadImage(){
        loadedImage.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        Picasso.with(this).load(urls).placeholder(android.R.drawable.stat_sys_download).into(loadedImage, new Callback() {
            @Override
            public void onSuccess() {
                insertLink(urls, 1, Calendar.getInstance().getTimeInMillis());
            }
            @Override
            public void onError() {
                insertLink(urls, 2, Calendar.getInstance().getTimeInMillis());
            }
        });
    }

    private void setNoInternetInfoMessage(){
        loadedImage.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(getString(R.string.info_no_connection));
        insertLink(urls,3, Calendar.getInstance().getTimeInMillis());
    }

    private void insertLink(String url, int status, long time) {
        Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        ContentValues cv = new ContentValues();
        cv.put(URL, url);
        cv.put(STATUS, status);
        cv.put(TIME, time);
        Uri resultUri = getContentResolver().insert(CONTACT_URI, cv);
        Log.i(LOG_TAG, "Uri " + resultUri);
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
