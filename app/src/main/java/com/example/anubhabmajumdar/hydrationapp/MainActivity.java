package com.example.anubhabmajumdar.hydrationapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import static com.example.anubhabmajumdar.hydrationapp.R.id.chart;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int notification_interval, glass_size, totalWaterConsumption;
    double quantity;
    String appUser, start_day, end_day;
    Boolean wantNotification;
    NavigationView navigationView;
    Stack waterConsumptionStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(setNextNotification,
                new IntentFilter(getString(R.string.setNextNotification)));
        LocalBroadcastManager.getInstance(this).registerReceiver(drinkBroadcast,
                new IntentFilter(getString(R.string.drink_broadcast)));
        LocalBroadcastManager.getInstance(this).registerReceiver(reset,
                new IntentFilter(getString(R.string.reset_name)));

//        boolean timeChanged = changedTime();
        extractSettingsData();
        setUpPieChart();
        setAppUser();
        multipleFAB();
//        if (!timeChanged)
//        {
//            setRepeatingAlarm();
//            stopAlarm();
//        }
//        showToast("onCreate");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        boolean timeChanged = changedTime();
        extractSettingsData();
        if (!timeChanged)
        {
            setRepeatingAlarm();
            stopAlarm();
        }
        setUpPieChart();
        setAppUser();
        multipleFAB();
//        showToast("onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveTotalWaterConsumption();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        saveTotalWaterConsumption();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share)
        {

        }
        else if (id == R.id.nav_send)
        {

        }

        else if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.reset)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Reset water consumption data?")
                    .setMessage("Are you sure you want to reset this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            totalWaterConsumption = 0;
                            saveTotalWaterConsumption();
                            setWaterConsumptionStack(new Stack());
                            setUpPieChart();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else if (id == R.id.undo)
        {
            undoWaterConsumption();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/* --------------------------------------------- myFunctions() ------------------------------------------------------------------ */

    public void extractSettingsData()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        this.start_day = sharedPref.getString(getString(R.string.start_day_key), "8:00");
        this.end_day = sharedPref.getString(getString(R.string.end_day_key), "23:00");
        this.notification_interval = parseInt(sharedPref.getString(getString(R.string.notification_interval), getString(R.string.notification_default)));
        this.quantity = Double.parseDouble(sharedPref.getString(getString(R.string.quantity), getString(R.string.quantity_default)));
        this.glass_size = parseInt(sharedPref.getString(getString(R.string.glass_size), getString(R.string.glass_size_default)));
        this.totalWaterConsumption = parseInt(sharedPref.getString(getString(R.string.total_consumption), getString(R.string.totalWaterConsumption_default)));
        this.appUser = sharedPref.getString(getString(R.string.display_name_key), getString(R.string.pref_default_display_name));
        this.waterConsumptionStack = deserialize
                (sharedPref.getString(getString(R.string.waterConsumptionStack_key), serialize(new Stack())));
        this.wantNotification = sharedPref.getBoolean(getString(R.string.notification_state_key), true);
        //String ringtone = sharedPref.getString(getString(R.string.ringtone_key), getString(R.string.default_ringtone));
        //showToast(Boolean.toString(wantNotification));
        //showToast(ringtone);
    }

    public void saveTotalWaterConsumption()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.total_consumption), Integer.toString(totalWaterConsumption));
        editor.apply();
    }

    public void showToast(String text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setUpPieChart()
    {
        PieChart pieChart = (PieChart) findViewById(chart);
        if (pieChart != null)
        {
            pieChart.getDescription().setEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.setTouchEnabled(false);
            pieChart.setRotationEnabled(false);

            int color_grey = getResources().getColor(R.color.grey);
            pieChart.setBackgroundColor(color_grey);
            pieChart.setHoleColor(color_grey);
            pieChart.animateY(1000);

            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(totalWaterConsumption, "Water Consumed (in ml)"));
            int remaining = Math.max(0, ((int) (quantity * 1000) - totalWaterConsumption));
            entries.add(new PieEntry(remaining, "Remaining (in ml)"));

            PieDataSet set = new PieDataSet(entries, "Water Consumption");
            int color_green = getResources().getColor(R.color.darkgreen);
            int color_blue = getResources().getColor(R.color.darkblue);

            set.setColors(new int[]{color_blue, color_green});
            set.setValueTextSize(25);

            PieData data = new PieData(set);
            pieChart.setData(data);

            pieChart.invalidate(); // refresh
        }
    }

    public void updateWaterConsumption(int glass_size)
    {
        totalWaterConsumption = totalWaterConsumption + glass_size;

        saveTotalWaterConsumption();
        updateLatestWaterConsumption(totalWaterConsumption);

        setUpPieChart();
    }

    public void setAppUser()
    {
        View header=navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.appUser);
        userName.setText(appUser);
    }

    public void multipleFAB()
    {
        final int[] buttonGlassSizes = glassSizesForFAB();

        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setTitle(buttonGlassSizes[0]+" ml");
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                updateWaterConsumption(buttonGlassSizes[0]);
            }
        });

        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setTitle(buttonGlassSizes[1]+" ml");
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                updateWaterConsumption(buttonGlassSizes[1]);
            }
        });

        final FloatingActionButton actionC = (FloatingActionButton) findViewById(R.id.action_c);
        actionC.setTitle(buttonGlassSizes[2]+" ml");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                updateWaterConsumption(buttonGlassSizes[2]);
            }
        });
    }

    public int[] glassSizesForFAB()
    {
        String[] test;
        int[] buttonGlassSizes = {0, 0, 0};
        int start, end, i;

        test = getResources().getStringArray(R.array.glass_size_array);

        int index = java.util.Arrays.asList(test).indexOf(Integer.toString(glass_size));

        if (index == 0)
        {
            start = 0;
            end = 2;
        }
        else if (index == (test.length-1))
        {
            start = index-2;
            end = index;
        }
        else
        {
            start = index-1;
            end = index+1;
        }

        for (i=start;i<=end;i++)
            buttonGlassSizes[i-start] = parseInt(test[i]);

        return buttonGlassSizes;
    }

    public void updateLatestWaterConsumption(int totalWaterConsumption)
    {
        waterConsumptionStack = getWaterConsumptionStack();
        waterConsumptionStack.push(totalWaterConsumption);
        setWaterConsumptionStack(waterConsumptionStack);
    }

    public void undoWaterConsumption()
    {
        waterConsumptionStack = getWaterConsumptionStack();

        if (!waterConsumptionStack.empty())
        {
            waterConsumptionStack.pop();

            if (waterConsumptionStack.empty())
                totalWaterConsumption = 0;
            else
                totalWaterConsumption = (Integer) waterConsumptionStack.peek();

            setWaterConsumptionStack(waterConsumptionStack);
            saveTotalWaterConsumption();
            setUpPieChart();
            //showToast("non empty undoWaterConsumption");
        }

    }

    public Stack getWaterConsumptionStack()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Stack waterConsumptionStack = deserialize
                (sharedPref.getString(getString(R.string.waterConsumptionStack_key), serialize(new Stack())));

        return waterConsumptionStack;
    }

    public void setWaterConsumptionStack(Stack waterConsumptionStack)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.waterConsumptionStack_key), serialize(waterConsumptionStack));
        editor.apply();
    }

    public String serialize(Stack stack)
    {
        String serializedArrayList = "";

        if (stack.empty())
            return serializedArrayList;

        while (!stack.empty())
        {
            serializedArrayList = serializedArrayList + Integer.toString((Integer) stack.pop()) + ",";
        }

        serializedArrayList = serializedArrayList.substring(0, serializedArrayList.length()-1);
        //showToast(serializedArrayList);
        return  serializedArrayList;
    }

    public Stack deserialize(String serializedArrayList)
    {
        Stack waterConsumptionStack = new Stack();

        if (serializedArrayList.equals(""))
            return waterConsumptionStack;

        String[] numbers = serializedArrayList.split(",");


        for (int i=(numbers.length-1); i>=0; i--)
            waterConsumptionStack.push(Integer.parseInt(numbers[i]));

        return waterConsumptionStack;
    }


    public void resetWaterConsumption()
    {
        totalWaterConsumption = 0;
        saveTotalWaterConsumption();
        setWaterConsumptionStack(new Stack());
    }


