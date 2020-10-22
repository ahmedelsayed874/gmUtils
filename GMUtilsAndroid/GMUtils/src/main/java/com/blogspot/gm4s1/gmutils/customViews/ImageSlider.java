package com.blogspot.gm4s1.gmutils.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    private static final int DEFAULT_DELAY_TIME = 3000;

    private ViewPager viewPager;
    private ImageAdapter imageAdapter;
    private Timer mTimer;
    private TimerTask2 mTimerTask;
    private Runnable2 mRunnable;

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
        //viewPager.setPadding(150, 0, 150, 0);
        viewPager.setPageMargin(viewPager.getPaddingLeft() / 4);

        viewPager.setAdapter(imageAdapter = new ImageAdapter(enableEnlargeImageOnClick));

        viewPager.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
            @Override
            public void onWindowAttached() {

            }

            @Override
            public void onWindowDetached() {
                dispose();
            }
        });

        if (delayTime != null) setTimer(delayTime);

    }

    //----------------------------------------------------------------------------------------------

    private void setTimer(int delayTime) {
        if (mRunnable != null) mRunnable.dispose();
        mRunnable = new Runnable2(this) {
            @Override
            public void run() {
                int p = imageSlider.viewPager.getCurrentItem() + 1;
                if (p >= imageSlider.imageAdapter.getCount()) p = 0;

                imageSlider.viewPager.setCurrentItem(p);
            }
        };

        if (mTimerTask != null) mTimerTask = null;
        mTimerTask = new TimerTask2(this) {
            @Override
            public void run() {
                int count = imageSlider.imageAdapter.getCount();
                if (count < 2) return;
                imageSlider.viewPager.post(imageSlider.mRunnable);
            }
        };

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, delayTime, delayTime);
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

        if (imageAdapter != null)
            imageAdapter.dispose();
        imageAdapter = null;

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
        private ImageView.ScaleType imageScaleType = ImageView.ScaleType.FIT_XY;

        ImageAdapter(boolean enableEnlargeImageOnClick) {
            this.enableEnlargeImageOnClick = enableEnlargeImageOnClick;
        }

        //------------------------------------------------------------------------------------------

        void setImagesUrl(List<String> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            }
            else this.images.clear();
        }

        void setImagesUri(List<Uri> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            }
            else this.images.clear();
        }

        void setImagesResources(List<Integer> images) {
            if (images != null) {
                this.images.clear();
                this.images.addAll(images);
            }
            else this.images.clear();
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
         *
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

            try {
                Object obj = images.get(i);
                if (obj != null) {
                    if (obj instanceof Uri) {
                        if (obj.toString().indexOf("http") == 0) {
                            ImageLoader.load(obj.toString(), mImageView);
                        } else {
                            mImageView.setImageURI((Uri) obj);
                        }
                    } else if (obj instanceof String) {
                        if (obj.toString().indexOf("http") == 0) {
                            ImageLoader.load(obj.toString(), mImageView);
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
            if (imgViewAreaClickListener == null) {
                imgViewAreaClickListener = v -> {
                    if (!enableEnlargeImageOnClick) return;
                    if (v instanceof ImageView) {
                        Intents.getInstance().getImageIntents().showImage(((ImageView) v));
                    }
                };
            }

            return imgViewAreaClickListener;
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

        }
    }
}
