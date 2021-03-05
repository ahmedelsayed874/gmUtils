package com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders;

import androidx.annotation.Nullable;
import androidx.room.Ignore;

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
    public String _internalMessage;

    @Ignore
    private String _internalStatus = null;

    @Ignore
    public String _requestId;

    @Ignore
    public Integer _code;

    @Ignore
    public Object extra;

    @Ignore
    public byte[] rawResponse;

    //----------------------------------------------------------------------------------------------

    public void setInternalStatus(@Nullable Statuses status) {
        this._internalStatus = status.name();
    }

    @Nullable
    public final Statuses getInternalStatus() {
        if (_internalStatus == null) return null;
        else return Statuses.valueOf(_internalStatus);
    }

    public abstract Statuses getExternalStatus();

    private Statuses getFinalStatus() {
        if (_internalStatus == null) return getExternalStatus();
        else return Statuses.valueOf(_internalStatus);
    }

    public boolean isSuccess() {
        return getFinalStatus() == Statuses.Succeeded;
    }

    public boolean hasErrors() {
        return getFinalStatus() == Statuses.Error;
    }

    public void copyResponseStatus(BaseResponse otherResponse) {
        _internalMessage = otherResponse._internalMessage;
        _internalStatus = otherResponse._internalStatus;
        _requestId = otherResponse._requestId;
        _code = otherResponse._code;

    }
}
