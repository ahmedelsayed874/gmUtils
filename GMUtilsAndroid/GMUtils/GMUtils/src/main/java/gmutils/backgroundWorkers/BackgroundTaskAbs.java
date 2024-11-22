package gmutils.backgroundWorkers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;

public interface BackgroundTaskAbs {
    static BackgroundTaskAbs getInstance() {
        return new BackgroundTask();
    }

    void execute(
            @NotNull Runnable task,
            @Nullable Runnable resultCallback
    );

    void execute(
            @NotNull Runnable task,
            @Nullable ResultCallback<Throwable> onException,
            @Nullable Runnable resultCallback
    );

    <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<T> resultCallback
    );

    <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<Throwable> onException,
            @Nullable ResultCallback<T> resultCallback
    );

}
