package gmutils.listeners;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ActivityLifecycleCallbacks extends Application.ActivityLifecycleCallbacks {
    @Override
    default void onActivityCreated(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    default void onActivityStarted(@NotNull Activity activity) {
    }

    @Override
    default void onActivityResumed(@NotNull Activity activity) {

    }

    @Override
    default void onActivityPaused(@NotNull Activity activity) {

    }

    @Override
    default void onActivityStopped(@NotNull Activity activity) {

    }

    @Override
    default void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

    }

    @Override
    default void onActivityDestroyed(@NotNull Activity activity) {

    }
}
