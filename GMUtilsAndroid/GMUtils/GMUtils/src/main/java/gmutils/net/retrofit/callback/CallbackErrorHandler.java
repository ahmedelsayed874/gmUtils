package gmutils.net.retrofit.callback;

import gmutils.net.retrofit.responseHolders.IResponse;

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
public interface CallbackErrorHandler {
    IResponse.Status getInternalStatus(int code, String error);
    String getErrorMessage(int code, String error);
}