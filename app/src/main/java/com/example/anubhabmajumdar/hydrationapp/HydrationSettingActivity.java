package com.example.anubhabmajumdar.hydrationapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HydrationSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // add back button
    }
}
