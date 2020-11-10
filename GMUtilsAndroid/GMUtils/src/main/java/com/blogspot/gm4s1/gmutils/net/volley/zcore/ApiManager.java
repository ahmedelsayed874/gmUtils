package com.blogspot.gm4s1.gmutils.net.volley.zcore;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import com.blogspot.gm4s1.gmutils.AppLog;
import com.blogspot.gm4s1.gmutils.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * this class will manage API request cycle
 * I created it to solve the issue of recreating Activities
 */

/**
 * it depends on::
 * 'com.android.volley:volley:1.1.1'
 */
public class ApiManager {
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragment;
    private Context mAppContext;
    private static List<Request> mRequests = new ArrayList<>();
    private Map<ApiURL.IURL, ApiRequestCreator.BaseRequest<?>> apiRequests = new ArrayMap<>();
    private Map<ApiURL.IURL, String> responses = new ArrayMap<>();
    private boolean disableCache = true;
    private int timeout = 30000;

    /**
     * it's not recommend to use this constructor
     */
    @Deprecated
    public ApiManager() {}

    public ApiManager(@NonNull Activity activity) {
        this.mActivity = new WeakReference<>(activity);
        try {
            this.mAppContext = activity.getApplicationContext();
        } catch (Exception e) {
        }
    }

    public ApiManager(@NonNull Fragment fragment) {
        this.mFragment = new WeakReference<>(fragment);
        try {
            this.mAppContext = fragment.getContext().getApplicationContext();
        } catch (Exception e) {
        }
    }

    public ApiManager(@NonNull Context context) {
        this.mAppContext = context.getApplicationContext();
    }

