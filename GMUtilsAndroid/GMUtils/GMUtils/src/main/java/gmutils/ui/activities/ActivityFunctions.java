package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import gmutils.BackgroundTask;
import gmutils.KeypadOp;
import gmutils.Logger;
import gmutils.R;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.dialogs.WaitDialog;
import gmutils.ui.fragments.BaseFragmentListener;
import gmutils.ui.toast.MyToast;
import gmutils.ui.utils.ViewSource;
import gmutils.utils.Utils;

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
        private int showBugsMenuItemId = 0;

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
        }

        public void onStart(Activity activity) {

            if (delegate != null && !TextUtils.isEmpty(delegate.getActivityTitle()))
                activity.setTitle(delegate.getActivityTitle());

        }

        public void onConfigurationChanged(Activity activity, Configuration newConfig) {
            SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
        }

        //-----------------------------------------------------------------------------------

        public void onCreateOptionsMenu(Menu menu) {
            if (Logger.d().getLogConfigs().isWriteToFileEnabled()) {
                if (showBugsMenuItemId == 0) {
                    showBugsMenuItemId = "Show Log".hashCode();
                    if (showBugsMenuItemId < 0) showBugsMenuItemId *= -1;
                }
                menu.add(0, showBugsMenuItemId, 10, "Show Log");
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

            if (item.getItemId() == showBugsMenuItemId) {
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).showWaitView();
                }
                BackgroundTask.run(() -> Logger.d().readAllFilesContents(activity), (ResultCallback<String>) txt -> {
                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).hideWaitView();
                    }
                    MessageDialog.create(activity)
                            .setMessage(txt)
                            .setMessageGravity(Gravity.START)
                            .setButton1(R.string.copy, (d) -> {
                                if (Utils.createInstance().copyText(activity, txt)) {
                                    MyToast.showError(activity, "copied");
                                } else {
                                    MyToast.showError(activity, "failed");
                                }
                            })
                            .setButton2(R.string.delete, (d) -> {
                                Logger.d().deleteSavedFiles(activity);
                            })
                            .show();
                });

                return true;
            }

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
    public void updateWaitViewMsg(CharSequence msg) {
        try {
            if (waitDialog != null) waitDialog.textView().setText(msg);
        } catch (Exception e) {}
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

    public static class ShowMessageDialogOptions {
        private CharSequence title;
        private Pair<String, MessageDialog.Listener> button1;
        private Pair<String, MessageDialog.Listener> button2;
        private Pair<String, MessageDialog.Listener> button3;

        public ShowMessageDialogOptions setTitle(@NotNull CharSequence title) {
            this.title = title;
            return this;
        }

        public ShowMessageDialogOptions setButton1(String text, MessageDialog.Listener listener) {
            button1 = new Pair<>(text, listener);
            return this;
        }

        public ShowMessageDialogOptions setButton2(String text, MessageDialog.Listener listener) {
            button2 = new Pair<>(text, listener);
            return this;
        }

        public ShowMessageDialogOptions setButton3(String text, MessageDialog.Listener listener) {
            button3 = new Pair<>(text, listener);
            return this;
        }


    }

    public MessageDialog showMessageDialog(Context context, int msg, ShowMessageDialogOptions options) {
        return showMessageDialog(context, context.getString(msg), options);
    }

    @Override
    public MessageDialog showMessageDialog(Context context,CharSequence msg, ShowMessageDialogOptions options) {
        MessageDialog dialog = MessageDialog.create(context);

        if (options != null && options.title != null) dialog.setTitle(options.title);
        else dialog.setTitle(R.string.message);

        dialog.setMessage(msg);

        //Pair<String, MessageDialog.Listener> button1;
        if (options != null && (options.button1 != null || options.button2 != null || options.button3 != null)) {
            if (options.button1 != null) {
                dialog.setButton1(options.button1.first, options.button1.second);
            }

            if (options.button2 != null) {
                dialog.setButton2(options.button2.first, options.button2.second);
            }

            if (options.button3 != null) {
                dialog.setButton3(options.button3.first, options.button3.second);
            }

        } else {
            dialog.setButton1(R.string.ok, null);
        }

        dialog.show();

        return dialog;
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