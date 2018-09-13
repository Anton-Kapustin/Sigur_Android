package com.anton.sigurmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    String LOG_TAG = getClass().getName().toString();
    Button buttonAccept;
    Button buttonReject;
    FrameLayout frameLayoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonAccept = (Button) findViewById(R.id.button_accept);
        buttonReject = (Button) findViewById(R.id.button_reject);
        frameLayoutMain = (FrameLayout) findViewById(R.id.frameLayout_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
