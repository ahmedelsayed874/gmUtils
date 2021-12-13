package gmutils.ui.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import gmutils.R;

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
public class WaitDialog extends BaseDialog {

    public static WaitDialog show(Context context) {
        return show(context, R.string.wait_moments);
    }

    public static WaitDialog show(Context context, @StringRes int msg) {
        return show(context, context.getString(msg));
    }

    public static WaitDialog show(Context context, CharSequence msg) {
        WaitDialog waitDialog = new WaitDialog(context);
        if (!TextUtils.isEmpty(msg)) waitDialog.textView.setText(msg);
        waitDialog.show();
        return waitDialog;
    }

    private TextView textView;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_wait, null);
    }

    public WaitDialog(Context context) {
        super(context);
        textView = getView().findViewById(R.id.tv_msg);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public TextView textView() { return textView; }

    @Override
    protected void onDestroy() {
        textView = null;
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        return WaitDialog.show(context, textView.getText());
    }
}
