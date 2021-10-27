package gmutilssupport.ui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmutils.Intents;
import gmutils.Logger;
import gmutils.listeners.SimpleWindowAttachListener;

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
    private ImageLoader mImageLoader;


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

        //------------------------------------------------------------------------------------------

        ImageLoader imageLoader = (url, imageView, complete) -> {
            if (mLoadingProgress != null)
                mLoadingProgress.setLoadingProgressVisibility(this, true);

            if (mImageLoader != null) {
                mImageLoader.load(url, imageView, new ImageLoaderComplete() {
                    @Override
                    public void end() {
                        if (mLoadingProgress != null)
                            mLoadingProgress.setLoadingProgressVisibility(ImageSlider.this, false);
                    }
                });
            } else {
                gmutils.images.ImageLoader.load(
                        url,
                        imageView,
                        (imgUrl, imageView1, success) -> {
                            if (mLoadingProgress != null)
                                mLoadingProgress.setLoadingProgressVisibility(ImageSlider.this, false);
                        }
                );
            }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            viewPager.getViewTreeObserver()
                    .addOnWindowAttachListener(new SimpleWindowAttachListener() {
                        @Override
                        public void onWindowAttached() {

                        }

                        @Override
                        public void onWindowDetached() {
                            dispose();
                        }
                    });
        }

        //------------------------------------------------------------------------------------------

        mDelayTime = delayTime;

        if (delayTime != null && delayTime != 0) {
            startTimerPrivate(mDelayTime);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setImageLoaderListener(@NotNull ImageLoader imageLoader) {
        mImageLoader = imageLoader;
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

    public ImageSlider setPageMargin() {
        viewPager.setPageMargin(viewPager.getPaddingLeft() / 4);
        return this;
    }

    public ImageSlider setPageMargin(@DimenRes int dimenId) {
        int margin = viewPager.getResources().getDimensionPixelOffset(dimenId);
        viewPager.setPageMargin(margin);
        return this;
    }

    public ImageSlider setImageScaleType(ImageView.ScaleType imageScaleType) {
        viewPager.setAdapter(null);

        imageAdapter.setImageScaleType(imageScaleType);
        viewPager.setAdapter(imageAdapter);
        //viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * using this will ignore other methods
     */
    public ImageSlider setImageSliderAdapter(@NotNull ImageSliderAdapter sliderAdapter) {
        imageAdapter.setImageSliderAdapter(sliderAdapter);

        viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    public ImageSlider setImagesUri(List<Uri> images) {
        imageAdapter.setImagesUri(images);

        viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    public ImageSlider setImagesUrl(List<String> images) {
        imageAdapter.setImagesUrl(images);

        viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    public ImageSlider setImagesResources(List<Integer> images) {
        imageAdapter.setImagesResources(images);

        viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    public ImageSlider addImagesUri(List<Uri> images) {
        imageAdapter.addImagesUri(images);

        viewPager.getAdapter().notifyDataSetChanged();

        return this;
    }

    public ImageSlider addImagesUrl(List<String> images) {
        imageAdapter.addImagesUrl(images);

        viewPager.getAdapter().notifyDataSetChanged();

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


    public void remove(Object item) {
        imageAdapter.remove(item);
        imageAdapter.notifyDataSetChanged();
    }

    public void remove(int position) {
        imageAdapter.remove(position);
        imageAdapter.notifyDataSetChanged();
    }

    public void remove(List<Object> items) {
        imageAdapter.remove(items);
        imageAdapter.notifyDataSetChanged();
    }

    public void removeFirst(int count) {
        imageAdapter.removeFirst(count);
        imageAdapter.notifyDataSetChanged();
    }

    public void removeLast(int count) {
        imageAdapter.removeLast(count);
        imageAdapter.notifyDataSetChanged();
    }

    public void removeRange(int firstIndex, int lastIndex) {
        imageAdapter.removeRange(firstIndex, lastIndex);
        imageAdapter.notifyDataSetChanged();
    }


    public void clear() {
        imageAdapter.clear();
        imageAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------

    public void displayImage(int position) {
        try {
            viewPager.setCurrentItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurrentDisplayedImage() {
        return viewPager.getCurrentItem();
    }

    //----------------------------------------------------------------------------------------------

    public void dispose() {
        this.viewPager.setAdapter(null);
        this.viewPager = null;

        if (imageAdapter != null) imageAdapter.dispose();
        imageAdapter = null;

        stopTimerPrivate();

        mLoadingProgress = null;
        mImageLoader = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    //----------------------------------------------------------------------------------------------

    private static class ImageAdapter extends PagerAdapter {
        private ImageSliderAdapter sliderAdapter = null; //using this will ignore {List<Object> images}
        private List<Object> images = new ArrayList<>();//uri | string | integer

//        private Set<ImageView> mImageViews = new HashSet<>();
        private View.OnClickListener imgViewAreaClickListener;
        private final boolean enableEnlargeImageOnClick;
        private View.OnTouchListener imgViewAreaTouchListener;
        private ImageView.ScaleType imageScaleType = ImageView.ScaleType.FIT_XY;
        private ImageLoader mImageLoader;
        private TimerActions mTimerActions;

        ImageAdapter(boolean enableEnlargeImageOnClick, ImageLoader imageLoader, TimerActions timerActions) {
            this.enableEnlargeImageOnClick = enableEnlargeImageOnClick;
            this.mImageLoader = imageLoader;
            this.mTimerActions = timerActions;
        }

        //------------------------------------------------------------------------------------------


        public void setImageSliderAdapter(@NotNull ImageSliderAdapter sliderAdapter) {
            this.sliderAdapter = sliderAdapter;
            if (sliderAdapter == null) throw new IllegalArgumentException("sliderAdapter = null");
        }

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


        public void remove(Object item) {
            if (item == null) return;
            images.remove(item);
        }

        public void remove(int position) {
            try {
                images.remove(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void remove(List<Object> items) {
            if (items == null || items.size() == 0) return;
            images.removeAll(items);
        }

        public void removeFirst(int count) {
            removeRange(0, count - 1);
        }

        public void removeLast(int count) {
            removeRange(images.size() - count, images.size() - 1);
        }

        public void removeRange(int firstIndex, int lastIndex) {
            int itemCount = images.size();

            if (itemCount == 0) return;
            if (firstIndex < 0) firstIndex = 0;
            if (lastIndex >= itemCount) lastIndex = itemCount - 1;
            if (firstIndex > lastIndex) {
                int t = firstIndex;
                firstIndex = lastIndex;
                lastIndex = t;
            }

            int i = firstIndex;
            while (i <= lastIndex) {
                images.remove(firstIndex);
                i++;
            }
        }


        void clear() {
            this.images.clear();
        }

        //------------------------------------------------------------------------------------------

        void setImageScaleType(ImageView.ScaleType imageScaleType) {
            this.imageScaleType = imageScaleType;
        }

        //------------------------------------------------------------------------------------------

        @Override
        public int getCount() {
            if (sliderAdapter != null)
                return sliderAdapter.getImagesCount();
            else
                return images.size();
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == obj;
        }

        @NotNull
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            Context context = container.getContext();

            ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            ImageView mImageView = new ImageView(context);
            mImageView.setLayoutParams(params);
            mImageView.setScaleType(imageScaleType);
            mImageView.setOnClickListener(imgViewAreaClickListener());
            mImageView.setOnTouchListener(imgViewAreaTouchListener());

            container.addView(mImageView, 0);

            if (sliderAdapter != null) {
                Bitmap imageBitmap = sliderAdapter.getImageBitmap(i);
                if (imageBitmap != null) {
                    mImageView.setImageBitmap(imageBitmap);
                } else {
                    setImage(mImageView, sliderAdapter.getImageSource(i));
                }

            } else if (images.size() > 0) {
                setImage(mImageView, images.get(i));
            }

            return mImageView;
        }

        /**
         * @param obj uri | string | integer
         */
        private void setImage(ImageView mImageView, Object obj) {
            try {
                if (obj instanceof Uri) {
                    if (obj.toString().indexOf("http") == 0) {
                        mImageLoader.load(obj.toString(), mImageView, null);
                    } else {
                        mImageView.setImageURI((Uri) obj);
                    }
                } else if (obj instanceof String) {
                    if (obj.toString().indexOf("http") == 0) {
                        mImageLoader.load(obj.toString(), mImageView, null);
                    } else {
                        Uri uri = Uri.parse(obj.toString());
                        mImageView.setImageURI(uri);
                    }
                } else if (obj instanceof Integer) {
                    mImageView.setImageResource((Integer) obj);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Logger.print(e);
            }
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
                int moves = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    Logger.print("ViewPager :: ACTION :: " + event.getAction());

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Logger.print("ViewPager :: ACTION-DOWN");
                        mTimerActions.pauseTimer();
                        wasDown = true;
                        downTime = System.currentTimeMillis();
                        moves = 0;

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (++moves > 5) downTime = 0;

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                        Logger.print("ViewPager :: ACTION-UP");
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
            this.sliderAdapter = null;

            this.images.clear();
            this.images = null;

//            if (mImageView != null) {
//                if (mImageView.getParent() != null) {
//                    if (mImageView.getParent() instanceof ViewGroup) {
//                        ((ViewGroup) mImageView.getParent()).removeAllViews();
//                    }
//                }
//            }
//            this.mImageView = null;

            this.imgViewAreaClickListener = null;
            this.imgViewAreaTouchListener = null;

            this.mImageLoader = null;
            this.mTimerActions = null;
        }
    }

    //----------------------------------------------------------------------------------------------

    public interface LoadingProgress {
        void setLoadingProgressVisibility(ImageSlider imageSlider, boolean show);
    }

    public interface ImageSliderAdapter {
        int getImagesCount();

        @Nullable
        Bitmap getImageBitmap(int index);

        /**
         * @return uri | string | integer
         */
        @Nullable
        Object getImageSource(int index);
    }

    public interface ImageLoader {
        void load(String url, ImageView imageView, ImageLoaderComplete complete);
    }

    private ImageLoaderComplete unusedCallback; // it required to tell compiler to let it and don't remove
    public interface ImageLoaderComplete {
        void end();
    }

    private interface TimerActions {
        void pauseTimer();

        void resumeTimer();
    }
}
