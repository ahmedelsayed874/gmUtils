package gmutils.backgroundWorkers;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;
import gmutils.ui.utils.UiHandlerAbs;

public class BackgroundTask implements BackgroundTaskAbs {

    public static void run(Runnable task, Runnable resultCallback) {
        new BackgroundTask().execute(task, resultCallback);
    }

    public static void run(Runnable task, @Nullable ResultCallback<Throwable> onException, Runnable resultCallback) {
        new BackgroundTask().execute(task, onException, resultCallback);
    }

    public static <T> void run(ActionCallback0<T> task, ResultCallback<T> resultCallback) {
        new BackgroundTask().execute(task, resultCallback);
    }

    public static <T> void run(ActionCallback0<T> task, @Nullable ResultCallback<Throwable> onException, ResultCallback<T> resultCallback) {
        new BackgroundTask().execute(task, onException, resultCallback);
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    private final String threadName;
    private UiHandlerAbs handler;

    public BackgroundTask() {
        this(null);
    }

    public BackgroundTask(@Nullable String threadName) {
        if (TextUtils.isEmpty(threadName))
            this.threadName = null;
        else
            this.threadName = threadName.trim();
    }

    public void setHandler(UiHandlerAbs handler) {
        this.handler = handler;
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            Runnable resultCallback
    ) {
        execute(task, null, resultCallback);
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            @Nullable ResultCallback<Throwable> onException,
            @Nullable Runnable resultCallback
    ) {
        execute(
                () -> {
                    task.run();
                    return null;
                },
                onException,
                resultCallback == null ? null : result -> resultCallback.run()
        );
    }

    @Override
    public <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<T> resultCallback
    ) {
        execute(task, null, resultCallback);
    }

    @Override
    public <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<Throwable> onException,
            @Nullable ResultCallback<T> resultCallback
    ) {
        Runnable runnable = () -> {
            try {
                T result = task.invoke();
                if (resultCallback != null) {
                    runOnUiThread(() -> {
                        resultCallback.invoke(result);
                    });
                }
            } catch (Throwable e) {
                if (onException != null) {
                    runOnUiThread(() -> {
                        onException.invoke(e);
                    });
                }
            }
        };

        Thread thread = TextUtils.isEmpty(threadName) ?
                new Thread(runnable) :
                new Thread(runnable, threadName);

        thread.start();
    }

    private void runOnUiThread(Runnable task) {
        UiHandlerAbs uiHandler = handler;
        if (uiHandler == null) uiHandler = UiHandlerAbs.getInstance();
        uiHandler.post(0, task);
    }

}
