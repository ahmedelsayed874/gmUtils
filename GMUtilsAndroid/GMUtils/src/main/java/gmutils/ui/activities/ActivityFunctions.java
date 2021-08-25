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

import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import gmutils.KeypadOp;
import gmutils.Logger;
import gmutils.R;
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
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
@SuppressLint("Registered")
public class ActivityFunctions implements BaseFragmentListener {
    public interface Delegate {
        ViewSource getViewSource(@NotNull LayoutInflater inflater);

        CharSequence getActivityTitle();

        boolean allowApplyingPreferenceLocale();

        boolean isOrientationDisabled();

        int initialKeyboardState();
    }

    private Delegate delegate;
    private WaitDialog waitDialog = null;
    private int waitDialogCount = 0;
    private ViewBinding activityViewBinding;
    private boolean preCreateExecuted = false;

    //----------------------------------------------------------------------------------------------

    public ActivityFunctions(Delegate delegate) {
        this.delegate = delegate;
    }

    //----------------------------------------------------------------------------------------------

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

    public void onPostCreate(Activity activity) {
    }

    public void onStart(Activity activity) {

        if (delegate != null && !TextUtils.isEmpty(delegate.getActivityTitle()))
            activity.setTitle(delegate.getActivityTitle());

    }

    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
    }

    //----------------------------------------------------------------------------------------------

    public final ViewBinding getViewBinding() {
        return activityViewBinding;
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
        return true;
    }

    public void keyboardDidHide() {
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void showWaitView(Context context, int msg) {
        if (waitDialogCount == 0) {
            if (msg == 0) msg = R.string.wait_moments;
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
        if (waitDialog != null) waitDialog.textView().setText(msg);
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
        return showMessageDialog(context, 0, msg);
    }

    public MessageDialog showMessageDialog(Context context, int msg, Pair<Integer, MessageDialog.Listener> button) {
        return showMessageDialog(context, 0, msg, button);
    }

    @SafeVarargs
    public final MessageDialog showMessageDialog(Context context, int title, int msg, Pair<Integer, MessageDialog.Listener>... buttons) {
        Pair<String, MessageDialog.Listener>[] buttons2 = null;
        if (buttons != null) {
            buttons2 = new Pair[buttons.length];
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i] != null)
                    buttons2[i] = new Pair<>(context.getString(buttons[i].first), buttons[i].second);
            }
        }

        return showMessageDialog(
                context,
                title == 0 ? null : context.getString(title),
                msg == 0 ? null : context.getString(msg),
                buttons2
        );
    }

    public MessageDialog showMessageDialog(Context context, CharSequence msg) {
        return showMessageDialog(context, null, msg);
    }

    public MessageDialog showMessageDialog(Context context, CharSequence msg, Pair<String, MessageDialog.Listener> button) {
        return showMessageDialog(context, null, msg, button);
    }

    @SafeVarargs
    public final MessageDialog showMessageDialog(Context context, CharSequence title, CharSequence msg, Pair<String, MessageDialog.Listener>... buttons) {
        MessageDialog dialog = MessageDialog.create(context);

        if (title != null) dialog.setTitle(title);
        else dialog.setTitle(R.string.message);

        dialog.setMessage(msg);

        if (buttons != null && buttons.length > 0) {
            dialog.setButton1(buttons[0].first, buttons[0].second);

            if (buttons.length > 1) {
                dialog.setButton2(buttons[1].first, buttons[1].second);
            }

            if (buttons.length > 2) {
                dialog.setButton3(buttons[2].first, buttons[2].second);
            }

        } else {
            dialog.setButton1(R.string.ok, null);
        }

        dialog.show();

        return dialog;
    }

    //----------------------------------------------------------------------------------------------

    private int showBugsMenuItemId = 0;

    public void onCreateOptionsMenu(Menu menu) {
        if (Logger.IS_WRITE_TO_FILE_ENABLED()) {
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
            String txt = Logger.readAllFilesContents(activity);

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
                        Logger.deleteSavedFiles(activity);
                    })
                    .show();

            return true;
        }

        return false;
    }


    //----------------------------------------------------------------------------------------------

    public void onDestroy() {
        if (waitDialog != null) waitDialog.dismiss();
        waitDialogCount = 0;
        waitDialog = null;

        this.delegate = null;
    }

}