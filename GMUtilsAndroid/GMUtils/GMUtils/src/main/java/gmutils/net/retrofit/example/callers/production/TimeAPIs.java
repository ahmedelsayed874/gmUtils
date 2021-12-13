package gmutils.net.retrofit.example.callers.production;

import java.util.HashMap;
import java.util.Map;

import gmutils.net.retrofit.RetrofitService;
import gmutils.net.retrofit.callback.Callback;
import gmutils.net.retrofit.example.apiServices.TimeAPIsRequests;
import gmutils.net.retrofit.example.data.TimeOfArea;
import gmutils.net.retrofit.example.data.TimeZones;
import gmutils.net.retrofit.listeners.OnResponseReady;
import retrofit2.Call;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

public class TimeAPIs implements gmutils.net.retrofit.example.callers._interfaces.TimeAPIs {
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
