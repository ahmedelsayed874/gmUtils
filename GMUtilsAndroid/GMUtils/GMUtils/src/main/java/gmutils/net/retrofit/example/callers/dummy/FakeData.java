package gmutils.net.retrofit.example.callers.dummy;

import android.os.Handler;
import android.os.Looper;

import java.util.Random;

import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.net.retrofit.example.data.TimeOfArea;
import gmutils.net.retrofit.example.data.TimeZones;
import gmutils.net.retrofit.responseHolders.BaseObjectResponse;
import gmutils.net.retrofit.responseHolders.BaseResponse;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
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
                response.setCallbackStatus(BaseResponse.Status.ConnectionFailed);
                result.invoke(instance(), response);

            } else if (r == 1) {
                response._code = 400;
                response.setCallbackStatus(BaseResponse.Status.Error);
                result.invoke(instance(), response);

            } else {
                response._code = 200;
                response.setCallbackStatus(BaseResponse.Status.Succeeded);
                result.invoke(instance(), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void run2(Class<BaseObjectResponse<T>> cls, ResultCallback2<FakeData, BaseObjectResponse<T>> result) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int r = new Random().nextInt(10);

            try {
                BaseObjectResponse<T> response = cls.newInstance();

                if (r == 0) {
                    response._code = 0;
                    response.setCallbackStatus(BaseResponse.Status.ConnectionFailed);
                    result.invoke(instance(), response);

                } else if (r == 1) {
                    response._code = 400;
                    response.setCallbackStatus(BaseResponse.Status.Error);
                    result.invoke(instance(), response);

                } else {
                    response._code = 200;
                    response.setCallbackStatus(BaseResponse.Status.Succeeded);
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
