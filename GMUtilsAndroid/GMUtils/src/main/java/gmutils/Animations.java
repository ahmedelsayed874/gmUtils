package gmutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

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
public class Animations {
    public static Animations getInstance() {
        return new Animations();
    }

    private Animations scale(View view, float initialScaleX, float lastScaleX, float initialScaleY, float lastScaleY, int duration, int startDelay, AnimatorListenerAdapter listener) {
        view.setScaleX(initialScaleX);
        view.setScaleY(initialScaleY);

        view.animate()
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new LinearInterpolator())
                .scaleX(lastScaleX)
                .scaleY(lastScaleY)
                .setListener(listener)
                .start();

        return this;
    }

    public Animations scale(View view, float initialScale, float lastScale, int duration, int startDelay, AnimatorListenerAdapter listener) {
        scale(view, initialScale, lastScale, initialScale, lastScale, duration, startDelay, listener);

        return this;
    }

    public Animations scaleY(View view, float initialScale, float lastScale, int duration, int startDelay, AnimatorListenerAdapter listener) {
        scale(view, view.getScaleX(), view.getScaleX(), initialScale, lastScale, duration, startDelay, listener);

        return this;
    }

    public Animations scaleX(View view, float initialScale, float lastScale, int duration, int startDelay, AnimatorListenerAdapter listener) {
        scale(view, initialScale, lastScale, view.getScaleY(), view.getScaleY(), duration, startDelay, listener);

        return this;
    }

    public void scaleDown(View view) {
        scaleDown(view, 10);
    }

    public void scaleDown(View view, int percent) {
        float initScale = (100 - percent) / 100.0f;

        scale(view,
                initScale,
                1,

                initScale,
                1,

                200,
                0,

                null);

    }

    public Animations alpha(View view, float initialAlpha, float lastAlpha, int duration, int startDelay, AnimatorListenerAdapter listener) {
        view.setAlpha(initialAlpha);

        view.animate()
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new LinearInterpolator())
                .alpha(lastAlpha)
                .setListener(listener)
                .start();

        return this;
    }

    public Animations translate(View view, float translationOffset, int duration, int startDelay, AnimatorListenerAdapter listener) {
        view.setTranslationY(translationOffset);

        view.animate()
                .setDuration(duration)
                .setStartDelay(startDelay)
                .translationY(0)
                .setListener(listener)
                .start();

        return this;
    }

    public ObjectAnimator createAlphaAnimator(View target, boolean hide) {
        if (hide)
            return createAlphaAnimator(target, 1, 0);
        else
            return createAlphaAnimator(target, 0, 1);
    }

    public ObjectAnimator createAlphaAnimator(View target, float start, float end) {
        return ObjectAnimator.ofFloat(target, "alpha", start, end);
    }

    public ObjectAnimator createMoveVerticallyAnimator(View target, float startPoint, float endPoint) {
        return ObjectAnimator.ofFloat(target, "translationY", startPoint, endPoint);
    }

    //--------------------------------------------------------------------------------------------//

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void reveal(final View view, boolean fromInToOut) {
        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float radius = (float) Math.hypot(cx, cy);


        // create the animator for this view (the start radius is zero)
        Animator anim;
        if (fromInToOut)
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius / 3, radius);
        else anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);

        // make the view visible and start the animation
        if (fromInToOut) view.setVisibility(View.VISIBLE);
        else {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //view.setVisibility(View.INVISIBLE);
                }
            });
        }

        anim.start();
    }

    //--------------------------------------------------------------------------------------------//

    public TextAnimations getTextAnimations(TextView button) {
        return new TextAnimations(button);
    }

    public TextAnimations getTextAnimations(TextView button, @StringRes int specialText) {
        return new TextAnimations(button, specialText);
    }

    public static class TextAnimations {
        private TextView mBtnForAnim;
        private String mBtnText;

        private TextAnimations(TextView button) {
            mBtnForAnim = button;
            mBtnText = button.getText().toString();
        }

        private TextAnimations(TextView button, @StringRes int specialText) {
            mBtnForAnim = button;
            mBtnText = button.getContext().getString(specialText);
        }

        public void startAnimation() {
            TextView tv = mBtnForAnim;

            if (tv == null) return;
            tv.setEnabled(false);

            String text1 = mBtnText;
            String text2 = tv.getText().toString();
            int dots = (text2.length() - text1.length()) + 1;

            if (dots < 4) {
                for (int i = 0; i < dots && i < 3; i++) {
                    text1 += ".";
                }
            }

            tv.setText(text1);

            tv.animate()
                    .setDuration(800)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startAnimation();
                        }
                    })
                    .start();
        }

        public void stopAnimation(@StringRes int specialText) {
            mBtnText = mBtnForAnim.getContext().getString(specialText);
            stopAnimation();
        }
        public void stopAnimation() {
            mBtnForAnim.animate().setListener(null);
            mBtnForAnim.setText(mBtnText);
            mBtnForAnim.setEnabled(true);
            mBtnForAnim = null;
            mBtnText = null;
        }
    }

}
