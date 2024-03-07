package gmutils.ui.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

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
        return show(context, true);
    }

    public static WaitDialog show(Context context, @StringRes int msg) {
        return show(context, msg, true);
    }

    public static WaitDialog show(Context context, CharSequence msg) {
        return show(context, msg, true);
    }

    public static WaitDialog show(Context context, boolean useHorizontalLayout) {
        return show(context, R.string.wait_moments, useHorizontalLayout);
    }

    public static WaitDialog show(Context context, @StringRes int msg, boolean useHorizontalLayout) {
        return show(context, context.getString(msg), useHorizontalLayout);
    }

    public static WaitDialog show(Context context, CharSequence msg, boolean useHorizontalLayout) {
        WaitDialog.useHorizontalLayout = useHorizontalLayout;

        WaitDialog waitDialog = new WaitDialog(context);
        if (!TextUtils.isEmpty(msg)) waitDialog.textView.setText(msg);
        waitDialog.show();
        return waitDialog;
    }

    private static boolean useHorizontalLayout = true;


    private LinearLayout container;
    private ProgressBar progressBar;
    private TextView textView;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(
                useHorizontalLayout ?
                        R.layout.dialog_wait_hor :
                        R.layout.dialog_wait_ver,
                null
        );
    }

    public WaitDialog(Context context) {
        super(context);
        container = getView().findViewById(R.id.container);
        progressBar = getView().findViewById(R.id.progressBar);
        textView = getView().findViewById(R.id.tv_msg);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public TextView textView() {
        return textView;
    }

    public ProgressBar progressBar() {
        return progressBar;
    }

    public LinearLayout container() {
        return container;
    }

    @Override
    public WaitDialog setTitleColorRes(int resid) {
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), resid));
        return this;
    }

    @Override
    public WaitDialog setTextColorRes(int resid) {
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), resid));
        return this;
    }

    @Override
    protected void onDestroy() {
        textView = null;
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        return WaitDialog.show(context, textView.getText());
    }
}
