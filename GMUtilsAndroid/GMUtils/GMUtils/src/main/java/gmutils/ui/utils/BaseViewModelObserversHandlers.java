package gmutils.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.text.TextUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import gmutils.R;
import gmutils.StringSet;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.viewModels.BaseViewModel;
import gmutils.utils.TextHelper;

public class BaseViewModelObserversHandlers {
    public void onProgressOfViewModelTaskChanged(
            Context context,
            BaseViewModel.ProgressStatus progressStatus,
            ResultCallback<CharSequence> showWaitView,
            ResultCallback<CharSequence> updateWaitViewMsg,
            ResultCallback<Boolean /*forceHide*/> hideWaitView
    ) {
        CharSequence msg = null;
        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show ps) {
            try {
                msg = getText(context, ps);
            } catch (Exception e) {
                msg = null;
            }
        }

        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show) {
            if (progressStatus instanceof BaseViewModel.ProgressStatus.Update) {
                updateWaitViewMsg.invoke(msg);
            } else {
                showWaitView.invoke(msg);
            }
        }
        //
        else if (progressStatus instanceof BaseViewModel.ProgressStatus.Hide ps) {
            hideWaitView.invoke(ps.forceHide);
        }
    }

    public void onMessageReceivedFromViewModel(
            Context context,
            BaseViewModel.Message message,
            ActionCallback<CharSequence, MessageDialog> showMessageDialog,
            ResultCallback2<CharSequence, Boolean/*normal?*/> showToast,
            ActionCallback2<CharSequence, Runnable, RetryPromptDialog> showRetryPromptDialog
    ) {
        CharSequence msg = getText(context, message);

        if (message.type instanceof BaseViewModel.MessageType.Dialog mt) {
            MessageDialog dialog = showMessageDialog.invoke(msg);

            if (mt.getIconRes() > 0) dialog.setIcon(mt.getIconRes());

            CharSequence title = getText(context, mt.getTitle());
            if (!TextUtils.isEmpty(title)) dialog.setTitle(title);

            dialog.setCancelable(mt.isEnableOuterDismiss());

            if (mt.getOnDismiss() != null) {
                Runnable onDismiss = mt.getOnDismiss();
                dialog.setOnDismissListener(dialog1 -> onDismiss.run());
            }

            if (mt.hasSpecialButtons()) {
                if (mt.button1() != null) {
                    Runnable runnable = mt.button1().value2;
                    MessageDialog.Listener listener = runnable == null ? null : runnable::run;

                    CharSequence txt = getText(context, mt.button1().value1);
                    if (TextUtils.isEmpty(txt)) txt = context.getString(R.string.action);
                    dialog.setButton1(txt, listener);
                }
                if (mt.button2() != null) {
                    Runnable runnable = mt.button2().value2;
                    MessageDialog.Listener listener = runnable == null ? null : runnable::run;

                    CharSequence txt = getText(context, mt.button2().value1);
                    if (TextUtils.isEmpty(txt)) txt = context.getString(R.string.action);
                    dialog.setButton2(txt, listener);
                }
                if (mt.button3() != null) {
                    Runnable runnable = mt.button3().value2;
                    MessageDialog.Listener listener = runnable == null ? null : runnable::run;

                    CharSequence txt = getText(context, mt.button3().value1);
                    if (TextUtils.isEmpty(txt)) txt = context.getString(R.string.action);
                    dialog.setButton3(txt, listener);
                }
            }

            mt.destroy();
        }
        //
        else if (message.type instanceof
                BaseViewModel.MessageType.Hint mt) {
            showToast.invoke(msg, !mt.error);

            mt.destroy();
        }
        //
        else if (message.type instanceof
                BaseViewModel.MessageType.Retry mt) {
            Runnable onRetry = mt.onRetry();
            mt.destroy();

            RetryPromptDialog dialog = showRetryPromptDialog.invoke(msg, () -> {
                if (onRetry != null) onRetry.run();
            });

            if (mt.getIconRes() > 0) {
                dialog.dialog.setIcon(mt.getIconRes());
            }
        }
    }

    private CharSequence getText(@Nullable StringSet string) {
        if (string == null) return "";

        List<String> langCodes = string.getLangCodes();
        if (langCodes.size() == 1) {
            return string.getDefault();
        } else {
            if (SettingsStorage.Language.usingEnglish()) {
                return string.getEnglish();
            } else {
                return string.getArabic();
            }
        }
    }

    private CharSequence getText(Context context, @Nullable Object m) {
        CharSequence txt;
        if (m instanceof Integer) {
            txt = context.getString((Integer) m);
        }
        //
        else if (m instanceof CharSequence) {
            txt = (CharSequence) m;
        }
        //
        else if (m instanceof StringSet) {
            txt = getText((StringSet) m);
        }
        //
        else {
            txt = (m == null ? "" : m.toString());
        }
        return txt;
    }

    private CharSequence getText(Context context, @Nullable BaseViewModel.MessageDependent message) {
        if (message == null) return "";

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < message.getMessagesCount(); i++) {
            if (stringBuilder.length() > 0) stringBuilder.append("\n");

            Object m = message.getMessage(i);
            stringBuilder.append(getText(context, m));
        }

        CharSequence msg = TextHelper.createInstance().parseHtmlText(stringBuilder.toString());

        return msg;
    }

}
