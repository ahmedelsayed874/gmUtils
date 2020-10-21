package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers.exampleFake;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.BaseResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.Response;

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
public class FakeImageAPI implements com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces.ImageAPI{

    public void post(String text, Bitmap image, OnResponseReady<Object> callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = _FakeData.randomNumber();
            Response<Object> s = Response.createInstance(Object.class);

            if (r >= 2) { //success:-> 2 : 10
                s.setData(new Object());
                s._code = 200;

            } else if (r >= 1) {//error-> 1
                s.setInternalStatus(BaseResponse.Statuses.Error);
                s._code = 400;

            } else { //connection failed-> 0
                s.setInternalStatus(BaseResponse.Statuses.ConnectionFailed);
                s._code = 0;
            }

            if (callback != null) callback.invoke(s);
        }, _FakeData.delay);
    }

}
