package gmutils.net.retrofit.callback;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import gmutils.logger.LoggerAbs;
import gmutils.net.retrofit.listeners.OnResponseReady;
import gmutils.net.retrofit.listeners.OnResponseReady2;
import gmutils.net.retrofit.listeners.OnResponseReady2o;
import gmutils.net.retrofit.responseHolders.BaseObjectResponse;
import okhttp3.Request;
import retrofit2.Call;

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
/*
    use this when Response hold data inside it
 */
public class Callback2<DT, R extends BaseObjectResponse<DT>> implements retrofit2.Callback<R> {
    private CallbackOperations<R> callbackOperations;
    private OnResponseReady2<DT> onResponseReady;


    public Callback2(
            Request request,
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady
    ) {
        this(request, responseClass, onResponseReady, null, null);
    }

    public Callback2(
            Request request,
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LoggerAbs logger
    ) {
        this(
                request,
                responseClass,
                onResponseReady,
                null,
                logger
        );
    }

    public Callback2(
            Request request,
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LogsOptions logsOptions
    ) {
        this(
                request,
                responseClass,
                onResponseReady,
                logsOptions,
                null
        );
    }

    public Callback2(
            Request request,
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LogsOptions logsOptions,
            LoggerAbs logger
    ) {
        this.callbackOperations = new CallbackOperations<R>(request, responseClass, Callback2.this::setResult, logsOptions, logger);
        this.onResponseReady = onResponseReady;
    }

    public Callback2(
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LoggerAbs.ContentGetter requestInfo
    ) {
        this(responseClass, onResponseReady, requestInfo, null, null);
    }

    public Callback2(
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LoggerAbs.ContentGetter requestInfo,
            LoggerAbs logger
    ) {
        this(
                responseClass,
                onResponseReady,
                requestInfo,
                null,
                logger
        );
    }

    public Callback2(
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LoggerAbs.ContentGetter requestInfo,
            LogsOptions.Replacements replacedTextsInLog
    ) {
        this(
                responseClass,
                onResponseReady,
                requestInfo,
                replacedTextsInLog,
                null
        );
    }

    public Callback2(
            Class<R> responseClass,
            OnResponseReady2<DT> onResponseReady,
            LoggerAbs.ContentGetter requestInfo,
            LogsOptions.Replacements replacedTextsInLog,
            LoggerAbs logger
    ) {
        this.callbackOperations = new CallbackOperations<R>(responseClass, Callback2.this::setResult, requestInfo, replacedTextsInLog, logger);
        this.onResponseReady = onResponseReady;
    }

    private void setResult(R result) {
        if (onResponseReady != null) onResponseReady.invoke(result);
        onResponseReady = null;
        callbackOperations = null;
    }

    //----------------------------------------------------------------------------------------------

    public Callback2<DT, R> setExtras(Map<String, Object> extras) {
        this.callbackOperations.setExtras(extras);
        return this;
    }

    public Callback2<DT, R> setErrorListener(CallbackErrorHandler errorListener) {
        this.callbackOperations.setErrorListener(errorListener);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onResponse(@NotNull Call<R> call, @NotNull retrofit2.Response<R> response) {
        callbackOperations.onResponse(call, response);
    }

    @Override
    public void onFailure(@NotNull Call<R> call, @NotNull Throwable t) {
        callbackOperations.onFailure(call, t);
    }

}