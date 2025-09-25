package com.example.smokecounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmokeManager {

    private static SmokeManager instance;
    private int dailyCount = 0;
    private SharedPreferences prefs;

    private SmokeManager(Context context) {
        prefs = context.getSharedPreferences("smoke_data", Context.MODE_PRIVATE);
        dailyCount = prefs.getInt("today_count", 0);
    }

    public static SmokeManager getInstance(Context context) {
        if (instance == null) {
            instance = new SmokeManager(context.getApplicationContext());
        }
        return instance;
    }

    public void incrementCount() {
        dailyCount++;
        saveTodayCount();
    }

    public int getDailyCount() {
        return dailyCount;
    }

    public void resetDailyCount() {
        dailyCount = 0;
        saveTodayCount();
    }

    private void saveTodayCount() {
        prefs.edit().putInt("today_count", dailyCount).apply();
    }

    public void logDaily() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        prefs.edit().putInt(today, dailyCount).apply();
    }

    public int getMonthlyTotal() {
        int total = 0;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(currentTime - i * 86400000L);
            total += prefs.getInt(date, 0);
        }
        return total;
    }
}