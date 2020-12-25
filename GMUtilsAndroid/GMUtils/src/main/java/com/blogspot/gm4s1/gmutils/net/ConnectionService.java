package com.blogspot.gm4s1.gmutils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import java.net.InetAddress;

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

                    returnResult(b);
                } catch (Exception e) {
                    returnResult(false);
                }
            }

            private void returnResult(final boolean success) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setConnectionCheckResult(success);
                        callback = null;
                    }
                });
            }
        }

        ConnectivityManager cnm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cnm != null) {
            NetworkInfo networkInfo = cnm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Thread(new Test(url, callback)).start();

            } else {
                callback.setConnectionCheckResult(false);
            }
        } else {
            callback.setConnectionCheckResult(true);
        }
    }

    public interface ConnectionCheckCallback {
        void setConnectionCheckResult(boolean connected);
    }
}