    protected Context getContext() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) return activity;
        } else if (mFragment != null) {
            Fragment fragment = mFragment.get();
            if (fragment != null) return fragment.getContext();
        }

        return mAppContext;
    }

    private boolean isCallerAlive() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) {
                return !activity.isDestroyed();
            }
        } else if (mFragment != null) {
            Fragment fragment = mFragment.get();
            if (fragment != null) {
                if (fragment.getActivity() != null && !fragment.getActivity().isDestroyed()) {
                    return !fragment.isDetached();
                }
            }
        } else {
            return true;
        }

        return false;
    }


    private String getCallerName() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) return activity.toString();

        } else if (mFragment != null) {
            Fragment fragment = mFragment.get();
            if (fragment != null) return fragment.toString();

        }

        /*if (mAppContext != null) {
            return mAppContext.toString();
        }*/

        return "";
    }


    private void printLog(String msg) {
        AppLog.print("ApiManager", msg);
    }

    //--------------------------------------------------------------------------------------------//

    public void setDisableCache(boolean disableCache) {
        this.disableCache = disableCache;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    //--------------------------------------------------------------------------------------------//

    protected synchronized void doRequest(ApiURL.IURL url, Listener listener) {
        doRequest(url, listener, false);
    }

    protected synchronized void doRequest(ApiURL.IURL url, Listener listener, boolean startOver) {
        printLog("doRequest----------------------\n" +
                "Caller: " + getCallerName() + "\n" +
                url.toString());

        Request request = isRequestRunning(url);
        if (request == Request.NULL || startOver) {
            request = registerRequest(url);
            executeRequest(request, listener);
        } else {
            Object[] result = isRequestFinished(request);
            if ((boolean) result[0]) {
                alterListener(request, listener);
            } else {
                printLog("doRequest: return back cached response");
                boolean success = listener.onDataFetchingFinished(
                        request,
                        (String) result[1],
                        ResponseStatus.Success,
                        200);
                if (!success) removeRequest(request);
            }
        }
    }

    protected void excludeRequestFromCache(ApiURL.IURL url) {
        removeRequest(findUrl(url));
    }

    protected void cacheRequestOf(ApiURL.IURL url, float periodMin) {
        Request request = registerRequest(url);
        request.setCachePeriodMS((int) (periodMin * 60 * 1000));
    }

    protected void cancelRequest(ApiURL.IURL url) {
        if (apiRequests.containsKey(url)) {
            ApiRequestCreator.BaseRequest<?> request = apiRequests.get(url);
            if (request != null) {
                request.cancel();
            }
            apiRequests.remove(url);
            printLog("Request cancelled \n(" + url.getEndPointURL() + ")\n");
        }
    }

    public String getResponse(ApiURL.IURL url) {
        if (responses.containsKey(url)) {
            return responses.get(url);
        }

        return null;
    }
    
    //--------------------------------------------------------------------------------------------//

    private synchronized Request getRequest(int index) {
        Request request = mRequests.get(index);
        if (request == null) request = Request.NULL;

        return request;
    }

    private synchronized void removeRequest(Request request) {
        try {
            mRequests.remove(request);
        } catch (Exception e) {
            printLog(e.toString());
        }
    }

    private void clearOldRequests() {
        printLog("clearOldRequests");

        int size = mRequests.size();
        int clearCount = 0;

        for (int i = 0; i < size; i++) {
            Request request = getRequest(i);
            if (request.isCacheTimeElapsed()) {
                printLog("clearOldRequests, clearing: (" + i + ") " + request.url.getEndPointURL());
                removeRequest(request);
                size--;
                i--;
                clearCount++;
            }
        }

        printLog(clearCount + " Request(s) has been cleared");
    }

    private Request isRequestRunning(ApiURL.IURL url) {
        clearOldRequests();

        Request request = findUrl(url);

        printLog("isRequestRunning()" + (request == Request.NULL ? "NO" : "YES"));

        return request;
    }

    private Request findUrl(ApiURL.IURL url) {
        int size = mRequests.size();
        Request request = new Request(url);

        for (int i = 0; i < size; i++) {
            Request req = getRequest(i);
            if (req.equals(request)) {
                printLog("isRequestRunning (" + i + ") " + req.url.getEndPointURL());
                return req;
            }
        }

        return Request.NULL;
    }

    private Object[] isRequestFinished(Request request) {
        Object[] returnRes = new Object[2];
        returnRes[0] = TextUtils.isEmpty(request.response);
        returnRes[1] = request.response;

        printLog("isRequestFinished? " + (((boolean) returnRes[0]) ? "Y" : "N"));

        return returnRes;
    }

    private Request registerRequest(ApiURL.IURL url) {
        Request request = new Request(url);
        mRequests.add(request);

        printLog("request registered");

        return request;
    }

    private boolean alterListener(Request request, Listener listener) {
        try {
            printLog("alterListener");

            request.listener = listener;
            request.requestCallback = new RequestCallback(this, request);

            return true;
        } catch (Exception e) {
            printLog("alterListener || exception thrown");
            return false;
        }
    }

    private void executeRequest(final Request request, Listener listener) {
        printLog("executing request");

        request.listener = listener;
        request.requestCallback = new RequestCallback(this, request);

        OnResponseReadyCallback<String> callback = new OnResponseReadyCallback<String>() {
            @Override
            public void onResponseFetched(String response) {
                if (request.requestCallback != null) {
                    request.requestCallback.onResponseFetched(response);
                }

                request.requestCallback = null;
            }

            @Override
            public void onResponseFetchedFailed(String msg, String response, int statusCode) {
                if (request.requestCallback != null) {
                    request.requestCallback.onResponseFetchedFailed(msg, response, statusCode);
                }

                request.requestCallback = null;
            }
        };

        ApiRequestCreator apiRequestCreator = ApiRequestCreator
                .getInstance(getContext())
                .setDisableCache(disableCache)
                .setTimeout(timeout);

        ApiRequestCreator.BaseRequest<?> apiRequest;

        if (request.url.getParams().hasMultiPartParams()) {
            Context context = getContext();
            if (context != null) {
                apiRequest = apiRequestCreator.addMultipartRequest(context.getContentResolver(), request.url, callback);
            } else {
                if (request.url.getParams().getFilesParams().size() == 0) {
                    apiRequest = apiRequestCreator.addMultipartRequest(null, request.url, callback);
                } else {
                    throw new IllegalArgumentException("You need to upload files, therefor you must construct this class with a context (use one of provided constructor)");
                }
            }
        } else {
            apiRequest = apiRequestCreator.addRequest(request.url, callback);
        }

        try {
            apiRequests.put(request.url, apiRequest);
        } catch (Exception e) {
            e.printStackTrace();
            printLog(e.toString());
        }

    }

    /**
     * I save responses to get it back later if i need
     *
     * @param url
     * @param response
     */
    private void saveResponse(ApiURL.IURL url, String response) {
        if (responses.containsKey(url)) {
            responses.remove(url);
        }

        responses.put(url, response);
    }

    //--------------------------------------------------------------------------------------------//

    private static class RequestCallback implements OnResponseReadyCallback<String> {
        private ApiManager apiManager;
        private Request request;

        private RequestCallback(ApiManager apiManager, Request request) {
            this.apiManager = apiManager;
            this.request = request;
        }

        @Override
        public void onResponseFetched(String response) {
            apiManager.printLog("Response:(" + request.url.getFinalURL() + ")\n" + response);
            request.setResponse(response);

            if (apiManager.isCallerAlive()) {
                apiManager.printLog("onResponseFetched, caller alive, response going to return back");
                apiManager.saveResponse(request.url, response);

                boolean success = request.listener.onDataFetchingFinished(
                        request,
                        response,
                        ResponseStatus.Success,
                        200);
                if (!success) apiManager.removeRequest(request);
            } else {
                apiManager.printLog("onResponseFetched, caller died, response cached");
            }

            removeAPIRequest(request.url);
        }

        @Override
        public void onResponseFetchedFailed(String msg, String response, int statusCode) {
            if (response == null) {
                Toast.makeText(apiManager.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
            apiManager.printLog("onResponseFetchedFailed: (" + request.url.getEndPointURL() + ")\nmsg:\n\t" + msg + "\nResponse:\n\t" + response);
            apiManager.saveResponse(request.url, response);
            boolean succeeded = request.listener.onDataFetchingFinished(
                    request,
                    response,
                    statusCode <= 0 ? ResponseStatus.ConnectionFailed : ResponseStatus.Error,
                    statusCode);
            if (!succeeded) apiManager.removeRequest(request);
            removeAPIRequest(request.url);

        }

        private void removeAPIRequest(ApiURL.IURL url) {
            try {
                apiManager.apiRequests.remove(url);
            } catch (Exception e) {
                e.printStackTrace();
                apiManager.printLog(e.toString());
            }
        }
    }

    protected static class Request {
        private ApiURL.IURL url;
        private String response;
        private long requestTime;
        private long responseTime;
        private Listener listener;
        private RequestCallback requestCallback;
        private int cachePeriodMS = 300000; //5 * 60 * 1000; //5 minute

        Request(ApiURL.IURL url) {
            this.url = url;
            this.requestTime = System.currentTimeMillis();
        }

        public Request setCachePeriodMS(int cachePeriodMS) {
            this.cachePeriodMS = cachePeriodMS;
            return this;
        }

        void setResponse(String response) {
            this.response = response;
            this.responseTime = System.currentTimeMillis();
        }

        boolean isCacheTimeElapsed() {
            long time = cachePeriodMS;
            long diff = System.currentTimeMillis() - requestTime;

            return diff >= time;
        }

        long getResponseDelay() {
            return this.responseTime - this.requestTime;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;

            if (obj instanceof Request) {
                Request request = ((Request) obj);
                ApiURL.IURL requestRrl = request.url;

                if (TextUtils.equals(requestRrl.getEndPointURL(), this.url.getEndPointURL())) {
                    Utils utils = Utils.createInstance();
                    if (!utils.checkEquality(requestRrl.getParams(), this.url.getParams()))
                        return false;
                    if (!utils.checkEquality(requestRrl.getBody(), this.url.getBody()))
                        return false;

                    return true;
                }
            }

            return false;
        }

        static final Request NULL = new Request(new ApiURL.getURL() {
            @Override
            public String getEndPointURL() {
                return "";
            }
        }) {
            @Override
            boolean isCacheTimeElapsed() {
                return false;
            }
        };


        @Override
        public String toString() {
            return url.getEndPointURL() + ",,," + super.toString();
        }
    }

    protected enum ResponseStatus { Success, Error, ConnectionFailed }

    protected interface Listener {
        /**
         * @return if false, the request will remove (not cached)
         */
        boolean onDataFetchingFinished(Request request, String response, ResponseStatus responseStatus, int statusCode);
    }
}
