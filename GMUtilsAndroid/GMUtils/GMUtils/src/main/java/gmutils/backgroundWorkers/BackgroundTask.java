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

    public static void run(Runnable task, Runnable resultCallback, @Nullable ResultCallback<Throwable> onException) {
        new BackgroundTask().execute(task, resultCallback, onException);
    }

    public static <T> void run(ActionCallback0<T> task, ResultCallback<T> resultCallback) {
        new BackgroundTask().execute(task, resultCallback);
    }

    public static <T> void run(ActionCallback0<T> task, ResultCallback<T> resultCallback, @Nullable ResultCallback<Throwable> onException) {
        new BackgroundTask().execute(task, resultCallback, onException);
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    private final String threadName;
    private final UiHandlerAbs uiHandler;

    public BackgroundTask() {
        this(null, UiHandlerAbs.getInstance());
    }

    public BackgroundTask(UiHandlerAbs uiHandler) {
        this(null, uiHandler);
    }

    public BackgroundTask(@Nullable String threadName) {
        this(threadName, UiHandlerAbs.getInstance());
    }

    public BackgroundTask(@Nullable String threadName, @NotNull UiHandlerAbs uiHandler) {
        if (TextUtils.isEmpty(threadName))
            this.threadName = null;
        else
            this.threadName = threadName.trim();

        this.uiHandler = uiHandler;
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            Runnable onComplete
    ) {
        execute(task, onComplete, null, true);
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            @Nullable Runnable onComplete,
            @Nullable ResultCallback<Throwable> onException
    ) {
        execute(task, onComplete, onException, true);
    }

    @Override
    public void execute(
            @NotNull Runnable task,
            @Nullable Runnable onComplete,
            @Nullable ResultCallback<Throwable> onException,
            boolean dispatchResultOnUIThread
    ) {
        execute(
                () -> {
                    task.run();
                    return null;
                },
                onComplete == null ?
                        null :
                        result -> onComplete.run(),
                onException,
                dispatchResultOnUIThread
        );
    }

    //-------------------------------------------------------------------------------------------

    @Override
    public <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<T> resultCallback
    ) {
        execute(task, resultCallback, null, true);
    }

    @Override
    public <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<T> resultCallback,
            @Nullable ResultCallback<Throwable> onException
    ) {
        execute(task, resultCallback, onException, true);
    }

    @Override
    public <T> void execute(
            @NotNull ActionCallback0<T> task,
            @Nullable ResultCallback<T> resultCallback,
            @Nullable ResultCallback<Throwable> onException,
            boolean dispatchResultOnUIThread
    ) {
        Runnable runnable = () -> {
            try {
                T result = task.invoke();
                if (resultCallback != null) {
                    if (dispatchResultOnUIThread) {
                        runOnUiThread(() -> {
                            resultCallback.invoke(result);
                        });
                    }
                    //
                    else {
                        resultCallback.invoke(result);
                    }
                }
            }
            //
            catch (Throwable e) {
                if (onException != null) {
                    if (dispatchResultOnUIThread) {
                        runOnUiThread(() -> {
                            onException.invoke(e);
                        });
                    }
                    //
                    else {
                        onException.invoke(e);
                    }
                }
                //
                else {
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
        UiHandlerAbs handler = uiHandler;
        if (handler == null) handler = UiHandlerAbs.getInstance(); //unnecessary, but for precaution
        handler.post(0, task);
    }

}
