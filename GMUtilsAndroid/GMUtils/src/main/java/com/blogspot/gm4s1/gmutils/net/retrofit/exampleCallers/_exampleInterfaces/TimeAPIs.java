package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces;

import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeZones;
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
public interface TimeAPIs {

    public void geTimeZoneList(String ofSpecificArea, OnResponseReady2<TimeZones> callback);

    public void getCurrentTime(String zone, OnResponseReady2<TimeOfArea> callback);
}
