package com.example.regexps;

import android.os.Handler;
import android.widget.TextView;

public class Timer {
    private TextView timerTextView;
    private static long startTime, pauseTime;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    Timer (TextView timerTextView) {
        this.timerTextView = timerTextView;
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void pauseTimer() {
        pauseTime = System.currentTimeMillis();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void continueTimer() {
        startTime = startTime + (System.currentTimeMillis() - pauseTime);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public int stopAndReturnTimer() {
        return 0;
    }
}
