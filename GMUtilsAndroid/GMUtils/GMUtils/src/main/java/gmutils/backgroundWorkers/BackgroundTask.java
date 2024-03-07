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

    public static <T> void run(ActionCallback0<T> task, ResultCallback<T> resultCallback) {
        new BackgroundTask().execute(task, resultCallback);
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
    public void execute(@NotNull Runnable task, Runnable resultCallback) {
        run(() -> {
            task.run();
            return null;
        }, resultCallback == null ? null : result -> resultCallback.run());
    }

    @Override
    public <T> void execute(@NotNull ActionCallback0<T> task, ResultCallback<T> resultCallback) {
        Runnable runnable = () -> {
            T result = task.invoke();
            if (resultCallback != null) {
                UiHandlerAbs uiHandler = handler;
                if (uiHandler == null) uiHandler = UiHandlerAbs.getInstance();
                uiHandler.post(0, () -> {
                    resultCallback.invoke(result);
                });
            }
        };

        Thread thread = TextUtils.isEmpty(threadName) ?
                new Thread(runnable) :
                new Thread(runnable, threadName);

        thread.start();
    }

}
