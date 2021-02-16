package com.blogspot.gm4s1.gmutils.net.volley.utils;

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
public class Result<T> {
    private boolean isSuccess;
    private T result;
    private boolean connectionError = false;
    private Object extraData;


    public void setResult(T result) {
        setResult(result, (result != null));
    }

    public void setResult(T result, boolean isSuccess) {
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public void setConnectionError(boolean connectionError) {
        this.connectionError = connectionError;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }


    public boolean isSuccess() {
        return isSuccess;
    }

    public T getResult() {
        return result;
    }

    public Object getExtraData() {
        return extraData;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

}
