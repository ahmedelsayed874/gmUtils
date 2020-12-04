package com.blogspot.gm4s1.gmutils.listeners;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ActivityLifecycleCallbacks extends Application.ActivityLifecycleCallbacks {
    @Override
    public default void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public default void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public default void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public default void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public default void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public default void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public default void onActivityDestroyed(@NonNull Activity activity) {

    }
}
