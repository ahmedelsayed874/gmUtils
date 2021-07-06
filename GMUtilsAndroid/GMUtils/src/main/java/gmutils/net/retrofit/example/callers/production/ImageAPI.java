package gmutils.net.retrofit.example.callers.production;

import android.graphics.Bitmap;

import gmutils.images.ImageUtils;
import gmutils.net.retrofit.RetrofitService;
import gmutils.net.retrofit.callback.Callback2o;
import gmutils.net.retrofit.example.apiServices.ImageRequest;
import gmutils.net.retrofit.example.callers._interfaces.ImageAPIs;
import gmutils.net.retrofit.example.data.ImageResponse;
import gmutils.net.retrofit.listeners.OnResponseReady2o;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

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

public class ImageAPI implements ImageAPIs {
    public static String URL = "BASE URL OF API";

    public void post(String text, Bitmap image, OnResponseReady2o<Object, ImageResponse> callback) {
        ImageRequest request = RetrofitService.create(URL, ImageRequest.class);

        Call<ImageResponse> call = request.post(
                RequestBody.create(MediaType.parse("text/plain"), text),
                ImageUtils.createInstance().createRetrofitMultipartBodyForImage(image, "image")
        );

        Callback2o<Object, ImageResponse> callback2 = new Callback2o<>(
                call.request(),
                ImageResponse.class,
                callback
        );

        call.enqueue(callback2);
    }

}
