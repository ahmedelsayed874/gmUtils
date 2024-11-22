package gmutils.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import gmutils.Intents;
import gmutils.firebase.fcm.GmFirebaseMessagingService;
import gmutils.logger.Logger;
import gmutils.MessagingCenter;
import gmutils.R;
import gmutils.logger.LoggerAbs;
import gmutils.storage.StorageManager;
import gmutils.ui.dialogs.MessageDialog;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static class GlobalVariableDisposal {
        private Object instance;
        private gmutils.listeners.Runnable2<Object> onDispose;

        public GlobalVariableDisposal(Object instance, gmutils.listeners.Runnable2<Object> onDispose) {
            this.instance = instance;
            this.onDispose = onDispose;
        }

        private void dispose() {
            if (onDispose != null) onDispose.run(instance);
            instance = null;
            onDispose = null;
        }
    }

    public static final class GlobalVariables {
        private final int secret;
        private final Map<String, Object> globalInstances = new HashMap<>();

        private GlobalVariables() {
            secret = new Random().nextInt();
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

        public void clear(int secret) {
            if (this.secret != secret) {
                throw new RuntimeException("this method can call only from container class");
            }
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

    //==============================================================================================

    private int activityCount = 0;
    private final long delayAmount = 500L;
    private GlobalVariables globalVariables = null;
    private MessagingCenter messagingCenter = null;

    //----------------------------------------------------------------------------------------------

    public static BaseApplication register(Application application) {
        BaseApplication baseApplication = new BaseApplication() {
            final WeakReference<Application> app = new WeakReference<>(application);

            @Override
            Application thisApp() {
                return app.get();
            }

        };

        baseApplication.onCreate();

        return baseApplication;
    }

    //----------------------------------------------------------------------------------------------

    private static BaseApplication current;

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
    Application thisApp() {
        return this;
    }

    @Override
    public void onCreate() {
        current = this;

        if (thisApp() == this)
            super.onCreate();

        StorageManager.registerCallback(this::thisApp);

        registerDefaultUncaughtExceptionHandler();

        thisApp().registerActivityLifecycleCallbacks(this);

        printRecommendedResources();
    }

    private void printRecommendedResources() {
        String colors =
                "It's recommend to override those resources in color (normal & night):\n\n" +
                        "    <color name=\"gmPrimaryDark\">@color/black</color>\n" +
                        "    <color name=\"gmPrimary\">@color/orange</color>\n" +
                        "    <color name=\"gmPrimaryVariant\">@color/white</color>\n" +
                        "    <color name=\"gmAccent\">@color/red1</color>\n" +
                        "\n" +
                        "    <color name=\"gmBackground\">@color/white</color>\n" +
                        "    <color name=\"gmText\">@color/black</color>\n" +
                        "    <color name=\"gmHint\">@color/gray1</color>\n" +
                        "    <color name=\"gmTitle\">@color/black</color>\n" +
                        "    <color name=\"gmScreenTitle\">@color/gray5</color>\n" +
                        "\n" +
                        "    <color name=\"gmButtonLink\">@color/blue0</color>\n" +
                        "    <color name=\"gmButtonActive\">@color/green5</color>\n" +
                        "    <color name=\"gmButtonDestructive\">@color/red1</color>\n" +
                        "    <color name=\"gmButtonDark\">@color/black</color>\n" +
                        "\n" +
                        "    <color name=\"gmDialogBackground\">@color/gmPrimary</color>\n" +
                        "    <color name=\"gmDialogText\">#fff</color>\n" +
                        "\n" +
                        "    <color name=\"gmSeparateLine\">@color/gray1</color>\n" +
                        "    <color name=\"gmRatingBar\">@color/gmPrimary</color>\n" +
                        "    <color name=\"gmEditTextBackground\">@color/white</color>";

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

            try {
                boolean isLoggerActive = false;

                for (LoggerAbs logger : Logger.loggers()) {
                    if (logger.getLogConfigs().isWriteLogsToFileEnabled()) {
                        isLoggerActive = true;
                        break;
                    }
                }

                if (isLoggerActive) {
                    File bugFile = getBugFile();
                    Logger.LogFileWriter fileWriter = new Logger.LogFileWriter(bugFile, false, null);
                    fileWriter.write(stack.toString());
                }
            } catch (Exception e) {
                Logger.instance("bugs").writeToFile(thisApp(), stack::toString);
            }

            for (LoggerAbs logger : Logger.loggers()) {
                logger.print(stack::toString);
            }

            try {
                dispose();
            } catch (Throwable ignore) {
            }

            if (!onAppCrashed(thread, throwable, stack.toString(), defaultHandler)) {
                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, throwable);
                }
            }
        });
    }

    protected boolean onAppCrashed(Thread thread, Throwable throwable, String stack, Thread.UncaughtExceptionHandler defaultHandler) {
        return false;
    }

    private File getBugDir() {
        File bugDir = new File(getExternalFilesDir(null), "bugs");
        if (!bugDir.exists()) {
            if (bugDir.mkdirs()) return bugDir;
            else return null;
        }
        return bugDir;
    }

    private File getBugFile() {
        File bugDir = getBugDir();
        File bugFile = new File(bugDir, "bugs.bugs");
        if (!bugFile.exists()) {
            try {
                bugFile.createNewFile();
            } catch (Exception e) {
                return null;
            }
        }

        return bugFile;
    }

    //----------------------------------------------------------------------------------------------

    public void checkBugsExist(Context context, Runnable onComplete) {
        if (!hasBugs()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        String bugs = getReportedBugs();
        if (bugs.isEmpty()) {
            if (onComplete != null) onComplete.run();
            deleteBugs();
            return;
        }

        MessageDialog.create(context)
                .setMessage(bugs)
                .setMessageGravity(Gravity.START)
                .setButton1(R.string.dismiss, null)
                .setButton2(R.string.delete, () -> {
                    deleteBugs();
                })
                .setButton3("Send", () -> {
                    onSendBugClick(bugs, getBugFile());
                })
                .setOnDismissListener(dialog -> {
                    if (onComplete != null) onComplete.run();
                })
                .show();
    }

    public boolean hasBugs() {
        File bugDir = getBugDir();
        if (bugDir != null) {
            String[] list = bugDir.list();
            return list != null && list.length > 0;
        }

        return false;
    }

    public String getReportedBugs() {
        try {
            return new Logger.LogFileWriter(
                    getBugFile(),
                    Logger.d().getLogConfigs().isFileContentEncryptEnabled(),
                    Logger.d().getLogConfigs().getFileContentEncryptionKey()
            ).readFileContent();
        } catch (Exception e) {
            return "";
        }
    }

    public void deleteBugs() {
        try {
            boolean b = getBugFile().delete();
            Log.d(getClass().getSimpleName(), "deleteBugs: isBugFileDeleted: " + b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            boolean b = getBugDir().delete();
            Log.d(getClass().getSimpleName(), "deleteBugs: isBugDirDeleted: " + b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSendBugClick(String bugs, File bugFile) {
        Intents.getInstance().composeEmail(
                this,
                null,
                "REPORTEDBUG: " + this.getPackageName(),
                bugs,
                Uri.fromFile(bugFile)
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCount++;
        current = this;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activityCount == 1) {
                onApplicationStartedFirstActivity(activity);
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

                if (onApplicationFinishedLastActivity != null)
                    onApplicationFinishedLastActivity.run();

                activityCount = 0;

                dispose();
            }
        }, delayAmount);
    }

    //----------------------------------------------------------------------------------------------

    protected void onApplicationStartedFirstActivity(Activity activity) {
    }

    protected void onApplicationFinishedLastActivity(Activity activity) {
    }

    //----------------------------------------------------------------------------------------------

    private Runnable onApplicationFinishedLastActivity;

    public void setOnApplicationFinishedLastActivity(Runnable runnable) {
        this.onApplicationFinishedLastActivity = runnable;
    }

    //----------------------------------------------------------------------------------------------

    private void dispose() {
        current = null;

        if (globalVariables != null) globalVariables.clear(globalVariables.secret);
        globalVariables = null;

        if (messagingCenter != null) messagingCenter.clearObservers();
        messagingCenter = null;

        onApplicationFinishedLastActivity = null;

        if (disposeCallbacks != null) {
            for (Runnable value : disposeCallbacks.values()) {
                if (value != null) value.run();
            }
        }

        onDispose();
    }

    protected void onDispose() {
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        StorageManager.registerCallback(null);
        try {
            dispose();
        } catch (Exception ignored) {}
    }

    //----------------------------------------------------------------------------------------------

    private Map<String, Runnable> disposeCallbacks;

    public void registerOnDispose(Class<?> owner, Runnable callback) {
        if (disposeCallbacks == null) disposeCallbacks = new HashMap<>();
        disposeCallbacks.put(owner.getName(), callback);
    }
}
