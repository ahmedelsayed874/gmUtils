package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces;

import android.graphics.Bitmap;

import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady2;

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
public interface ImageAPI {

    void post(String text, Bitmap image, OnResponseReady2<Object> callback);

}
