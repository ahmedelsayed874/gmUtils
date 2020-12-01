package com.blogspot.gm4s1.gmutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.blogspot.gm4s1.gmutils._bases.BaseApplication;
import com.blogspot.gm4s1.gmutils.utils.ImageUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


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

/**
 * this class depends on:
 * 'com.squareup.picasso:picasso:2.5.2'
 */
public class ImageLoader {
    @SuppressLint("StaticFieldLeak")
    private static volatile Picasso INSTANCE;

    public static Integer MIN_IMAGE_SIZE; //will initialize later, it's important to reduce image size
    public static int DEFAULT_LOADING_PLACEHOLDER = android.R.drawable.stat_sys_download;
    public static int DEFAULT_ERROR_PLACEHOLDER = android.R.drawable.stat_notify_error;
    public static long CACHE_SIZE_IN_BYTES = 50 /*mega*/ * 1024 /*kb*/ * 1024 /*byte*/;

    public static void CACHE_SIZE_IN_BYTES(int mega) {
        CACHE_SIZE_IN_BYTES = mega * 1024 /*kb*/ * 1024 /*byte*/;
    }


    public static void load(String url, ImageView imageView) {
        load(url, imageView, null, null);
    }

    public static void load(String url, ImageView imageView, Callback callback) {
        load(url, imageView, null, callback);
    }

    public static void load(String url, ImageView imageView, Options options) {
        load(url, imageView, options, null);
    }

    public static void load(String url, ImageView imageView, Options options, Callback callback) {
        if (imageView == null) return;

        if (MIN_IMAGE_SIZE == null) {
            DisplayMetrics displayMetrics = imageView.getResources().getDisplayMetrics();
            //MIN_IMAGE_SIZE = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
            MIN_IMAGE_SIZE = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
            MIN_IMAGE_SIZE = (int) (MIN_IMAGE_SIZE * 0.8f);
        }

        if (options == null) {
            options = new Options(imageView);
        }

        if (TextUtils.isEmpty(url)) {
            if (options.errorPlaceHolderRes != 0) {
                imageView.setImageResource(options.errorPlaceHolderRes);
                try {
                    imageView.setScaleType(options.errorScaleType);
                } catch (Exception e) {
                }
            }

            if (callback != null) {
                callback.onComplete(url, imageView, false);
            }

            return;
        }

        url = url.replace(" ", "%20");

        BaseApplication application = BaseApplication.current();
        Picasso picasso;
        if (application == null) {
            picasso = INSTANCE;
        } else {
            picasso = (Picasso) application.getGlobalInstance(Picasso.class.getCanonicalName());
        }
        if (picasso == null) {
            synchronized (ImageLoader.class) {
                picasso = createPicassoInstance(imageView.getContext());
                if (application == null) {
                    INSTANCE = picasso;
                } else {
                    application.addGlobalInstance(Picasso.class.getCanonicalName(), picasso);
                }
            }
        }

        RequestCreator request = picasso.load(url);

        if (options.minWidth != null && options.minWidth > 0) {
            if (options.minHeight != null && options.minHeight > 0) {
                request.resize(options.minWidth, options.minHeight);
                request.onlyScaleDown();
            }
        }

        if (options.loadingPlaceHolderRes != 0) {
            request.placeholder(options.loadingPlaceHolderRes);
        } else {
            request.noPlaceholder();
        }

        if (options.errorPlaceHolderRes != 0) {
            request.error(options.errorPlaceHolderRes);
        }

        request.into(
                imageView,
                createPicassoCallback(url, imageView, options, callback));
    }

    public static void load(Context context, String url, Callback2 callback) {
        ImageView imageView = new ImageView(context);

        Options options = new Options()
                .setMinSize((Integer) null)
                .setLoadingPlaceHolderRes(0)
                .setErrorPlaceHolderRes(0);

        Callback innerCallback = (imgUrl, imageView1, success) -> {
            if (success) {
                Bitmap image = ImageUtils.createInstance().getBitmap(imageView1);
                callback.onComplete(imgUrl, image, true);
            } else {
                callback.onComplete(imgUrl, null, false);
            }
        };

        load(url, imageView, options, innerCallback);
    }

    //----------------------------------------------------------------------------------------------

