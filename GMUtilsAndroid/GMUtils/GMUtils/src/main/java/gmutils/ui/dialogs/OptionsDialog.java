package gmutils.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import gmutils.R;
import gmutils.listeners.ResultCallback;

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
    public static class SingleChoice {
        public interface Listener {
            void onItemSelected(Object item, Integer position);
        }

        public static OptionsDialog show(Context context, @Nullable String title, CharSequence[] list, Listener listener) {
            return show(context, title, list, -1, listener);
        }

        public static OptionsDialog show(Context context, @Nullable String title, CharSequence[] list, int defaultSelect, Listener listener) {
            return new OptionsDialog(context, title, d -> {
                d.setSingleChoiceItems(list, defaultSelect, (dialog, which) -> {
                            if (listener != null) listener.onItemSelected(list[which], which);
                            dialog.dismiss();
                        })
                        .setPositiveButton(R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                        });
            });
        }

        public static OptionsDialog show(Context context, @Nullable String title, Object[] list, Listener listener) {
            return show(context, title, list, -1, listener);
        }

        public static OptionsDialog show(Context context, @Nullable String title, Object[] list, int defaultSelect, Listener listener) {
            CharSequence[] items = convertObjectsToCharSequences(list);

            return new OptionsDialog(context, title, d -> {
                d.setSingleChoiceItems(items, defaultSelect, (dialog, which) -> {
                            if (listener != null) listener.onItemSelected(list[which], which);
                            dialog.dismiss();
                        })
                        .setPositiveButton(R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                        });
            });
        }
    }

    public static class MultiChoice {
        public interface Listener {
            void onItemsSelected(Pair<Object, Integer>[] itemsAndPositions);
        }

        public static OptionsDialog show(Context context, @Nullable String title, CharSequence[] list, Listener listener) {
            return show(context, title, list, null, listener);
        }

        public static OptionsDialog show(Context context, @Nullable String title, CharSequence[] list, int[] defaultSelect, Listener listener) {
            boolean[] checkedItem = checkedItems(list, defaultSelect);

            return new OptionsDialog(context, title, d -> {
                Map<Integer, Object> selections = new HashMap<>();

                d.setMultiChoiceItems(list, checkedItem, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) selections.put(which, list[which]);
                                else selections.remove(which);
                            }
                        })
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            Pair<Object, Integer>[] itemsAndPositions = new Pair[selections.size()];
                            int i = 0;
                            for (Integer integer : selections.keySet()) {
                                itemsAndPositions[i] = new Pair<>(selections.get(integer), integer);
                                i++;
                            }
                            if (listener != null) listener.onItemsSelected(itemsAndPositions);
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                        });
            });
        }

        public static OptionsDialog show(Context context, @Nullable String title, Object[] list, Listener listener) {
            return show(context, title, list, null, listener);
        }

        public static OptionsDialog show(Context context, @Nullable String title, Object[] list, int[] defaultSelect, Listener listener) {
            CharSequence[] items = convertObjectsToCharSequences(list);

            boolean[] checkedItem = checkedItems(list, defaultSelect);

            return new OptionsDialog(context, title, d -> {
                Map<Integer, Object> selections = new HashMap<>();

                d.setMultiChoiceItems(items, checkedItem, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) selections.put(which, list[which]);
                                else selections.remove(which);
                            }
                        })
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            Pair<Object, Integer>[] itemsAndPositions = new Pair[selections.size()];
                            int i = 0;
                            for (Integer integer : selections.keySet()) {
                                itemsAndPositions[i] = new Pair<>(selections.get(integer), integer);
                                i++;
                            }
                            if (listener != null) listener.onItemsSelected(itemsAndPositions);
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                        });
            });

        }

        private static boolean[] checkedItems(Object[] list, int[] defaultSelect) {
            boolean[] checkedItem;

            if (defaultSelect != null && defaultSelect.length > 0) {
                checkedItem = new boolean[list.length];
                for (int idx : defaultSelect) {
                    checkedItem[idx] = true;
                }
            } else {
                checkedItem = null;
            }

            return checkedItem;
        }
    }

    private static CharSequence[] convertObjectsToCharSequences(Object[] list) {
        CharSequence[] items = new CharSequence[list.length];
        for (int i = 0; i < list.length; i++) {
            items[i] = list[i].toString();
        }
        return items;
    }

    public final AlertDialog dialog;

    private OptionsDialog(Context context, @Nullable String title, ResultCallback<AlertDialog.Builder> build) {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(context)
                .setTitle(title);

        build.invoke(builder);

        dialog = builder.show();
    }

    public OptionsDialog getDialog(ResultCallback<AlertDialog> callback) {
        callback.invoke(dialog);
        return this;
    }
}