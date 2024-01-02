package gmutils.net.retrofit;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import gmutils.listeners.ActionCallback0;
import gmutils.logger.Logger;
import gmutils.logger.LoggerAbs;
import gmutils.net.retrofit.listeners.OnResponseReady;
import gmutils.net.retrofit.responseHolders.BaseResponse;
import gmutils.storage.GeneralStorage;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

/**
 * https://square.github.io/retrofit/
 * <p>
 * it depends on
 * 'com.squareup.retrofit2:retrofit:2.7.1'
 * 'com.squareup.retrofit2:converter-gson:2.7.1'
 * 'com.squareup.okhttp:okhttp:2.4.0'
 */
public class RetrofitService {
    public static class TrustManagerHelper {

        public X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init((KeyStore) null);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }

            return (X509TrustManager) trustManagers[0];
        }

        //-----------------------------------------------------------

        public X509TrustManager getUnsafeTrustManager() {
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return new X509ExtendedTrustManager() {
                    //                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                        tm.checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                        tm.checkServerTrusted(chain, authType);
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                        tm.checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                        tm.checkServerTrusted(chain, authType);
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        tm.checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        tm.checkServerTrusted(chain, authType);
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return tm.getAcceptedIssuers();
                    }
                };
            }
            //
            else {
                return tm;
            }
        }

        //-----------------------------------------------------------

        /*public X509TrustManager createTrustManagerFromCertificate(
                InputStream cert
        ) throws Exception {
            return createTrustManagerFromCertificate(cert, "ca");
        }

        public X509TrustManager createTrustManagerFromCertificate(
                InputStream cert,
                String desiredCertificateAlias
        ) throws Exception {
            // loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally {
                cert.close();
            }

            // creating a KeyStore containing our trusted CAs
            KeyStore keyStore = getKeyStore(null, null);
            keyStore.setCertificateEntry(desiredCertificateAlias, ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            return (X509TrustManager) tmf.getTrustManagers()[0];
        }*/

        //-----------------------------------------------------------

        /**
         * @param cert stream file of extension .cert or .crt
         * @return
         */
        /**
         * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
         * certificates have not been signed by these certificates will fail with a {@code
         * SSLHandshakeException}.
         *
         * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
         * set. This is useful in development where certificate authority-trusted certificates aren't
         * available. Or in production, to avoid reliance on third-party certificate authorities.
         *
         * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
         *
         * <p>Relying on your own trusted certificates limits your server team's ability to update their
         * TLS certificates. By installing a specific set of trusted certificates, you take on additional
         * operational complexity and limit your ability to migrate between certificate authorities. Do
         * not use custom trusted certificates in production without the blessing of your server's TLS
         * administrator.
         */
        public X509TrustManager getOrCreateTrustManagerFromCertificate(
                String keyStoreFilePath,
                String certificateAlias,
                @Nullable String password,
                @NotNull ActionCallback0<InputStream> certificateFile
        ) throws Exception {
            char[] password2 = password.toCharArray();

            //region get/create KeyStore
            File ksf = new File(keyStoreFilePath);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream in = new FileInputStream(ksf)) {
                keyStore.load(in, password2);
            } catch (Exception e) {
                keyStore.load(null, password2);
            }
            //endregion

            //region check if the certificate was registered in the keystore or not
            boolean isCertRegistered = false;

            for (int i = 0; i < 5; i++) {
                try {
                    String certificateAlias2 = certificateAlias + i;
                    if (keyStore.containsAlias(certificateAlias2)) {
                        isCertRegistered = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //endregion

            //region register the certificate if not exist in keystore
            if (!isCertRegistered) {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> certificates;
                certificates = certificateFactory.generateCertificates(certificateFile.invoke());
                if (certificates.isEmpty()) {
                    throw new IllegalArgumentException("expected non-empty set of trusted certificates");
                }

                // Put the certificates a key store.
                int index = 0;
                for (Certificate certificate : certificates) {
                    String certificateAlias2 = certificateAlias + (index++);
                    keyStore.setCertificateEntry(certificateAlias2, certificate);
                }

                ksf.deleteOnExit();
                ksf.createNewFile();

                try (OutputStream out = new FileOutputStream(ksf)) {
                    keyStore.store(out, password2);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            //endregion

            //region Use it to build an X509 trust manager.
            String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgorithm);
            keyManagerFactory.init(keyStore, password2);

            String trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            //endregion

            return (X509TrustManager) trustManagers[0];
        }
    }

    public enum SSLContextProtocols {
        Default("Default"),

        //---------------------------------

        SSL("SSL"),
        @Deprecated(since = "allowed from API 10 till API 25")
        SSLv3("SSLv3"),

        //---------------------------------

        TLS("TLS"),
        TLSv1("TLSv1"),
        @RequiresApi(value = 16)
        TLSv1_1("TLSv1.1"),
        @RequiresApi(value = 16)
        TLSv1_2("TLSv1.2"),
        @RequiresApi(value = 29)
        TLSv1_3("TLSv1.3");

        //---------------------------------

        final String asString;

        SSLContextProtocols(String string) {
            asString = string;
        }
    }

    public interface ClientBuildCallback {
        @Nullable
        default X509TrustManager getX509TrustManager() {
            return null;
        }

        @NotNull
        default SSLContextProtocols getSSLContextProtocol() {
            return SSLContextProtocols.TLS;
        }

        void config(@NotNull OkHttpClient.Builder httpClient, String error);
    }

    public static class Parameters {
        private final String baseUrl;
        private String allowedHostname;

        private int connectionTimeoutInSeconds = 15;
        private int readTimeoutInSeconds = 15;

        public Parameters(@NotNull String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Parameters setAllowedHostname(String allowedHostname) {
            this.allowedHostname = allowedHostname;
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

            if (!TextUtils.equals(allowedHostname, that.allowedHostname)) return false;
            if (connectionTimeoutInSeconds != that.connectionTimeoutInSeconds) return false;
            if (readTimeoutInSeconds != that.readTimeoutInSeconds) return false;
            return baseUrl.equals(that.baseUrl);
        }

        @Override
        public int hashCode() {
            int result = baseUrl.hashCode();
            result = 31 * result + allowedHostname.hashCode();
            result = 31 * result + connectionTimeoutInSeconds;
            result = 31 * result + readTimeoutInSeconds;
            return result;
        }
    }

    public static class CertificateConfigurations {
        public static CertificateConfigurations getInstance() {
            return new CertificateConfigurations();
        }

        //---------------------------------------------------------------------------------

        private GeneralStorage storage = GeneralStorage.getInstance("net-connection-preference");

        //------------------------------------------

        @Nullable
        public Boolean isAllowConnectToAnySSL() {
            String v = storage.retrieve("allowAny", "");
            if (v.isEmpty()) return null;
            else return "a".equalsIgnoreCase(v);
        }

        public void setAllowConnectToAnySSL(boolean allowConnectToAnySSL) {
            storage.save("allowAny", allowConnectToAnySSL ? "a" : "x");
            gmutils.net.retrofit.RetrofitService.destroy();
        }

        //---------------------------------------------------------------------------------

        @Nullable
        public String getSSLCertificatePath() {
            String v = storage.retrieve("sslcertpath", "");
            if (v.isEmpty()) return null;
            else return v;
        }

        public void setSSLCertificatePath(String sslCertificatePath) {
            storage.save("sslcertpath", sslCertificatePath);
            gmutils.net.retrofit.RetrofitService.destroy();
        }

        public void saveSSLCertificate(Context context, Uri fileUri) throws Exception {
            String enc = Charset.defaultCharset().name();
            String fileName = null;
            try {
                fileName = URLDecoder.decode(fileUri.toString(), enc);
            } catch (Exception e) {
                fileName = fileUri.toString();
            }

            int li = fileName.lastIndexOf("/");
            fileName = fileName.substring(li + 1);

            File certDir = new File(context.getFilesDir(), "certificates");
            if (!certDir.exists()) certDir.mkdirs();
            File certFile = new File(certDir, fileName);
            if (!certFile.exists()) certFile.createNewFile();

            InputStream instream = context.getContentResolver().openInputStream(fileUri);
            FileOutputStream outstream = new FileOutputStream(certFile);

            byte[] arr = new byte[instream.available()];
            instream.read(arr);
            outstream.write(arr);

            outstream.flush();
            outstream.close();

            instream.close();

            setAllowConnectToAnySSL(false);
            setSSLCertificatePath(certFile.getPath());
        }

        //------------------------------------------

        private String getKeystoreFilePath() {
            String sslCertificatePath = getSSLCertificatePath();
            int i = sslCertificatePath.lastIndexOf("/");
            String dirPath = sslCertificatePath.substring(0, i);
            String fileName = sslCertificatePath.substring(i + 1);
            String keystorePath = dirPath + "/cert-keystore-" + fileName.hashCode();
            return keystorePath;
        }
    }

    //----------------------------------------------------------------------------------------------

    private Retrofit mRetrofit;
    private final Parameters parameters;

    public RetrofitService(@NotNull Parameters parameters, @Nullable ClientBuildCallback clientBuildCallback) {
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

        OkHttpClient client = createOkHttpClient(parameters, clientBuildCallback)
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

    /**
     * https://developer.android.com/privacy-and-security/security-ssl#UnknownCa
     * https://developer.android.com/privacy-and-security/security-config#TrustingAdditionalCas
     * ,
     * https://www.positioniseverything.net/trust-anchor-for-certification-path-not-found
     * https://stackoverflow.com/questions/6825226/trust-anchor-not-found-for-android-ssl-connection
     * https://stackoverflow.com/questions/29273387/certpathvalidatorexception-trust-anchor-for-certificate-path-not-found-retro
     */
    private OkHttpClient.Builder createOkHttpClient(@NotNull Parameters parameters, ClientBuildCallback clientBuildCallback) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        String error = null;

        try {
            //region TrustManager
            X509TrustManager trustManager = null;

            if (clientBuildCallback != null) {
                trustManager = clientBuildCallback.getX509TrustManager();
            }

            if (trustManager == null) {
                trustManager = new TrustManagerHelper().getDefaultTrustManager();
            }
            //endregion

            //region ssl factory
            String protocol = null;
            if (clientBuildCallback != null) {
                SSLContextProtocols sslContextProtocol = clientBuildCallback.getSSLContextProtocol();
                if (sslContextProtocol != null) {
                    protocol = sslContextProtocol.asString;
                }
            }
            if (TextUtils.isEmpty(protocol)) protocol = SSLContextProtocols.TLS.asString;
            SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(null, new TrustManager[]{trustManager}, null);//new java.security.SecureRandom()
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            //endregion

            builder.sslSocketFactory(sslSocketFactory, trustManager);

        } catch (Exception e) {
            Logger.d().print(() -> RetrofitService.class.getSimpleName() + ".createOkHttpClient() >> EXCEPTION:: ");
            Logger.d().print(e);
            error = "Trusting establish failed: " + e.getMessage();
        }

        builder.hostnameVerifier((hostname, session) -> TextUtils.isEmpty(parameters.allowedHostname) || TextUtils.equals(parameters.allowedHostname, hostname));

        if (clientBuildCallback != null) clientBuildCallback.config(builder, error);

        return builder;
    }

    //----------------------------------------------------------------------------------------------

    public <T> T create(Class<T> servicesInterface) {
        return mRetrofit.create(servicesInterface);
    }

    //----------------------------------------------------------------------------------------------

    private static RetrofitService sInstance; //singleton

    public static <T> T create(@NotNull String baseURL, @NotNull Class<T> servicesInterface) {
        return create(new Parameters(baseURL), servicesInterface);
    }

    public static <T> T create(@NotNull String baseURL, @NotNull Class<T> servicesInterface, @Nullable ClientBuildCallback clientBuildCallback) {
        return create(new Parameters(baseURL), servicesInterface, clientBuildCallback);
    }

    public static <T> T create(@NotNull Parameters parameters, @NotNull Class<T> servicesInterface) {
        return create(parameters, servicesInterface, null);
    }

    public static <T> T create(@NotNull Parameters parameters, @NotNull Class<T> servicesInterface, @Nullable ClientBuildCallback clientBuildCallback) {
        if (sInstance != null) {
            if (!parameters.equals(sInstance.parameters)) {
                destroy();
            }
            if (clientBuildCallback != null) {
                if (TextUtils.equals(clientBuildCallbackId, clientBuildCallback.toString())) {
                    destroy();
                }
            }
        }

        if (sInstance == null) {
            if (clientBuildCallback == null) {
                clientBuildCallback = getDefaultClientBuildCallback(parameters.baseUrl);
            }
            sInstance = new RetrofitService(parameters, clientBuildCallback);
            clientBuildCallbackId = clientBuildCallback.toString();
        }

        return sInstance.mRetrofit.create(servicesInterface);
    }

    private static String clientBuildCallbackId;

    public static void destroy() {
        if (sInstance != null) {
            sInstance.mRetrofit = null;
            sInstance = null;
        }
    }

    public static ClientBuildCallback getDefaultClientBuildCallback(String baseURL) {
        return new ClientBuildCallback() {
            @androidx.annotation.Nullable
            @Override
            public X509TrustManager getX509TrustManager() {
                if (baseURL.startsWith("https://")) {
                    CertificateConfigurations cert = CertificateConfigurations.getInstance();
                    String sslCertificatePath = cert.getSSLCertificatePath();
                    Boolean allowConnectToAnySSL = cert.isAllowConnectToAnySSL();

                    if (!TextUtils.isEmpty(sslCertificatePath)) {
                        try {
                            return new TrustManagerHelper()
                                    .getOrCreateTrustManagerFromCertificate(
                                            cert.getKeystoreFilePath(),
                                            "crt",
                                            "crt-pw-pw",
                                            () -> {
                                                try {
                                                    return new FileInputStream(sslCertificatePath);
                                                } catch (FileNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                    );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else if (allowConnectToAnySSL != null && allowConnectToAnySSL) {
                        return new RetrofitService.TrustManagerHelper().getUnsafeTrustManager();
                    }
                }

                return null;
            }

            @Override
            public void config(@NonNull OkHttpClient.Builder httpClient, String error) {
            }
        };
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    public static <R extends BaseResponse> R executeWebService(
            boolean async,
            Class<R> responseClass,
            Call<R> call,
            @Nullable OnResponseReady<R> callback
    ) {
        return executeWebService(
                async,
                responseClass,
                call,
                callback,
                null,
                Logger.d()
        );
    }

    @Nullable
    public static <R extends BaseResponse> R executeWebService(
            boolean async,
            Class<R> responseClass,
            Call<R> call,
            @Nullable OnResponseReady<R> callback,
            LoggerAbs loggerAbs
    ) {
        return executeWebService(
                async,
                responseClass,
                call,
                callback,
                null,
                loggerAbs
        );
    }

    @Nullable
    public static <R extends BaseResponse> R executeWebService(
            boolean async,
            Class<R> responseClass,
            Call<R> call,
            @Nullable OnResponseReady<R> callback,
            String[] excludedTextsFromLog,
            LoggerAbs loggerAbs
    ) {
        if (async) {
            gmutils.net.retrofit.callback.Callback<R> callback2;
            callback2 = new gmutils.net.retrofit.callback.Callback<>(
                    call.request(),
                    responseClass,
                    callback,
                    excludedTextsFromLog,
                    loggerAbs
            );
            call.enqueue(callback2);
            return null;
        } else {
            AtomicReference<R> response = new AtomicReference<>();

            gmutils.net.retrofit.callback.Callback<R> callback2;
            callback2 = new gmutils.net.retrofit.callback.Callback<>(
                    call.request(),
                    responseClass,
                    response::set,
                    excludedTextsFromLog,
                    loggerAbs
            );

            try {
                Response<R> retrofitResponse = call.execute();
                callback2.onResponse(call, retrofitResponse);
            } catch (Exception e) {
                callback2.onFailure(call, e);
            }

            if (callback != null) {
                callback.invoke(response.get());
            }

            return response.get();
        }
    }

}
