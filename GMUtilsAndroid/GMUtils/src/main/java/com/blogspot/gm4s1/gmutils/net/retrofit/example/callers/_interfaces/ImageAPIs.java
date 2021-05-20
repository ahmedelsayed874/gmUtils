package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers._interfaces;

import android.graphics.Bitmap;

import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady2;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.ImageResponse;
import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseObjectResponse;

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

    void post(String text, Bitmap image, OnResponseReady2<Object, ImageResponse> callback);

}
