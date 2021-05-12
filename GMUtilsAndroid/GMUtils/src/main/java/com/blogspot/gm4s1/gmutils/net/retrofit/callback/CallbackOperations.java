package com.blogspot.gm4s1.gmutils.net.retrofit.callback;

import android.text.TextUtils;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseDataWrapperResponse;

import java.util.Map;
import java.util.Set;

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
final class CallbackOperations<R extends BaseResponse> {
    interface Listener<R> {
        void onResponseReady(R response);
    }

    private final Class<R> responseClass;
    private Listener<R> listener;
    private Map<String, Object> extras;
    private CallbackErrorHandler errorListener;


    public CallbackOperations(Request request, Class<R> responseClass, Listener<R> listener) {
        this("", responseClass, listener);
        try {
            if (Logger.IS_LOG_ENABLED())
                Logger.print("API:Request:", request.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CallbackOperations(String requestInfo, Class<R> responseClass, Listener<R> listener) {
        this.responseClass = responseClass;
        this.listener = listener;

        if (requestInfo != null && !"".equals(requestInfo))
            if (Logger.IS_LOG_ENABLED())
                Logger.print("API:Request:", requestInfo);

        try {
            responseClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------

    void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    void setErrorListener(CallbackErrorHandler errorListener) {
        this.errorListener = errorListener;
    }

    //----------------------------------------------------------------------------------------------

    void onResponse(Call<R> call, retrofit2.Response<R> response) {
        String error = "";
        try {
            error = response.errorBody().string();
        } catch (Exception e) {
            error = response.message() + "\nCode: " + response.code();
        }

        printCallInfo(call, response, error);

        if (response.isSuccessful()) {
            R body = response.body();
            if (body != null) {
                if (extras != null) {
                    body._extras = extras;
                }

                body._code = response.code();

                setResult(response.body());

            } else {
                setError("", response.code());
            }
        } else {
            setError(error, response.code());
        }
    }

    void onFailure(Call<R> call, Throwable t) {
        printCallInfo(call, null, "Exception[" + t.getMessage() + "]");

        setError(t.getMessage(), 0);
    }

    private void setResult(R result) {
        if (Logger.IS_LOG_ENABLED()) {
            Logger.print(
                    "EXTRA_INFO:",
                    "[callbackStatus=" + result.getCallbackStatus() +
                            ", responseStatus=" + result.getResponseStatus() + "]"
            );
        }
        listener.onResponseReady(result);
        destroyReferences();
    }

    private void setError(String error, int code) {
        R response = null;

        try {
            response = responseClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (response != null) {
            if (errorListener != null) {
                response.setCallbackStatus(errorListener.getInternalStatus(code, error));
            } else {
                if (code == 0)
                    response.setCallbackStatus(BaseDataWrapperResponse.Statuses.ConnectionFailed);
                else
                    response.setCallbackStatus(BaseDataWrapperResponse.Statuses.Error);
            }

            if (errorListener != null) {
                response._error = errorListener.getErrorMessage(code, error);

            } else {
                if (TextUtils.isEmpty(error)) {
                    if (code == 0) {
                        response._error = "Connection Timeout, Please check your connection";
                    } else if (code == 401) {
                        response._error = "Your session has been expired, Please close application and open again";
                    } else {
                        response._error = "Error (" + code + ")";
                    }
                } else {
                    response._error = error;
                }
            }

            if (extras != null) {
                response._extras = extras;
            }

            response._code = code;

        }

        setResult(response);
    }

    void destroyReferences() {
        listener = null;
        extras = null;
        errorListener = null;
    }

    //----------------------------------------------------------------------------------------------

    private void printCallInfo(Call<R> call, retrofit2.Response<R> response, String errorBody) {
        if (!Logger.IS_LOG_ENABLED()) return;

        String url = "";

        try {
            url = call.request().url().toString();
        } catch (Exception e) {
        }

        String responseType = responseClass == null ? "" : responseClass.getName();
        String extrasString = "";

        if (extras != null) {
            Set<Map.Entry<String, Object>> entries = extras.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (extrasString.length() > 0) extrasString += ", ";
                extrasString += "[" + entry.getKey() + ": " + entry.getValue() + "]";
            }
        }

        if (response != null) {
            Logger.print(
                    "API:Response:",
                    "url: <" + url + ">," +
                            "\nresponseType: " + responseType + ", " +
                            "\nresponse: " + response.body() + ", " +
                            "\ncode= " + response.code() + ", " +
                            "\nmsg= " + response.message() + ", " +
                            "\nerrorBody= " + errorBody +
                            "\nextras= " + extrasString
            );
        } else {
            Logger.print(
                    "API:Response:",
                    "url: <" + url + ">," +
                            "\nresponseType: " + responseType + ", " +
                            "\nerrorBody: " + errorBody +
                            "\nextras= " + extrasString
            );
        }
    }
}