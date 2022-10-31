package gmutilssupport.ui.fragments;

import android.app.Activity;
import android.content.Context;

import gmutilssupport.ui.activities.ActivityFunctions;
import gmutilssupport.ui.dialogs.MessageDialog;
import gmutilssupport.ui.dialogs.RetryPromptDialog;


public interface BaseFragmentListener {

    void setKeyboardAutoHidden(Activity activity);

    void showWaitView(Context context, int msg); //R.string.wait_moments

    void hideWaitView();

    void hideWaitViewImmediately();

    void updateWaitViewMsg(CharSequence msg);

    MessageDialog showMessageDialog(Context context, CharSequence msg, ActivityFunctions.ShowMessageDialogOptions options);

    RetryPromptDialog showRetryPromptDialog(
            Context context,
            CharSequence msg,
            RetryPromptDialog.Listener onRetry,
            RetryPromptDialog.Listener onCancel
    );

}
