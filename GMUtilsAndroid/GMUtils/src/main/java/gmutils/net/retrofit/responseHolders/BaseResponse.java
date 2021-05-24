package gmutils.net.retrofit.responseHolders;

import androidx.annotation.Nullable;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
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
    public enum Statuses { Succeeded, Error, ConnectionFailed }

    @Ignore
    private String _callbackStatus = null;

    @Ignore
    public Integer _code;

    @Ignore
    public String _error;

    @Ignore
    public Map<String, Object> _extras;

    @Ignore
    public long _requestTime;

    @Ignore
    public long _responseTime;

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
        _extras = otherResponse._extras;

        _requestTime = otherResponse._requestTime;
        _responseTime = otherResponse._responseTime;
    }

}
