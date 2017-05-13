package com.example.anubhabmajumdar.hydrationapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationDrinkWaterService extends IntentService {
    public NotificationDrinkWaterService() {
        super("NotificationDrinkWaterService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Intent intentNotification = new Intent(getString(R.string.drink_broadcast));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNotification);
    }




}
