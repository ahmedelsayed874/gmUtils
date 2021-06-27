package gmutils.ui.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import gmutils.R;

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
public class ToastCustom implements MyToast.IToast {

    private ViewGroup windowLayout;
    private View rootLayout;
    private View textContainer;
    private TextView tv;
    private long duration = 3000;
    

    public ToastCustom(Activity activity, int msg, boolean systemStyle) {
        this(activity, activity.getString(msg), systemStyle);
    }

    @SuppressLint("ShowToast")
    public ToastCustom(Activity activity, CharSequence msg, boolean systemStyle) {
        windowLayout = activity.findViewById(android.R.id.content);

        rootLayout = LayoutInflater.from(activity)
                .inflate(R.layout.mytoast_gmutils, null);
        
        textContainer = rootLayout.findViewById(R.id.text_container);

        tv = textContainer.findViewById(R.id.tv_msg);
        tv.setText(msg);

        if (!systemStyle) {
            setBackground(MyToast.BACKGROUND_RES);
            setTextColor(MyToast.TEXT_COLOR_RES);
        }
    }


    @Override
    public MyToast.IToast setBackground(int bgRes) {
        try {
            textContainer.setBackgroundResource(bgRes);

            int plr = textContainer.getContext().getResources().getDimensionPixelOffset(R.dimen.size_10);
            int ptd = 0;

            tv.setPadding(plr, ptd, plr, ptd);
        } catch (Exception ignored) {
        }

        return this;
    }

    @Override
    public MyToast.IToast setTextColor(int textColorRes) {
        try {
            tv.setTextColor(textColorRes);
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public MyToast.IToast setMessage(int msgRes) {
        tv.setText(msgRes);
        return this;
    }

    @Override
    public MyToast.IToast setMessage(CharSequence msg) {
        tv.setText(msg);
        return this;
    }

    public ToastCustom setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public MyToast.IToast show() {
        windowLayout.addView(rootLayout);

        rootLayout.postDelayed(this::hide, duration);

        return this;
    }

    public ToastCustom hide() {
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

}
