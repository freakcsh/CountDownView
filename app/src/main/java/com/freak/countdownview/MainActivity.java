package com.freak.countdownview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.freak.countdown.CountDownView;

public class MainActivity extends AppCompatActivity {
    private CountDownView count_down_view;
    private CountDownView count_down_view1;
    private CountDownView count_down_view2;
    private TextView test;
    private TextView test1;
    private CountDownImage count_down_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count_down_view = findViewById(R.id.count_down_view);
        count_down_view1 = findViewById(R.id.count_down_view1);
        count_down_view2 = findViewById(R.id.count_down_view2);
        test = findViewById(R.id.test);
        test1 = findViewById(R.id.test1);
        count_down_image = findViewById(R.id.count_down_image);

        count_down_view.setCountDownTime(13, 6).startCountDown().setCountDownBringForwardNotificationListener(new CountDownView.CountDownBringForwardNotificationListener() {
            @Override
            public void bringForwardNotification() {
                Log.e("TAG", "调用提前通知");
            }
        }).setCountDownEndListener(new CountDownView.CountDownEndListener() {
            @Override
            public void onCountDownEnd() {
                Log.e("TAG", "倒计时结束");
            }
        });
        count_down_view1.setCountDownTime(-1).startCountDown();
        count_down_view2.setCountDownTime(-1).startCountDown();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count_down_image.startCountDown();
            }
        });
        count_down_image.setCountDownResource(R.drawable.ic_icon_1, R.drawable.ic_icon_2, R.drawable.ic_icon_3, R.drawable.ic_icon_4, R.drawable.ic_icon_5).setCountDownEndListener(new CountDownImage.CountDownEndListener() {
            @Override
            public void onCountDownEnd() {
                Log.e("TAG", "图片倒计时结束");
                count_down_image.setVisibility(View.GONE);
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
