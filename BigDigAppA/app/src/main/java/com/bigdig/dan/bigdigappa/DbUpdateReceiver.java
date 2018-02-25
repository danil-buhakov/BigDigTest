package com.bigdig.dan.bigdigappa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class DbUpdateReceiver extends BroadcastReceiver {

    private MainActivity mActivity;

    public DbUpdateReceiver(MainActivity activity){
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mActivity.updateLinks();
        Toast.makeText(mActivity,"База данных обновлена",Toast.LENGTH_SHORT).show();
    }
}
