package gmutils.net.retrofit.responseHolders;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public abstract class BaseResponse {
    public enum Statuses {Succeeded, Error, ConnectionFailed}

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

    //----------------------------------------------------------------------------------------------

    public void setCallbackStatus(@Nullable Statuses status) {
        this._callbackStatus = status.name();
    }

    @Nullable
    public final Statuses getCallbackStatus() {
        if (_callbackStatus == null) return null;
        else return Statuses.valueOf(_callbackStatus);
    }

    public abstract Statuses getResponseStatus();

    public boolean isSuccess() {
        return getResponseStatus() == Statuses.Succeeded;
    }

    public boolean hasErrors() {
        return getResponseStatus() == Statuses.Error;
    }

    public void cloneResponseStatus(@NotNull BaseResponse otherResponse) {
        _error = otherResponse._error;
        _callbackStatus = otherResponse._callbackStatus;
        _code = otherResponse._code;
        _headers = otherResponse._headers == null ? null : new HashMap<>(otherResponse._headers);
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
        } else {
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
                "header=" + header + "\n" +
                '}';
    }
}
