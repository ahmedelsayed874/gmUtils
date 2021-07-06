package gmutils.net.volley.example.URLs;

import android.text.TextUtils;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

public class CountriesURLs {
    public static final String baseURL = "https://restcountries.eu/rest/v2/";

    public static class CountriesListURL extends ApiURL.getURL {
        private String regionName;

        /**
         * empty constructor
         */
        public CountriesListURL() {}

        /**
         * @param regionName maybe null or has value ex: Africa, Americas, Asia, Europe, Oceania
         * @param queryExample for demonstrating only, it will append after endpoint name like that; ...?queryExample=value
         */
        public CountriesListURL(@Nullable String regionName, String queryExample) {
            super(new ApiURL.Parameters() {
            });
            getParams().put("queryExample", queryExample);

            this.regionName = regionName;
        }

        @Override
        public String getAuthorizationCredential(String type, String value) {
            return super.getAuthorizationCredential(type, value);
        }

        @Override
        public Map<String, String> getHeaders() {
            return super.getHeaders();
        }

        @Override
        public String getEndPointURL() {
            if (TextUtils.isEmpty(regionName))
                return baseURL + "all";
            else
                return baseURL + "region/" + regionName;

        }
    }

}
