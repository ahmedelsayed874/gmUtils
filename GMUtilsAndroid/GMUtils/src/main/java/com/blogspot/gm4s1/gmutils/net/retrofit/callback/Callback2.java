package com.blogspot.gm4s1.gmutils.net.retrofit.callback;

import androidx.annotation.NonNull;

import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady2;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.Response;

import java.util.Map;

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
/*
    use this when Response hold data inside it
 */
public class Callback2<DT, R extends Response<DT>> implements retrofit2.Callback<R> {

    private CallbackOperations<R> callbackOperations;
    private OnResponseReady2<DT> onResponseReady;


    public Callback2(
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady
    ) {
        this.callbackOperations = new CallbackOperations<>(responseClass, Callback2.this::setResult);
        this.onResponseReady = onResponseReady;
    }

    private void setResult(R result) {
        onResponseReady.invoke(result);
        onResponseReady = null;
        callbackOperations = null;
    }

    //----------------------------------------------------------------------------------------------

    public Callback2<DT, R> setExtras(Map<String, Object> extras) {
        this.callbackOperations.setExtras(extras);
        return this;
    }

    public Callback2<DT, R> printRequestInfo(Request request) {
        this.callbackOperations.printRequestInfo(request.toString());
        return this;
    }

    public Callback2<DT, R> printRequestInfo(String requestInfo) {
        this.callbackOperations.printRequestInfo(requestInfo);
        return this;
    }

    public Callback2<DT, R> setErrorListener(CallbackErrorHandler errorListener) {
        this.callbackOperations.setErrorListener(errorListener);
        return this;
    }

    public Callback2<DT, R> includeRawResponse() {
        this.callbackOperations.includeRawResponse();
        return this;
    }

    public Callback2<DT, R> printRawResponse() {
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