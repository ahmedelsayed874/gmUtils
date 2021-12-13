package gmutils.ui.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

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

        boolean isFast();

        IToast setFast(boolean fast);

        IToast setBackground(int bgRes);

        IToast setTextColor(int textColorRes);
        
        IToast setMessage(int msgRes);

        IToast setMessage(CharSequence msg);
        
        IToast show();
    }

    //----------------------------------------------------------------------------------------------

    public static Boolean DEFAULT_STYLE = true;

    public static Integer BACKGROUND_RES = R.color.gmPrimary; //android.R.color.black;
    public static Integer TEXT_COLOR_RES = Color.WHITE;

    public static Integer ERROR_BACKGROUND_RES = android.R.color.holo_red_dark;
    public static Integer ERROR_TEXT_COLOR_RES = Color.WHITE;

    //----------------------------------------------------------------------------------------------

    public final MyToast.IToast toast;

    public MyToast(Context context, int msg) {
        this(context, msg, true);
    }

    public MyToast(Activity activity, CharSequence msg) {
        this(activity, msg, true);
    }

    public MyToast(Activity activity, int msg, boolean systemStyle) {
        this((Context) activity, msg, systemStyle);
    }

    public MyToast(Context context, int msg, boolean systemStyle) {
        this(context, context.getString(msg), systemStyle);
    }

    public MyToast(Activity activity, CharSequence msg, boolean systemStyle) {
        this((Context) activity, msg, systemStyle);
    }

    public MyToast(Context context, CharSequence msg, boolean systemStyle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            toast = new ToastNative(context, msg, systemStyle);

        } else {
            if (context instanceof Activity)
                toast = new ToastCustom((Activity) context, msg, systemStyle);
            else
                toast = new ToastNative(context, msg, systemStyle);
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
        toast.show();
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public static void show(Context context, int msgRes) {
        show(context, context.getString(msgRes), null, null);
    }

    public static void show(Context context, CharSequence msg) {
        show(context, msg, null, null);
    }

    public static void show(Context context, CharSequence msg, @Nullable Integer bgRes) {
        show(context, msg, bgRes, null);
    }

    public static void show(Context context, CharSequence msg, @Nullable Integer bgRes, @Nullable Integer textColorRes) {
        MyToast toast = new MyToast(context, msg, DEFAULT_STYLE);
        if (bgRes != null) {
            toast.setBackground(bgRes);
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
 
