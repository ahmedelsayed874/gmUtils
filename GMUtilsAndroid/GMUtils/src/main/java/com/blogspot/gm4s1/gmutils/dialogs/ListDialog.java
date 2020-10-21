package com.blogspot.gm4s1.gmutils.dialogs;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.blogspot.gm4s1.gmutils.R;

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
public class ListDialog {

    public interface Listener {
        void onItemSelected(int position);
    }

    public static void show(Context context, @Nullable String title, CharSequence[] list, Listener listener) {
        show(context, title, list, 0, listener);
    }

    public static void show(Context context, @Nullable String title, CharSequence[] list, int defaultSelect, Listener listener) {
        new ListDialog(context, title, list, defaultSelect, listener);
    }

    private ListDialog(Context context, @Nullable String title, CharSequence[] list, int defaultSelect, Listener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(list, defaultSelect, (dialog, which) -> {
                    if (listener != null) listener.onItemSelected(which);
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}