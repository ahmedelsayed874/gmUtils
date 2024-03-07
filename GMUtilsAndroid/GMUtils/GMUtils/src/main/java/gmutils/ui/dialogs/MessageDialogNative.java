package gmutils.ui.dialogs;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

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
public class MessageDialogNative {

    public interface Listener {
        void invoke();
    }

    public static MessageDialogNative create(Context context) {
        MessageDialogNative dialog = new MessageDialogNative(context);
        return dialog;
    }

    public final AlertDialog.Builder dialog;

    MessageDialogNative(Context context) {
        dialog = new AlertDialog.Builder(context);
    }

    public MessageDialogNative setIcon(int imageRes) {
        dialog.setIcon(imageRes);
        return this;
    }


    public MessageDialogNative setTitle(int txt) {
        dialog.setTitle(txt);
        return this;
    }

    public MessageDialogNative setTitle(CharSequence txt) {
        dialog.setTitle(txt);
        return this;
    }


    public MessageDialogNative setMessage(int msg) {
        dialog.setMessage(msg);
        return this;
    }

    public MessageDialogNative setMessage(CharSequence msg) {
        dialog.setMessage(msg);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialogNative setButton1(int textId, Listener listener) {
        dialog.setPositiveButton(textId, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }

    public MessageDialogNative setButton1(CharSequence text, Listener listener) {

        dialog.setPositiveButton(text, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }

    public MessageDialogNative setButton2(int textId, Listener listener) {

        dialog.setNeutralButton(textId, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }

    public MessageDialogNative setButton2(CharSequence text, Listener listener) {

        dialog.setNeutralButton(text, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }

    public MessageDialogNative setButton3(int textId, Listener listener) {

        dialog.setNegativeButton(textId, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }

    public MessageDialogNative setButton3(CharSequence text, Listener listener) {

        dialog.setNegativeButton(text, (x, y) -> {
            if (listener != null) {
                listener.invoke();
            }
            x.dismiss();
        });
        return this;
    }


    public MessageDialogNative setCancelable(boolean cancellable) {
        dialog.setCancelable(cancellable);
        return this;
    }

    public MessageDialogNative getDialog(ResultCallback<AlertDialog.Builder> callback) {
        callback.invoke(dialog);
        return this;
    }

    public MessageDialogNative show() {
        dialog.show();
        return this;
    }

}
