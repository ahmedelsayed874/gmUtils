package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers.exampleProduction;

import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeAPIsRequests;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.Callback2;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.OnResponseReady2;
import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.RetrofitService;

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

public class TimeAPIs implements com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers._exampleInterfaces.TimeAPIs {
    public static final String baseURL = "http://worldtimeapi.org/api/";

    @Override
    public void geTimeZoneList(String ofSpecificArea, OnResponseReady2<TimeZones> callback) {
        TimeAPIsRequests request = RetrofitService.create(baseURL, TimeAPIsRequests.class);
        Call<TimeZones> call = request.geTimeZoneList(ofSpecificArea);
        call.enqueue(new Callback2<>(
                call.request().url().toString(),
                TimeZones.class,
                callback,
                "" + ofSpecificArea //it's optional
        ));
    }

    @Override
    public void getCurrentTime(String zone, OnResponseReady2<TimeOfArea> callback) {
        TimeAPIsRequests request = RetrofitService.create(baseURL, TimeAPIsRequests.class);
        Call<TimeOfArea> call = request.getCurrentTime(zone);
        call.enqueue(new Callback2<>(
                call.request().url().toString(),
                TimeOfArea.class,
                callback,
                "" + zone //it's optional
        ));
    }
}
