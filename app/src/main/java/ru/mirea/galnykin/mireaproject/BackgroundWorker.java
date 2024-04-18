package ru.mirea.galnykin.mireaproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class BackgroundWorker extends Worker {
    static final String TAG = "BackgroundWorker";
    public BackgroundWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat timems = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = timems.format(new Date(currentTimeMillis));
        Log.d(TAG,"Time: " + time);
        scheduleNextUpdate();
        return Result.success();
    }

    private void scheduleNextUpdate() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
    }
}