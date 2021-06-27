package gmutils.net.retrofit.example.callers._interfaces;

import android.graphics.Bitmap;

import gmutils.net.retrofit.listeners.OnResponseReady2o;
import gmutils.net.retrofit.example.data.ImageResponse;

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
public interface ImageAPIs {

    void post(String text, Bitmap image, OnResponseReady2o<Object, ImageResponse> callback);

}
