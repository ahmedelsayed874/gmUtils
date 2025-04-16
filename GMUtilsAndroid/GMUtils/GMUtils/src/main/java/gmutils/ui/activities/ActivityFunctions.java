package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import gmutils.Intents;
import gmutils.KeypadOp;
import gmutils.R;
import gmutils.firebase.fcm.FCM;
import gmutils.listeners.ResultCallback;
import gmutils.logger.Logger;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.dialogs.WaitDialog;
import gmutils.ui.fragments.BaseFragmentListener;
import gmutils.ui.utils.ViewSource;

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
@SuppressLint("Registered")
public class ActivityFunctions implements BaseFragmentListener {
    public static final int SOFT_INPUT_ADJUST_PAN = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
    public static final int SOFT_INPUT_STATE_HIDDEN = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

    public interface Delegate {
        ViewSource getViewSource(@NotNull LayoutInflater inflater);

        CharSequence getActivityTitle();

        boolean allowApplyingPreferenceLocale();

        boolean isOrientationDisabled();

        /**
         * @return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN; WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
         */
        int initialKeyboardState();

        boolean keyboardShouldAutoHide(float rawX, float rawY);

        void keyboardDidHide();
    }

    private Delegate delegate;
    private WaitDialog waitDialog = null;
    private int waitDialogCount = 0;
    private Lifecycle lifecycle;

    //----------------------------------------------------------------------------------------------

    public ActivityFunctions(Delegate delegate) {
        this.delegate = delegate;
    }

    //----------------------------------------------------------------------------------------------

    public static final class Lifecycle {
        private Delegate delegate;
        private Runnable onDestroy;
        private boolean preCreateExecuted = false;
        private ViewBinding activityViewBinding;


        private Lifecycle(Delegate delegate, Runnable onDestroy) {
            this.delegate = delegate;
            this.onDestroy = onDestroy;
        }

        public Context getAttachBaseContext(Context newBase) {
            if (delegate == null || delegate.allowApplyingPreferenceLocale()) {
                return SettingsStorage.getInstance().languagePref().createNewContext(newBase);
            } else {
                return newBase;
            }
        }

