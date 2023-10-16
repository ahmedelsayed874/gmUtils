package gmutils.net.retrofit.callback;

import androidx.annotation.NonNull;

import java.util.Map;

import gmutils.Logger;
import gmutils.net.retrofit.listeners.OnResponseReady;
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
public class Callback2o<DT, R extends BaseObjectResponse<DT>> implements retrofit2.Callback<R> {
    public static Logger getLogger() { return CallbackOperations.getLogger(); }
    public static void setLogger(Logger logger) {
        CallbackOperations.setLogger(logger);
    }

    private CallbackOperations<R> callbackOperations;
    private OnResponseReady2o<DT, R> onResponseReady;


    public Callback2o(
            Request request,
            Class<R> responseClass,
            OnResponseReady2o<DT, R> onResponseReady
    ) {
        this(request, responseClass, onResponseReady, null);
    }

    public Callback2o(
            Request request,
            Class<R> responseClass,
            OnResponseReady2o<DT, R> onResponseReady,
            Logger logger
    ) {
        this.callbackOperations = new CallbackOperations<R>(request, responseClass, Callback2o.this::setResult, logger);
        this.onResponseReady = onResponseReady;
    }

    public Callback2o(
            String requestInfo,
            Class<R> responseClass,
            OnResponseReady2o<DT, R> onResponseReady
    ) {
        this(requestInfo, responseClass, onResponseReady, null);
    }

    public Callback2o(
            String requestInfo,
            Class<R> responseClass,
            OnResponseReady2o<DT, R> onResponseReady,
            Logger logger
    ) {
        this.callbackOperations = new CallbackOperations<R>(requestInfo, responseClass, Callback2o.this::setResult, logger);
        this.onResponseReady = onResponseReady;
    }

    private void setResult(R result) {
        if (onResponseReady != null) onResponseReady.invoke(result);
        onResponseReady = null;
        callbackOperations = null;
    }

    //----------------------------------------------------------------------------------------------

    public Callback2o<DT, R> setExtras(Map<String, Object> extras) {
        this.callbackOperations.setExtras(extras);
        return this;
    }

    public Callback2o<DT, R> setErrorListener(CallbackErrorHandler errorListener) {
        this.callbackOperations.setErrorListener(errorListener);
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