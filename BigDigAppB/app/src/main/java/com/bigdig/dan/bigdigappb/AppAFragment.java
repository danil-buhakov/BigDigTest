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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class AppAFragment extends Fragment {

    private ImageView loadedImage;
    private TextView infoTextView;
    private int mStatus;//todo: arguments + id
    private Bitmap imageBitmap;
    private String urls;//todo: arguments

    private final static String URL = "url";
    private final static String STATUS = "status";
    private final static String TIME = "time";
    public final static String ID = "_id";
    public static final Uri HISTORY_URI = Uri
            .parse("content://com.bigdig.dan.provider.History/history");

    private static final String ARGS_STATUS = "Status";
    private static final String ARGS_ID = "Id";
    private static final String ARGS_URL = "Url";

    private static final String LOAD_FOLDER = "/BIGDIG/test/B";

    private final static String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    public static AppAFragment newInstance(int status, int id, String url) {
        Bundle args = new Bundle();
        args.putInt(ARGS_STATUS, status);
        args.putInt(ARGS_ID, id);
        args.putString(ARGS_URL, url);
        AppAFragment fragment = new AppAFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_a, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        loadedImage = (ImageView) v.findViewById(R.id.loaded_image);
        infoTextView = (TextView) v.findViewById(R.id.txt_info);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        urls = getArguments().getString(ARGS_URL);
        checkFromWhereCalled();
    }

    private void checkFromWhereCalled() {
        mStatus = getArguments().getInt(ARGS_STATUS);
        if (mStatus == -1)
            calledFromTest();
        else
            calledFromHistory();
    }

    private void calledFromTest() {
        tryLoadImage();
    }

    private void tryLoadImage() {
        if (hasConnection(getActivity())) {
            loadImage();
        } else {
            setNoInternetInfoMessage();
        }
    }

    private void calledFromHistory() {
        if (mStatus == 1) {
            if (hasConnection(getActivity())) {
                loadImage();
                calledGreenFromHistory();
            } else {
                setNoInternetInfoMessage();
            }
        } else {
            tryLoadImage();
        }
    }

    private void calledGreenFromHistory() {
        startDeletionService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else
            saveImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (getActivity().checkSelfPermission(PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED)
            saveImage();
        else if (!shouldShowRequestPermissionRationale(PERMISSIONS[0]))
            requestPermissions(PERMISSIONS, 1);
        else
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_message)
                    .setTitle(R.string.permission_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(PERMISSIONS, 1);
                        }
                    }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                saveImage();
        }
    }

    private void startDeletionService() {
        Intent serviceIntent = new Intent(getActivity(), DeletionService.class);
        serviceIntent.putExtra(ARGS_ID, getArguments().getInt(ARGS_ID));
        getActivity().startService(serviceIntent);
    }

    private void loadImage() {
        loadedImage.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        Picasso.with(getActivity()).load(urls).into(new Target() {
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
        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = new File(root + LOAD_FOLDER);
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
        getActivity().getContentResolver().insert(HISTORY_URI, cv);
    }

    private void updateLink(int status, long time) {
        ContentValues cv = new ContentValues();
        cv.put(URL, urls);
        cv.put(STATUS, status);
        cv.put(TIME, time);
        getActivity().getContentResolver().update(HISTORY_URI, cv, ID + "= ?", new String[]{String.valueOf(getArguments().getInt("Id"))});
    }

}