    private static Picasso createPicassoInstance(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier((s, sslSession) -> true);

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        File cacheDir = new File(context.getCacheDir(), "images-cache");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                cacheDir = context.getCacheDir();
            }
        }
        Cache cache = new Cache(cacheDir, CACHE_SIZE_IN_BYTES);
        client.setCache(cache);
        client.setConnectTimeout(15000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(20000, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(20000, TimeUnit.MILLISECONDS);

        Picasso instance = new Picasso.Builder(context.getApplicationContext())
                .downloader(new OkHttpDownloader(client))
                .listener((picasso, uri, exception) -> Log.e("PICASSO", exception.getMessage()))
                .build();

        return instance;
    }

    private static com.squareup.picasso.Callback createPicassoCallback(
            String imgUrl, ImageView imageView,
            Options options, Callback otherCallback) {

        class PicassoCallback implements com.squareup.picasso.Callback {
            String imgUrl;
            ImageView imageView;
            Options options;
            Callback outerCallback;

            public PicassoCallback(String imgUrl, ImageView imageView, Options options, Callback outerCallback) {
                this.imgUrl = imgUrl;
                this.imageView = imageView;
                this.options = options;
                this.outerCallback = outerCallback;

                try {
                    imageView.setScaleType(options.loadingScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Logger.print(e.getMessage());
                }
            }

            @Override
            public void onSuccess() {
                try {
                    imageView.setScaleType(options.successScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Logger.print(e.getMessage());
                }

                if (outerCallback != null) {
                    outerCallback.onComplete(imgUrl, imageView, true);
                }

                PicassoCallback.this.imgUrl = null;
                PicassoCallback.this.imageView = null;
                PicassoCallback.this.options = null;
                PicassoCallback.this.outerCallback = null;
            }

            @Override
            public void onError() {
                try {
                    imageView.setScaleType(options.errorScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Logger.print(e.getMessage());
                }

                if (outerCallback != null) {
                    outerCallback.onComplete(imgUrl, imageView, false);
                }

                PicassoCallback.this.imgUrl = null;
                PicassoCallback.this.imageView = null;
                PicassoCallback.this.options = null;
                PicassoCallback.this.outerCallback = null;
            }
        }

        return new PicassoCallback(imgUrl, imageView, options, otherCallback);

    }

    //----------------------------------------------------------------------------------------------

    public static class Options {
        private Integer minWidth;
        private Integer minHeight;
        private int loadingPlaceHolderRes;
        private int errorPlaceHolderRes;
        private ImageView.ScaleType loadingScaleType;
        private ImageView.ScaleType successScaleType;
        private ImageView.ScaleType errorScaleType;


        public Options() {
            this.setMinSize(MIN_IMAGE_SIZE);

            this.loadingPlaceHolderRes = DEFAULT_LOADING_PLACEHOLDER;
            this.errorPlaceHolderRes = DEFAULT_ERROR_PLACEHOLDER;

            this.loadingScaleType = ImageView.ScaleType.CENTER;
            this.successScaleType = ImageView.ScaleType.FIT_XY;
            this.errorScaleType = ImageView.ScaleType.CENTER;
        }

        public Options(@Nullable ImageView imageView) {
            this(imageView, true);
        }

        public Options(@Nullable ImageView imageView, boolean willImageFitViewPort) {
            this();

            if (imageView != null) {
                this.successScaleType = imageView.getScaleType();
                //this.errorScaleType = this.successScaleType;

                if (willImageFitViewPort)
                    setMinSize(imageView);
            }
        }

        public Options(@Nullable ImageView imageView, @DrawableRes int placeHolderRes) {
            this(imageView);

            this.loadingPlaceHolderRes = placeHolderRes;
            this.errorPlaceHolderRes = placeHolderRes;
        }


        public Options setMinSize(Integer minSize) {
            if (minSize != null) {
                return setMinSize(minSize, minSize);
            } else {
                this.minWidth = null;
                this.minHeight = null;
                return this;
            }
        }

        public Options setMinSize(int minWidth, int minHeight) {
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            return this;
        }

        public Options setMinSize(ImageView imageView) {
            int w = imageView.getWidth();
            int h = imageView.getHeight();
            if (w > 5 && h > 5) {
                setMinSize(w, h);
            }
            return this;
        }

        public Options setLoadingPlaceHolderRes(@DrawableRes int loadingPlaceHolderRes) {
            this.loadingPlaceHolderRes = loadingPlaceHolderRes;
            return this;
        }

        public Options setErrorPlaceHolderRes(@DrawableRes int errorPlaceHolderRes) {
            this.errorPlaceHolderRes = errorPlaceHolderRes;
            return this;
        }

        public Options setLoadingScaleType(ImageView.ScaleType loadingScaleType) {
            this.loadingScaleType = loadingScaleType;
            return this;
        }

        public Options setSuccessScaleType(ImageView.ScaleType successScaleType) {
            this.successScaleType = successScaleType;
            return this;
        }

        public Options setErrorScaleType(ImageView.ScaleType errorScaleType) {
            this.errorScaleType = errorScaleType;
            return this;
        }

        public int getLoadingPlaceHolderRes() {
            return loadingPlaceHolderRes;
        }

        public int getErrorPlaceHolderRes() {
            return errorPlaceHolderRes;
        }

        public ImageView.ScaleType getLoadingScaleType() {
            return loadingScaleType;
        }

        public ImageView.ScaleType getSuccessScaleType() {
            return successScaleType;
        }

        public ImageView.ScaleType getErrorScaleType() {
            return errorScaleType;
        }
    }

    public interface Callback {
        void onComplete(String imgUrl, ImageView imageView, boolean success);
    }

    public interface Callback2 {
        void onComplete(String imgUrl, Bitmap image, boolean success);
    }

}