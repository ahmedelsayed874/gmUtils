package gmutils.net.retrofit.responseHolders;

import androidx.annotation.NonNull;

public final class StringResponse extends BaseResponse {

    private String text;

    public StringResponse() {
        this(null);
    }

    public StringResponse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public Status getResponseStatus() {
        Status callbackStatus = getCallbackStatus();
        if (callbackStatus == null) {
            return null != text ? Status.Succeeded : Status.Error;

        } else {
            return callbackStatus;
        }
    }

    @Override
    public void copyFrom(@NonNull BaseResponse otherResponse) {
        super.copyFrom(otherResponse);
        if (otherResponse instanceof StringResponse) {
            this.text = ((StringResponse) otherResponse).text;
        }
    }

    @Override
    public String toString() {
        return "StringResponse{" + "\n" +
                "text='" + text + '\'' + ",\n" +
                "code=" + _code + ",\n" +
                "error='" + _error + '\'' + ",\n" +
                "extras=" + _extras + ",\n" +
                "requestTime=" + _requestTime + ",\n" +
                "responseTime=" + _responseTime + "\n" +
                '}';
    }
}
