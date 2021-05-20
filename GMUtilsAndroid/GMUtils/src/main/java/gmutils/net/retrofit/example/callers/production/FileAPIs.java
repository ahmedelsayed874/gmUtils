package gmutils.net.retrofit.example.callers.production;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import gmutils.Logger;
import gmutils.listeners.ResultCallback2;
import gmutils.listeners.ResultCallback3;
import gmutils.net.retrofit.example.apiServices.FileDownloadRequest;
import gmutils.net.retrofit.RetrofitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class FileAPIs implements gmutils.net.retrofit.example.callers._interfaces.FileAPIs {

    public void downloadFile(
            String fileURL, //http:.......
            File destFile, //destination location on your memory (you will specify first then path here) (4e: externalCacheDir())
            ResultCallback3<File, Boolean, String> onResponseReady//: (localUri: Uri, success: Boolean, msg: String) -> Unit
    ) throws FileNotFoundException {
        goDownloadFile(fileURL, new FileOutputStream(destFile), (suc, m) -> {
            onResponseReady.invoke(destFile, suc, m);
        });
    }

    public void downloadFile(
            String fileURL,
            Uri localUri,
            OutputStream outputStream,
            ResultCallback3<Uri, Boolean, String> onResponseReady//: (localUri: Uri, success: Boolean, msg: String) -> Unit
    ) {
        goDownloadFile(fileURL, outputStream, (suc, m) -> {
            onResponseReady.invoke(localUri, suc, m);
        });
    }

    private void goDownloadFile(String fileURL, OutputStream outputStream, ResultCallback2<Boolean, String> callback) {
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
                            callback.invoke(true, "");
                        });
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.invoke(false, "Can't download");
                        });
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.invoke(false, "Connection Failed");
            }
        });
    }

}
