package com.blogspot.gm4s1.gmutils.net.volley.exampleAPIs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blogspot.gm4s1.gmutils.net.volley.exampleURLs.UploadImageURL;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.ApiManager;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.utils.OnDataFetchedListener;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.utils.Result;

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
public class UploadImageAPIs extends ApiManager {

    /*
        use one of those constructor
     */

    public UploadImageAPIs(@NonNull Activity activity) {
        super(activity);
    }

    public UploadImageAPIs(@NonNull Fragment fragment) {
        super(fragment);
    }

    public UploadImageAPIs(@NonNull Context context) {
        super(context);
    }

    //----------------------------------------------------------------------------------------------

    public void uploadImage(String text, Bitmap bitmap, final OnDataFetchedListener<Result<Object>> callback) {
        UploadImageURL url = new UploadImageURL(text, bitmap);

        doRequest(url, (request, response, responseStatus, statusCode) -> {
            Result<Object> result = new Result<>();

            try {
                result.setResult(response);

            } catch (Exception e) {
                if (responseStatus == ResponseStatus.ConnectionFailed) {
                    result.setConnectionError(true);
                }
            }

            if (callback != null) callback.onDataFetched(result);

            return true;
        });
    }

}
