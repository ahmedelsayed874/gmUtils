package gmutils.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import gmutils.listeners.ResultCallback;
import gmutils.listeners.SimpleWindowAttachListener;

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
public abstract class BaseDialog {
    public final AlertDialog dialog;
    private View view;

    @NonNull
    protected abstract View createView(LayoutInflater layoutInflater);

    public BaseDialog(Context context) {
        this(context, null);
    }

    public BaseDialog(Context context, ResultCallback<AlertDialog.Builder> onBuildDialog) {
        view = createView(LayoutInflater.from(context));

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view);
        if (onBuildDialog != null) onBuildDialog.invoke(builder);
        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                view.getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
                    @Override
                    public void onWindowAttached() {
                        onViewCreated(view);
                    }

                    @Override
                    public void onWindowDetached() {
                        destroy();
                    }
                });
            }
        }
    }

    protected void onViewCreated(View view) {
    }

    private void destroy() {
        onDestroy();

//        this.dialog = null;
        this.view = null;
    }

    protected abstract void onDestroy();

    public final BaseDialog getDialog(ResultCallback<AlertDialog> callback) {
        callback.invoke(dialog);
        return this;
    }

    public final View getView() {
        return view;
    }

    public final BaseDialog getView(ResultCallback<View> callback) {
        callback.invoke(view);
        return this;
    }

    public final BaseDialog getContext(ResultCallback<Context> callback) {
        if (view != null) callback.invoke(view.getContext());
        return this;
    }

    //----------------------------------------------------------------------------------------------

    private String dismissedBy;

    //public BaseDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
    public BaseDialog setOnDismissListener(ResultCallback<String> listener) {
        if (listener == null) {
            dialog.setOnDismissListener(null);
        } else {
            dialog.setOnDismissListener((d) -> {
                listener.invoke(dismissedBy);
            });
        }
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public BaseDialog setCancelable(boolean cancellable) {
        dialog.setCancelable(cancellable);
        dialog.setCanceledOnTouchOutside(cancellable);
        return this;
    }

    public abstract BaseDialog setTitleColorRes(@ColorRes int resid);
    public abstract BaseDialog setTextColorRes(@ColorRes int resid);

    public BaseDialog setBackground(int color) {
        view.setBackgroundColor(color);
        return this;
    }

    public BaseDialog setBackgroundRes(@DrawableRes int resid) {
        view.setBackgroundResource(resid);
        return this;
    }

    public BaseDialog show() {
        if (!dialog.isShowing())
            dialog.show();
        return this;
    }

    public void dismiss(String dismissedBy) {
        this.dismissedBy = dismissedBy;

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void reshow(Context context) {
        try {
            BaseDialog dialog = reinitialize(context);
            dialog.show();
            this.dismiss(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract BaseDialog reinitialize(Context context);
}
