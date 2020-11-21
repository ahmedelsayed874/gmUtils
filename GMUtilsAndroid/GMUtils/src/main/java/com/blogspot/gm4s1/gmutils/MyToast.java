package com.blogspot.gm4s1.gmutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

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
public class MyToast {
    public static Boolean DEFAULT_STYLE = true;

    @AnyRes
    public static Integer BACKGROUND = R.color.colorPrimary; //android.R.color.black;
    @ColorInt
    public static Integer TEXT_COLOR = Color.WHITE;

    @AnyRes
    public static Integer ERROR_BACKGROUND = android.R.color.holo_red_dark;
    @ColorInt
    public static Integer ERROR_TEXT_COLOR = Color.WHITE;

    //----------------------------------------------------------------------------------------------

    public static MyToast createInstance(Context context, @StringRes int msg) {
        return createInstance(context, msg, true);
    }

    public static MyToast createInstance(Context context, @StringRes int msg, boolean defaultStyle) {
        return new MyToast(context, msg, defaultStyle);
    }

    private Toast toast;
    private View root;
    private TextView tv;


    private MyToast(Context context, @StringRes int msg, boolean defaultStyle) {
        this(context, context.getString(msg), defaultStyle);
    }

    @SuppressLint("ShowToast")
    public MyToast(Context context, CharSequence msg) {
        this(context, msg, true);
    }

    @SuppressLint("ShowToast")
    public MyToast(Context context, CharSequence msg, boolean systemStyle) {
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

        root = toast.getView();
        View view = ((ViewGroup) root).getChildAt(0);

        tv = ((TextView) view);
        tv.setGravity(Gravity.CENTER);

        if (!systemStyle) {
            setBackground(BACKGROUND);
            setTextColor(TEXT_COLOR);
        }

        root.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
            @Override
            public void onWindowAttached() {

            }

            @Override
            public void onWindowDetached() {
                MyToast.this.toast = null;
                MyToast.this.root = null;
                MyToast.this.tv = null;
            }
        });
    }

    public MyToast setBackground(@AnyRes int bg) {
        root.setBackgroundResource(bg);

        int plr = root.getContext().getResources().getDimensionPixelOffset(R.dimen.size_10);
        int ptd = 0;

        tv.setPadding(plr, ptd, plr, ptd);

        return this;
    }

    public MyToast setTextColor(@ColorInt int textColor) {
        tv.setTextColor(textColor);
        return this;
    }

    public MyToast show() {
        try {
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public static void show(Context context, @StringRes int msg) {
        show(context, context.getString(msg), null, null);
    }

    public static void show(Context context, CharSequence msg) {
        show(context, msg, null, null);
    }

    public static void show(Context context, CharSequence msg, @AnyRes Integer bg) {
        show(context, msg, bg, null);
    }

    public static void show(Context context, CharSequence msg, @AnyRes Integer bg, @ColorInt Integer textColor) {
        MyToast toast = new MyToast(context, msg, DEFAULT_STYLE);
        if (bg != null) {
            toast.setBackground(bg);
        }
        if (textColor != null) {
            toast.setTextColor(textColor);
        }
        toast.show();
    }

    //----------------------------------------------------------------------------------------------

    public static void showError(Context context, CharSequence msg) {
        show(context, msg, ERROR_BACKGROUND, ERROR_TEXT_COLOR);
    }

    public static void showError(Context context, @StringRes int msg) {
        show(context, context.getString(msg), ERROR_BACKGROUND, ERROR_TEXT_COLOR);
    }
}
