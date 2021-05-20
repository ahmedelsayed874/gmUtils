package gmutils.net.retrofit.example.callers.dummy;

import gmutils.net.retrofit.example.data.TimeOfArea;
import gmutils.net.retrofit.example.data.TimeZones;
import gmutils.net.retrofit.example.callers._interfaces.TimeAPIs;
import gmutils.net.retrofit.OnResponseReady;

import java.util.HashMap;
import java.util.Map;

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
public class FakeTimeAPIs implements TimeAPIs {

    @Override
    public void geTimeZoneList(String ofSpecificArea, OnResponseReady<TimeZones> callback) {
        Map<String, Object> extras = new HashMap<>();
        extras.put("ofSpecificArea", ofSpecificArea);

        FakeData.run1(TimeZones.class, (d, r) -> {
            r._extras = extras;
            r.add(d.timeZones().toArray(new String[0])[0]);
            if (callback != null) callback.invoke(r);
        });
    }

    @Override
    public void getCurrentTime(String zone, OnResponseReady<TimeOfArea> callback) {
        Map<String, Object> extras = new HashMap<>();
        extras.put("zone", zone);

        FakeData.run1(TimeOfArea.class, (d, r) -> {
            r._extras = extras;
            r.setDatetime(d.timeOfArea().getDatetime());
            if (callback != null) callback.invoke(r);
        });
    }
}
