package gmutils.net.volley.example.URLs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.net.volley.ApiURL;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

public class TimeURLs {
    private static final String baseURL = "https://worldtimeapi.org/api/";

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

        public CurrentTimeURL(@NotNull String zone) {
            this.zone = zone;
        }

        @Override
        public String getEndPointURL() {
            return baseURL + "timezone/" + zone;
        }
    }

}
