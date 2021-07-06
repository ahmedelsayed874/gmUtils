package gmutils.net.volley.example.APIs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmutils.net.volley.ApiManager;
import gmutils.net.volley.example.URLs.TimeURLs;
import gmutils.net.volley.utils.OnDataFetchedListener;
import gmutils.net.volley.utils.Result;

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

    public TimeAPIs(@NotNull Activity activity) {
        super(activity);
    }

    public TimeAPIs(@NotNull Fragment fragment) {
        super(fragment);
    }

    public TimeAPIs(@NotNull Context context) {
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

    public void CurrentTimeURL(@NotNull String zone, final OnDataFetchedListener<Result<String>> callback) {
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
