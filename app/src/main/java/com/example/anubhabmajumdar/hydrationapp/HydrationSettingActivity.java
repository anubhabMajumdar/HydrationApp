package com.example.anubhabmajumdar.hydrationapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class HydrationSettingActivity extends AppCompatActivity {

    int cur_hour, cur_min;
    TextView t;

    int start_hour, start_min, end_hour, end_min, notification_interval, glass_size;
    double quantity;
    public HydrationSettingActivity() {
        this.start_hour = -1;
        this.start_min = -1;
        this.end_hour = -1;
        this.end_min = -1;
        this.notification_interval = -1;
        this.quantity = 2.0;
        this.glass_size = 150;
    }

    /* --------------------------------------------------- Helper functions ----------------------------------------- */



    public void setTime(int hour, int min)
    {
        this.cur_hour = hour;
        this.cur_min = min;
        this.setTimeInstanceVariable(t);
        this.setTimeTextView(t);
    }

    public void setTimeInstanceVariable(TextView t)
    {
        if(t==((TextView)findViewById(R.id.start_of_day)))
        {
            start_hour = cur_hour;
            start_min = cur_min;
        }
        else
        {
            end_hour = cur_hour;
            end_min = cur_min;
        }

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

    public void setDefaultTime()
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour, minute;
        if(t==(findViewById(R.id.start_of_day)))
        {
            hour = sharedPref.getInt(getString(R.string.start_hour), c.get(Calendar.HOUR_OF_DAY));
            minute = sharedPref.getInt(getString(R.string.start_min), c.get(Calendar.MINUTE));

        }
        else
        {
            hour = sharedPref.getInt(getString(R.string.end_hour), c.get(Calendar.HOUR_OF_DAY));
            minute = sharedPref.getInt(getString(R.string.end_min), c.get(Calendar.MINUTE));
        }

        this.setTime(hour, minute);
    }

    public void showTimePickerDialog(View v) {

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");

    }

    public void initialSetup()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // add back button

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);


        Spinner spinner = (Spinner) findViewById(R.id.notification_interval);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_interval_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // set OnItemSelectedListener
        spinner.setOnItemSelectedListener(new SpinnerActivity());
        int spinnerVal = sharedPref.getInt(getString(R.string.notification_interval), 15);
        int spinnerPosition = adapter.getPosition(Integer.toString(spinnerVal));
        spinner.setSelection(spinnerPosition);

        Spinner spinner_glass_size = (Spinner) findViewById(R.id.glass_size);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_glass_size = ArrayAdapter.createFromResource(this,
                R.array.glass_size_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_glass_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_glass_size.setAdapter(adapter_glass_size);
        // set OnItemSelectedListener
        spinner_glass_size.setOnItemSelectedListener(new SpinnerActivity());
        spinnerVal = sharedPref.getInt(getString(R.string.glass_size), 150);
        spinnerPosition = adapter_glass_size.getPosition(Integer.toString(spinnerVal));
        spinner_glass_size.setSelection(spinnerPosition);

        final EditText quant = (EditText) findViewById(R.id.quantity);
        final String saved_quantity = sharedPref.getString(getString(R.string.quantity), "2.5");
        quant.setText(saved_quantity);

        quant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {
                if (s.length()>0)
                {
                    quantity = Double.parseDouble(quant.getText().toString());
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                quantity = Double.parseDouble(saved_quantity);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        t = (TextView) findViewById(R.id.start_of_day);
        this.setDefaultTime();

        t = (TextView) findViewById(R.id.end_of_day);
        this.setDefaultTime();
    }

    public Intent setIntentToSend()
    {
        Intent intent = new Intent(this, HydrationTrackerActivity.class);
        return intent;
    }

    public void saveSettings()
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.state_key), getString(R.string.state_ready));
        editor.putInt(getString(R.string.start_hour), start_hour);
        editor.putInt(getString(R.string.start_min), start_min);
        editor.putInt(getString(R.string.end_hour), end_hour);
        editor.putInt(getString(R.string.end_min), end_min);
        editor.putInt(getString(R.string.notification_interval), notification_interval);
        editor.putString(getString(R.string.quantity), Double.toString(quantity));
        editor.putInt(getString(R.string.glass_size), glass_size);

        editor.apply();
    }

    public void setTimePickerState(String state)
    {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.timepicker_state), state);
        editor.apply();

    }
    /* --------------------------------------------------- onCreate ----------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration_setting);

        this.initialSetup();

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        this.saveSettings();
        Intent intent = this.setIntentToSend();
        startActivity(intent);
        finish();
        return true;
    }
    /* --------------------------------------------------- Actual Work ----------------------------------------- */


    public void selectStartTime(View v)
    {
        t = (TextView) findViewById(R.id.start_of_day);
        this.setTimePickerState(getString(R.string.timepicker_start));
        this.showTimePickerDialog(v);


    }

    public void selectEndTime(View v)
    {
        t = (TextView) findViewById(R.id.end_of_day);
        this.setTimePickerState(getString(R.string.timepicker_end));
        this.showTimePickerDialog(v);


    }



    @Override
    public void onBackPressed() {
        this.saveSettings();
        Intent intent = this.setIntentToSend();
        startActivity(intent);
        finish();
    }


    /* --------------------------------------------------- Inner Class ------------------------------------------- */

    public class SpinnerActivity extends Activity implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            if (parent.getId()==R.id.glass_size)
                glass_size = Integer.parseInt(parent.getItemAtPosition(pos).toString());
            else
                notification_interval =  Integer.parseInt(parent.getItemAtPosition(pos).toString());


        }

        public void onNothingSelected(AdapterView<?> parent) {
            if (parent.getId()==R.id.glass_size)
                glass_size = Integer.parseInt(parent.getItemAtPosition(0).toString());
            else
                notification_interval = Integer.parseInt(parent.getItemAtPosition(0).toString());


        }
    }

}
