package com.example.anubhabmajumdar.hydrationapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class StopNotificationService extends IntentService {

    public StopNotificationService() {
        super("StopNotificationService");
    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            this.stopNotification();
        }
    }

    public void stopNotification()
    {
        Intent myIntent = new Intent(this , StartNotificationService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        if (alarmManager!=null)
            alarmManager.cancel(pendingIntent);

        Intent intentNotification = new Intent(getString(R.string.reset_name));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNotification);
     }



}
