package gmutils.ui.utils;

import android.content.Context;
import android.text.TextUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import gmutils.R;
import gmutils.StringSet;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.viewModels.BaseViewModel;
import gmutils.utils.TextHelper;

public class BaseViewModelObserversHandlers {
    public void onProgressOfViewModelTaskChanged(
            Context context,
            BaseViewModel.ProgressStatus progressStatus,
            ResultCallback<CharSequence> showWaitView,
            ResultCallback<CharSequence> updateWaitViewMsg,
            Runnable hideWaitView
    ) {
        CharSequence msg = null;
        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show ps) {
            msg = getText(context, ps);
        }

        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show) {
            if (progressStatus instanceof BaseViewModel.ProgressStatus.Update) {
                updateWaitViewMsg.invoke(msg);
            } else {
                showWaitView.invoke(msg);
            }
        }
        //
        else if (progressStatus instanceof BaseViewModel.ProgressStatus.Hide) {
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
        CharSequence msg = getText(context, message);

        if (message.type instanceof BaseViewModel.MessageType.Normal mt) {
            if (message.popup) {
                MessageDialog dialog = showMessageDialog.invoke(msg);
                dialog.setCancelable(message.isEnableOuterDismiss());

                if (mt.button1() != null) {
                    Runnable runnable = mt.button1().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (mt.button1().value1 == null) {
                        dialog.setButton1(getText(mt.button1().value2), listener);
                    } else {
                        int i = mt.button1().value1;
                        if (i == 0) i = R.string.action;
                        dialog.setButton1(i, listener);
                    }
                }
                if (mt.button2() != null) {
                    Runnable runnable = mt.button2().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (mt.button2().value1 == null) {
                        dialog.setButton2(getText(mt.button2().value2), listener);
                    } else {
                        int i = mt.button2().value1;
                        if (i == 0) i = R.string.action;
                        dialog.setButton2(i, listener);
                    }
                }
                if (mt.button3() != null) {
                    Runnable runnable = mt.button3().value3;
                    MessageDialog.Listener listener = runnable == null ? null : d -> runnable.run();

                    if (mt.button3().value1 == null) {
                        dialog.setButton3(getText(mt.button3().value2), listener);
                    } else {
                        int i = mt.button3().value1;
                        if (i == 0) i = R.string.action;
                        dialog.setButton3(i, listener);
                    }
                }
            } else {
                showToast.invoke(msg);
            }
            mt.destroy();

            if (message.type instanceof BaseViewModel.MessageType.Error) {
            }
        }
        //
        else if (message.type instanceof BaseViewModel.MessageType.Retry mt) {
            Runnable onRetry = mt.onRetry();
            mt.destroy();
            showRetryPromptDialog.invoke(msg, () -> {
                if (onRetry != null) onRetry.run();
            });
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

    private CharSequence getText(Context context, @Nullable BaseViewModel.MessageDependent message) {
        if (message == null) return "";

        CharSequence msg;
        if (message.getMessagesCount() == 1) {
            Object m = message.getMessage(0);
            if (m instanceof Integer) {
                msg = context.getString((Integer) m);
            }
            //
            else if (m instanceof StringSet) {
                msg = getText((StringSet) m);
            }
            //
            else {
                msg = m == null ? "" : m.toString();
            }
        }
        //
        else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < message.getMessagesCount(); i++) {
                //if (stringBuilder.length() > 0) stringBuilder.append(" ");

                Object m = message.getMessage(i);
                if (m instanceof Integer) {
                    stringBuilder.append(context.getString((Integer) m));
                }
                //
                else if (m instanceof StringSet) {
                    stringBuilder.append(getText((StringSet) m));
                }
                //
                else {
                    stringBuilder.append(m == null ? "" : m.toString());
                }
            }

            msg = TextHelper.createInstance().parseHtmlText(stringBuilder.toString());
        }

        return msg;
    }

}
