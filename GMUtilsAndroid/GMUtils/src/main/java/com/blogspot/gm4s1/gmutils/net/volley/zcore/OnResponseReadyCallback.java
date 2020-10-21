package com.blogspot.gm4s1.gmutils.net.volley.zcore;

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

public interface OnResponseReadyCallback<T> {
    int StatusCode_NotConnected = 0;
    int StatusCode_Timeout = -1;

    void onResponseFetched(T response);

    void onResponseFetchedFailed(String msg, String response, int statusCode);
}
