package com.example.anubhabmajumdar.hydrationapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HydrationTrackerActivity extends AppCompatActivity {

    int start_hour, start_min, end_hour, end_min, notification_interval, glass_size, totalWaterConsumption;
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
        {
            setContentView(R.layout.activity_main);
            //showToast("default");
        }
        else if (state.equals(state_ready))
        {
            setContentView(R.layout.activity_hydration_tracker);
            this.handleSettings();
            //showToast("ready");
        }
        else
        {
            setContentView(R.layout.activity_hydration_tracker);
            this.extractSettingsData();
            setUpPieChart();
            //this.showToast("set");
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
        this.glass_size = sharedPref.getInt(getString(R.string.notification_interval), -1);
        this.totalWaterConsumption = sharedPref.getInt(getString(R.string.total_consumption), 0);
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
        {
            this.handleNotification();
            this.setUpPieChart();

            SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(getString(R.string.state_key), getString(R.string.state_set));
            editor.apply();
        }

    }

    /* --------------------------------------------------- Actual functions ----------------------------------------- */


    public void openHydrationSetting(MenuItem item)
    {
        Intent intent = new Intent(this, HydrationSettingActivity.class);
        startActivity(intent);
    }

    public void handleNotification()
    {
        this.startNotification();
        this.stopNotification();
    }

    public void startNotification()
    {
        Intent myIntent = new Intent(this , NotificationService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, start_hour);
        calendar.set(Calendar.MINUTE, start_min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notification_interval*60*1000 , pendingIntent);  //set repeating every 24 hours
    }

    public void stopNotification()
    {
        Intent myIntent = new Intent(this , StopNotificationService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, end_hour);
        calendar.set(Calendar.MINUTE, end_min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void setUpPieChart()
    {
        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setTransparentCircleColor(Color.WHITE);

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(totalWaterConsumption, "Water Consumed"));
        int remaining = Math.max (0, ((int) (quantity*1000) - totalWaterConsumption));
        entries.add(new PieEntry(remaining, "Remaining"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        int color_green = getResources().getColor(R.color.darkgreen);
        int color_blue = getResources().getColor(R.color.darkblue);

        set.setColors(new int[] { color_blue, color_green });


        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }

    public void updateWaterConsumption(View v)
    {
        totalWaterConsumption = totalWaterConsumption + glass_size;

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.total_consumption), totalWaterConsumption);
        editor.apply();

        setUpPieChart();
    }
}
