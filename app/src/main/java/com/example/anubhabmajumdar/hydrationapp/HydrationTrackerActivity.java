package com.example.anubhabmajumdar.hydrationapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HydrationTrackerActivity extends AppCompatActivity {

    int start_hour, start_min, end_hour, end_min, notification_interval;
    double quantity;
    int mId = 1;

    /* --------------------------------------------------- onCreate ------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String state_default = getResources().getString(R.string.state_default);
        String state_ready = getResources().getString(R.string.state_ready);
        String state = sharedPref.getString(getString(R.string.state_key), state_default);

        if (state.equals(state_default))
            setContentView(R.layout.activity_main);
        else if (state.equals(state_ready))
        {
            setContentView(R.layout.activity_hydration_tracker);
            this.handleSettings();
            this.sendNotification();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /* --------------------------------------------------- Helper functions ----------------------------------------- */

    public void showToast(String text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void extractSettingsData()
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        this.start_hour = sharedPref.getInt(getString(R.string.start_hour), -1);
        this.start_min = sharedPref.getInt(getString(R.string.start_min), -1);
        this.end_hour = sharedPref.getInt(getString(R.string.end_hour), -1);
        this.end_min = sharedPref.getInt(getString(R.string.end_min), -1);
        this.notification_interval = sharedPref.getInt(getString(R.string.notification_interval), -1);
        this.quantity = (Math.round(Double.parseDouble(sharedPref.getString(getString(R.string.quantity), "2.0"))*10.0))/10.0;
    }

    public boolean verifySettingsData()
    {
        boolean flag = true;

        if (start_hour == -1 || start_min == -1 || end_hour == -1 || end_min == -1 || notification_interval == -1 || quantity == -1)
            flag = false;

        return flag;
    }


    public void handleSettings()
    {
        this.extractSettingsData();
        if (!this.verifySettingsData())
            this.showToast("Oops! Something went wrong with settings");
        else
            this.showToast(Double.toString(quantity));
    }

    /* --------------------------------------------------- Actual functions ----------------------------------------- */


    public void openHydrationSetting(MenuItem item)
    {
        Intent intent = new Intent(this, HydrationSettingActivity.class);
        startActivity(intent);
    }

    public void sendNotification()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.water_glass)
                        .setContentTitle("Drink water")
                        .setContentText("It's time to have a glass of water!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HydrationTrackerActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HydrationTrackerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

}
