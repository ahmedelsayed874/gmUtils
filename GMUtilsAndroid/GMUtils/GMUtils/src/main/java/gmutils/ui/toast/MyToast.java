package gmutils.ui.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.StringRes;

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
public class MyToast {

    public interface IToast {

        IToast setBackground(int bgRes);

        IToast setTextColor(int textColorRes);

        IToast setMessage(int msgRes);

        IToast setMessage(CharSequence msg);

        IToast show();
    }

    //----------------------------------------------------------------------------------------------

    public static class CustomStyle {
        public Integer BACKGROUND_RES = R.color.gmPrimary; //android.R.color.black;
        public Integer TEXT_COLOR_RES = R.color.gmPrimaryVariant;

        public Integer ERROR_BACKGROUND_RES = android.R.color.holo_red_dark;
        public Integer ERROR_TEXT_COLOR_RES = Color.WHITE;
    }

    public static CustomStyle customStyle = null;

    //----------------------------------------------------------------------------------------------

    private MyToast.IToast toast;

    public MyToast(Context context, @StringRes int msg) {
        this(context, context.getString(msg), false);
    }

    public MyToast(Context context, @StringRes int msg, boolean fastShow) {
        this(context, context.getString(msg), fastShow);
    }

    public MyToast(Context context, CharSequence msg) {
        this(context, msg, false);
    }

    public MyToast(Context context, CharSequence msg, boolean fastShow) {
        this(context, msg, fastShow, false);
    }

    public MyToast(Context context, CharSequence msg, boolean fastShow, boolean useCustomView) {
        init(context, msg, fastShow, useCustomView || customStyle != null);

        if (customStyle != null) {
            setBackground(MyToast.customStyle.BACKGROUND_RES);
            setTextColor(MyToast.customStyle.TEXT_COLOR_RES);
        }
    }

    private void init(Context context, CharSequence msg, boolean fastShow, boolean willCustomize) {
        if (!willCustomize && Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            toast = new ToastNative(context, msg, fastShow, false);
        }
        //
        else {
            try {
                toast = new ToastNative(context, msg, fastShow, true);
            } catch (Exception e) {
                if (context instanceof Activity)
                    toast = new ToastCustom((Activity) context, msg, fastShow);
                else
                    toast = new ToastNative(context, msg, fastShow);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    public MyToast setBackground(int bgRes) {
        toast.setBackground(bgRes);
        return this;
    }

    public MyToast setTextColor(int textColorRes) {
        toast.setTextColor(textColorRes);
        return this;
    }

    public MyToast show() {
        try {
            toast.show();
        } catch (Throwable e) {
        }

        return this;
    }

    //----------------------------------------------------------------------------------------------

    public static void show(Context context, @StringRes int msgRes) {
        show(context, context.getString(msgRes), false, null, null);
    }

    public static void show(Context context, @StringRes int msgRes, boolean fastShow) {
        show(context, context.getString(msgRes), fastShow, null, null);
    }


    public static void show(Context context, CharSequence msg) {
        show(context, msg, false, null, null);
    }

    public static void show(Context context, CharSequence msg, boolean fastShow) {
        show(context, msg, fastShow, null, null);
    }


    public static void show(Context context, CharSequence msg, boolean fastShow, @Nullable Integer bgRes) {
        show(context, msg, fastShow, bgRes, null);
    }

    public static void show(Context context, CharSequence msg, boolean fastShow, @Nullable Integer bgRes, @Nullable Integer textColorRes) {
        MyToast toast = new MyToast(context, msg, fastShow, bgRes != null || textColorRes != null);
        if (bgRes != null) {
            toast.setBackground(bgRes);
        }
        if (textColorRes != null) {
            toast.setTextColor(textColorRes);
        }
        toast.show();
    }

    //----------------------------------------------------------------------------------------------

    public static void showError(Context context, CharSequence msg, boolean fastShow) {
        show(context, msg, fastShow, android.R.color.holo_red_dark, android.R.color.holo_red_dark);
    }

    public static void showError(Context context, CharSequence msg) {
        showError(context, msg, false);
    }

    public static void showError(Context context, @StringRes int msgRes) {
        showError(context, context.getString(msgRes), false);
    }

    public static void showError(Context context, @StringRes int msgRes, boolean fastShow) {
        showError(context, context.getString(msgRes), fastShow);
    }

    //----------------------------------------------------------------------------------------------

    /*public static void showOS(Context context, @StringRes int msg) {
        showOS(context, msg, false);
    }

    public static void showOS(Context context, @StringRes int msg, boolean fastShow) {
        Toast.makeText(context, msg, fastShow ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void showOS(Context context, CharSequence msg) {
        showOS(context, msg, false);
    }

    public static void showOS(Context context, CharSequence msg, boolean fastShow) {
        Toast.makeText(context, msg, fastShow ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }*/

    //----------------------------------------------------------------------------------------------

    public static ToastCustom custom(Activity context, CharSequence msg) {
        return new ToastCustom(context, msg);
    }

    public static ToastCustom custom(Activity context, CharSequence msg, boolean fastShow) {
        return new ToastCustom(context, msg, fastShow);
    }

    public static ToastNative system(Context context, CharSequence msg) {
        return new ToastNative(context, msg);
    }

    public static ToastNative system(Context context, CharSequence msg, boolean fastShow) {
        return new ToastNative(context, msg, fastShow);
    }

}
 
