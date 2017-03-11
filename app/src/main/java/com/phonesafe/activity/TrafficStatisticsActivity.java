package com.phonesafe.activity;

import android.app.Activity;
import android.os.Bundle;

import com.phonesafe.R;
import com.phonesafe.engine.TrafficParser;

public class TrafficStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_statistics);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TrafficParser.getAllTrafficList(TrafficStatisticsActivity.this);
            }
        }).start();
    }
}
