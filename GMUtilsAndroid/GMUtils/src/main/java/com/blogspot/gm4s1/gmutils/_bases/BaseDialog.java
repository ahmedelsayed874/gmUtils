package com.blogspot.gm4s1.gmutils._bases;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.blogspot.gm4s1.gmutils.dialogs.MessageDialog;

public abstract class BaseDialog {
    private AlertDialog dialog;

    public BaseDialog(Context context) {
        View view = getView();

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
    }

    protected AlertDialog getDialog() {
        return dialog;
    }

    @NonNull
    public abstract View getView();

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
        if (dialog.isShowing())
            dialog.dismiss();
    }
}
