package gmutils.net.retrofit.callback;

import androidx.annotation.NonNull;

import gmutils.net.retrofit.OnResponseReady2;
import gmutils.net.retrofit.responseHolders.BaseObjectResponse;

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
public class Callback2<DT, R extends BaseObjectResponse<DT>> implements retrofit2.Callback<R> {

    private CallbackOperations<R> callbackOperations;
    private OnResponseReady2<DT, R> onResponseReady;


    public Callback2(
            Request request,
            Class<R> responseClass,
            OnResponseReady2<DT, R> onResponseReady
    ) {
        this.callbackOperations = new CallbackOperations<>(request, responseClass, Callback2.this::setResult);
        this.onResponseReady = onResponseReady;
    }

    public Callback2(
            String requestInfo,
            Class<R> responseClass,
            OnResponseReady2<DT, R> onResponseReady
    ) {
        this.callbackOperations = new CallbackOperations<>(requestInfo, responseClass, Callback2.this::setResult);
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

    public Callback2<DT, R> setErrorListener(CallbackErrorHandler errorListener) {
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