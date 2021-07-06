package gmutils.net.retrofit.callback;

import androidx.annotation.NonNull;

import java.util.Map;

import gmutils.net.retrofit.listeners.OnResponseReady;
import gmutils.net.retrofit.responseHolders.BaseResponse;
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
            Request request,
            Class<R> responseClass,
            OnResponseReady<R> onResponseReady
    ) {
        this.callbackOperations = new CallbackOperations<R>(request, responseClass, Callback.this::setResult);
        this.onResponseReady = onResponseReady;
    }

    public Callback(
            String requestInfo,
            Class<R> responseClass,
            OnResponseReady<R> onResponseReady
    ) {
        this.callbackOperations = new CallbackOperations<R>(requestInfo, responseClass, Callback.this::setResult);
        this.onResponseReady = onResponseReady;
    }

    private void setResult(R result) {
        onResponseReady.invoke(result);
        onResponseReady = null;
        callbackOperations = null;
    }

    //----------------------------------------------------------------------------------------------

    public Callback<R> setExtras(Map<String, Object> extras) {
        this.callbackOperations.setExtras(extras);
        return this;
    }

    public Callback<R> setErrorListener(CallbackErrorHandler errorListener) {
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