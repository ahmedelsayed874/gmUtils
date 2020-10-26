package com.blogspot.gm4s1.gmutils.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.gm4s1.gmutils.AppLog;
import com.blogspot.gm4s1.gmutils.ImageLoader;
import com.blogspot.gm4s1.gmutils.ImageUtils;
import com.blogspot.gm4s1.gmutils.Intents;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ImageSlider {
    private abstract static class TimerTask2 extends TimerTask {
        ImageSlider imageSlider;

        TimerTask2(ImageSlider imageSlider) {
            this.imageSlider = imageSlider;
        }

        void dispose() {
            imageSlider = null;
        }
    }

    private abstract static class Runnable2 implements Runnable {
        ImageSlider imageSlider;

        Runnable2(ImageSlider imageSlider) {
            this.imageSlider = imageSlider;
        }

        void dispose() {
            imageSlider = null;
        }
    }

    private static final int DEFAULT_DELAY_TIME = 4000;

    private ViewPager viewPager;
    private ImageAdapter imageAdapter;
    private Timer mTimer;
    private Integer mDelayTime;
    private TimerTask2 mTimerTask;
    private Runnable2 mRunnable;
    private boolean timerStoppedByUser = false;
    private LoadingProgress mLoadingProgress;


    public ImageSlider(ViewPager viewPager) {
        this(viewPager, DEFAULT_DELAY_TIME, false);
    }

    public ImageSlider(ViewPager viewPager, Integer delayTime) {
        this(viewPager, delayTime, false);
    }

    public ImageSlider(ViewPager viewPager, boolean enableEnlargeImageOnClick) {
        this(viewPager, DEFAULT_DELAY_TIME, enableEnlargeImageOnClick);
    }

    public ImageSlider(ViewPager viewPager, Integer delayTime, boolean enableEnlargeImageOnClick) {
        this.viewPager = viewPager;

        viewPager.setClipToPadding(false);
        //viewPager.setPageMargin(viewPager.getPaddingLeft() / 4);

        //------------------------------------------------------------------------------------------

        //ImageLoader imageLoader = com.blogspot.gm4s1.gmutils.ImageLoader::load;
        ImageLoader imageLoader = (url, imageView) -> {
            if (mLoadingProgress != null)
                mLoadingProgress.setLoadingProgressVisibility(true);

            com.blogspot.gm4s1.gmutils.ImageLoader.load(
                    url,
                    imageView,
                    new com.blogspot.gm4s1.gmutils.ImageLoader.Callback() {
                        @Override
                        public void onSuccess(String s) {
                            if (mLoadingProgress != null)
                                mLoadingProgress.setLoadingProgressVisibility(false);
                        }

                        @Override
                        public void onError(String s) {
                            if (mLoadingProgress != null)
                                mLoadingProgress.setLoadingProgressVisibility(false);
                        }
                    }
            );
        };
        TimerActions timerActions = new TimerActions() {
            @Override
            public void pauseTimer() {
                ImageSlider.this.stopTimerPrivate();
            }

            @Override
            public void resumeTimer() {
                ImageSlider.this.startTimerPrivate(mDelayTime);
            }
        };
        imageAdapter = new ImageAdapter(enableEnlargeImageOnClick, imageLoader, timerActions);
        viewPager.setAdapter(imageAdapter);

        //------------------------------------------------------------------------------------------

        viewPager.getViewTreeObserver()
                .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                    @Override
                    public void onWindowAttached() {

                    }

                    @Override
                    public void onWindowDetached() {
                        dispose();
                    }
                });

        //------------------------------------------------------------------------------------------

        mDelayTime = delayTime;

        if (delayTime != null && delayTime != 0) {
            startTimerPrivate(mDelayTime);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setImageLoaderListener(@NonNull ImageLoader imageLoader) {
        imageAdapter.mImageLoader = imageLoader;
    }

    public void setLoadingProgressListener(LoadingProgress loadingProgress) {
        this.mLoadingProgress = loadingProgress;
    }

    //----------------------------------------------------------------------------------------------

    public void startTimer() {
        startTimer(mDelayTime);
    }

    public void startTimer(int delayTime) {
        timerStoppedByUser = false;
        startTimerPrivate(delayTime);
    }

    private void startTimerPrivate(Integer delayTime) {
        stopTimerPrivate();
        if (timerStoppedByUser) return;

        mDelayTime = delayTime;
        if (delayTime == null) return;

        mRunnable = new Runnable2(this) {
            @Override
            public void run() {
                int p = imageSlider.viewPager.getCurrentItem() + 1;
                if (p >= imageSlider.imageAdapter.getCount()) {
                    //p = 0;
                    viewPager.setAdapter(imageAdapter);
                    return;
                }

                imageSlider.viewPager.setCurrentItem(p);
            }
        };

        mTimerTask = new TimerTask2(this) {
            @Override
            public void run() {
                int count = imageSlider.imageAdapter.getCount();
                if (count < 2) return;
                imageSlider.viewPager.post(imageSlider.mRunnable);
            }
        };

        mTimer = new Timer();

        mTimer.scheduleAtFixedRate(mTimerTask, mDelayTime, mDelayTime);
    }

    public void stopTimer() {
        timerStoppedByUser = true;
        stopTimerPrivate();
    }

    private void stopTimerPrivate() {
        if (mTimer != null) {
            try {
                mTimer.cancel();
            } catch (Exception e) {
            }
            try {
                mTimer.purge();
            } catch (Exception e) {
            }
        }
        mTimer = null;

        if (mTimerTask != null) mTimerTask.dispose();
        mTimerTask = null;

        if (mRunnable != null) mRunnable.dispose();
        mRunnable = null;
    }

    //----------------------------------------------------------------------------------------------

    public ImageSlider setImageScaleType(ImageView.ScaleType imageScaleType) {
        imageAdapter.setImageScaleType(imageScaleType);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public ImageSlider setImagesUri(List<Uri> images) {
        imageAdapter.setImagesUri(images);

        viewPager.setAdapter(null);
        viewPager.setAdapter(imageAdapter);

        return this;
    }

    public ImageSlider setImagesUrl(List<String> images) {
        imageAdapter.setImagesUrl(images);

        viewPager.setAdapter(null);
        viewPager.setAdapter(imageAdapter);

        return this;
    }

    public ImageSlider setImagesResources(List<Integer> images) {
        imageAdapter.setImagesResources(images);

        viewPager.setAdapter(null);
        viewPager.setAdapter(imageAdapter);

        return this;
    }

    public ImageSlider addImagesUri(List<Uri> images) {
        imageAdapter.addImagesUri(images);

        viewPager.setAdapter(null);
        viewPager.setAdapter(imageAdapter);

        return this;
    }

    public ImageSlider addImagesUrl(List<String> images) {
        imageAdapter.addImagesUrl(images);

        viewPager.setAdapter(null);
        viewPager.setAdapter(imageAdapter);

        return this;
    }

    /**
     * @param image string or uri or resource id
     */
    public ImageSlider addImage(Object image) {
        imageAdapter.addImage(image);
        imageAdapter.notifyDataSetChanged();

        return this;
    }

    public ImageSlider clear() {
        imageAdapter.clear();
        imageAdapter.notifyDataSetChanged();

        return this;
    }

    //----------------------------------------------------------------------------------------------

    public void displayImage(int position) {
        try {
            viewPager.setCurrentItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------------------

    public Bitmap getCurrentImage() {
        if (viewPager.getAdapter() != null) {
            ImageView imageView = imageAdapter.getImageView();

            if (imageView != null) {
                return ImageUtils.createInstance().getBitmap(imageView);
            }
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------

    public void dispose() {
        this.viewPager.setAdapter(null);
        this.viewPager = null;

        if (imageAdapter != null) imageAdapter.dispose();
        imageAdapter = null;

        stopTimerPrivate();

        mLoadingProgress = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    //----------------------------------------------------------------------------------------------

    private static class ImageAdapter extends PagerAdapter {
        private List<Object> images = new ArrayList<>();//uri | string | integer
        private ImageView mImageView;
        private View.OnClickListener imgViewAreaClickListener;
        private final boolean enableEnlargeImageOnClick;
        private View.OnTouchListener imgViewAreaTouchListener;
        private ImageView.ScaleType imageScaleType = ImageView.ScaleType.FIT_XY;
        private ImageSlider.ImageLoader mImageLoader;
        private TimerActions mTimerActions;

        ImageAdapter(boolean enableEnlargeImageOnClick, ImageSlider.ImageLoader imageLoader, TimerActions timerActions) {
            this.enableEnlargeImageOnClick = enableEnlargeImageOnClick;
            this.mImageLoader = imageLoader;
            this.mTimerActions = timerActions;
        }

        //------------------------------------------------------------------------------------------

        void setImagesUrl(List<String> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            } else this.images.clear();
        }

        void setImagesUri(List<Uri> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            } else this.images.clear();
        }

        void setImagesResources(List<Integer> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            } else this.images.clear();
        }

        void addImagesUrl(List<String> images) {
            if (images == null) return;

            this.images.addAll(images);
        }

        void addImagesUri(List<Uri> images) {
            if (images == null) return;

            this.images.addAll(images);
        }

        void addImageResources(List<Integer> images) {
            if (images == null) return;

            this.images.addAll(images);
        }

        /**
         * @param image string or uri or resource id
         */
        void addImage(Object image) {
            if (image == null) return;

            this.images.add(image);
        }

        void clear() {
            this.images.clear();
        }

        //------------------------------------------------------------------------------------------

        void setImageScaleType(ImageView.ScaleType imageScaleType) {
            this.imageScaleType = imageScaleType;
            if (mImageView != null) {
                mImageView.setScaleType(imageScaleType);
            }
        }


        //------------------------------------------------------------------------------------------

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == ((ImageView) obj);
        }

        @NonNull
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            Context context = container.getContext();

            ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            mImageView = new ImageView(context);
            mImageView.setLayoutParams(params);
            mImageView.setScaleType(imageScaleType);
            mImageView.setOnClickListener(imgViewAreaClickListener());
            mImageView.setOnTouchListener(imgViewAreaTouchListener());

            try {
                Object obj = images.get(i);
                if (obj != null) {
                    if (obj instanceof Uri) {
                        if (obj.toString().indexOf("http") == 0) {
                            mImageLoader.load(obj.toString(), mImageView);
                        } else {
                            mImageView.setImageURI((Uri) obj);
                        }
                    } else if (obj instanceof String) {
                        if (obj.toString().indexOf("http") == 0) {
                            mImageLoader.load(obj.toString(), mImageView);
                        } else {
                            Uri uri = Uri.parse(obj.toString());
                            mImageView.setImageURI(uri);
                        }
                    } else if (obj instanceof Integer) {
                        mImageView.setImageResource((Integer) obj);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                AppLog.print(e);
            }

            container.addView(mImageView, 0);

            return mImageView;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        private View.OnClickListener imgViewAreaClickListener() {
//            if (imgViewAreaClickListener == null) {
//                imgViewAreaClickListener = v -> {
//                    if (!enableEnlargeImageOnClick) return;
//                    if (v instanceof ImageView) {
//                        Intents.getInstance().getImageIntents().showImage(((ImageView) v));
//                    }
//                };
//            }

            return imgViewAreaClickListener;
        }

        private View.OnTouchListener imgViewAreaTouchListener() {
            imgViewAreaTouchListener = new View.OnTouchListener() {
                boolean wasDown;
                long downTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    AppLog.print("ViewPager :: ACTION :: " + event.getAction());

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        AppLog.print("ViewPager :: ACTION-DOWN");
                        mTimerActions.pauseTimer();
                        wasDown = true;
                        downTime = System.currentTimeMillis();

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        downTime = 0;

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        AppLog.print("ViewPager :: ACTION-UP");
                        v.postDelayed(() -> {
                            if (wasDown) mTimerActions.resumeTimer();
                            wasDown = false;
                        }, 700);

                        if (System.currentTimeMillis() - downTime < 900) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (enableEnlargeImageOnClick) {
                                    if (v instanceof ImageView) {
                                        Intents.getInstance().getImageIntents().showImage(((ImageView) v));
                                    }
                                }
                            }
                        }
                    }

                    return false;
                }
            };
            return imgViewAreaTouchListener;
        }

        @Override
        public void destroyItem(ViewGroup container, int i, Object obj) {
            container.removeView((ImageView) obj);
        }

        void dispose() {
            this.images.clear();
            this.images = null;

            if (mImageView != null) {
                if (mImageView.getParent() != null) {
                    if (mImageView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) mImageView.getParent()).removeAllViews();
                    }
                }
            }
            this.mImageView = null;

            this.imgViewAreaClickListener = null;
            this.imgViewAreaTouchListener = null;

            this.mImageLoader = null;
            this.mTimerActions = null;
        }
    }

    //----------------------------------------------------------------------------------------------

    public interface LoadingProgress {
        void setLoadingProgressVisibility(boolean show);
    }

    public interface ImageLoader {
        void load(String url, ImageView imageView);
    }

    private interface TimerActions {
        void pauseTimer();

        void resumeTimer();
    }
}
