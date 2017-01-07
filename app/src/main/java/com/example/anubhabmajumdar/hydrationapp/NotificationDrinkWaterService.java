package com.example.anubhabmajumdar.hydrationapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationDrinkWaterService extends IntentService {
    public NotificationDrinkWaterService() {
        super("NotificationDrinkWaterService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            updateWaterConsumption();
            updatePieChart();

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }
    }

    public void updatePieChart()
    {
        this.updateWaterConsumption();

        Intent intent = new Intent(getString(R.string.bigText_updatePiechart));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void updateWaterConsumption()
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int totalConsumption = sharedPref.getInt(getString(R.string.total_consumption), 0);
        int glass_size = sharedPref.getInt(getString(R.string.glass_size), 150);

        editor.putInt(getString(R.string.total_consumption), (totalConsumption+glass_size));
        editor.apply();
    }


}