/* ------------------------------------ Notifications -----------------------------------------------------------*/
    private BroadcastReceiver setNextNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            extractSettingsData();
        }
    };

    private BroadcastReceiver drinkBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWaterConsumption(glass_size);
            extractSettingsData();
        }
    };

    private BroadcastReceiver reset = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            extractSettingsData();
            resetWaterConsumption();
            setUpPieChart();
            setRepeatingAlarm(timeDifference());
        }
    };

    public void setRepeatingAlarm()
    {
        //showToast("Set Repeating");
        int[] time = splitTime(start_day);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartNotificationService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
        calendar.set(Calendar.MINUTE, time[1]);
        //calendar.set(Calendar.AM_PM, Calendar.AM);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * notification_interval, alarmIntent);
    }

    public void setRepeatingAlarm(long diff)
    {
        showToast("Set Repeating diff " + Long.toString(diff));
        int[] time = splitTime(start_day);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartNotificationService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long curTime = ((calendar.get(calendar.HOUR_OF_DAY)*60) + calendar.get(calendar.MINUTE))*60*1000;
        curTime += diff;

        //showToast(Long.toString(System.currentTimeMillis()));

//        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
//        calendar.set(Calendar.MINUTE, time[1]);
        //calendar.set(Calendar.AM_PM, Calendar.AM);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, curTime,
                1000 * 60 * notification_interval, alarmIntent);
        Log.v("ReapeatAlarm", Long.toString(curTime));
    }

    public void stopAlarm()
    {
        //showToast("Set stop alarm");
        int[] time = splitTime(end_day);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StopNotificationService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
        calendar.set(Calendar.MINUTE, time[1]);
        //calendar.set(Calendar.AM_PM, Calendar.AM);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public int[] splitTime(String timeString)
    {
        String[] time = timeString.split(":");
        int[] actualTime = {0,0};
        actualTime[0] = Integer.parseInt(time[0]);
        actualTime[1] = Integer.parseInt(time[1]);
        //showToast(Integer.toString(actualTime[0]));
        return actualTime;
    }

    public long timeDifference()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long curTime = ((calendar.get(calendar.HOUR_OF_DAY)*60) + calendar.get(calendar.MINUTE))*60*1000;

        int[] time = splitTime(start_day);
        long startTime = ((time[0]*60) + time[1])*60*1000;

        long diff;

        if (curTime>startTime)
        {
            diff = ((24*60*60*1000) - curTime) + startTime;
        }
        else
        {
            diff = startTime-curTime;
        }

        Log.v("timeDifference_curtime", Long.toString(curTime));
        Log.v("timeDifference_start", Long.toString(startTime));
        Log.v("timeDifference", Long.toString(diff));
        return diff;
    }

    public boolean changedTime()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String tempStartTime = sharedPref.getString(getString(R.string.start_day_key), "8:00");
        String tempEndTime = sharedPref.getString(getString(R.string.end_day_key), "23:00");
        int tempNotificationInterval = parseInt(sharedPref.getString(getString(R.string.notification_interval), getString(R.string.notification_default)));

        return (tempStartTime.equals(this.start_day) && tempEndTime.equals(this.end_day) && (tempNotificationInterval==this.notification_interval));

    }


}
