package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers.production;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.listeners.ResultCallback3;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.apiServices.FileDownloadRequest;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.RetrofitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


class FilesAPIs {

    public void downloadFile(
            String fileURL,
            Uri localUri,
            OutputStream outputStream,
            ResultCallback3<Uri, Boolean, String> onResponseReady//: (localUri: Uri, success: Boolean, msg: String) -> Unit
    ) {
        FileDownloadRequest service = RetrofitService.create(FileDownloadRequest.class);

        Call<ResponseBody> call = service.downloadFile(fileURL);

        Logger.print(call.request());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                new Thread(() -> {
                    try {
                        byte[] body = response.body().bytes();

                        ByteArrayOutputStream bw = new ByteArrayOutputStream();
                        bw.write(body);
                        bw.writeTo(outputStream);

                        try {
                            bw.flush();
                            bw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        new Handler(Looper.getMainLooper()).post(() -> {
                            onResponseReady.invoke(localUri, true, "");
                        });
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            onResponseReady.invoke(localUri, false, "Can't download");
                        });
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onResponseReady.invoke(localUri, false, "Connection Failed");
            }
        });
    }

}
