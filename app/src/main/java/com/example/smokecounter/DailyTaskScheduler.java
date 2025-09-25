package com.example.smokecounter;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyTaskScheduler {
    public static void scheduleDailyWork(Context context) {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 23);
        next.set(Calendar.MINUTE, 59);
        next.set(Calendar.SECOND, 0);

        if (next.before(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = next.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(DailyWorker.class, 24, TimeUnit.HOURS)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
