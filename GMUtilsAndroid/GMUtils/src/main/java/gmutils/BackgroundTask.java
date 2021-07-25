package gmutils;

import android.os.Handler;
import android.os.Looper;

import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;

public class BackgroundTask {
    private static int count = 0;

    public static void run(Runnable task, Runnable resultCallback) {

        run(() -> {
            task.run();
            return null;
        }, result -> resultCallback.run());

    }

    public static <T> void run(ActionCallback0<T> task, ResultCallback<T> resultCallback) {
        Thread thread = new Thread(() -> {
            T result = task.invoke();

            if (resultCallback != null)
                new Handler(Looper.getMainLooper()).post(() -> {
                    resultCallback.invoke(result);
                });
        }, BackgroundTask.class.getSimpleName() + "-" + (++count));

        thread.start();
    }

}
