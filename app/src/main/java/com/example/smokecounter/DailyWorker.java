package com.example.smokecounter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyWorker extends Worker {

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SmokeManager smokeManager = SmokeManager.getInstance(getApplicationContext());

        // Log today’s count before resetting
        smokeManager.logDaily();

        // Reset daily count
        smokeManager.resetDailyCount();

        return Result.success();
    }
}