package com.freak.countdownview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.freak.countdown.CountDownView;

public class MainActivity extends AppCompatActivity {
    private CountDownView count_down_view;
    private CountDownView count_down_view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count_down_view = findViewById(R.id.count_down_view);
        count_down_view1 = findViewById(R.id.count_down_view1);
        count_down_view.setCountTime(1574158840).startCountDown();
        count_down_view1.setCountAllTime(4000000).startCountDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count_down_view.stopCountDown();
        count_down_view1.stopCountDown();
    }
}
