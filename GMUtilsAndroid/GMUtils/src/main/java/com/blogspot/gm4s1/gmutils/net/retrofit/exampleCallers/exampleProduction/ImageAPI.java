package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers.exampleProduction;

import android.graphics.Bitmap;

import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.APIConstants;
import com.blogspot.gm4s1.gmutils.utils.ImageUtils;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.ImageRequest;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.ImageResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.Callback;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.RetrofitService;

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

public class ImageAPI implements com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces.ImageAPI {
    public static String URL = "BASE URL OF API";

    public void post(String text, Bitmap image, OnResponseReady<Object> callback) {
        ImageRequest request = RetrofitService.create(URL, ImageRequest.class);
        Call<ImageResponse> call = request.post(
                APIConstants.TOKEN(),
                RequestBody.create(MediaType.parse("text/plain"), text),
                ImageUtils.createInstance().createRetrofitMultipartBodyForImage(image, "image")
        );

        call.enqueue(new Callback<>(
                call.request().url().toString(),
                ImageResponse.class,
                callback
        ));
    }

}
