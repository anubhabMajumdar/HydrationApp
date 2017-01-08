package com.example.anubhabmajumdar.hydrationapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StartNotificationService extends IntentService
{

    int mId = 1;


    public StartNotificationService() {

        super("StartNotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String notification_state = sharedPref.getString(getString(R.string.notification_state), getString(R.string.normal_notification));
        String no_n = getString(R.string.no_notification);

        if (intent != null)
        {
            if (!notification_state.equals(no_n))
                this.sendNotification();

            Intent intentNotification = new Intent(getString(R.string.setNextNotification));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentNotification);
        }
    }

    public void sendNotification()
    {

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String notification_state = sharedPref.getString(getString(R.string.notification_state), getString(R.string.normal_notification));
        String normal_n = getString(R.string.normal_notification);



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

        if (notification_state.equals(normal_n))
        {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }

        mBuilder.setAutoCancel(true);

        Intent drinkWater = new Intent(this, NotificationDrinkWaterService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, drinkWater, 0);

        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("It's time to have a glass of water"))
                .addAction (R.drawable.water_glass,
                        getString(R.string.bigText_glass), pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

}
