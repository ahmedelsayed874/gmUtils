package gmutils.net.retrofit.responseHolders;

import android.util.Log;
import gmutils.net.retrofit.RetrofitService;

public final class StringResponse extends BaseResponse {

    private final String text;

    public StringResponse() {
        this(null);
    }

    public StringResponse(String text) {
        this.text = text;

        Log.d(StringResponse.class.getSimpleName(), "Please make sure of enable " +
                "String Response Converter when calling " +
                "\"" + RetrofitService.class.getName() + "\" through \"" + RetrofitService.Parameters.class.getName() + "\"");
    }

    public String getText() {
        return text;
    }

    @Override
    public Statuses getResponseStatus() {
        Statuses callbackStatus = getCallbackStatus();
        if (callbackStatus == null) {
            return null != text ? Statuses.Succeeded : Statuses.Error;

        } else return callbackStatus;
    }

    @Override
    public String toString() {
        return "StringResponse{" +
                "text='" + text + '\'' +
                ", _code=" + _code +
                ", _error='" + _error + '\'' +
                ", _extras=" + _extras +
                ", _requestTime=" + _requestTime +
                ", _responseTime=" + _responseTime +
                '}';
    }
}
