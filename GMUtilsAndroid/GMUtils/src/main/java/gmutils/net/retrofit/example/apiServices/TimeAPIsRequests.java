package gmutils.net.retrofit.example.apiServices;

import gmutils.net.retrofit.example.data.TimeOfArea;
import gmutils.net.retrofit.example.data.TimeZones;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
public interface TimeAPIsRequests {

    @GET("timezone/{ofSpecificArea}")
    Call<TimeZones> geTimeZoneList(
            @Path("ofSpecificArea") String ofSpecificArea
    );

    @GET("timezone/{zone}")
    Call<TimeOfArea> getCurrentTime(
            @Path("zone") String zone
    );

}
