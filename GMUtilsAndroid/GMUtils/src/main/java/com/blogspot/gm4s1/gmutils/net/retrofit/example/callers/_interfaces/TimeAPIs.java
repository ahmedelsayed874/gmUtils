package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers._interfaces;

import com.blogspot.gm4s1.gmutils.net.retrofit.example.apiServices.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.apiServices.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady;

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
public interface TimeAPIs {

    void geTimeZoneList(String ofSpecificArea, OnResponseReady<TimeZones> callback);

    void getCurrentTime(String zone, OnResponseReady<TimeOfArea> callback);
}
