package com.blogspot.gm4s1.gmutils.listeners;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.listeners.TextChangedListener;

import java.lang.ref.WeakReference;

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
public abstract class SearchTextChangeListener extends TextChangedListener {

    public static SearchTextChangeListener create(OnChange change) {
        return create(1500, change);
    }

    public static SearchTextChangeListener create(int delayOffset, OnChange change) {
        return new SearchTextChangeListener(delayOffset) {

            @Override
            public void onSearchTextUpdated(String text) {
                change.onChange(text);
            }
        };
    }


    public int delayOffset = 1500;

    private Handler handler;
    private TaskRunnable taskRunnable;
    private String currentInput = "";
    private String lastInput = "";
    private boolean started = false;
    private boolean enabled = true;

    public SearchTextChangeListener() {
        this(null);
    }

    public SearchTextChangeListener(@Nullable Integer delayOffset) {
        if (delayOffset != null) this.delayOffset = delayOffset;

        handler = new Handler(Looper.getMainLooper());

    }

    private void start() {
        if (taskRunnable == null || taskRunnable.isCorrupted()) {
            taskRunnable = new TaskRunnable(this);
        }
        handler.postDelayed(taskRunnable, delayOffset);
        started = true;
    }

    private static class TaskRunnable implements Runnable {
        private WeakReference<SearchTextChangeListener> stclRef;

        private TaskRunnable(SearchTextChangeListener o) {
            stclRef = new WeakReference<>(o);
        }

        @Override
        public void run() {
            SearchTextChangeListener stcl = stclRef.get();
            if (stcl == null || !stcl.started) return;

            if (!TextUtils.equals(stcl.currentInput, stcl.lastInput)) {
                Logger.print("CurrentInput: " + stcl.currentInput + ", PreviousInput: " + stcl.lastInput);

                stcl.lastInput = stcl.currentInput;

                stcl.onSearchTextUpdated(stcl.currentInput);
                stcl.start();
            }

            stcl.started = false;
        }

        private boolean isCorrupted() {
            SearchTextChangeListener stcl = stclRef.get();
            return stcl == null;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void reset() {
        started = false;
        lastInput = "";
        currentInput = "";

        handler.removeCallbacks(taskRunnable);
    }

    public void destroy() {
        reset();
        handler = null;
        taskRunnable.stclRef.clear();
        taskRunnable = null;
    }

    @Override
    public void onTextChanged(String text) {
        if (!enabled) return;
        if (!started) start();
        if (taskRunnable.isCorrupted()) started = false;
        currentInput = text;
    }

    public abstract void onSearchTextUpdated(String text);

}
