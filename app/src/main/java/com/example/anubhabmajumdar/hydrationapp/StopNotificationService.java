package com.example.anubhabmajumdar.hydrationapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

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
        Intent myIntent = new Intent(this , NotificationService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        alarmManager.cancel(pendingIntent);
     }


}
