package gmutils.ui.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import gmutils.R;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.toast.MyToast;
import gmutils.ui.viewModels.BaseViewModel;

public class BaseViewModelObserversHandlers {
    public void onProgressOfViewModelTaskChanged(
            Context context,
            BaseViewModel.ProgressStatus progressStatus,
            ResultCallback<CharSequence> showWaitView,
            ResultCallback<CharSequence> updateWaitViewMsg,
            Runnable hideWaitView
    ) {
        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show) {
            BaseViewModel.ProgressStatus.Show ps = (BaseViewModel.ProgressStatus.Show) progressStatus;
            if (!TextUtils.isEmpty(ps.message)) showWaitView.invoke(ps.message);
            else if (ps.messageId != 0) showWaitView.invoke(context.getString(ps.messageId));
            else showWaitView.invoke(null);

        } else if (progressStatus instanceof BaseViewModel.ProgressStatus.Update) {
            BaseViewModel.ProgressStatus.Update ps = (BaseViewModel.ProgressStatus.Update) progressStatus;
            if (!TextUtils.isEmpty(ps.message)) updateWaitViewMsg.invoke(ps.message);
            else if (ps.messageId != 0) updateWaitViewMsg.invoke(context.getString(ps.messageId));
            else updateWaitViewMsg.invoke("");

        } else if (progressStatus instanceof BaseViewModel.ProgressStatus.Hide) {
            hideWaitView.run();
        }
    }

    public void onMessageReceivedFromViewModel(
            Context context,
            BaseViewModel.Message message,
            ActionCallback<CharSequence, MessageDialog> showMessageDialog,
            ResultCallback<CharSequence> showToast,
            ResultCallback2<CharSequence, Runnable> showRetryPromptDialog
    ) {
        CharSequence msg = "";
        if (message.messageIds != null && !message.messageIds.isEmpty()) {
            for (Integer messageId : message.messageIds) {
                if (msg.length() > 0) msg += message.getMultiMessageIdsSeparator();
                msg += message.getMultiMessageIdsPrefix() + " " + context.getString(messageId);
            }
        } else if (message.messageString != null) {
            List<String> langCodes = message.messageString.getLangCodes();
            if (langCodes.size() == 1) {
                msg = message.messageString.getDefault();
            } else {
                if (SettingsStorage.Language.usingEnglish()) {
                    msg = message.messageString.getEnglish();
                } else {
                    msg = message.messageString.getArabic();
                }
            }
        }

        if (message.type instanceof BaseViewModel.MessageType.Normal) {
            if (message.popup) {
                showMessageDialog.invoke(msg);
            } else {
                showToast.invoke(msg);
            }
        } else if (message.type instanceof BaseViewModel.MessageType.Error) {
            BaseViewModel.MessageType.Error mt = (BaseViewModel.MessageType.Error) message.type;
            if (message.popup) {
                MessageDialog dialog = showMessageDialog.invoke(msg);
                if (mt.button1() != null) {
                    Runnable runnable = mt.button1().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (!TextUtils.isEmpty(mt.button1().value2)) {
                        dialog.setButton1(mt.button1().value2, listener);
                    } else {
                        Integer i = mt.button1().value1;
                        if (i == null || i == 0) i = R.string.action;
                        dialog.setButton1(i, listener);
                    }
                }
                if (mt.button2() != null) {
                    Runnable runnable = mt.button2().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (!TextUtils.isEmpty(mt.button2().value2)) {
                        dialog.setButton2(mt.button2().value2, listener);
                    } else {
                        Integer i = mt.button2().value1;
                        if (i == null || i == 0) i = R.string.action;
                        dialog.setButton2(i, listener);
                    }
                }
                if (mt.button3() != null) {
                    Runnable runnable = mt.button3().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (!TextUtils.isEmpty(mt.button3().value2)) {
                        dialog.setButton3(mt.button3().value2, listener);
                    } else {
                        Integer i = mt.button3().value1;
                        if (i == null || i == 0) i = R.string.action;
                        dialog.setButton3(i, listener);
                    }
                }
            } else {
                showToast.invoke(msg);
            }
            mt.destroy();

        } else if (message.type instanceof BaseViewModel.MessageType.Retry) {
            BaseViewModel.MessageType.Retry mt = (BaseViewModel.MessageType.Retry) message.type;
            Runnable onRetry = mt.onRetry();
            mt.destroy();
            showRetryPromptDialog.invoke(msg, () -> {
                if (onRetry != null) onRetry.run();
            });
        }
    }
}
