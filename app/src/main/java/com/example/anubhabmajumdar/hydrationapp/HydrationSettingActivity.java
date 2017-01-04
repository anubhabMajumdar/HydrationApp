package com.example.anubhabmajumdar.hydrationapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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
import java.util.Locale;

public class HydrationSettingActivity extends AppCompatActivity {

    int cur_hour, cur_min;
    TextView t;

    int start_hour, start_min, end_hour, end_min, notification_interval;
    double quantity;

    public HydrationSettingActivity() {
        this.start_hour = -1;
        this.start_min = -1;
        this.end_hour = -1;
        this.end_min = -1;
        this.notification_interval = -1;
        this.quantity = 2.0;
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
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        this.setTime(hour, minute);
    }

    public void showTimePickerDialog(View v) {

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");

    }

    public void initialSetup()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // add back button

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

        final EditText quant = (EditText) findViewById(R.id.quantity);
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
                quantity = 2.0;
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
        Intent intent = new Intent();
        intent.putExtra("start_hour", Integer.toString(start_hour));
        intent.putExtra("start_min", Integer.toString(start_min));
        intent.putExtra("end_hour", Integer.toString(end_hour));
        intent.putExtra("end_min", Integer.toString(end_min));
        intent.putExtra("notification_interval", Integer.toString(notification_interval));
        intent.putExtra("quantity", Double.toString(quantity));
        return intent;
    }

    /* --------------------------------------------------- onCreate ----------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration_setting);

        this.initialSetup();

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = this.setIntentToSend();
        setResult(RESULT_OK, intent);
        finish();
        return true;
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



    @Override
    public void onBackPressed() {
        Intent intent = this.setIntentToSend();

        setResult(RESULT_OK, intent);
        finish();
    }
    /* --------------------------------------------------- Inner Class ------------------------------------------- */

    public class SpinnerActivity extends Activity implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            notification_interval =  Integer.parseInt(parent.getItemAtPosition(pos).toString());

         }

        public void onNothingSelected(AdapterView<?> parent) {
            notification_interval = Integer.parseInt(parent.getItemAtPosition(0).toString());

        }
    }

}
