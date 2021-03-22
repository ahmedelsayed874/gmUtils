package com.blogspot.gm4s1.gmutils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.gm4s1.gmutils.R;

import org.jetbrains.annotations.Nullable;

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
public class MyToast2 {
    public static Boolean DEFAULT_STYLE = true;

    public static Integer BACKGROUND_RES = R.color.colorPrimary; //android.R.color.black;
    public static Integer TEXT_COLOR_RES = Color.WHITE;

    public static Integer ERROR_BACKGROUND_RES = android.R.color.holo_red_dark;
    public static Integer ERROR_TEXT_COLOR_RES = Color.WHITE;

    //----------------------------------------------------------------------------------------------

    public static MyToast2 createInstance(Activity activity, int msgRes) {
        return createInstance(activity, msgRes, true);
    }

    public static MyToast2 createInstance(Activity activity, int msgRes, boolean defaultStyle) {
        return new MyToast2(activity, msgRes, defaultStyle);
    }

    private ViewGroup windowLayout;
    private View rootLayout;
    private View textContainer;
    private TextView tv;
    private long duration = 3000;


    private MyToast2(Activity activity, int msgRes, boolean defaultStyle) {
        this(activity, activity.getString(msgRes), defaultStyle);
    }

    @SuppressLint("ShowToast")
    public MyToast2(Activity activity, CharSequence msg) {
        this(activity, msg, true);
    }

    @SuppressLint("ShowToast")
    public MyToast2(Activity activity, CharSequence msg, boolean systemStyle) {
        windowLayout = activity.findViewById(android.R.id.content);

        rootLayout = LayoutInflater.from(activity)
                .inflate(R.layout.mytoast_gmutils, null);
        
        textContainer = rootLayout.findViewById(R.id.text_container);

        tv = textContainer.findViewById(R.id.tv_msg);
        tv.setText(msg);

        if (!systemStyle) {
            setBackground(BACKGROUND_RES);
            setTextColor(TEXT_COLOR_RES);
        }
    }

    public MyToast2 setBackground(int bgRes) {
        try {
            textContainer.setBackgroundResource(bgRes);

            int plr = textContainer.getContext().getResources().getDimensionPixelOffset(R.dimen.size_10);
            int ptd = 0;

            tv.setPadding(plr, ptd, plr, ptd);
        } catch (Exception e) {
        }

        return this;
    }

    public MyToast2 setTextColor(int textColorRes) {
        try {
            tv.setTextColor(textColorRes);
        } catch (Exception e) {
        }
        return this;
    }

    public MyToast2 setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public MyToast2 show() {
        windowLayout.addView(rootLayout);

        rootLayout.postDelayed(this::hide, duration);

        return this;
    }

    public MyToast2 hide() {
        windowLayout.removeView(rootLayout);
        dispose();
        return this;
    }

    private void dispose() {
        windowLayout = null;
        rootLayout = null;
        textContainer  = null;
        tv = null;
    }

    //----------------------------------------------------------------------------------------------

    public static void show(Activity activity, int msgRes) {
        show(activity, activity.getString(msgRes), null, null);
    }

    public static void show(Activity activity, CharSequence msg) {
        show(activity, msg, null, null);
    }

    public static void show(Activity activity, CharSequence msg, @Nullable Integer bgRes) {
        show(activity, msg, bgRes, null);
    }

    public static void show(Activity activity, CharSequence msg, @Nullable Integer bgRes, @Nullable Integer textColorRes) {
        MyToast2 toast = new MyToast2(activity, msg, DEFAULT_STYLE);
        if (bgRes != null) {
            toast.setBackground(bgRes);
        }
        if (textColorRes != null) {
            toast.setTextColor(textColorRes);
        }
        toast.show();
    }

    //----------------------------------------------------------------------------------------------

    public static void showError(Activity activity, CharSequence msg) {
        show(activity, msg, ERROR_BACKGROUND_RES, ERROR_TEXT_COLOR_RES);
    }

    public static void showError(Activity activity, int msgRes) {
        show(activity, activity.getString(msgRes), ERROR_BACKGROUND_RES, ERROR_TEXT_COLOR_RES);
    }
}
