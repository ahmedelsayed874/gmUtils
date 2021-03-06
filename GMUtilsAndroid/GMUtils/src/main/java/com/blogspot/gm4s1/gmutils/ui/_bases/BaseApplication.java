package com.blogspot.gm4s1.gmutils.ui._bases;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.MessagingCenter;
import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.ui.dialogs.MessageDialog;
import com.blogspot.gm4s1.gmutils.storage.StorageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public interface Callbacks {
        void onApplicationStartedFirstActivity();

        void onApplicationFinishedLastActivity();

    }

    public interface GlobalVariableDisposal {
        void dispose();
    }

    public static final class GlobalVariables {
        private final Map<String, Object> globalInstances = new HashMap<>();

        private GlobalVariables() {
        }

        public void add(String key, Object instance) {
            this.globalInstances.put(key, instance);
        }

        public void add(String key, GlobalVariableDisposal instance) {
            this.globalInstances.put(key, instance);
        }

        public Object retrieve(String key) {
            return this.globalInstances.get(key);
        }

        public void remove(String key) {
            Object o = this.globalInstances.remove(key);
            if (o instanceof GlobalVariableDisposal) {
                ((GlobalVariableDisposal) o).dispose();
            }
        }

        public void clear() {
            Set<Map.Entry<String, Object>> entries = this.globalInstances.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof GlobalVariableDisposal) {
                    ((GlobalVariableDisposal) entry.getValue()).dispose();
                }
            }
            this.globalInstances.clear();
        }

    }

    private static BaseApplication current;

    private int activityCount = 0;
    private final long delayAmount = 500L;
    private final String bugFileName = "BUGS";
    private String bugs = "";
    private boolean hasBugs = false;
    private boolean isBugMessageDisplayed = false;
    private Runnable onBugMessageClosed = null;

    private GlobalVariables globalVariables = null;
    private MessagingCenter messagingCenter = null;

    private Map<String, Callbacks> mCallbacks;

    //----------------------------------------------------------------------------------------------

    public static BaseApplication current() {
        return current;
    }

    protected abstract void onPreCreate();

    @Override
    public void onCreate() {
        onPreCreate();

        super.onCreate();

        StorageManager.registerCallback(() -> BaseApplication.this);

        registerDefaultUncaughtExceptionHandler();

        registerActivityLifecycleCallbacks(this);

        onPostCreate();
    }

    protected abstract void onPostCreate();

    //----------------------------------------------------------------------------------------------

    public GlobalVariables globalVariables() {
        if (globalVariables == null) globalVariables = new GlobalVariables();
        return globalVariables;
    }

    public MessagingCenter messagingCenter() {
        if (messagingCenter == null) MessagingCenter.createInstance();
        return messagingCenter;
    }

    //----------------------------------------------------------------------------------------------

    private void registerDefaultUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Throwable t = throwable;
            StringBuilder stack = new StringBuilder();

            while (t != null) {
                stack.append("\n-------\n");
                stack.append(t.getMessage());
                stack.append("\n-------\n\n");

                for (StackTraceElement it : t.getStackTrace()) {
                    stack.append("")
                            .append(it.getClassName())
                            .append(".")
                            .append(it.getMethodName())
                            .append("::")
                            .append(it.getLineNumber())
                            .append("+ \n");
                }

                stack.append("\n");

                t = t.getCause();
            }

            Logger.writeToFile(this, stack.toString(), bugFileName);
            Logger.print(stack.toString());

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        if (Logger.IS_WRITE_TO_FILE_ENABLED()) {
            bugs = getReportedBugs();
        }
    }

    //----------------------------------------------------------------------------------------------

    public boolean isBugMessageDisplayed() {
        return isBugMessageDisplayed;
    }

    public void setOnBugMessageClosedListener(Runnable action) {
        this.onBugMessageClosed = action;
    }

    //----------------------------------------------------------------------------------------------

    public void addCallback(String name, Callbacks callbacks) {
        if (mCallbacks == null) mCallbacks = new HashMap<>();
        mCallbacks.put(name, callbacks);
    }

    public void removeCallback(String name) {
        if (mCallbacks == null) return;
        mCallbacks.remove(name);
    }


    //----------------------------------------------------------------------------------------------

    public boolean hasBugs() {
        return hasBugs;
    }

    public String getReportedBugs() {
        return Logger.readFile(this, bugFileName);
    }

    public void deleteBugs() {
        try {
            Logger.deleteSavedFile(this, bugFileName);
        } catch (Exception e) {}
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCount++;
        current = this;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount == 1) {
                onApplicationStartedFirstActivity();

                if (bugs.length() != 0 && Logger.IS_WRITE_TO_FILE_ENABLED()) {
                    hasBugs = true;
                    isBugMessageDisplayed = true;
                    try {
                        MessageDialog.create(activity)
                                .setMessage(bugs)
                                .setButton1(R.string.ok, null)
                                .setButton2(R.string.delete, dialog -> {
                                    deleteBugs();
                                })
                                .setOnDismissListener(dialog -> {
                                    isBugMessageDisplayed = false;
                                    if (onBugMessageClosed != null) onBugMessageClosed.run();
                                    onBugMessageClosed = null;
                                })
                                .show();
                    } catch (Throwable t) {
                        isBugMessageDisplayed = false;
                    }

                    bugs = "";
                }
            }
        }, delayAmount);

    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityCount--;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount <= 0) {
                onApplicationFinishedLastActivity();
                activityCount = 0;
                dispose();
            }
        }, delayAmount);
    }

    //----------------------------------------------------------------------------------------------

    protected abstract void onApplicationStartedFirstActivity();

    protected abstract void onApplicationFinishedLastActivity();

    //----------------------------------------------------------------------------------------------

    private void dispose() {
        current = null;

        onBugMessageClosed = null;

        if (globalVariables != null) globalVariables.clear();
        globalVariables = null;

        if (messagingCenter != null) messagingCenter.clearObservers();
        messagingCenter = null;

        if (mCallbacks != null) mCallbacks.clear();
        mCallbacks = null;
    }

}
