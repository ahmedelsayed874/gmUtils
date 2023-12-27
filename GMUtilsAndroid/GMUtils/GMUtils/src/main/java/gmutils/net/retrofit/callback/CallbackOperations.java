package gmutils.net.retrofit.callback;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import gmutils.logger.Logger;
import gmutils.logger.LoggerAbs;
import gmutils.net.retrofit.responseHolders.BaseObjectResponse;
import gmutils.net.retrofit.responseHolders.BaseResponse;
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
public final class CallbackOperations<R extends BaseResponse> {
    interface Listener<R> {
        void onResponseReady(R response);
    }

    private final Class<R> responseClass;
    private Listener<R> listener;
    private Map<String, Object> extras;
    private CallbackErrorHandler errorListener;
    private LoggerAbs logger;

    private final long requestTime;


    public CallbackOperations(
            Request request,
            Class<R> responseClass,
            Listener<R> listener,
            String[] excludedTextsFromLog,
            LoggerAbs logger
    ) {
        this(request.toString(), responseClass, listener, excludedTextsFromLog, logger);
    }

    public CallbackOperations(
            String requestInfo,
            Class<R> responseClass,
            Listener<R> listener,
            String[] excludedTextsFromLog,
            LoggerAbs logger
    ) {
        this.responseClass = responseClass;
        this.listener = listener;
        this.logger = logger != null ? logger : Logger.d();

        this.requestTime = System.currentTimeMillis();

        if (requestInfo != null && !"".equals(requestInfo))
            this.logger.print(() -> "API:Request:", () -> {
                String txt = requestInfo;
                if (excludedTextsFromLog != null) {
                    for (String t : excludedTextsFromLog) {
                        txt = txt.replaceAll(t, "##########");
                    }
                }
                return txt;
            });
        try {
            responseClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public void setErrorListener(CallbackErrorHandler errorListener) {
        this.errorListener = errorListener;
    }

    //----------------------------------------------------------------------------------------------

    public void onResponse(Call<R> call, retrofit2.Response<R> response) {
        String error = "";
        try {
            error = response.errorBody().string();
        } catch (Exception e) {
            error = response.message() + "\nCode: " + response.code();
        }

        Map<String, List<String>> headers;
        try {
            headers = response.headers().toMultimap();
        } catch (Exception e) {
            headers = null;
        }

        printCallInfo(call, response, error);

        if (response.isSuccessful()) {
            R body = response.body();
            if (body != null) {
                if (extras != null) {
                    body._extras = extras;
                }

                body._code = response.code();

                setResult(body, headers);

            } else {
                setError("", false, response.code(), headers);
            }
        } else {
            setError(error, false, response.code(), headers);
        }
    }

    public void onFailure(Call<R> call, Throwable t) {
        printCallInfo(
                call,
                null,
                "Exception[" +
                        t.getClass().getName() + ":: " + t.getMessage() +
                        "]"
        );

        setError(t.getClass().getName() + ":\n" + t.getMessage(), true, 0, null);
    }

    private void setError(String error, boolean exception, int code, Map<String, List<String>> headers) {
        R response = null;

        try {
            response = responseClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (errorListener != null) {
            response.setCallbackStatus(errorListener.getInternalStatus(code, error));
        } else {
            if (code == 0)
                response.setCallbackStatus(BaseObjectResponse.Statuses.ConnectionFailed);
            else
                response.setCallbackStatus(BaseObjectResponse.Statuses.Error);
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

        response._isErrorDueException = exception;
        if (error.contains("Trust anchor for certification path not found")) {
            response._isSSLCertificateRequired = true;
        }

        response._code = code;
        response._headers = headers;

        setResult(response, headers);
    }

    private void setResult(R result, Map<String, List<String>> headers) {
        if (this.logger.getLogConfigs().isLogEnabled()) {
            this.logger.print(
                    () -> "EXTRA_INFO:",
                    () -> "[callbackStatus=" + result.getCallbackStatus() +
                            ", responseStatus=" + result.getResponseStatus() + "]"
            );
        }

        result._headers = headers;
        result._requestTime = requestTime;
        result._responseTime = System.currentTimeMillis();
        if (listener != null) listener.onResponseReady(result);

        destroyReferences();
    }

    public void destroyReferences() {
        listener = null;
        extras = null;
        errorListener = null;
    }

    //----------------------------------------------------------------------------------------------

    private void printCallInfo(Call<R> call, retrofit2.Response<R> response, String errorBody) {
        if (!this.logger.getLogConfigs().isLogEnabled()) return;

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
            String finalUrl = url;
            String finalExtrasString = extrasString;
            this.logger.print(
                    () -> "API:Response:",
                    () -> "url: <" + finalUrl + ">," +
                            "\nresponseType: " + responseType + ", " +
                            "\nresponse: " + response.body() + ", " +
                            "\ncode= " + response.code() + ", " +
                            "\nmsg= " + response.message() + ", " +
                            "\nerrorBody= " + errorBody +
                            "\nextras= " + finalExtrasString
            );
        } else {
            String finalUrl1 = url;
            String finalExtrasString1 = extrasString;
            this.logger.print(
                    () -> "API:Response:",
                    () -> "url: <" + finalUrl1 + ">," +
                            "\nresponseType: " + responseType + ", " +
                            "\nerrorBody: " + errorBody +
                            "\nextras= " + finalExtrasString1
            );
        }
    }
}