package com.blogspot.gm4s1.gmutils;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
public class ImageLoader {
    private static Integer sMinSize;
    public static int DEFAULT_LOADING_PLACEHOLDER = android.R.drawable.stat_sys_download;
    public static int DEFAULT_ERROR_PLACEHOLDER = android.R.drawable.stat_notify_error;


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

        if (sMinSize == null) {
            DisplayMetrics displayMetrics = imageView.getResources().getDisplayMetrics();
            sMinSize = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
            sMinSize = (int) (sMinSize * 0.8f);
        }

        if (options == null) options = new Options(imageView);

        if (TextUtils.isEmpty(url)) {
            if (options.errorPlaceHolderRes != 0) {
                imageView.setImageResource(options.errorPlaceHolderRes);
                try {
                    imageView.setScaleType(options.errorScaleType);
                } catch (Exception e) {}
            }

            if (callback != null) {
                callback.onError(url);
            }

            return;
        }

        url = url.replace(" ", "%20");

        Picasso picasso = createPicassoInstance(imageView.getContext());

        RequestCreator request = picasso
                .load(url)
                .resize(options.minSize, options.minSize)
                .onlyScaleDown();

        if (options.loadingPlaceHolderRes != 0) {
            request.placeholder(options.loadingPlaceHolderRes);
        }

        if (options.errorPlaceHolderRes != 0) {
            request.error(options.errorPlaceHolderRes);
        }

        request.into(
                imageView,
                createTaskCallback(url, imageView, options, callback).picassoCallback);
        //callback == null ? null : callback.setImgUrl(url).picassoCallback);
    }

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

        Picasso instance = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("PICASSO", exception.getMessage());
                    }
                }).build();

        return instance;
    }

    private static Callback createTaskCallback(String imgUrl, ImageView imageView, Options options, Callback otherCallback) {
        class TaskCallback extends Callback {
            private ImageView imageView;
            private Options options;
            private Callback otherCallback;


            public TaskCallback(String imgUrl, ImageView imageView, Options options, Callback otherCallback) {
                this.setImgUrl(imgUrl);
                this.imageView = imageView;
                this.options = options;
                this.otherCallback = otherCallback;

                try {
                    imageView.setScaleType(options.loadingScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    AppLog.print(e.getMessage());
                }
            }

            @Override
            public void onSuccess(String imgUrl) {
                try {
                    imageView.setScaleType(options.successScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    AppLog.print(e.getMessage());
                }

                if (otherCallback != null) {
                    otherCallback.onSuccess(imgUrl);
                }
            }

            @Override
            public void onError(String imgUrl) {
                try {
                    imageView.setScaleType(options.errorScaleType);
                } catch (Exception e) {
                    //e.printStackTrace();
                    AppLog.print(e.getMessage());
                }

                if (otherCallback != null) {
                    otherCallback.onError(imgUrl);
                }
            }
        }

        return new TaskCallback(imgUrl, imageView, options, otherCallback);
    }

    //----------------------------------------------------------------------------------------------

    public static class Options {
        private int loadingPlaceHolderRes;
        private int errorPlaceHolderRes;
        private Integer minSize;
        private ImageView.ScaleType loadingScaleType;
        private ImageView.ScaleType successScaleType;
        private ImageView.ScaleType errorScaleType;


        public Options() {
            this.loadingPlaceHolderRes = DEFAULT_LOADING_PLACEHOLDER;
            this.errorPlaceHolderRes = DEFAULT_ERROR_PLACEHOLDER;

            this.loadingScaleType = ImageView.ScaleType.CENTER;
            this.successScaleType = ImageView.ScaleType.FIT_XY;
            this.errorScaleType = ImageView.ScaleType.CENTER;

            this.minSize = sMinSize;
        }

        public Options(@Nullable ImageView imageView) {
            this();

            if (imageView != null) {
                this.successScaleType = imageView.getScaleType();
                //this.errorScaleType = this.successScaleType;

                if (imageView.getWidth() > 5 || imageView.getHeight() > 5) {
                    if (imageView.getWidth() > imageView.getHeight()) {
                        this.minSize = imageView.getWidth();
                    } else {
                        this.minSize = imageView.getHeight();
                    }
                }
            }
        }

        public Options(@Nullable ImageView imageView, @DrawableRes int placeHolderRes) {
            this(imageView);

            this.loadingPlaceHolderRes = placeHolderRes;
            this.errorPlaceHolderRes = placeHolderRes;
        }


        public Options setMinSize(Integer minSize) {
            this.minSize = minSize;
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
    }

    public static abstract class Callback {
        private com.squareup.picasso.Callback picassoCallback;
        private String imgUrl;

        public Callback() {
            picassoCallback = new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Callback.this.onSuccess(imgUrl);
                }

                @Override
                public void onError() {
                    Callback.this.onError(imgUrl);
                }
            };
        }

        Callback setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public abstract void onSuccess(String imgUrl);

        public abstract void onError(String imgUrl);
    }
}
