package com.blogspot.gm4s1.gmutils._bases;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.MessagingCenter;
import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.dialogs.MessageDialog;
import com.blogspot.gm4s1.gmutils.storage.StorageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static BaseApplication current;
    public static BaseApplication current() {
        return  current;
    }

    private final Map<String, Object> globalInstances = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        StorageManager.registerCallback(() -> BaseApplication.this);

        registerDefaultUncaughtExceptionHandler();

        registerActivityLifecycleCallbacks(this);

        onPostCreate();
    }

    protected abstract void onPostCreate();

    public void addGlobalInstance(String key, Object instance) {
        this.globalInstances.put(key, instance);
    }

    public Object getGlobalInstance(String key) {
        return this.globalInstances.get(key);
    }

    public MessagingCenter getMessagingCenter() {
        return MessagingCenter.getInstance();
    }

    //----------------------------------------------------------------------------------------------

    private int activityCount = 0;
    private final long delayAmount = 500L;
    private final String bugFileName = "BUGS";
    private String bugs = "";


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activityCount++;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount == 1) {
                current = this;
                onApplicationStartedFirstActivity();

                if (bugs.length() != 0 && Logger.IS_WRITE_TO_FILE_ENABLED()) {
                    MessageDialog.create(activity)
                            .setMessage(bugs)
                            .setButton1(R.string.ok, null)
                            .show();

                    bugs = "";

                    Logger.deleteSavedFile(this, bugFileName);
                }
            }
        }, delayAmount);

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        activityCount--;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount <= 0) {
                onApplicationFinishedLastActivity();
                activityCount = 0;
                current = null;
            }
        }, delayAmount);

    }

    //----------------------------------------------------------------------------------------------

    private void registerDefaultUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Throwable t = throwable;
            StringBuilder stack = new StringBuilder();

            while (t != null) {
                stack.append(t.getMessage()).append(" -------\n\n ");

                for (StackTraceElement it : throwable.getStackTrace()) {
                    stack.append(it.getClassName())
                            .append(".")
                            .append(it.getMethodName())
                            .append("::").append(it.getLineNumber())
                            .append("+++ \n");
                }

                stack.append("\n\n ");

                t = t.getCause();
            }

            Logger.writeToFile(this, stack.toString(), bugFileName);
            Logger.print(stack.toString());

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        if (Logger.IS_WRITE_TO_FILE_ENABLED()) {
            bugs = Logger.readFile(this, bugFileName);
        }
    }

    //----------------------------------------------------------------------------------------------

    protected abstract void onApplicationStartedFirstActivity();

    protected abstract void onApplicationFinishedLastActivity();

}
