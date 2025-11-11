package gmutilssupport.ui.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import gmutils.listeners.SimpleWindowAttachListener;
import gmutilsSupport.R;

public class ToastNative implements MyToast.IToast {
    private Toast toast;
    private View root;
    private TextView tv;
    private boolean isFast;

    public ToastNative(Context context, int msg, boolean systemStyle) {
        this(context, context.getString(msg), systemStyle);
    }

    @SuppressLint("ShowToast")
    public ToastNative(Context context, CharSequence msg, boolean systemStyle) {
        toast = Toast.makeText(context, msg, isFast ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);

        try {
            root = toast.getView();
            View view = ((ViewGroup) root).getChildAt(0);

            tv = ((TextView) view);
            tv.setGravity(Gravity.CENTER);

            if (!systemStyle) {
                setBackground(MyToast.BACKGROUND_RES);
                setTextColor(MyToast.TEXT_COLOR_RES);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                root.getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
                    @Override
                    public void onWindowAttached() {

                    }

                    @Override
                    public void onWindowDetached() {
                        ToastNative.this.toast = null;
                        ToastNative.this.root = null;
                        ToastNative.this.tv = null;
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean isFast() {
        return isFast;
    }

    @Override
    public MyToast.IToast setFast(boolean fast) {
        this.isFast = fast;
        return this;
    }

    @Override
    public MyToast.IToast setBackground(int bgRes) {
        try {
            root.setBackgroundResource(bgRes);

            int plr = root.getContext().getResources().getDimensionPixelOffset(gmutils.R.dimen.size_10);
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
        try {
            tv.setText(msgRes);
        } catch (Exception ignored) {}

        return this;
    }

    @Override
    public MyToast.IToast setMessage(CharSequence msg) {
        try {
            tv.setText(msg);
        } catch (Exception ignored) {}

        return this;
    }

    @Override
    public MyToast.IToast show() {
        try {
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

}
