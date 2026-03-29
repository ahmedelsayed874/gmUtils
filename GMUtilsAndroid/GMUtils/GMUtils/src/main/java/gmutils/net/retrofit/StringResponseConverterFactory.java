package gmutils.net.retrofit;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import gmutils.listeners.ResultCallback;
import gmutils.logger.LoggerAbs;
import gmutils.net.retrofit.responseHolders.StringResponse;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * this class depend on {@link StringResponse}
 */
public class StringResponseConverterFactory extends Converter.Factory {
    private ResultCallback<String> responseListener;
    private LoggerAbs logger;

    public StringResponseConverterFactory(ResultCallback<String> responseListener, LoggerAbs logger) {
        super();
        this.responseListener = responseListener;
        this.logger = logger;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (StringResponse.class.equals(type)) {
            return new Converter<ResponseBody, StringResponse>() {
                @Override
                public StringResponse convert(ResponseBody value) throws IOException {
                    String text = value.string();
                    if (logger != null) logger.print(() -> "RawResponse", () -> text);
                    if (responseListener != null) responseListener.invoke(text);
                    return new StringResponse(text);
                }
            };
        }
        //
        else if (String.class.equals(type)) {
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    String res = value.string();
                    if (logger != null) logger.print(() -> "RawResponse", () -> res);
                    if (responseListener != null) responseListener.invoke(res);
                    return res;
                }
            };
        }

        return null;
    }
}
