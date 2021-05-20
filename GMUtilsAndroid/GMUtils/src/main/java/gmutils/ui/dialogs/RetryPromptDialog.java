package gmutils.ui.dialogs;


import android.content.Context;
import gmutils.R;

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
public class RetryPromptDialog {

    public interface Listener {
        void invoke(RetryPromptDialog dialog);
    }

    public static RetryPromptDialog show(Context context, CharSequence msg, Listener onRetry) {
        return show(context, msg, onRetry, null);
    }

    public static RetryPromptDialog show(Context context, CharSequence msg, Listener onRetry, Listener onCancel) {
        RetryPromptDialog dialog = new RetryPromptDialog(context, msg, onRetry, onCancel);
        dialog.show();
        return dialog;
    }

    private final MessageDialog dialog;


    RetryPromptDialog(Context context, CharSequence msg, Listener onRetry, Listener onCancel) {
        dialog = new MessageDialog(context);
        dialog.setMessage(msg);
        dialog.setButton1(R.string.retry, d -> {
            if (onRetry != null) onRetry.invoke(this);
            dialog.dismiss();
        });

        dialog.setButton2(R.string.cancel, d -> {
            if (onCancel != null) onCancel.invoke(this);
            dialog.dismiss();
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public RetryPromptDialog setBackground(int resId) {
        dialog.setBackground(resId);
        return this;
    }

    public RetryPromptDialog setTextColor(int color) {
        dialog.setTextColor(color);
        return this;
    }

    public RetryPromptDialog setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public RetryPromptDialog show() {
        dialog.show();
        return this;
    }

}