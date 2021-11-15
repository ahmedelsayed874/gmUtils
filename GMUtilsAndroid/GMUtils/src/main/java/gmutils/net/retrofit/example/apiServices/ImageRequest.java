package gmutils.net.retrofit.example.apiServices;

import gmutils.net.retrofit.example.data.ImageResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public interface ImageRequest {

    @Multipart
    @POST("post")
    Call<ImageResponse> post(
            @Part("text") RequestBody text,
            @Part MultipartBody.Part image
    );

}
