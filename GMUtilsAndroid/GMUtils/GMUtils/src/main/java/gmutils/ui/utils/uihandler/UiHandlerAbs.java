package gmutils.ui.utils.uihandler;

import org.jetbrains.annotations.NotNull;

public interface UiHandlerAbs {
    public static UiHandlerAbs getInstance() {
        return new UiHandler();
    }

    void post(int delay, @NotNull Runnable task);
}
