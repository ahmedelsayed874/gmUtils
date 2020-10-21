package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers.exampleFake;

import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady2;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.BaseResponse;

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
public class FakeTimeAPIs implements com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces.TimeAPIs {

    @Override
    public void geTimeZoneList(String ofSpecificArea, OnResponseReady2<TimeZones> callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = _FakeData.randomNumber();
            TimeZones t = _FakeData.instance().timeZones();

            if (r >= 2) { //success:-> 2 : 10
                t._code = 200;
                t._requestId = ofSpecificArea;

            } else if (r >= 1) {//error-> 1
                t.clear();
                t.setInternalStatus(BaseResponse.Statuses.Error);
                t._code = 400;
                t._requestId = ofSpecificArea;

            } else { //connection failed-> 0
                t.clear();
                t.setInternalStatus(BaseResponse.Statuses.ConnectionFailed);
                t._code = 0;
                t._requestId = ofSpecificArea;
            }

            if (callback != null) callback.invoke(t);
        }, _FakeData.delay);
    }

    @Override
    public void getCurrentTime(String zone, OnResponseReady2<TimeOfArea> callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = _FakeData.randomNumber();
            TimeOfArea t = _FakeData.instance().timeOfArea();

            if (r >= 2) { //success:-> 2 : 10
                t._code = 200;
                t._requestId = zone;

            } else if (r >= 1) {//error-> 1
                t.setDatetime("");
                t.setInternalStatus(BaseResponse.Statuses.Error);
                t._code = 400;
                t._requestId = zone;

            } else { //connection failed-> 0
                t.setDatetime("");
                t.setInternalStatus(BaseResponse.Statuses.ConnectionFailed);
                t._code = 0;
                t._requestId = zone;
            }

            if (callback != null) callback.invoke(t);
        }, _FakeData.delay);
    }
}
