package com.example.anubhabmajumdar.hydrationapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class fabTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_test);



        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionA.setTitle("Action A clicked");
                showToast("A clicked");
            }
        });

        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionB.setTitle("Action B clicked");
            }
        });

        final FloatingActionButton actionC = (FloatingActionButton) findViewById(R.id.action_c);
        actionC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionC.setTitle("Action C clicked");
            }
        });

    }

    public void showToast(String text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}