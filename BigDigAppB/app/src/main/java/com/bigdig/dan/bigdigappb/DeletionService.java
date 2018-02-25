package com.bigdig.dan.bigdigappb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class DeletionService extends Service {
    int id;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getIntExtra("Id", -1);
        if (id >= 0)
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getContentResolver().delete(MainActivity.HISTORY_URI, MainActivity.ID + "= ?"
                            , new String[]{String.valueOf(id)});
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.bigdig.dan.actin.DB_UPDATED");
                    sendBroadcast(broadcastIntent);
                    stopSelf();
                }
            }, 15000);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
