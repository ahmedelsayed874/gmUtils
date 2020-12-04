package com.blogspot.gm4s1.gmutils.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils._bases.BaseDialog;

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
public class WaitDialog extends BaseDialog {

    public static WaitDialog show(Context context) {
        return show(context, R.string.wait_moments);
    }

    public static WaitDialog show(Context context, @StringRes int msg) {
        WaitDialog waitDialog = new WaitDialog(context);
        if (msg != 0) waitDialog.textView.setText(msg);
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
    protected void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
    }

    public TextView textView() { return textView; }

    @Override
    protected void onDestroy() {
        textView = null;
    }
}
