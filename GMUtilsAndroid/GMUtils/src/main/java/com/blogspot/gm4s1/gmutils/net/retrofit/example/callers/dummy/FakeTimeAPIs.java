package com.blogspot.gm4s1.gmutils.net.retrofit.example.callers.dummy;

import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.data.TimeZones;
import com.blogspot.gm4s1.gmutils.net.retrofit.example.callers._interfaces.TimeAPIs;
import com.blogspot.gm4s1.gmutils.net.retrofit.OnResponseReady;

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
        FakeData.run1(TimeZones.class, (d, r) -> {
            r._requestId = ofSpecificArea;
            r.add(d.timeZones().toArray(new String[0])[0]);
            if (callback != null) callback.invoke(r);
        });
    }

    @Override
    public void getCurrentTime(String zone, OnResponseReady<TimeOfArea> callback) {
        FakeData.run1(TimeOfArea.class, (d, r) -> {
            r._requestId = zone;
            r.setDatetime(d.timeOfArea().getDatetime());
            if (callback != null) callback.invoke(r);
        });
    }
}
