package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.KeypadOp;
import gmutils.Logger;
import gmutils.R;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.dialogs.WaitDialog;
import gmutils.ui.fragments.BaseFragment;
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
        CharSequence getActivityTitle();
        boolean allowApplyingPreferenceLocale();
        boolean isOrientationDisabled();
        int initialKeyboardState();
    }

    private Delegate delegate;
    private WaitDialog waitDialog = null;
    private int waitDialogCount = 0;

    //----------------------------------------------------------------------------------------------

    public ActivityFunctions(@NotNull Delegate delegate) {
        this.delegate = delegate;
    }


    //----------------------------------------------------------------------------------------------

    public void onPreCreate(Activity activity) {
        if (delegate.allowApplyingPreferenceLocale()) {
            SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
        }

        if (delegate.isOrientationDisabled())
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            activity.getWindow().setSoftInputMode(delegate.initialKeyboardState());
        } catch (Exception ignored) {
        }

    }

    public void onCreate(Activity activity, @Nullable Bundle savedInstanceState) {
    }

    public void onPostCreate(Activity activity) {}

    public void onStart(Activity activity) {
        if (!TextUtils.isEmpty(delegate.getActivityTitle())) activity.setTitle(delegate.getActivityTitle());
    }

    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        SettingsStorage.getInstance().languagePref().applySavedLanguage(activity);
    }

    public Context getAttachBaseContext(Context newBase) {
        if (delegate.allowApplyingPreferenceLocale()) {
            return SettingsStorage.getInstance().languagePref().createNewContext(newBase);
        } else {
            return newBase;
        }
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
        topView.setOnTouchListener((View v,  MotionEvent e) -> {
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