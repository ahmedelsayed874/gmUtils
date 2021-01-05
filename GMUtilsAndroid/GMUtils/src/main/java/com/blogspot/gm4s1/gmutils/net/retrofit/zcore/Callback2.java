package com.blogspot.gm4s1.gmutils.net.retrofit.zcore;

import androidx.annotation.NonNull;

import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.Response;

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
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady
    ) {
        init("", TClass, onResponseReady, null);
    }

    public Callback2(
            String requestDetails,
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady
    ) {
        init(requestDetails, TClass, onResponseReady, null);
    }

    public Callback2(
            String requestDetails,
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady,
            String requestId
    ) {
        init(requestDetails, TClass, onResponseReady, requestId);
    }

    public Callback2(
            Request request,
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady
    ) {
        init(request.toString(), TClass, onResponseReady, null);
    }

    public Callback2(
            Request request,
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady,
            String requestId
    ){
        init(request.toString(), TClass, onResponseReady, requestId);
    }

    private void init(
            String requestDetails,
            Class<R> TClass,
            OnResponseReady2<DT> onResponseReady,
            String requestId
    ) {
        callbackOperations = new CallbackOperations<>(TClass, requestDetails, requestId, Callback2.this::setResult);
    }

    public void setErrorListener(CallbackErrorHandler errorListener) {
        this.callbackOperations.setErrorListener(errorListener);
    }

    @Override
    public void onResponse(@NonNull Call<R> call, @NonNull retrofit2.Response<R> response) {
        callbackOperations.onResponse(call, response);
    }

    @Override
    public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
        callbackOperations.onFailure(call, t);
    }

    private void setResult(R result) {
        onResponseReady.invoke(result);
        onResponseReady = null;
    }

}