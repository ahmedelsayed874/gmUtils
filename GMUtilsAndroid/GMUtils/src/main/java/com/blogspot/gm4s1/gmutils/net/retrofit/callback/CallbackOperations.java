package com.blogspot.gm4s1.gmutils.net.retrofit.callback;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.Response;

import java.io.IOException;

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

    private final Class<R> TClass;
    private String requestId = null;
    private Object extra;
    private Listener<R> listener;
    private CallbackErrorHandler errorListener;
    private boolean includeRawResponse = false;
    private boolean printRawResponse = false;

    public CallbackOperations(Class<R> TClass, String requestDetails, String requestId, Listener<R> listener) {
        this.TClass = TClass;
        this.requestId = requestId;
        this.listener = listener;

        try {
            Logger.print("API:Request:", requestDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void setExtra(Object extra) {
        this.extra = extra;
    }

    void setErrorListener(CallbackErrorHandler errorListener) {
        this.errorListener = errorListener;
    }

    public void includeRawResponse() {
        this.includeRawResponse = true;
    }

    public void printRawResponse() {
        this.printRawResponse = true;
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
                if (requestId != null) {
                    body._requestId = requestId;
                }

                if (extra != null) {
                    body.extra = extra;
                }

                body._code = response.code();

                if (includeRawResponse) {
                    try {
                        body.rawResponse = response.raw().body().bytes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                setResult(response.body());

            } else {
                setError(response.body()._internalMessage, response.code());
            }
        } else {

            setError(error, response.code());
        }
    }

    void onFailure(Call<R> call, Throwable t) {
        printCallInfo(call, null, "FAILURE");
        Logger.print(t);

        setError(t.getMessage(), 0);
    }

    private void setResult(R result) {
        listener.onResponseReady(result);
        listener = null;
        errorListener = null;
    }

    private void setError(String error, int code) {
        R response = null;

        try {
            response = TClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            //error += "\n--------\n" + e.getMessage();
        }

        if (response != null) {
            if (errorListener != null) {
                response.setInternalStatus(errorListener.getInternalStatus(code));
            } else {
                if (code == 0)
                    response.setInternalStatus(Response.Statuses.ConnectionFailed);
                else
                    response.setInternalStatus(Response.Statuses.Error);
            }

            if (errorListener != null) {
                response._internalMessage = errorListener.getInternalMessage(code);

            } else {
                if (code == 0) {
                    response._internalMessage = "Connection Timeout, Please check your connection";
                } else if (code == 401) {
                    response._internalMessage = "Your session has been expired, Please close application and open again";
                } else if (code == 404) {
                    response._internalMessage = "Error (404)";
                } else {
                    response._internalMessage = error;
                }
            }

            if (requestId != null) {
                response._requestId = requestId;
            }

            if (extra != null) {
                response.extra = extra;
            }

            response._code = code;
        }

        setResult(response);
    }

    //----------------------------------------------------------------------------------------------

    private void printCallInfo(Call<R> call, retrofit2.Response<R> response, String errorBody) {
        if (!Logger.IS_LOG_ENABLED()) return;

        String url = "";

        try {
            url = call.request().url().toString();
        } catch (Exception e){}

        if (response != null) {
            String resStr;
            if (printRawResponse) {
                try {
                    resStr = "response(RAW): " + response.raw().body().string() + "\n"
                            + "response(NOW): " + response.body();
                } catch (Exception e) {
                    resStr = "response(RAW): " + "NULL\n"
                            + "response(NOW): " + response.body();
                }
            } else {
                resStr = "response: " + response.body();
            }

            Logger.print(
                    "API:Response:",
                    "url: <" + url + ">, \n" +
                            resStr + ", " +
                            "\ncode= " + response.code() + ", \n" +
                            "msg= " + response.message() + ", " +
                            "\nerrorBody= " + errorBody
            );
        } else {
            Logger.print(
                    "API:Response:",
                    "url: <" + url + ">, \n" +
                            "response: " + errorBody
            );
        }
    }
}