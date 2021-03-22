package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers.production;

import com.blogspot.gm4s1.gmutils.net.retrofit.example.apiServices.TimeAPIsRequests;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.callback.Callback;
import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady;
import com.blogspot.gm4s1.gmutils.net.retrofit.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

public class TimeAPIs implements com.blogspot.gm4s1.gmutils.net.retrofit.example.callers._interfaces.TimeAPIs {
    public static final String baseURL = "http://worldtimeapi.org/api/";

    @Override
    public void geTimeZoneList(String ofSpecificArea, OnResponseReady<TimeZones> callback) {
        TimeAPIsRequests request = RetrofitService.create(baseURL, TimeAPIsRequests.class);
        Call<TimeZones> call = request.geTimeZoneList(ofSpecificArea);

        Map<String, Object> extras= new HashMap<>();
        extras.put("ofSpecificArea", ofSpecificArea);

        call.enqueue(new Callback<>(
                call.request(),
                TimeZones.class,
                callback
        ).setExtras(extras));
    }

    @Override
    public void getCurrentTime(String zone, OnResponseReady<TimeOfArea> callback) {
        TimeAPIsRequests request = RetrofitService.create(baseURL, TimeAPIsRequests.class);
        Call<TimeOfArea> call = request.getCurrentTime(zone);

        Map<String, Object> extras= new HashMap<>();
        extras.put("zone", zone);

        call.enqueue(new Callback<>(
                call.request(),
                TimeOfArea.class,
                callback
        ).setExtras(extras));
    }
}
