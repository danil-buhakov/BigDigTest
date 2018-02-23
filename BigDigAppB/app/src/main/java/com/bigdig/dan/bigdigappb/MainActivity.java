package com.bigdig.dan.bigdigappb;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MainActivity";

    final Uri CONTACT_URI = Uri
            .parse("content://com.bigdig.dan.provider.History/history");

    private final static String URL = "url";
    private final static String STATUS = "status";
    private final static String TIME = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String urls=getIntent().getStringExtra("String");
        ImageView loadedImage = (ImageView) findViewById(R.id.loaded_image);
        if(urls==null)
            urls="";
        else {
            Picasso.with(this).load(urls).into(loadedImage);
            insertLink(urls,1, System.currentTimeMillis());
            }
    }

    private void insertLink(String url, int status, long time){
        Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        ContentValues cv = new ContentValues();
        cv.put(URL,url);
        cv.put(STATUS,status);
        cv.put(TIME,time);
        Uri resultUri = getContentResolver().insert(CONTACT_URI,cv);
        Log.i(LOG_TAG,"Uri "+resultUri);
    }

}