        public void onPreCreate(Activity activity) {
            preCreateExecuted = true;

            if (delegate == null || delegate.allowApplyingPreferenceLocale()) {
                SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
            }

            if (delegate != null && delegate.isOrientationDisabled())
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            try {
                if (delegate != null) {
                    activity.getWindow().setSoftInputMode(delegate.initialKeyboardState());
                } else {
                    //activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            } catch (Exception ignored) {
            }

        }

        public void onCreate(Activity activity, @Nullable Bundle savedInstanceState) {
            if (!preCreateExecuted) onPreCreate(activity);

            if (delegate != null) {
                ViewSource viewSource = delegate.getViewSource(activity.getLayoutInflater());

                if (viewSource instanceof ViewSource.LayoutResource) {
                    activity.setContentView(((ViewSource.LayoutResource) viewSource).getResourceId());

                } else if (viewSource instanceof ViewSource.View) {
                    activity.setContentView(((ViewSource.View) viewSource).getView());

                } else if (viewSource instanceof ViewSource.ViewBinding) {
                    activityViewBinding = ((ViewSource.ViewBinding) viewSource).getViewBinding();
                    activity.setContentView(activityViewBinding.getRoot());
                }
            }

            checkIntent(activity.getIntent());
        }

        public void onNewIntent(Activity activity, Intent intent) {
            checkIntent(intent);
        }

        private void checkIntent(@NotNull Intent intent) {
            try {
                Class<?> cls = Class.forName("com.google.firebase.messaging.FirebaseMessaging");
                FCM.instance().onActivityStarted(intent.getExtras());
            } catch (ClassNotFoundException ignored) {
            }
        }

        public void onStart(Activity activity) {

            if (delegate != null && !TextUtils.isEmpty(delegate.getActivityTitle()))
                activity.setTitle(delegate.getActivityTitle());

        }

        public void onConfigurationChanged(Activity activity, Configuration newConfig) {
            SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
        }

        //-----------------------------------------------------------------------------------

        private int showBugsMenuItemId;
//        private int reportBugsMenuItemId;

        public void onCreateOptionsMenu(Menu menu) {
            if (Logger.d().getLogConfigs().isWriteLogsToFileEnabled()) {
                if (showBugsMenuItemId == 0) {
                    showBugsMenuItemId = "Show Log".hashCode();
                    if (showBugsMenuItemId < 0) showBugsMenuItemId *= -1;
                }
                menu.add(0, showBugsMenuItemId, 10, "Show Log");

//                if (reportBugsMenuItemId == 0) {
//                    reportBugsMenuItemId = "Send Error Logs".hashCode();
//                    if (reportBugsMenuItemId < 0) reportBugsMenuItemId *= -1;
//                }
//                menu.add(0, reportBugsMenuItemId, 9, "Send Error Logs");
            }
        }

        public boolean onOptionsItemSelected(Activity activity, MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                try {
                    activity.onBackPressed();
                } catch (Exception e) {
                }
                return true;
            }
            //
            else if (item.getItemId() == showBugsMenuItemId) {
                MessageDialog.create(activity)
                        .setMessage("Go to this path to find logs:\n" +
                                Logger.d().getLogDirector(activity, true).getAbsolutePath()
                        )
                        .setMessageGravity(Gravity.START)
                        .setButton1(R.string.ok, () -> {
                            try {
                                Intents.getInstance().showDir(
                                        activity,
                                        Logger.d().getLogDirector(activity, true).getAbsolutePath()
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .show();

                return true;
            }
            //
//            else if (item.getItemId() == reportBugsMenuItemId) {
//                ZipFileUtils zipFileUtils = new ZipFileUtils(Logger.d());
//                if (!delegate.reportBugs()) {
//
//                }
//                return true;
//            }

            return false;
        }

        public void onDestroy() {
            if (this.onDestroy != null) this.onDestroy.run();
            this.onDestroy = null;
            this.delegate = null;
            this.activityViewBinding = null;
        }
    }

    public Lifecycle lifecycle() {
        if (lifecycle == null)
            lifecycle = new Lifecycle(delegate, this::destroy0);
        return lifecycle;
    }

    //----------------------------------------------------------------------------------------------

    public final ViewBinding getViewBinding() {
        return lifecycle().activityViewBinding;
    }

    //----------------------------------------------------------------------------------------------

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setKeyboardAutoHidden(Activity activity) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        View topView = new View(activity);
        topView.setLayoutParams(layoutParams);
        topView.setOnTouchListener((View v, MotionEvent e) -> {
            if (keyboardShouldAutoHide(e.getRawX(), e.getRawY())) {
                KeypadOp.hide(activity);
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus != null) currentFocus.clearFocus();
                keyboardDidHide();
            }
            return false;
        });

        ViewGroup contentView = activity.findViewById(android.R.id.content);
        contentView.addView(topView);
    }

    public boolean keyboardShouldAutoHide(float rawX, float rawY) {
        return delegate.keyboardShouldAutoHide(rawX, rawY);
    }

    public void keyboardDidHide() {
        delegate.keyboardDidHide();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void showWaitView(Context context, int msg) {
        showWaitView(context, msg == 0 ? null : context.getString(msg));
    }

    @Override
    public void showWaitView(Context context, CharSequence msg) {
        if (waitDialogCount == 0) {
            if (msg == null) msg = context.getString(R.string.wait_moments);
            waitDialog = WaitDialog.show(context, msg);
        }

        waitDialogCount++;
    }

    @Override
    public void hideWaitView() {
        if (waitDialogCount == 1) {
            if (waitDialog != null) waitDialog.dismiss();
            waitDialog = null;
        }

        waitDialogCount--;
        if (waitDialogCount < 0) waitDialogCount = 0;
    }

    @Override
    public void hideWaitViewImmediately() {
        if (waitDialog != null) waitDialog.dismiss();
        waitDialog = null;
        waitDialogCount = 0;
    }

    @Override
    public boolean updateWaitViewMsg(CharSequence msg) {
        try {
            if (waitDialog != null) {
                waitDialog.textView().setText(msg);
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    public boolean isWaitViewShown() {
        return waitDialogCount > 0;
    }

    //----------------------------------------------------------------------------------------------

    public RetryPromptDialog showRetryPromptDialog(Context context, CharSequence msg, RetryPromptDialog.Listener onRetry) {
        return RetryPromptDialog.show(context, msg, onRetry, null);
    }

    @Override
    public RetryPromptDialog showRetryPromptDialog(Context context, CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return RetryPromptDialog.show(context, msg, onRetry, onCancel);
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialog showMessageDialog(Context context, int msg) {
        return showMessageDialog(context, context.getString(msg));
    }

    @Override
    public MessageDialog showMessageDialog(Context context, CharSequence msg) {
        MessageDialog dialog = MessageDialog.create(context);

        dialog.setTitle(R.string.message);
        dialog.setMessage(msg);
        dialog.setButton1(R.string.ok, null);
        dialog.show();

        return dialog;
    }

    //----------------------------------------------------------------------------------------------

    private Map<Integer, ResultCallback<Intent>> activityResultCallback;

    public void startActivityForResult(Activity activity, @NonNull Intent intent, int requestCode, ResultCallback<Intent> callback, Bundle options) {
        if (activityResultCallback == null) activityResultCallback = new HashMap<>();
        activityResultCallback.put(requestCode, callback);
        activity.startActivityForResult(intent, requestCode, options);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (activityResultCallback != null) {
            if (activityResultCallback.containsKey(requestCode)) {
                ResultCallback<Intent> callback = activityResultCallback.remove(requestCode);
                if (callback == null) {
                    Logger.d().print(() -> "startActivityForResult called with NULL callback");
                    return;
                }

                callback.invoke(resultCode == Activity.RESULT_OK ? data : null);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private void destroy0() {
        if (waitDialog != null) waitDialog.dismiss();
        waitDialogCount = 0;
        waitDialog = null;

        this.delegate = null;
        this.lifecycle = null;
    }

    public void destroy() {
        if (lifecycle != null) lifecycle.onDestroy();
    }

}