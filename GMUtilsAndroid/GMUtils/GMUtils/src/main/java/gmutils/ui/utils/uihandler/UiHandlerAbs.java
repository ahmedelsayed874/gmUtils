package gmutils.ui.utils.uihandler;

import org.jetbrains.annotations.NotNull;

public interface UiHandlerAbs {
    static UiHandlerAbs getInstance() {
        return new UiHandler();
    }

    void post(int delay, @NotNull Runnable task);
}
