package gmutils.net.volley.example.URLs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import gmutils.net.volley.ApiURL;

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

public class TimeURLs {
    public static final String baseURL = "http://worldtimeapi.org/api/";

    public static class TimeZoneListURL extends ApiURL.getURL {
        private String ofSpecificArea = ""; //ex: Africa

        public TimeZoneListURL() {}

        public TimeZoneListURL(@Nullable String ofSpecificArea) {
            if (ofSpecificArea != null) {
                this.ofSpecificArea = ofSpecificArea;
            }

        }

        @Override
        public String getEndPointURL() {
            return baseURL + "timezone/" + ofSpecificArea;
        }
    }

    public static class CurrentTimeURL extends ApiURL.getURL {
        private String zone = "";

        public CurrentTimeURL(@NonNull String zone) {
            this.zone = zone;
        }

        @Override
        public String getEndPointURL() {
            return baseURL + "timezone/" + zone;
        }
    }

}
