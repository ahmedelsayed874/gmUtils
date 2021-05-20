package gmutils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.AnimRes;
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
public class Activities {
    private static final int DEFAULT_TRANSITION_RES = android.R.anim.slide_in_left;


    public static void start(Class<?> activity, Context context) {
        start(activity, context, null, DEFAULT_TRANSITION_RES);
    }

    public static void start(Class<?> activity, Context context, Bundle extraData) {
        start(activity, context, extraData, DEFAULT_TRANSITION_RES, 0, false, false);
    }

    public static void start(Class<?> activity, Context context, Bundle extraData, @AnimRes int enterResId) {
        start(activity, context, extraData, enterResId, 0, false, false);
    }

    public static void start(Class<?> activity, Context context, Bundle extraData, @AnimRes int enterResId, @AnimRes int exitResId) {
        start(activity, context, extraData, enterResId, exitResId, false, false);
    }

    public static void start(Class<?> activityClass, Context context, Bundle extraData, @AnimRes int enterResId, @AnimRes int exitResId, boolean bringToTop, boolean startNewTask) {
        Intent intent = createIntent(activityClass, context, extraData, bringToTop, startNewTask);
        try {
            Bundle bundle = createAnimationBundle(
                    context,
                    enterResId,
                    exitResId);

            context.startActivity(intent, bundle);

        } catch (Exception e) {
            context.startActivity(intent);
        }
    }

    //----------------------------------------------------------------------------------------------


    public static Intent createIntent(Class<?> activityClass, Context context, Bundle extraData) {
        return createIntent(activityClass, context, extraData, false, false);
    }

    public static Intent createIntent(Class<?> activityClass, Context context, Bundle extraData, boolean singleInstance) {
        return createIntent(activityClass, context, extraData, singleInstance, false);
    }

    public static Intent createIntent(Class<?> activityClass, Context context, Bundle extraData, boolean bringToTop, boolean startNewTask) {
        Intent intent = new Intent(context, activityClass);

        if (extraData != null) {
            intent.putExtras(extraData);
        }

        if (bringToTop) {
            if (startNewTask) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else {
            if (startNewTask) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }

        return intent;
    }

    public static Bundle createAnimationBundle(Context context, @AnimRes int enterResId, @AnimRes int exitResId) {
        Bundle bundle = null;
        try {
            bundle = ActivityOptions.makeCustomAnimation(
                    context,
                    enterResId,
                    exitResId).toBundle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }


    //----------------------------------------------------------------------------------------------

    public static void bringToTop(Class<?> activity, Context context) {
        start(activity, context, null, 0, 0, true, false);
    }

    public static void bringToTop(Class<?> activity, Context context, Bundle extraData) {
        start(activity, context, extraData, 0, 0, true, false);
    }


    public static void startNewTask(Class<?> activity, Context context, Bundle extraData) {
        start(activity, context, extraData, DEFAULT_TRANSITION_RES, 0, false, true);
    }

    public static void startNewTask(Class<?> activity, Context context, Bundle extraData, boolean overrideIfExist) {
        start(activity, context, extraData, DEFAULT_TRANSITION_RES, 0, overrideIfExist, true);
    }

    //----------------------------------------------------------------------------------------------


    public static void startForResult(Class<?> activity, Activity context, Bundle extraData, int requestCode) {
        Intent intent = createIntent(activity, context, extraData);
        Bundle bundle = createAnimationBundle(
                context,
                DEFAULT_TRANSITION_RES,
                0);
        context.startActivityForResult(intent, requestCode, bundle);
    }

    public static void startForResult(Class<?> activity, Fragment fragment, Bundle extraData, int requestCode) {
        Intent intent = createIntent(activity, fragment.getContext(), extraData);
        Bundle bundle = createAnimationBundle(
                fragment.getContext(),
                DEFAULT_TRANSITION_RES,
                0);
        fragment.startActivityForResult(intent, requestCode, bundle);
    }

    //----------------------------------------------------------------------------------------------

    public static void startActivityWithFadeTrans(Activity activity, Class<?> activityToStart, boolean finishCurrent) {
        startActivityWithFadeTrans(activity, new Intent(activity, activityToStart), finishCurrent);
    }

    public static void startActivityWithFadeTrans(Activity activity, Intent intent, boolean finishCurrent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finishCurrent) activity.finish();
    }

    public static void startActivityWithSlideTrans(Activity activity, Class<?> activityToStart, boolean finishCurrent) {
        startActivityWithSlideTrans(activity, new Intent(activity, activityToStart), finishCurrent);
    }

    public static void startActivityWithSlideTrans(Activity activity, Intent intent, boolean finishCurrent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (finishCurrent) activity.finish();
    }

}
