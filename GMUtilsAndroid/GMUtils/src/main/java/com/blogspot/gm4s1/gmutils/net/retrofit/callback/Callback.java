package com.blogspot.gm4s1.gmutils.net.retrofit.callback;

import androidx.annotation.NonNull;

import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;

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
public class Callback<R extends BaseResponse> implements retrofit2.Callback<R> {

    private CallbackOperations<R> callbackOperations;
    private OnResponseReady<R> onResponseReady;

    public Callback(
            Class<R> RClass,
            OnResponseReady<R> onResponseReady
    ) {
        init("", RClass, onResponseReady, null);
    }

    public Callback(
            String requestDetails,
            Class<R> RClass,
            OnResponseReady<R> onResponseReady
    ) {
        init(requestDetails, RClass, onResponseReady, null);
    }

    public Callback(
            String requestDetails,
            Class<R> RClass,
            OnResponseReady<R> onResponseReady,
            String requestId
    ) {
        init(requestDetails, RClass, onResponseReady, requestId);
    }

    public Callback(
            Request request,
            Class<R> RClass,
            OnResponseReady<R> onResponseReady
    ) {
        init(request.toString(), RClass, onResponseReady, null);
    }

    public Callback(
            Request request,
            Class<R> RClass,
            OnResponseReady<R> onResponseReady,
            String requestId
    ) {
        init(request.toString(), RClass, onResponseReady, requestId);
    }

    private void init(
            String requestDetails,
            Class<R> RClass,
            OnResponseReady<R> onResponseReady,
            String requestId
    ) {
        this.callbackOperations = new CallbackOperations<R>(RClass, requestDetails, requestId, Callback.this::setResult);
        this.onResponseReady = onResponseReady;
    }

    private void setResult(R result) {
        onResponseReady.invoke(result);
        onResponseReady = null;
    }

    //----------------------------------------------------------------------------------------------

    public Callback<R> setExtra(Object extra) {
        this.callbackOperations.setExtra(extra);
        return this;
    }

    public Callback<R> setErrorListener(CallbackErrorHandler errorListener) {
        this.callbackOperations.setErrorListener(errorListener);
        return this;
    }

    public Callback<R> includeRawResponse() {
        this.callbackOperations.includeRawResponse();
        return this;
    }

    public Callback<R> printRawResponse() {
        this.callbackOperations.printRawResponse();
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onResponse(@NonNull Call<R> call, @NonNull retrofit2.Response<R> response) {
        callbackOperations.onResponse(call, response);
    }

    @Override
    public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
        callbackOperations.onFailure(call, t);
    }

}