package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers.dummy;

import android.graphics.Bitmap;

import com.blogspot.gm4s1.gmutils.net.retrofit.example.callers._interfaces.ImageAPIs;
import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady2;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.Response;

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
public class FakeImageAPI implements ImageAPIs {

    public void post(String text, Bitmap image, OnResponseReady2<Object> callback) {
        FakeData.run0((s) -> {
            Response<Object> r = Response.createInstance(Object.class);

            if (s == null) {
                r.setCallbackStatus(BaseResponse.Statuses.ConnectionFailed);
                r._code = 0;
            } else {
                if (s) {
                    r.setData(new Object());
                    r.setCallbackStatus(BaseResponse.Statuses.Succeeded);
                    r._code = 200;
                } else {
                    r.setCallbackStatus(BaseResponse.Statuses.Error);
                    r._code = 400;
                }
            }

            if (callback != null) callback.invoke(r);
        });
    }

}
