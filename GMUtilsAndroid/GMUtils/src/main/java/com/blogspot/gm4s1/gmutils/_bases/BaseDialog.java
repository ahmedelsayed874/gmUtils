package com.blogspot.gm4s1.gmutils._bases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.blogspot.gm4s1.gmutils.dialogs.MessageDialog;

public abstract class BaseDialog {
    private AlertDialog dialog;
    private View view;

    @NonNull
    protected abstract View createView(LayoutInflater layoutInflater);

    public BaseDialog(Context context) {
        view = createView(LayoutInflater.from(context));

        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (view != null) {
            view.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                @Override
                public void onWindowAttached() {
                    onStart();
                }

                @Override
                public void onWindowDetached() {
                    onDestroy();
                }
            });
        }
    }

    protected void onStart() {
    }

    protected void onDestroy() {
        this.dialog = null;
        this.view = null;
    }

    public final AlertDialog getDialog() {
        return dialog;
    }

    public final View getView() {
        return view;
    }

    public BaseDialog setCancelable(boolean cancellable) {
        dialog.setCancelable(cancellable);
        dialog.setCanceledOnTouchOutside(cancellable);
        return this;
    }

    public BaseDialog show() {
        if (!dialog.isShowing())
            dialog.show();
        return this;
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
