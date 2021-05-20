package gmutils.net.volley.example.URLs;

import android.graphics.Bitmap;

import gmutils.net.volley.ApiURL;

/*
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
public class UploadImageURL extends ApiURL.postURL {
    public static String baseURL = "BASE URL OF API";

    public UploadImageURL(String text, Bitmap bitmap) {
        super(new ApiURL.Parameters() {
        });

        getParams()
                .put("text", text)
                .put("image", bitmap);

    }

    @Override
    public String getEndPointURL() {
        return baseURL + "upload";
    }
}
