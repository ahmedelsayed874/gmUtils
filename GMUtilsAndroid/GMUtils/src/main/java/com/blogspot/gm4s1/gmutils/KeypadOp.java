package com.blogspot.gm4s1.gmutils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class KeypadOp {
    public static void hide(Activity activity) {
        if (activity == null) return;
        hide(activity.getCurrentFocus());
    }

    public static void hide(Fragment fragment) {
        if (fragment.getView() != null) {
            KeypadOp.hide(fragment.getView().findFocus());
        }
    }

    public static void hide(View focusedView) {
        if (focusedView == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) focusedView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);

    }

    public static void show(EditText editText) {
        if (editText == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);

    }

}
