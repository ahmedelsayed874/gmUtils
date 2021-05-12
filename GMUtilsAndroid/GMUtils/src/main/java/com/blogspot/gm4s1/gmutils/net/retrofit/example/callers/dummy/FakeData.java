package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers.dummy;

import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.listeners.ResultCallback;
import com.blogspot.gm4s1.gmutils.listeners.ResultCallback2;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseDataWrapperResponse;

import java.util.Random;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

public class FakeData {

    public static FakeData instance() {
        return new FakeData();
    }

    public static void run0(ResultCallback<Boolean> res) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = new Random().nextInt(10);
            if (r == 0) res.invoke(null);
            else if (r == 1) res.invoke(false);
            else res.invoke(true);
        }, 1200);
    }

    public static <T extends BaseResponse> void run1(Class<T> cls, ResultCallback2<FakeData, T> result) {
        int r = new Random().nextInt(10);

        try {
            T response = cls.newInstance();

            if (r == 0) {
                response._code = 0;
                response.setCallbackStatus(BaseResponse.Statuses.ConnectionFailed);
                result.invoke(instance(), response);

            } else if (r == 1) {
                response._code = 400;
                response.setCallbackStatus(BaseResponse.Statuses.Error);
                result.invoke(instance(), response);

            } else {
                response._code = 200;
                response.setCallbackStatus(BaseResponse.Statuses.Succeeded);
                result.invoke(instance(), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void run2(Class<BaseDataWrapperResponse<T>> cls, ResultCallback2<FakeData, BaseDataWrapperResponse<T>> result) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = new Random().nextInt(10);

            try {
                BaseDataWrapperResponse<T> response = cls.newInstance();

                if (r == 0) {
                    response._code = 0;
                    response.setCallbackStatus(BaseResponse.Statuses.ConnectionFailed);
                    result.invoke(instance(), response);

                } else if (r == 1) {
                    response._code = 400;
                    response.setCallbackStatus(BaseResponse.Statuses.Error);
                    result.invoke(instance(), response);

                } else {
                    response._code = 200;
                    response.setCallbackStatus(BaseResponse.Statuses.Succeeded);
                    result.invoke(instance(), response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1200);
    }

    //----------------------------------------------------------------------------------------------

    public TimeZones timeZones() {
        TimeZones t = new TimeZones();
        t.add("Africa/Cairo");
        return t;
    }

    public TimeOfArea timeOfArea() {
        TimeOfArea t = new TimeOfArea();
        t.setDatetime("2020-10-20T01:00:00.123456+02:00");
        return t;
    }
}
