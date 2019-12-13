package com.freak.countdownview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.freak.countdown.CountDownView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CountDownView count_down_view;
    private CountDownView count_down_view1;
    private CountDownView count_down_view2;
    private TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count_down_view = findViewById(R.id.count_down_view);
        count_down_view1 = findViewById(R.id.count_down_view1);
        count_down_view2 = findViewById(R.id.count_down_view2);
        test = findViewById(R.id.test);

        count_down_view.setCountTime(1574394865).startCountDown();
        count_down_view1.setCountDownTime(-1).startCountDown();
        count_down_view2.setCountDownTime(-1).startCountDown();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count_down_view.stopCountDown();
        count_down_view1.stopCountDown();
        count_down_view2.stopCountDown();
    }
}
