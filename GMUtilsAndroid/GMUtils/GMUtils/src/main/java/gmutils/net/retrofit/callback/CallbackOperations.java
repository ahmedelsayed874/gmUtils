package gmutils.net.retrofit.callback;

import android.text.TextUtils;
import android.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

import gmutils.logger.LoggerAbs;
import gmutils.net.retrofit.responseHolders.IResponse;
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
public final class CallbackOperations<R extends IResponse> {
    interface Listener<R> {
        void onResponseReady(R response);
    }

    private final Class<R> responseClass;
    private Listener<R> listener;
    private Map<String, Object> extras;
    private CallbackErrorHandler errorListener;
    private final LoggerAbs logger;

    private final long requestTime;
    private boolean keepOnRawResponse = false;


    public CallbackOperations(
            Request request,
            Class<R> responseClass,
            Listener<R> listener,
            LogsOptions logsOptions,
            LoggerAbs logger
    ) {
        this.responseClass = responseClass;
        this.requestTime = System.currentTimeMillis();
        this.logger = logger;// != null ? log ger : Log ger.d();

        if (logger != null && logsOptions == null) {
            logsOptions = new LogsOptions();
            /*if (!request.method().equalsIgnoreCase("get")) {
                logsOptions.setRequest Options(new LogsOptions.Request Options(
                        false,
                        true
                ));
            }*/
        }

        LogsOptions finalLogsOptions = logsOptions;
        init(
                responseClass,
                listener,
                logsOptions == null ?
                        null :
                        () -> {
                            StringBuilder sb = new StringBuilder();

                            LogsOptions.RequestOptions requestOptions = finalLogsOptions.getRequestOptions();
                            /*if (request Options == null)
                                request Options = new LogsOptions.Request Options(
                                        false,
                                        false
                                );*/

                            if (requestOptions == null || requestOptions.allowPrintRequestParameters()) {
                                sb.append(request.toString());
                            }
                            //
                            else {
                                sb.append("Request{method=");
                                sb.append(request.method());
                                sb.append(", url=");
                                sb.append(request.url());

                                if (requestOptions.allowPrintHeaders()) {
                                    if (request.headers().size() != 0) {
                                        sb.append(", headers=[");
                                        int i = 0;
                                        for (String name : request.headers().names()) {
                                            i++;
                                            if (i > 1) sb.append(", ");
                                            sb.append(name);
                                            sb.append(':');
                                            sb.append(request.headers().values(name));
                                        }
                                        sb.append(']');
                                    }
                                }

                                sb.append('}');
                            }

                            if (finalLogsOptions.getExtraInfo() != null) {
                                sb.append("\nExtraInfo:");
                                sb.append(finalLogsOptions.getExtraInfo().invoke());
                            }

                            return sb;
                        },
                logsOptions == null ?
                        null :
                        logsOptions.getReplacements()
        );
    }

    public CallbackOperations(
            Class<R> responseClass,
            Listener<R> listener,
            LoggerAbs.ContentGetter requestInfo,
            LogsOptions.Replacements replacedTextsInLog,
            LoggerAbs logger
    ) {
        this.responseClass = responseClass;
        this.requestTime = System.currentTimeMillis();
        this.logger = logger; //!= null ? log ger : Log ger.d();

        init(responseClass, listener, requestInfo, replacedTextsInLog);
    }

    private void init(
            Class<R> responseClass,
            Listener<R> listener,
            LoggerAbs.ContentGetter requestInfo,
            LogsOptions.Replacements replacedTextsInLog
    ) {
        this.listener = listener;

        if (requestInfo != null && this.logger != null) {
            this.logger.print(() -> "API:Request:", () -> {
                String txt = "";
                try {
                    txt += requestInfo.getContent().toString();

                    if (replacedTextsInLog != null && replacedTextsInLog.getReplacements() != null) {
                        for (Pair<String, String> replacement : replacedTextsInLog.getReplacements()) {
                            txt = txt.replace(replacement.first, replacement.second);
                        }
                    }
                } catch (Exception e) {
                    txt += "\n<[EXCEPTION:: " + e.getMessage() + "]>";
                }

                return txt;
            });
        }

        try {
            responseClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setKeepOnRawResponse(boolean keepOnRawResponse) {
        this.keepOnRawResponse = keepOnRawResponse;
    }

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

            String rawResponse = null;
            if (keepOnRawResponse) {
                try {
                    rawResponse = response.raw().body().string();
                } catch (Exception e) {
                }
            }

            if (body != null) {
                if (extras != null) {
                    body.setExtras(extras);
                }

                body.setStatusCode(response.code());

                setResult(body, rawResponse, headers);
            }
            //
            else {
                setError(rawResponse, "", false, response.code(), headers);
            }
        }
        //
        else {
            setError(null, error, false, response.code(), headers);
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

        setError(null, t.getClass().getName() + ":\n" + t.getMessage(), true, 0, null);
    }

    private void setError(String rawResponse, String error, boolean exception, int code, Map<String, List<String>> headers) {
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
                response.setCallbackStatus(IResponse.Status.ConnectionFailed);
            else
                response.setCallbackStatus(IResponse.Status.Error);
        }

        if (errorListener != null) {
            response.setError(errorListener.getErrorMessage(code, error));

        } else {
            if (TextUtils.isEmpty(error)) {
                if (code == 0) {
                    response.setError("Connection Timeout, Please check your connection");
                } else if (code == 401) {
                    response.setError("Your session has been expired, Please close application and open again");
                } else {
                    response.setError("Error (" + code + ")");
                }
            } else {
                response.setError(error);
            }
        }

        if (extras != null) {
            response.setExtras(extras);
        }

        response.setIsErrorDueException(exception);
        if (error.contains("Trust anchor for certification path not found")) {
            response.setIsSSLCertificateRequired(true);
        }

        response.setStatusCode(code);
        response.setHeaders(headers);

        setResult(response, rawResponse, headers);
    }

    private void setResult(R result, String rawResponse, Map<String, List<String>> headers) {
        if (this.logger != null && this.logger.getLogConfigs().isLogEnabled()) {
            this.logger.print(
                    () -> "EXTRA_INFO:",
                    () -> "[callbackStatus=" + result.getCallbackStatus() +
                            ", responseStatus=" + result.getResponseStatus() + "]"
            );
        }

        result.setRawResponse(rawResponse);
        result.setHeaders(headers);
        result.setRequestTime(requestTime);
        result.setResponseTime(System.currentTimeMillis());
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
        if (this.logger == null || !this.logger.getLogConfigs().isLogEnabled()) return;

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