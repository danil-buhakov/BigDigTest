package com.bigdig.dan.bigdigappb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MainActivity";

    public static final Uri HISTORY_URI = Uri
            .parse("content://com.bigdig.dan.provider.History/history");

    private final static String URL = "url";
    private final static String STATUS = "status";
    private final static String TIME = "time";
    public final static String ID = "_id";

    public final static String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private String urls;
    private ImageView loadedImage;
    private TextView infoTextView;
    private int mStatus;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkFromWhereCalled();
    }

    private void checkFromWhereCalled(){
        if (mStatus == -1)
            calledFromTest();
        else
            calledFromHistory();
    }

    private void init() {
        urls = getIntent().getStringExtra("String");
        loadedImage = (ImageView) findViewById(R.id.loaded_image);
        infoTextView = (TextView) findViewById(R.id.txt_info);
        mStatus = getIntent().getIntExtra("Status", -1);
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
        if (mStatus == 1) {
            if (hasConnection(this)) {
                loadImage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission();
                } else
                    calledGreenFromHistory();
            } else {
                setNoInternetInfoMessage();
            }
        } else {
            tryLoadImage();
        }
    }

    private void calledGreenFromHistory(){
        startDeletionService();
        saveImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission(){
        if(checkSelfPermission(PERMISSIONS[0])== PackageManager.PERMISSION_GRANTED)
            calledGreenFromHistory();
        else
        if(!shouldShowRequestPermissionRationale(PERMISSIONS[0]))
            requestPermissions(PERMISSIONS,1);
        else
            new AlertDialog.Builder(this)
                    .setMessage("Для сохранения фото необходимо разрешение на запись файлов")
                    .setTitle("Разрешение")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(PERMISSIONS,1);
                        }
                    }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                calledGreenFromHistory();
        }
    }

    private void startDeletionService() {
        Intent serviceIntent = new Intent(this, DeletionService.class);
        serviceIntent.putExtra("Id", getIntent().getIntExtra("Id", -1));
        startService(serviceIntent);
    }

    private void loadImage() {
        loadedImage.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        Picasso.with(this).load(urls).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                insertOrUpdate(1);
                imageBitmap = bitmap;
                loadedImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                insertOrUpdate(2);
                setErrorMessage();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void saveImage() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/BIGDIG/test/B");
        Log.i(LOG_TAG, myDir.getAbsolutePath());
        myDir.mkdirs();
        String fname = "Image-" + urls.hashCode() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNoInternetInfoMessage() {
        loadedImage.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(getString(R.string.info_no_connection));
        insertOrUpdate(3);
    }

    private void setErrorMessage() {
        loadedImage.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(getString(R.string.info_error_loading));
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
        getContentResolver().insert(HISTORY_URI, cv);
    }

    private void updateLink(int status, long time) {
        ContentValues cv = new ContentValues();
        cv.put(URL, urls);
        cv.put(STATUS, status);
        cv.put(TIME, time);
        int result = getContentResolver().update(HISTORY_URI, cv, ID + "= ?", new String[]{String.valueOf(getIntent().getIntExtra("Id", 0))});
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
