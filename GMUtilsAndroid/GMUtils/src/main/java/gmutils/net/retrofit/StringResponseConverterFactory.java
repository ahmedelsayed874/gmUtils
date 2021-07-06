package gmutils.net.retrofit;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import gmutils.net.retrofit.responseHolders.StringResponse;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * this class depend on {@link StringResponse}
 */
public class StringResponseConverterFactory extends Converter.Factory {

    public StringResponseConverterFactory() {
        super();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (StringResponse.class.equals(type)) {
            return new Converter<ResponseBody, StringResponse>() {
                @Nullable
                @Override
                public StringResponse convert(ResponseBody value) throws IOException {
                    String text = value.string();
                    return new StringResponse(text);
                }
            };
        }
        return null;
    }

    /*@Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {
                @Nullable
                @Override
                public RequestBody convert(String value) throws IOException {
                    return TextUtils.isEmpty(value) ? null : RequestBody.create(value, MediaType.parse("text/plain"));
                }
            };
        }
        return null;
    }*/

    /*@Nullable
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }*/
}
