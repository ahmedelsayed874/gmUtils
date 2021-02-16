package com.blogspot.gm4s1.gmutils.net.retrofit.callback;

import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.Response;

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
public interface CallbackErrorHandler {
    Response.Statuses getInternalStatus(int code);
    String getInternalMessage(int code);
}