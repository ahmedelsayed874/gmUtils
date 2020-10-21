package com.blogspot.gm4s1.gmutils.net.volley.exampleAPIs;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blogspot.gm4s1.gmutils.net.volley.exampleURLs.CountriesURLs;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.ApiManager;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.utils.OnDataFetchedListener;
import com.blogspot.gm4s1.gmutils.net.volley.zcore.utils.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
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
public class CountriesAPIs extends ApiManager {

    /*
        use one of those constructor
        but be noted, this "CountriesAPI()" is not recommended
     */

    public CountriesAPIs() {
        super();
    }

    public CountriesAPIs(@NonNull Activity activity) {
        super(activity);
    }

    public CountriesAPIs(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CountriesAPIs(@NonNull Context context) {
        super(context);
    }

    //----------------------------------------------------------------------------------------------

    public void getCountriesList(final OnDataFetchedListener<Result<List<String>>> callback) {
        getCountriesList("", callback);
    }

    public void getCountriesList(String regionName, final OnDataFetchedListener<Result<List<String>>> callback) {
        CountriesURLs.CountriesListURL url = new CountriesURLs.CountriesListURL(regionName, "123");

        doRequest(url, (request, response, responseStatus, statusCode) -> {
            Result<List<String>> result = new Result<>();

            try {
                JSONArray responseJson = new JSONArray(response);
                List<String> names = new ArrayList<>();

                for (int i = 0; i < responseJson.length(); i++) {
                    JSONObject jsonObject = responseJson.getJSONObject(i);
                    JSONObject languages = jsonObject.getJSONObject("translations");
                    names.add(languages.optString("de") + " - " + languages.optString("fa"));
                }

                result.setResult(names);

            } catch (Exception e) {
                if (responseStatus == ResponseStatus.ConnectionFailed) {
                    result.setConnectionError(true);
                }
            }

            if (callback != null) callback.onDataFetched(result);

            return true;
        });
    }
}
