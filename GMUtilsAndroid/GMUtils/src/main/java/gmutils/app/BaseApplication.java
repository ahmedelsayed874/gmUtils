package gmutils.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gmutils.Logger;
import gmutils.MessagingCenter;
import gmutils.R;
import gmutils.storage.StorageManager;
import gmutils.ui.dialogs.MessageDialog;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public interface Callbacks {
        void onApplicationStartedFirstActivity(String key);

        void onApplicationFinishedLastActivity(String key);

    }

    public interface GlobalVariableDisposal {
        void dispose();
    }

    public static final class GlobalVariables {
        private final Map<String, Object> globalInstances = new HashMap<>();

        private GlobalVariables() {
        }

        public void add(String key, Object instance) {
            this.globalInstances.put(key, instance);
        }

        public void add(String key, GlobalVariableDisposal instance) {
            this.globalInstances.put(key, instance);
        }

        public Object retrieve(String key) {
            return this.globalInstances.get(key);
        }

        public void remove(String key) {
            Object o = this.globalInstances.remove(key);
            if (o instanceof GlobalVariableDisposal) {
                ((GlobalVariableDisposal) o).dispose();
            }
        }

        public void clear() {
            Set<Map.Entry<String, Object>> entries = this.globalInstances.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof GlobalVariableDisposal) {
                    ((GlobalVariableDisposal) entry.getValue()).dispose();
                }
            }
            this.globalInstances.clear();
        }

        public int size() {
            return globalInstances.size();
        }
    }

    private static BaseApplication current;

    private int activityCount = 0;
    private final long delayAmount = 500L;
    private final String bugFileName = "BUGS";
    private String bugs = "";
    private boolean hasBugs = false;
    private boolean isBugMessageDisplayed = false;
    private Runnable onBugMessageClosed = null;

    private GlobalVariables globalVariables = null;
    private MessagingCenter messagingCenter = null;

    //----------------------------------------------------------------------------------------------

    public static BaseApplication current() {
        return current;
    }

    /**
     * it will help in case app inforced to use Application class from other type
     * and need to get benefits of this class
     * just override and change returned value
     *
     * @return
     */
    @NotNull
    protected Application thisApp() {
        return this;
    }

    protected abstract void onPreCreate();

    @Override
    public void onCreate() {
        onPreCreate();

        if (thisApp() == this)
            super.onCreate();

        StorageManager.registerCallback(this::thisApp);

        registerDefaultUncaughtExceptionHandler();

        thisApp().registerActivityLifecycleCallbacks(this);

        printRecommendedResources();

        onPostCreate();
    }

    protected abstract void onPostCreate();

    private void printRecommendedResources() {
        String colors =
                "It's recommend to override those resources in color (normal & night):\n\n" +
                        "    <color name=\"gmUtilsPrimaryDark\">@color/black</color>\n" +
                        "    <color name=\"gmUtilsPrimary\">@color/orange</color>\n" +
                        "    <color name=\"gmUtilsAccent\">@color/red1</color>\n" +
                        "\n" +
                        "    <color name=\"gmUtilsBackground\">@color/white</color>\n" +
                        "    <color name=\"gmUtilsText\">@color/black</color>\n" +
                        "    <color name=\"gmUtilsHint\">@color/gray1</color>\n" +
                        "    <color name=\"gmUtilsTitle\">@color/black</color>\n" +
                        "    <color name=\"gmUtilsScreenTitle\">@color/gray5</color>\n" +
                        "\n" +
                        "    <color name=\"gmUtilsButtonLink\">@color/blue0</color>\n" +
                        "    <color name=\"gmUtilsButtonActive\">@color/green5</color>\n" +
                        "    <color name=\"gmUtilsButtonDestructive\">@color/red1</color>\n" +
                        "    <color name=\"gmUtilsButtonDark\">@color/black</color>\n" +
                        "\n" +
                        "    <color name=\"gmUtilsDialogBackground\">@color/gmUtilsPrimary</color>\n" +
                        "    <color name=\"gmUtilsDialogText\">#fff</color>\n" +
                        "\n" +
                        "    <color name=\"gmUtilsSeparateLine\">@color/gray1</color>\n" +
                        "    <color name=\"gmUtilsRatingBar\">@color/gmUtilsPrimary</color>\n" +
                        "    <color name=\"gmUtilsEditTextBackground\">@color/white</color>";

        Log.i("*** " + thisApp().getClass().getSimpleName(), colors);
    }

    //----------------------------------------------------------------------------------------------

    public GlobalVariables globalVariables() {
        if (globalVariables == null) globalVariables = new GlobalVariables();
        return globalVariables;
    }

    public MessagingCenter messagingCenter() {
        if (messagingCenter == null) messagingCenter = MessagingCenter.createInstance();
        return messagingCenter;
    }

    //----------------------------------------------------------------------------------------------

    private void registerDefaultUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Throwable t = throwable;
            StringBuilder stack = new StringBuilder();

            while (t != null) {
                stack.append("\n-------\n");
                stack.append(t.getMessage());
                stack.append("\n-------\n\n");

                for (StackTraceElement it : t.getStackTrace()) {
                    stack.append("")
                            .append(it.getClassName())
                            .append(".")
                            .append(it.getMethodName())
                            .append("::")
                            .append(it.getLineNumber())
                            .append("+ \n");
                }

                stack.append("\n");

                t = t.getCause();
            }

            Logger.writeToFile(thisApp(), stack.toString(), bugFileName);
            Logger.print(stack.toString());

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        if (Logger.IS_WRITE_TO_FILE_ENABLED()) {
            bugs = getReportedBugs();
        }
    }

    //----------------------------------------------------------------------------------------------

    public boolean isBugMessageDisplayed() {
        return isBugMessageDisplayed;
    }

    public void setOnBugMessageClosedListener(Runnable action) {
        this.onBugMessageClosed = action;
    }

    //----------------------------------------------------------------------------------------------

    public boolean hasBugs() {
        return hasBugs;
    }

    public String getReportedBugs() {
        return Logger.readFile(thisApp(), bugFileName);
    }

    public void deleteBugs() {
        try {
            Logger.deleteSavedFile(thisApp(), bugFileName);
        } catch (Exception e) {
        }
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCount++;
        current = this;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount == 1) {
                onApplicationStartedFirstActivity(activity);

                if (bugs.length() != 0 && Logger.IS_WRITE_TO_FILE_ENABLED()) {
                    hasBugs = true;
                    isBugMessageDisplayed = true;
                    try {
                        MessageDialog.create(activity)
                                .setMessage(bugs)
                                .setButton1(R.string.ok, null)
                                .setButton2(R.string.delete, dialog -> {
                                    deleteBugs();
                                })
                                .setOnDismissListener(dialog -> {
                                    isBugMessageDisplayed = false;
                                    if (onBugMessageClosed != null) onBugMessageClosed.run();
                                    onBugMessageClosed = null;
                                })
                                .show();
                    } catch (Throwable t) {
                        isBugMessageDisplayed = false;
                    }

                    bugs = "";
                }
            }
        }, delayAmount);

    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityCount--;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount <= 0) {
                onApplicationFinishedLastActivity(activity);

                if (onApplicationFinishedLastActivity != null) onApplicationFinishedLastActivity.run();

                activityCount = 0;

                dispose();
            }
        }, delayAmount);
    }

    //----------------------------------------------------------------------------------------------

    protected void onApplicationStartedFirstActivity(Activity activity) {}

    protected void onApplicationFinishedLastActivity(Activity activity) {}

    //----------------------------------------------------------------------------------------------

    private Runnable onApplicationFinishedLastActivity;

    public void setOnApplicationFinishedLastActivity(Runnable runnable) {
        this.onApplicationFinishedLastActivity = runnable;
    }

    //----------------------------------------------------------------------------------------------

    private void dispose() {
        current = null;

        onBugMessageClosed = null;

        if (globalVariables != null) globalVariables.clear();
        globalVariables = null;

        if (messagingCenter != null) messagingCenter.clearObservers();
        messagingCenter = null;

        onApplicationFinishedLastActivity = null;

        onDispose();
    }

    protected void onDispose() {}

}
