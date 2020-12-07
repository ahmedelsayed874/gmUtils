package com.blogspot.gm4s1.gmutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import com.blogspot.gm4s1.gmutils.listeners.ActivityLifecycleCallbacks;
import com.blogspot.gm4s1.gmutils.listeners.SimpleWindowAttachListener;

public class UIUtils {
    public static UIUtils createInstance() {
        return new UIUtils();
    }

    private UIUtils() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean setOnFragmentDestroyedObserver(@NonNull android.app.Fragment fragment, @NonNull Runnable action) {
        try {
            final Runnable[] action2 = new Runnable[]{action};

            fragment.getView().getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
                @Override
                public void onWindowDetached() {
                    action2[0].run();
                    action2[0] = null;
                }
            });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addOnFragmentDestroyedObserver(@NonNull Fragment fragment, @NonNull Runnable action) {
        try {
            final LifecycleEventObserver[] fragmentLifecycleEventObserver = new LifecycleEventObserver[1];
            final Runnable[] action2 = new Runnable[]{action};

            LifecycleEventObserver observer = (LifecycleEventObserver) (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (fragmentLifecycleEventObserver[0] != null)
                        source.getLifecycle().removeObserver(fragmentLifecycleEventObserver[0]);

                    fragmentLifecycleEventObserver[0] = null;

                    action2[0].run();
                    action2[0] = null;
                }
            };

            fragmentLifecycleEventObserver[0] = observer;
            fragment.getLifecycle().addObserver(observer);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOnActivityDestroyed(@NonNull Activity activity, @NonNull Runnable action) {
        final ActivityLifecycleCallbacks[] callback = new ActivityLifecycleCallbacks[1];
        final Runnable[] action2 = new Runnable[]{action};

        String className = activity.getClass().getName();
        callback[0] = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (className.equals(activity.getClass().getName())) {
                    activity.getApplication().unregisterActivityLifecycleCallbacks(callback[0]);
                    callback[0] = null;

                    action2[0].run();
                    action2[0] = null;
                }
            }
        };
        activity.getApplication().registerActivityLifecycleCallbacks(callback[0]);
    }

    public int[] getScreenSize(Window window) {
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return new int[]{dm.widthPixels, dm.heightPixels};
    }


    //----------------------------------------------------------------------------------------------

    public void setProgressBarColor(ProgressBar progressBar, int color, boolean beforeLollipopOnly) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable progressDrawable = (LayerDrawable) progressBar.getProgressDrawable();
            progressDrawable.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else {
            if (!beforeLollipopOnly) {
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
            }
        }
    }

    public void setRatingBarColor(RatingBar ratingBar, int color, boolean beforeLollipopOnly) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable progressDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
            progressDrawable.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else {
            if (!beforeLollipopOnly) {
                ratingBar.setProgressTintList(ColorStateList.valueOf(color));
            }
        }
    }


    //------------------------------------------------------------------------------------------

    public void setScreenLightStatus(Window window, boolean on) {
        if (on) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public boolean isMobileHasVirtualNavigationBar() {
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return (hasHomeKey && hasBackKey);
    }

    public int getNavigationBarHeight(Context context) {
        if (!isMobileHasVirtualNavigationBar()) {
            int resourceId = context.getResources().getIdentifier(
                    "navigation_bar_height",
                    "dimen",
                    "android");

            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        }

        return 0;
    }


}
