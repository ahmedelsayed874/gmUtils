package gmutils.net.retrofit.responseHolders;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.DateOp;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

/*
    it's suitable for response of this format
    (This's a standard response: status and message are fixed keys)
    {
        "status" : "success",
        "message" : "",
        "key1" : "value1",
        "key2" : "value2",
        "key3" : "value3",
        "key4" : "value4",
        "key5" : "value5"
    }

    I suppose to inherit it this class (see the example)::

    abstract class MyBaseResponse extends BaseResponse {
        String status;
        String message;

        @Override
        public Statuses getInternalStatus() {
            //return "succeeded".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or
            //return "true".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or
            return "200".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or whatever you agree with your team
        }
    }
    class X extends MyBaseResponse {
        String key1; //= value1
        String key2; //= value2
        String key3; //= value3
        String key4; //= value4
        String key5; //= value5
    }
    class Example {
        void main() {
            X x = new X();
            print(x.key1);
            print(x.key2);
        }
    }
 */

public abstract class BaseResponse implements IResponse {

    @Expose(serialize = false, deserialize = false)
    @Ignore
    private String _callbackStatus = null;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Integer _code;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Map<String, List<String>> _headers;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public String _error;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Boolean _isErrorDueException;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Boolean _isSSLCertificateRequired;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Map<String, Object> _extras;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Long _requestTime;

    @Expose(serialize = false, deserialize = false)
    @Ignore
    public Long _responseTime;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer getStatusCode() {
        return _code;
    }

    @Override
    public void setStatusCode(int code) {
        _code = code;
    }

    //-------------------------------------

    @Override
    public Map<String, List<String>> getHeaders() {
        return _headers;
    }

    @Override
    public void setHeaders(Map<String, List<String>> headers) {
        _headers = headers;
    }

    //-------------------------------------

    @Override
    public void setRequestTime(long time) {
        _requestTime = time;
    }

    @Override
    public Long getRequestTime() {
        return _requestTime;
    }

    //-------------------------------------

    @Override
    public void setResponseTime(long time) {
        _responseTime = time;
    }

    @Override
    public Long getResponseTime() {
        return _responseTime;
    }

    public String requestInterval() {
        try {
            int[] DHMSMs = DateOp.timeComponentFromTimeMillis(_responseTime - _requestTime);
            return DHMSMs[2] + "M, " + DHMSMs[3] + "S, " + DHMSMs[4] + "Ms";
        } catch (Exception e) {
            return "";
        }
    }

    //-------------------------------------

    @Override
    public void setExtras(Map<String, Object> extras) {
        _extras = extras;
    }

    @Override
    public Map<String, Object> getExtras() {
        return _extras;
    }

    //-------------------------------------

    @Override
    public void setCallbackStatus(@Nullable Status status) {
        this._callbackStatus = status.name();
    }

    @Override
    @Nullable
    public final Status getCallbackStatus() {
        if (_callbackStatus == null) return null;
        else return Status.valueOf(_callbackStatus);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isSuccess() {
        return getResponseStatus() == Status.Succeeded;
    }

    public boolean isConnectionFailed() {
        return getResponseStatus() == Status.ConnectionFailed;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setError(String error) {
        _error = error;
    }

    @Override
    public String getError() {
        return _error;
    }

    //-----------------------------

    @Override
    public void setIsErrorDueException(boolean dueException) {
        _isErrorDueException = dueException;
    }

    @Override
    public Boolean isErrorDueException() {
        return _isErrorDueException;
    }

    //-----------------------------

    @Override
    public void setIsSSLCertificateRequired(boolean required) {
        _isSSLCertificateRequired = required;
    }

    @Override
    public Boolean isSSLCertificateRequired() {
        return _isSSLCertificateRequired;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void copyFrom(@NotNull BaseResponse otherResponse) {
        _callbackStatus = otherResponse._callbackStatus;
        _code = otherResponse._code;
        _headers = otherResponse._headers == null ? null : new HashMap<>(otherResponse._headers);
        _error = otherResponse._error;
        _isErrorDueException = otherResponse._isErrorDueException;
        _isSSLCertificateRequired = otherResponse._isSSLCertificateRequired;
        _extras = otherResponse._extras == null ? null : new HashMap<>(otherResponse._extras);

        _requestTime = otherResponse._requestTime;
        _responseTime = otherResponse._responseTime;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuffer header = new StringBuffer();
        if (_headers == null) {
            header.append("NULL");
        }
        //
        else {
            header.append("{");
            for (Map.Entry<String, List<String>> entry : _headers.entrySet()) {
                header.append(entry.getKey()).append(": [");
                if (entry.getValue() != null) {
                    for (String value : entry.getValue()) {
                        header.append(value).append(",");
                    }
                }
                header.append("],");
            }
            header.append("}");
        }

        return "BaseResponse{" + "\n" +
                "callbackStatus='" + _callbackStatus + "',\n" +
                "code=" + _code + ",\n" +
                "error='" + _error + '\'' + ",\n" +
                "isErrorDueException='" + _isErrorDueException + '\'' + ",\n" +
                "isSLLCertificateRequired='" + _isSSLCertificateRequired + '\'' + ",\n" +
                "extras=" + _extras + ",\n" +
                "requestTime=" + _requestTime + ",\n" +
                "responseTime=" + _responseTime + ",\n" +
                "requestInterval=" + requestInterval() + ",\n" +
                "header=" + header + "\n" +
                '}';
    }
}
