package com.mr.timeindicator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mr.timeindicatorview.TimeIndicatorView;

public class MainActivity extends AppCompatActivity {

    TimeIndicatorView timeIndicatorView;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeIndicatorView = findViewById(R.id.time_indicator_view);
        timeIndicatorView.setStartTime(1954654564);
        timeIndicatorView.start();

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeIndicatorView.start();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeIndicatorView.stop();
            }
        });
        findViewById(R.id.tv_smaller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeIndicatorView.setTimeTextSize(timeIndicatorView.getTimeTextSize() - 1);
            }
        });
        findViewById(R.id.tv_bigger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeIndicatorView.setTimeTextSize(timeIndicatorView.getTimeTextSize() + 1);
            }
        });

    }
}