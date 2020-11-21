package com.blogspot.gm4s1.gmutils.net.volley.zcore;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Header;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.gm4s1.gmutils.AppLog;
import com.blogspot.gm4s1.gmutils.storage.SettingsStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

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

/**
 * it depends on::
 * 'com.android.volley:volley:1.1.1'
 */
public class ApiRequestCreator {
    private static ApiRequestCreator mInstance;
    private RequestQueue mRequestQueue;
    private boolean disableCache = true; //use this boolean to enable or disable cache feature
    private int timeout = 30000;

    @SuppressLint("ShowToast")
    public static synchronized ApiRequestCreator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiRequestCreator(context);
        }

        return mInstance;
    }

    private ApiRequestCreator(Context context) {
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Context applicationContext = context.getApplicationContext();

        mRequestQueue = Volley.newRequestQueue(applicationContext, getHurlStack(applicationContext));
    }

    private HurlStack getHurlStack(Context context) {
        HurlStack hurlStack = null;
        try {
            //SSLSessionCache sslSessionCache = new SSLSessionCache(context);
            //SSLSocketFactory sslSocketFactory = SSLCertificateSocketFactory.getDefault(60000, sslSessionCache);

            SSLSocketFactory sslSocketFactory = new TLSSocketFactory();

            hurlStack = new HurlStack(null, sslSocketFactory);
        } catch (Exception e) {
        }

        return hurlStack;
    }

    public ApiRequestCreator setDisableCache(boolean disableCache) {
        this.disableCache = disableCache;
        return this;
    }

    public ApiRequestCreator setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    private <T> void addToRequestQueue(BaseRequest<?> req) {
        if (disableCache) mRequestQueue.getCache().clear();
        mRequestQueue.add(req.getRequest());

        /*Cache.Entry entry = mRequestQueue.getCache().get(_BaseURLs.FLIGHT_URL + "searchrequest/save");

        if (entry != null) {
            printLog(entry.toString());

            printLog("data: " + new String(entry.data));
            printLog("etag: " + entry.etag);
            printLog("serverDate: " + entry.serverDate);
            printLog("lastModified: " + entry.lastModified);
            printLog("ttl: " + entry.ttl);
            printLog("softTtl: " + entry.softTtl);

            if (entry.responseHeaders != null) {
                printLog("responseHeaders: " + entry.responseHeaders.size());

                Set<String> strings = entry.responseHeaders.keySet();
                for (String key : strings) {
                    printLog("responseHeaders: " + key + ": " + entry.responseHeaders.get(key));
                }
            }
            if (entry.allResponseHeaders != null) {
                printLog("allResponseHeaders: " + entry.allResponseHeaders.size());


                for (Header header : entry.allResponseHeaders) {
                    printLog("allResponseHeaders: " + header.getName() + ": " + header.getValue());

                }
            }

            printLog("Expired?" + entry.isExpired() + " [ttl < System.currentTimeMillis()]");
            printLog("NeededRefresh?" + entry.refreshNeeded() + " [softTtl < System.currentTimeMillis()]");

        }*/
    }

    public JSONObject getCacheContent(String url) {
        Cache.Entry entry = mRequestQueue.getCache().get(url);

        JSONObject json = new JSONObject();

        try {
            if (entry != null) {

                json.put("details", entry.toString());

                json.put("data", new String(entry.data));
                json.put("etag", entry.etag);
                json.put("serverDate", entry.serverDate);
                json.put("lastModified", entry.lastModified);

                json.put("ttl", entry.ttl);
                json.put("softTtl", entry.softTtl);

                json.put("expired", entry.isExpired()); // + " [ttl < System.currentTimeMillis()]");
                json.put("neededRefresh", entry.refreshNeeded()); // + " [softTtl < System.currentTimeMillis()]");

                JSONArray responseHeadersJson = new JSONArray();
                if (entry.responseHeaders != null) {
                    Set<String> strings = entry.responseHeaders.keySet();
                    for (String key : strings) {
                        try {
                            JSONObject item = new JSONObject();
                            item.put("key", key);
                            item.put("value", entry.responseHeaders.get(key));

                            responseHeadersJson.put(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                json.put("responseHeaders", responseHeadersJson);

                JSONArray allResponseHeadersJson = new JSONArray();
                if (entry.allResponseHeaders != null) {
                    for (Header header : entry.allResponseHeaders) {
                        try {
                            JSONObject item = new JSONObject();
                            item.put("key", header.getName());
                            item.put("value", header.getValue());

                            allResponseHeadersJson.put(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                json.put("allResponseHeaders", allResponseHeadersJson);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public Request addRequest(ApiURL.IURL urlObj, OnResponseReadyCallback<String> onDataReady) {
        Request stringRequest = new Request(urlObj, timeout, onDataReady);
        addToRequestQueue(stringRequest);

        return stringRequest;
    }

    /**
     * @param contentResolver required in case of uploading file by Uri
     */
    public MultipartRequest addMultipartRequest(@Nullable ContentResolver contentResolver, ApiURL.IURL urlObj, OnResponseReadyCallback<String> onDataReady) {
        MultipartRequest multipartRequest = new MultipartRequest(contentResolver, urlObj, timeout, onDataReady);
        addToRequestQueue(multipartRequest);
        return multipartRequest;
    }

    //-------------------------------------------------------------------------//

    public static abstract class BaseRequest<T> {
        ApiURL.IURL urlObject;
        OnResponseReadyCallback<T> onDataReady;
        private int timeout = 30000;

        BaseRequest(ApiURL.IURL urlObj, int timeout, OnResponseReadyCallback<T> onDataReady) {
            this.urlObject = urlObj;
            this.onDataReady = onDataReady;
            this.timeout = timeout;
        }

        DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        Response.Listener<T> listener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (onDataReady != null) {
                    onDataReady.onResponseFetched(response);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (onDataReady != null) {
                    String errorMsg = "";
                    String response = null;
                    int statusCode = 0;

                    try {
                        statusCode = error.networkResponse.statusCode;
                    } catch (Exception e) {
                    }

                    if (error instanceof NoConnectionError) {
                        if (SettingsStorage.Language.usingEnglish()) {
                            errorMsg = "No Available Internet Connection";
                        } else {
                            errorMsg = "لا يوجد اتصال متوفر بالانترنت";
                        }

                        statusCode = OnResponseReadyCallback.StatusCode_NotConnected;

                    } else if (error instanceof TimeoutError) {
                        if (SettingsStorage.Language.usingEnglish()) {
                            errorMsg = "Connection Timeout";
                        } else {
                            errorMsg = "تم تجاوز الوقت المحدد للاتصال، حاول ثانيةً";
                        }

                        statusCode = OnResponseReadyCallback.StatusCode_Timeout;

                    } else {
                        NetworkResponse networkResponse = null;

                        if (error instanceof NetworkError) {
                            networkResponse = ((NetworkError) error).networkResponse;

                        } else if (error instanceof AuthFailureError) {
                            networkResponse = ((AuthFailureError) error).networkResponse;

                        } else if (error instanceof ServerError) {
                            networkResponse = ((ServerError) error).networkResponse;

                        } else if (error instanceof ParseError) {
                            networkResponse = ((ParseError) error).networkResponse;
                        }

                        try {
                            response = new String(networkResponse.data);
                        } catch (Exception e) {
                        }

                        errorMsg = error.toString();
                    }

                    AppLog.print("WebRequest", error.toString());
                    onDataReady.onResponseFetchedFailed(errorMsg, response, statusCode);
                }
            }
        };

        protected abstract com.android.volley.Request<?> getRequest();

        public void cancel() {
            try {
                getRequest().cancel();
            } catch (Exception y) {
                y.printStackTrace();
            }
        }
    }

    public static class Request extends BaseRequest<String> {
        private StringRequest stringRequest;

        Request(ApiURL.IURL urlObj, int timeout, OnResponseReadyCallback<String> onDataReady) {
            super(urlObj, timeout, onDataReady);

            stringRequest = new StringRequest(
                    urlObj.getRequestMethod(),
                    urlObj.getFinalURL(),
                    listener,
                    errorListener
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return Request.this.urlObject.getHeaders();
                }

                @Override
                protected Map<String, String> getParams() {
                    ApiURL.Parameters params = Request.this.urlObject.getParams();
                    if (params != null) return params.getParams();
                    return null;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    ApiURL.Body body = Request.this.urlObject.getBody();
                    if (body != null) return body.getBody();
                    else return super.getBody();
                }

                @Override
                public String getBodyContentType() {
                    ApiURL.Body body = Request.this.urlObject.getBody();
                    if (body != null) return body.getContentType();
                    else return super.getBodyContentType();
                }
            };

            stringRequest.setRetryPolicy(defaultRetryPolicy);
        }

        @Override
        public com.android.volley.Request<?> getRequest() {
            return stringRequest;
        }
    }

    public static class MultipartRequest extends BaseRequest<String> {
        public static int MAX_IMAGE_SIZE = 1024 * 1024;

        private VolleyMultipartRequest multipartRequest;

        /**
         * @param contentResolver required in case of uploading file by Uri
         */
        public MultipartRequest(ContentResolver contentResolver, ApiURL.IURL urlObj, int timeout, OnResponseReadyCallback<String> onDataReady) {
            super(urlObj, timeout, onDataReady);

            multipartRequest = new VolleyMultipartRequest(
                    com.android.volley.Request.Method.POST,
                    urlObj.getFinalURL(),
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                listener.onResponse(new String(response.data));
                            } catch (Exception e) {
                                errorListener.onErrorResponse(new ParseError(response));
                            }
                        }
                    },
                    errorListener
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return MultipartRequest.this.urlObject.getHeaders();
                }

                @Override
                protected Map<String, String> getParams() {
                    ApiURL.Parameters params = MultipartRequest.this.urlObject.getParams();
                    if (params != null) return params.getParams();
                    return null;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    ApiURL.Body body = MultipartRequest.this.urlObject.getBody();
                    if (body != null) return body.getBody();
                    else return super.getBody();
                }

                @Override
                public String getBodyContentType() {
                    ApiURL.Body body = MultipartRequest.this.urlObject.getBody();
                    if (body != null) return body.getContentType();
                    else return super.getBodyContentType();
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    Map<String, Bitmap> imgParams = MultipartRequest.this.urlObject.getParams().getImagesParams();
                    if (imgParams != null) {
                        Set<Map.Entry<String, Bitmap>> entries = imgParams.entrySet();
                        String imageName;

                        for (Map.Entry<String, Bitmap> img : entries) {
                            if (img.getValue() == null) continue;
                            imageName = "IMG_" + System.currentTimeMillis();
                            params.put(img.getKey(), new DataPart(imageName + ".png", getFileDataFromImage(img.getValue())));
                        }
                    }

                    Map<String, Uri> vidParams = MultipartRequest.this.urlObject.getParams().getFilesParams();
                    if (vidParams != null) {
                        Set<Map.Entry<String, Uri>> entries = vidParams.entrySet();
                        String videoName;

                        for (Map.Entry<String, Uri> vid : entries) {
                            try {
                                if (vid.getValue() == null) continue;
                                InputStream inputStream = contentResolver.openInputStream(vid.getValue());
                                byte[] vidBytes = new byte[inputStream.available()];
                                inputStream.read(vidBytes);

                                videoName = "VID_" + System.currentTimeMillis();
                                params.put(vid.getKey(), new DataPart(videoName + ".mp4", vidBytes));

                                inputStream.close();

                            } catch (Exception e) {
                                Log.e(ApiRequestCreator.class.getSimpleName(), "contentResolver required in case of uploading file by Uri");
                                e.printStackTrace();
                                AppLog.print(e);
                            }
                        }
                    }

                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        @Override
        public com.android.volley.Request<?> getRequest() {
            return multipartRequest;
        }

        byte[] getFileDataFromImage(Bitmap bitmap) {
            if (bitmap == null) return null;

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int oneDimenLength = MAX_IMAGE_SIZE / 2;

            if (w * h > MAX_IMAGE_SIZE) {
                int nW, nH;

                if (w > h) {
                    nW = oneDimenLength;
                    nH = (int) (h / (float) w * oneDimenLength);
                } else {
                    nW = (int) (w / (float) h * oneDimenLength);
                    nH = oneDimenLength;
                }

                bitmap = Bitmap.createScaledBitmap(bitmap, nW, nH, false);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
