package com.example.smokecounter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyResetWorker extends Worker {

    public DailyResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SmokeManager smokeManager = SmokeManager.getInstance(getApplicationContext());

        // Log today’s count and reset
        smokeManager.logDaily();
        smokeManager.resetDailyCount();

        return Result.success();
    }
}