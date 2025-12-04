package gmutils.net.retrofit.responseHolders;

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

public interface IResponse {
    public enum Status {Succeeded, Error, ConnectionFailed}

    void setStatusCode(int code);
    Integer getStatusCode();

    //-----------------------------

    void setHeaders(Map<String, List<String>> headers);
    Map<String, List<String>> getHeaders();

    //-----------------------------

    void setRequestTime(long time);
    Long getRequestTime();

    //-----------------------------

    void setResponseTime(long time);
    Long getResponseTime();

    //-----------------------------

    void setExtras(Map<String, Object> extras);
    Map<String, Object> getExtras();

    //-----------------------------------------------------------------

    void setCallbackStatus(Status status);

    Status getCallbackStatus();

    //-----------------------------

    Status getResponseStatus();

    //-----------------------------------------------------------------

    void setError(String error);
    String getError();

    //-----------------------------

    void setIsErrorDueException(boolean dueException);
    Boolean isErrorDueException();

    //-----------------------------

    void setIsSSLCertificateRequired(boolean required);
    Boolean isSSLCertificateRequired();

}
