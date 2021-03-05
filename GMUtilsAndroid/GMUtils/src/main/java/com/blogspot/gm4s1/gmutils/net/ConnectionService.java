package com.blogspot.gm4s1.gmutils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import java.net.InetAddress;

/*
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
public class ConnectionService {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static void isInternetAvailable(Context context, @Nullable String url, ConnectionCheckCallback callback) {
        class Test implements Runnable {
            final String url;
            ConnectionCheckCallback callback;

            private Test(@Nullable String url, ConnectionCheckCallback callback) {
                this.url = url;
                this.callback = callback;
            }

            @Override
            public void run() {
                try {
                    InetAddress ipAddr = InetAddress.getByName(url == null ? "google.com" : url);
                    boolean b = !ipAddr.equals("");

                    returnResult(b, "");
                } catch (Exception e) {
                    returnResult(false, e.getMessage());
                }
            }

            private void returnResult(final boolean success, String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.setConnectionCheckResult(success, msg);
                    callback = null;
                });
            }
        }

        ConnectivityManager cnm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cnm != null) {
            NetworkInfo networkInfo = cnm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Thread(new Test(url, callback)).start();

            } else {
                callback.setConnectionCheckResult(false, "Network not available");
            }
        } else {
            callback.setConnectionCheckResult(false, "ConnectivityManager not available");
        }
    }

    public interface ConnectionCheckCallback {
        void setConnectionCheckResult(boolean connected, String msg);
    }
}
