package com.example.anubhabmajumdar.hydrationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int start_hour, start_min, end_hour, end_min, notification_interval;
    double quantity;
    String state = "initial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void extractSettingsData(Intent data)
    {
        this.start_hour = Integer.parseInt(data.getStringExtra("start_hour"));
        this.start_min = Integer.parseInt(data.getStringExtra("start_min"));
        this.end_hour = Integer.parseInt(data.getStringExtra("end_hour"));
        this.end_min = Integer.parseInt(data.getStringExtra("end_min"));
        this.notification_interval = Integer.parseInt(data.getStringExtra("notification_interval"));
        this.quantity = (Math.round(Double.parseDouble(data.getStringExtra("quantity"))*10.0))/10.0;
    }

    public boolean verifySettingsData()
    {
        boolean flag = true;

        if (start_hour == -1 || start_min == -1 || end_hour == -1 || end_min == -1 || notification_interval == -1 || quantity == -1)
            flag = false;

        return flag;
    }

    /* --------------------------------------------------- Actual functions ----------------------------------------- */


    public void openHydrationSetting(MenuItem item)
    {
        Intent intent = new Intent(this, HydrationSettingActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {

                this.extractSettingsData(data);
                if (!this.verifySettingsData())
                    this.showToast("Oops! Something went wrong with settings");

            }
        }
    }

}
