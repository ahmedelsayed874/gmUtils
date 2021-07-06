package gmutils.net.retrofit.example.callers.dummy;

import android.graphics.Bitmap;

import gmutils.net.retrofit.example.callers._interfaces.ImageAPIs;
import gmutils.net.retrofit.example.data.ImageResponse;
import gmutils.net.retrofit.listeners.OnResponseReady2o;
import gmutils.net.retrofit.responseHolders.BaseResponse;

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

    public void post(String text, Bitmap image, OnResponseReady2o<Object, ImageResponse> callback) {
        FakeData.run0((s) -> {
            ImageResponse r = new ImageResponse();

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
