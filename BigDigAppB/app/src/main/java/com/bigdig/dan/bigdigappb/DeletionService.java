package com.bigdig.dan.bigdigappb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class DeletionService extends Service {
    int id;
    private static final String ACTION = "com.bigdig.dan.actin.DB_UPDATED";
    final int delay = 15000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("Id", -1);
        if (id >= 0)
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getContentResolver().delete(AppAFragment.HISTORY_URI, AppAFragment.ID + "= ?"
                            , new String[]{String.valueOf(id)});
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ACTION);
                    sendBroadcast(broadcastIntent);
                    stopSelf();
                }
            }, delay);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
