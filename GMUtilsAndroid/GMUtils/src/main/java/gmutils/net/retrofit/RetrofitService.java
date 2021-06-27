package gmutils.net.retrofit;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public static class Parameters {
        private final String baseUrl;
        private boolean allowAllHostname = true;

        private int connectionTimeoutInSeconds = 30;
        private int readTimeoutInSeconds = 60;

        public Parameters(@NotNull String baseUrl) {
            this.baseUrl = baseUrl;
        }


        public Parameters setAllowAllHostname(boolean allowAllHostname) {
            this.allowAllHostname = allowAllHostname;
            return this;
        }

        public Parameters setConnectionTimeoutInSeconds(int connectionTimeoutInSeconds) {
            this.connectionTimeoutInSeconds = connectionTimeoutInSeconds;
            return this;
        }

        public Parameters setReadTimeoutInSeconds(int readTimeoutInSeconds) {
            this.readTimeoutInSeconds = readTimeoutInSeconds;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Parameters that = (Parameters) o;

            if (allowAllHostname != that.allowAllHostname) return false;
            if (connectionTimeoutInSeconds != that.connectionTimeoutInSeconds) return false;
            if (readTimeoutInSeconds != that.readTimeoutInSeconds) return false;
            return baseUrl.equals(that.baseUrl);
        }

        @Override
        public int hashCode() {
            int result = baseUrl.hashCode();
            result = 31 * result + (allowAllHostname ? 1 : 0);
            result = 31 * result + connectionTimeoutInSeconds;
            result = 31 * result + readTimeoutInSeconds;
            return result;
        }
    }

    //----------------------------------------------------------------------------------------------

    private Retrofit mRetrofit;
    private final Parameters parameters;

    public RetrofitService(@NotNull Parameters parameters, @Nullable Callback tmpBuildCallback) {
        try {
            Class.forName("retrofit2.Retrofit");
            Class.forName("okhttp3.OkHttpClient");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add those lines to gradle script file:\n" +
                    "implementation 'com.squareup.retrofit2:retrofit:2.7.1'\n" +
                    "implementation 'com.squareup.retrofit2:converter-gson:2.7.1'\n" +
                    "implementation 'com.squareup.okhttp:okhttp:2.4.0'\n" +
                    "implementation 'com.squareup.okhttp3:okhttp:4.9.0'\n" +
                    "implementation 'com.google.code.gson:gson:2.8.6' //preferred");
        }

        this.parameters = parameters;

        OkHttpClient client = createOkHttpClient(parameters, tmpBuildCallback)
                .readTimeout(parameters.readTimeoutInSeconds, TimeUnit.SECONDS)
                .connectTimeout(parameters.connectionTimeoutInSeconds, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(parameters.baseUrl)
                .addConverterFactory(new StringResponseConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private OkHttpClient.Builder createOkHttpClient(@NotNull Parameters parameters, Callback tmpBuildCallback) {
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
            if (parameters.allowAllHostname)
                builder.hostnameVerifier(new AllowAllHostnameVerifier());

            if (tmpBuildCallback != null) tmpBuildCallback.config(builder);
            tmpBuildCallback = null;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return builder;
    }

    //----------------------------------------------------------------------------------------------

    private static RetrofitService sInstance; //singleton

    public static <T> T create(@NotNull String baseURL, Class<T> serviceClass) {
        return create(new Parameters(baseURL), serviceClass);
    }

    public static <T> T create(@NotNull Parameters parameters, Class<T> serviceClass) {
        return create(parameters, null, serviceClass);
    }

    public static <T> T create(@NotNull Parameters parameters, @Nullable Callback tmpBuildCallback, Class<T> serviceClass) {
        if (sInstance == null) {
            sInstance = new RetrofitService(parameters, tmpBuildCallback);

        } else {
            /*String usedURL = "";

            try {
                usedURL = sInstance.mRetrofit.baseUrl().toString();

            } catch (Exception e) {
            }

            if (!parameters.baseUrl.equals(usedURL)) {
                destroy();
                sInstance = new RetrofitService(parameters, tmpBuildCallback);
            }*/

            if (!parameters.equals(sInstance.parameters)) {
                destroy();
                sInstance = new RetrofitService(parameters, tmpBuildCallback);
            }
        }

        return sInstance.mRetrofit.create(serviceClass);
    }

    public static void destroy() {
        if (sInstance != null) sInstance.mRetrofit = null;
    }

}
