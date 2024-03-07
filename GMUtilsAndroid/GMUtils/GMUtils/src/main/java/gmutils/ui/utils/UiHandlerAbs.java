package gmutils.ui.utils;

import org.jetbrains.annotations.NotNull;

import gmutils.backgroundWorkers.BackgroundTask;
import gmutils.backgroundWorkers.BackgroundTaskAbs;

public interface UiHandlerAbs {
    public static UiHandlerAbs getInstance() {
        return new UiHandler();
    }

    void post(int delay, @NotNull Runnable task);
}
