package com.blogspot.gm4s1.gmutils.net.retrofit.zcore;

import com.blogspot.gm4s1.gmutils.Logger;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
/**
 * https://square.github.io/retrofit/
 *
 * it depends on
 * 'com.squareup.retrofit2:retrofit:2.7.1'
 * 'com.squareup.retrofit2:converter-gson:2.7.1'
 * 'com.squareup.okhttp:okhttp:2.4.0'
 */
public class RetrofitService {
    public interface Callback {
        void config(OkHttpClient.Builder httpClient);
    }

    public static String baseUrl = "";
    public static boolean allowAllHostname = true;
    public static Callback tmpCallback = null;

    private static RetrofitService sInstance;
    private Retrofit mRetrofit;

    private RetrofitService(String url) {
        //OkHttpClient client = new OkHttpClient.Builder()
        OkHttpClient client = createOkHttpClient()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private static OkHttpClient.Builder createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm()
            );
            trustManagerFactory.init((KeyStore) null);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }

            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, trustManager);
            if (allowAllHostname) builder.hostnameVerifier(new AllowAllHostnameVerifier());

            if (tmpCallback != null) tmpCallback.config(builder);
            tmpCallback = null;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return builder;
    }

    //----------------------------------------------------------------------------------------------

    public static <T> T create(Class<T> serviceClass) {
        return create(baseUrl, serviceClass);
    }

    public static <T> T create(String baseURL, Class<T> serviceClass) {
        if (sInstance == null) {
            sInstance = new RetrofitService(baseURL);

        } else {
            String usedURL = "";

            try {
                usedURL = sInstance.mRetrofit.baseUrl().toString();

            } catch (Exception e) {
            }

            if (!baseURL.equals(usedURL)) {
                destroy();
                sInstance = new RetrofitService(baseURL);
            }
        }

        return (T) sInstance.mRetrofit.create(serviceClass);
    }

    public static void destroy() {
        if (sInstance != null) sInstance.mRetrofit = null;
    }


}
