package com.example.anubhabmajumdar.hydrationapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class HydrationSettingActivity extends AppCompatActivity {

    int cur_hour, cur_min;
    TextView t;
    TextView startTime;

    /* --------------------------------------------------- Helper functions ----------------------------------------- */

    public void setTime(int hour, int min)
    {
        this.cur_hour = hour;
        this.cur_min = min;
        this.setTimeTextView(t);
    }

    public void setTimeTextView(TextView t)
    {
        int h = cur_hour%12, m = cur_min;
        String suffix;

        if (cur_hour==12)
            h = 12;

        if (cur_hour>=12)
        {
            suffix = "PM";
        }
        else
        {
            suffix = "AM";
        }
        if (cur_min<10)
            t.setText(Integer.toString(h)+":0"+ Integer.toString(m)+" "+suffix);
        else
            t.setText(Integer.toString(h)+":"+ Integer.toString(m)+" "+suffix);
    }

    public void setDefaultTime(TextView t)
    {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        this.setTime(hour, minute);
    }

    public void showTimePickerDialog(View v) {

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");

    }

    /* --------------------------------------------------- onCreate ----------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // add back button

        t = (TextView) findViewById(R.id.start_of_day);
        this.setDefaultTime(startTime);

        t = (TextView) findViewById(R.id.end_of_day);
        this.setDefaultTime(startTime);
    }

    /* --------------------------------------------------- Actual Work ----------------------------------------- */


    public void selectStartTime(View v)
    {
        t = (TextView) findViewById(R.id.start_of_day);
        this.showTimePickerDialog(v);
    }

    public void selectEndTime(View v)
    {
        t = (TextView) findViewById(R.id.end_of_day);
        this.showTimePickerDialog(v);
    }


}
