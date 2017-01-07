package com.example.anubhabmajumdar.hydrationapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

public class ResetAppService extends IntentService {

    public ResetAppService() {
        super("ResetAppService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            this.resetApp();
        }
    }

    public void resetApp()
    {
        this.updateWaterConsumption();

        Intent intent = new Intent(getString(R.string.appReset));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public void updateWaterConsumption()
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.total_consumption), 0);
        editor.apply();
    }
}
