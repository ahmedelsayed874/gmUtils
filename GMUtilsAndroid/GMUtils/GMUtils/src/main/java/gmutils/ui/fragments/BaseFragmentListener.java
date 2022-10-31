package gmutils.ui.fragments;

import android.app.Activity;
import android.content.Context;

import gmutils.ui.activities.ActivityFunctions;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.viewModels.BaseViewModel;


public interface BaseFragmentListener {

    void setKeyboardAutoHidden(Activity activity);

    void showWaitView(Context context, int msg); //R.string.wait_moments

    void showWaitView(Context context, CharSequence msg); //R.string.wait_moments

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

    //void onProgressOfViewModelTaskChanged(BaseViewModel.ProgressStatus progressStatus);
    //void onMessageReceivedFromViewModel(BaseViewModel.Message message);
}
