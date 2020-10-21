package com.blogspot.gm4s1.gmutils.net.retrofit.zcore;

import com.blogspot.gm4s1.gmutils.AppLog;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.BaseResponse;

import okhttp3.Request;
import retrofit2.Call;

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
public class Callback2<R extends BaseResponse> implements retrofit2.Callback<R> {
    private Class<R> TClass;
    private OnResponseReady2<R> onResponseReady;
    private String requestId = null;
    public String url = "";

    public Callback2(
            Class<R> TClass,
            OnResponseReady2<R> onResponseReady
    ) {
        this("", "", TClass, onResponseReady, null);
    }

    public Callback2(
            String requestURL,
            Class<R> TClass,
            OnResponseReady2<R> onResponseReady
    ) {
        this(requestURL, requestURL, TClass, onResponseReady, null);
    }

    public Callback2(
            String requestURL,
            String requestDetails,
            Class<R> TClass,
            OnResponseReady2<R> onResponseReady
    ) {
        this(requestURL, requestDetails, TClass, onResponseReady, null);
    }

    public Callback2(
            String requestURL,
            Class<R> TClass,
            OnResponseReady2<R> onResponseReady,
            String requestId
    ) {
        this(requestURL, requestURL, TClass, onResponseReady, requestId);
    }

    public Callback2(
            String requestURL,
            String requestDetails,
            Class<R> TClass,
            OnResponseReady2<R> onResponseReady,
            String requestId
    ) {
        try {
            url = requestURL;
            AppLog.print("API:Request:", requestDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.TClass = TClass;
        this.onResponseReady = onResponseReady;
        this.requestId = requestId;
    }

    @Override
    public void onResponse(Call<R> call, retrofit2.Response<R> response) {
        printCallInfo(call, response);

        if (response.isSuccessful()) {
            R body = response.body();
            if (body != null && body.isSuccess()) {
                if (requestId != null) {
                    body._requestId = requestId;
                }

                body._code = response.code();

                setResult(response.body());

            } else {
                setError(response.body()._internalMessage, response.code());
            }
        } else {
            setError(response.message() + "\nCode: " + response.code(), response.code());
        }
    }

    @Override
    public void onFailure(Call<R> call, Throwable t) {
        printCallInfo(call, null);
        AppLog.print(t);

        setError(t.getMessage(), 0);
    }

    private void setResult(R result) {
        onResponseReady.invoke(result);
    }

    private void setError(String error, int code) {
        R response = null;
        try {
            response = TClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }


        if (response != null) {

            if (code == 0)
                response.setInternalStatus(BaseResponse.Statuses.ConnectionFailed);
            else
                response.setInternalStatus(BaseResponse.Statuses.Error);

            if (code == 0) {
                response._internalMessage = "Connection Timeout, Please check your connection";
            } else if (code == 401) {
                response._internalMessage = "Your session has been expired, Please close application and open again";
            } else {
                response._internalMessage = error;
            }

            if (requestId != null) {
                response._requestId = requestId;
            }
            response._code = code;
        }

        setResult(response);
    }

    private void printCallInfo(Call<R> call, retrofit2.Response<R> response) {
        if (response != null) {
            AppLog.print(
                    "API:Response:",
                    "url: <" + url + ">, \nresponse: " + response.body() + ", " +
                            "\ncode= " + response.code() + ", \nmsg= " + response.message() + ", " +
                            "\nerrorBody= " + response.errorBody()
            );
        }
    }
}