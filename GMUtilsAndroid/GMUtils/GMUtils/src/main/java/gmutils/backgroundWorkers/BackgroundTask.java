package gmutils.backgroundWorkers;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;
import gmutils.ui.utils.uihandler.UiHandlerAbs;

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
    private final UiHandlerAbs handler;

    public BackgroundTask() {
        this(null, UiHandlerAbs.getInstance());
    }

    public BackgroundTask(UiHandlerAbs handler) {
        this(null, handler);
    }

    public BackgroundTask(@Nullable String threadName) {
        this(threadName, UiHandlerAbs.getInstance());
    }

    public BackgroundTask(@Nullable String threadName, UiHandlerAbs handler) {
        if (TextUtils.isEmpty(threadName))
            this.threadName = null;
        else
            this.threadName = threadName.trim();

        this.handler = handler;
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            Runnable onComplete
    ) {
        execute(task, null, onComplete);
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            @Nullable ResultCallback<Throwable> onException,
            @Nullable Runnable onComplete
    ) {
        execute(
                () -> {
                    task.run();
                    return null;
                },
                onException,
                onComplete == null ? null : result -> onComplete.run()
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
                } else {
                    throw e;
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
