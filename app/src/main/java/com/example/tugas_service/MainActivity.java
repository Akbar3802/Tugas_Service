package com.example.tugas_service;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 600000;
    TextView mCountDown;
    Button BtnStartPause, BtnReset;
    CountDownTimer countDownTimer;
    boolean TimerRunning;
    long TimeLeftInMillis = START_TIME_IN_MILLIS;
    long EndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountDown = findViewById(R.id.tv_countDown);

        BtnStartPause = findViewById(R.id.btnStartPause);
        BtnReset = findViewById(R.id.btnReset);

        BtnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer() {
        EndTime = System.currentTimeMillis() + TimeLeftInMillis;
        countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                TimerRunning = false;
                BtnStartPause.setText("Start");
                BtnStartPause.setVisibility(View.INVISIBLE);
                BtnReset.setVisibility(View.VISIBLE);
            }
        }.start();

        TimerRunning = true;
        BtnStartPause.setText("pause");
        BtnReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        TimerRunning = false;
        BtnStartPause.setText("Start");
        BtnReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        TimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        BtnReset.setVisibility(View.INVISIBLE);
        BtnStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (TimeLeftInMillis / 1000) / 60;
        int seconds = (int) (TimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mCountDown.setText(timeLeftFormatted);
    }

    @Override
    protected void onStop() {
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("millisleft", TimeLeftInMillis);
        editor.putBoolean("timerRunning", TimerRunning);
        editor.putLong("endTime", EndTime);
        editor.apply();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onStop();

    }

    @Override
    protected void onStart() {
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        TimeLeftInMillis = preferences.getLong("millisLeft", START_TIME_IN_MILLIS);
        TimerRunning = preferences.getBoolean("timerRunning", false);
        updateCountDownText();
        if (TimerRunning) {
            EndTime = preferences.getLong("endTime", 0);
            TimeLeftInMillis = EndTime - System.currentTimeMillis();
            if (TimeLeftInMillis < 0) {
                TimeLeftInMillis = 0;
                TimerRunning = false;
                updateCountDownText();
            } else {
                startTimer();
            }
        }
        super.onStart();
    }
}