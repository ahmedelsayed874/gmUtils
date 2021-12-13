package gmutils.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import org.jetbrains.annotations.Nullable;

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
public class OptionsDialog {

    public interface Listener {
        void onItemSelected(int position);
    }

    public static void show(Context context, @Nullable String title, CharSequence[] list, Listener listener) {
        show(context, title, list, 0, listener);
    }

    public static void show(Context context, @Nullable String title, CharSequence[] list, int defaultSelect, Listener listener) {
        new OptionsDialog(context, title, list, defaultSelect, listener);
    }

    private OptionsDialog(Context context, @Nullable String title, CharSequence[] list, int defaultSelect, Listener listener) {
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