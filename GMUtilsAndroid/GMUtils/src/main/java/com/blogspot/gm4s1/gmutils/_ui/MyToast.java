package com.blogspot.gm4s1.gmutils._ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.listeners.SimpleWindowAttachListener;

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

    public static Integer BACKGROUND_RES = R.color.colorPrimary; //android.R.color.black;
    public static Integer TEXT_COLOR_RES = Color.WHITE;

    public static Integer ERROR_BACKGROUND_RES = android.R.color.holo_red_dark;
    public static Integer ERROR_TEXT_COLOR_RES = Color.WHITE;

    //----------------------------------------------------------------------------------------------

    public static MyToast createInstance(Context context, int msgRes) {
        return createInstance(context, msgRes, true);
    }

    public static MyToast createInstance(Context context, int msgRes, boolean defaultStyle) {
        return new MyToast(context, msgRes, defaultStyle);
    }

    private Toast toast;
    private View root;
    private TextView tv;


    private MyToast(Context context, int msgRes, boolean defaultStyle) {
        this(context, context.getString(msgRes), defaultStyle);
    }

    @SuppressLint("ShowToast")
    public MyToast(Context context, CharSequence msg) {
        this(context, msg, true);
    }

    @SuppressLint("ShowToast")
    public MyToast(Context context, CharSequence msg, boolean systemStyle) {
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

        try {
            root = toast.getView();
            View view = ((ViewGroup) root).getChildAt(0);

            tv = ((TextView) view);
            tv.setGravity(Gravity.CENTER);

            if (!systemStyle) {
                setBackground(BACKGROUND_RES);
                setTextColor(TEXT_COLOR_RES);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                root.getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
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
        } catch (Exception e) {
        }
    }

    public MyToast setBackground(int bgRes) {
        try {
            root.setBackgroundResource(bgRes);

            int plr = root.getContext().getResources().getDimensionPixelOffset(R.dimen.size_10);
            int ptd = 0;

            tv.setPadding(plr, ptd, plr, ptd);
        } catch (Exception e) {
        }

        return this;
    }

    public MyToast setTextColor(int textColorRes) {
        try {
            tv.setTextColor(textColorRes);
        } catch (Exception e) {
        }
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

    public static void show(Context context, int msgRes) {
        show(context, context.getString(msgRes), null, null);
    }

    public static void show(Context context, CharSequence msg) {
        show(context, msg, null, null);
    }

    public static void show(Context context, CharSequence msg, Integer bgRes) {
        show(context, msg, bgRes, null);
    }

    public static void show(Context context, CharSequence msg, Integer bg, Integer textColorRes) {
        MyToast toast = new MyToast(context, msg, DEFAULT_STYLE);
        if (bg != null) {
            toast.setBackground(bg);
        }
        if (textColorRes != null) {
            toast.setTextColor(textColorRes);
        }
        toast.show();
    }

    //----------------------------------------------------------------------------------------------

    public static void showError(Context context, CharSequence msg) {
        show(context, msg, ERROR_BACKGROUND_RES, ERROR_TEXT_COLOR_RES);
    }

    public static void showError(Context context, int msgRes) {
        show(context, context.getString(msgRes), ERROR_BACKGROUND_RES, ERROR_TEXT_COLOR_RES);
    }
}
