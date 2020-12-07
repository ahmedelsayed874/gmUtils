package com.blogspot.gm4s1.gmutils.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

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


}
