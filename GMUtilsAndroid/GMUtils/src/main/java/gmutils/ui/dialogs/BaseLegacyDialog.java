package gmutils.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;

import org.jetbrains.annotations.NotNull;

import gmutils.listeners.SimpleWindowAttachListener;

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
public abstract class BaseLegacyDialog {
    private AlertDialog dialog;
    private View view;


    @NotNull
    protected abstract View createView(LayoutInflater layoutInflater);

    public BaseLegacyDialog(Context context) {
        view = createView(LayoutInflater.from(context));

        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

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
        this.dialog = null;
        this.view = null;

        onDestroy();
    }

    protected abstract void onDestroy();

    public final AlertDialog getDialog() {
        return dialog;
    }

    public final View getView() {
        return view;
    }

    //----------------------------------------------------------------------------------------------

    public BaseLegacyDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
        dialog.setOnDismissListener(listener);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public BaseLegacyDialog setCancelable(boolean cancellable) {
        dialog.setCancelable(cancellable);
        dialog.setCanceledOnTouchOutside(cancellable);
        return this;
    }

    public BaseLegacyDialog setBackground(int color) {
        getView().setBackgroundColor(color);
        return this;
    }

    public BaseLegacyDialog setBackgroundRes(@DrawableRes int resid) {
        getView().setBackgroundResource(resid);
        return this;
    }

    public BaseLegacyDialog show() {
        if (!dialog.isShowing())
            dialog.show();
        return this;
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
