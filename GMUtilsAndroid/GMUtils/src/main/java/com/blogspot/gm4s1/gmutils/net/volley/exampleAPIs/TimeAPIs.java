package com.blogspot.gm4s1.gmutils.net.volley.exampleAPIs;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.gm4s1.gmutils.net.volley.exampleURLs.TimeURLs;
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
public class TimeAPIs extends ApiManager {

    /*
        use one of those constructor
     */

    public TimeAPIs(@NonNull Activity activity) {
        super(activity);
    }

    public TimeAPIs(@NonNull Fragment fragment) {
        super(fragment);
    }

    public TimeAPIs(@NonNull Context context) {
        super(context);
    }

    //----------------------------------------------------------------------------------------------

    public void getTimeZoneList(final OnDataFetchedListener<Result<List<String>>> callback) {
        getTimeZoneList("", callback);
    }

    public void getTimeZoneList(@Nullable String ofSpecificArea, final OnDataFetchedListener<Result<List<String>>> callback) {
        TimeURLs.TimeZoneListURL url = new TimeURLs.TimeZoneListURL(ofSpecificArea);

        doRequest(url, (request, response, responseStatus, statusCode) -> {
            Result<List<String>> result = new Result<>();

            try {
                JSONArray responseJson = new JSONArray(response);
                List<String> names = new ArrayList<>();

                for (int i = 0; i < responseJson.length(); i++) {
                    names.add(responseJson.getString(i));
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

    public void CurrentTimeURL(@NonNull String zone, final OnDataFetchedListener<Result<String>> callback) {
        TimeURLs.CurrentTimeURL url = new TimeURLs.CurrentTimeURL(zone);

        doRequest(url, (request, response, responseStatus, statusCode) -> {
            Result<String> result = new Result<>();

            try {
                JSONObject responseJson = new JSONObject(response);
                result.setResult(responseJson.getString("datetime"));

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
