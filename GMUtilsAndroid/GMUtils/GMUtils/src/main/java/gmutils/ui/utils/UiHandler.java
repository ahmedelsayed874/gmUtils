package gmutils.ui.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

public class UiHandler implements UiHandlerAbs {
    public final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void post(int delay, @NotNull Runnable task) {
        if (delay > 1) {
            handler.postDelayed(task, delay);
        } else {
            handler.post(task);
        }
    }
}
