package gmutils.net;

import android.os.Handler;
import android.os.Looper;

import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

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
public class SimpleHTTPRequest {
    /**
     * HTTP request header constants
     */
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String ACCEPT = "Accept";
    public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";
    public static final String MIME_APPLICATION_JSON = "application/json";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    public static class Configurations {
        private int connectionTimeOut = 15000;
        private int readTimeOut = 15000;
        private String charEncoding = "UTF-8";
        public boolean allowCaching;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;

        public void setConnectionTimeOut(int connectionTimeOut) {
            this.connectionTimeOut = connectionTimeOut;
        }

        public void setReadTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
        }

        public void setCharEncoding(String charEncoding) {
            this.charEncoding = charEncoding;
        }

        public void setAllowCaching(boolean allowCaching) {
            this.allowCaching = allowCaching;
        }

        public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
        }

        public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
        }
    }

    public enum Method {GET, POST, PUT, DELETE}

    public static class Request {
        private final long time;
        private final String url;
        private final Method method;
        private final Map<String, String> headers;
        private final Map<String, Object> postParameters;
        private final String postBody;


        public Request(String url, Method method, Map<String, String> headers) {
            this.time = System.currentTimeMillis();
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.postParameters = null;
            this.postBody = null;
        }

        public Request(String url, Method method, Map<String, String> headers, Map<String, Object> postParameters) {
            this.time = System.currentTimeMillis();
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.postParameters = postParameters;
            this.postBody = null;
        }

        public Request(String url, Method method, Map<String, String> headers, String postBody) {
            this.time = System.currentTimeMillis();
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.postParameters = null;
            this.postBody = postBody;
        }


        public long getTime() {
            return time;
        }

        public String getUrl() {
            return url;
        }

        public Method getMethod() {
            return method;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Map<String, Object> getPostParameters() {
            return postParameters;
        }

        public String getPostBody() {
            return postBody;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "\nurl='" + url + '\'' +
                    "\n, method=" + method +
                    "\n, postParameters=" + postParameters +
                    "\n, postBody='" + postBody + '\'' +
                    "\n}";
        }
    }

    public static class Response {
        private final long time;
        private Exception exception;
        private int code;

        protected Response() {
            this.time = System.currentTimeMillis();
        }

        public Response(Response other) {
            this();
            this.setCode(other.code);
            this.setException(other.exception);
        }

        Response setException(Exception exception) {
            this.exception = exception;
            return this;
        }

        Response setCode(int code) {
            this.code = code;
            return this;
        }

        public long getTime() {
            return time;
        }

        public Exception getException() {
            return exception;
        }

        public int getCode() {
            return code;
        }
    }

    public static class TextResponse extends Response {
        private String text;

        public TextResponse() {
        }

        public TextResponse(Response other) {
            super(other);
        }

        private TextResponse setText(String text) {
            this.text = text;
            return this;
        }

        public String getText() {
            return text;
        }
    }

    public static class FileResponse extends Response {
        private File file;

        public FileResponse() {
        }

        public FileResponse(Response other) {
            super(other);
        }

        private FileResponse setFile(File file) {
            this.file = file;
            return this;
        }

        public File getFile() {
            return file;
        }
    }

    //==============================================================================================

    public static void get(String url, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.GET, null), callback);
    }

    public static void get(String url, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.GET, null), configurations, callback);
    }

    public static void getFile(String url, @NotNull File destFile, @NotNull ResultCallback2<Request, FileResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.GET, null), destFile, callback);
    }

    public static void getFile(String url, @NotNull File destFile, Configurations configurations, @NotNull ResultCallback2<Request, FileResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.GET, null), destFile, configurations, callback);
    }

    public static void post(String url, Map<String, Object> parameters, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.POST, null, parameters), callback);
    }

    public static void post(String url, Map<String, Object> parameters, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.POST, null, parameters), configurations, callback);
    }

    public static void post(String url, String body, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.POST, null, body), callback);
    }

    public static void post(String url, String body, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(new Request(url, Method.POST, null, body), configurations, callback);
    }

    public static void create(@NotNull Request request, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(request, callback);
    }

    public static void create(@NotNull Request request, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new SimpleHTTPRequest(request, configurations, callback);
    }

    //----------------------------------------------------------------------------------------------

    private Request request;
    private File destFile;
    private Configurations configurations;
    private ResultCallback2<Request, TextResponse> textCallback;
    private ResultCallback2<Request, FileResponse> fileCallback;

    public SimpleHTTPRequest(Request request, ResultCallback2<Request, TextResponse> textCallback) {
        this(request, null, new Configurations(), textCallback, null);
    }

    public SimpleHTTPRequest(Request request, Configurations configurations, ResultCallback2<Request, TextResponse> textCallback) {
        this(request, null, configurations, textCallback, null);
    }

    public SimpleHTTPRequest(Request request, File destFile, ResultCallback2<Request, FileResponse> fileCallback) {
        this(request, destFile, new Configurations(), null, fileCallback);
    }

    public SimpleHTTPRequest(Request request, File destFile, Configurations configurations, ResultCallback2<Request, FileResponse> fileCallback) {
        this(request, destFile, configurations, null, fileCallback);
    }

    private SimpleHTTPRequest(Request request, File destFile, Configurations configurations, ResultCallback2<Request, TextResponse> textCallback, ResultCallback2<Request, FileResponse> fileCallback) {
        this.request = request;
        this.destFile = destFile;

        if (configurations != null)
            this.configurations = configurations;
        else
            this.configurations = new Configurations();

        this.textCallback = textCallback;
        this.fileCallback = fileCallback;

        doRequest();
    }

    private void doRequest() {
        new Thread(() -> {
            if (textCallback != null) {
                fetchText(this::onPostExecute);

            } else if (fileCallback != null) {
                fetchFile(this::onPostExecute);
            } else {
                dispose();
            }

        }).start();
    }

    //----------------------------------------------------------------------------------------------

    private void fetchText(ResultCallback<TextResponse> callback) {
        executeRequest((res, inputStream) -> {
            TextResponse response = new TextResponse(res);
            String text = null;

            if (inputStream != null) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder stringBuffer = new StringBuilder();

                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                } catch (Exception e) {
                    response.setException(e);
                }

                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                text = stringBuffer.toString();
            }

            response.setText(text);

            callback.invoke(response);
        });
    }

    private void fetchFile(ResultCallback<FileResponse> callback) {
        executeRequest((res, inputStream) -> {
            FileResponse response = new FileResponse(res);
            FileOutputStream os = null;

            try {
                byte[] data = new byte[inputStream.available()];
                inputStream.read(data);

                os = new FileOutputStream(destFile);
                os.write(data);

                response.setFile(destFile);

            } catch (Exception e) {
                response.setException(e);

            } finally {
                if (os != null) {
                    try {
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            callback.invoke(response);
        });
    }

    private void executeRequest(ResultCallback2<Response, InputStream> callback) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Response response = new Response();

        try {
            URL url = new URL(request.url);
            URLConnection connection = url.openConnection();

            if (connection instanceof HttpURLConnection) {
                urlConnection = (HttpURLConnection) connection;

            } else {
                HttpsURLConnection urlConnectionSecure = (HttpsURLConnection) connection;
                urlConnection = urlConnectionSecure;

                if (configurations.hostnameVerifier != null) {
                    urlConnectionSecure.setHostnameVerifier(configurations.hostnameVerifier);
                }

                if (configurations.sslSocketFactory != null) {
                    urlConnectionSecure.setSSLSocketFactory(configurations.sslSocketFactory);
                }

            }

            urlConnection.setRequestMethod(request.method.name());
            urlConnection.setConnectTimeout(configurations.connectionTimeOut);
            urlConnection.setReadTimeout(configurations.readTimeOut);
            urlConnection.setUseCaches(configurations.allowCaching);

            byte[] postDataBytes = null;

            if (request.method == Method.POST || request.method == Method.PUT) {
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                if (request.postParameters != null) {
                    StringBuilder postData = new StringBuilder();

                    for (Map.Entry<String, Object> param : request.postParameters.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), configurations.charEncoding));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), configurations.charEncoding));
                    }

                    postDataBytes = postData.toString().getBytes(configurations.charEncoding);

                    urlConnection.setRequestProperty(CONTENT_TYPE, MIME_FORM_ENCODED);
                    urlConnection.setRequestProperty(CONTENT_LENGTH, "" + postDataBytes.length);
                    //urlConnection.setRequestProperty(CONTENT_LANGUAGE, "en-US");

                } else if (request.postBody != null) {
                    postDataBytes = request.postBody.getBytes(configurations.charEncoding);
                    urlConnection.setRequestProperty(CONTENT_TYPE, MIME_APPLICATION_JSON);
                }
            } else if (request.method == Method.DELETE) {
                urlConnection.setRequestProperty(CONTENT_TYPE, MIME_FORM_ENCODED);
            }

            if (request.headers != null) {
                Set<Map.Entry<String, String>> entries = request.headers.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getKey());
                }
            }

            urlConnection.connect();

            if (postDataBytes != null) {
                outputStream = urlConnection.getOutputStream();
                outputStream.write(postDataBytes);
            }

            response.setCode(urlConnection.getResponseCode());

            inputStream = urlConnection.getInputStream();

            callback.invoke(response, inputStream);

        } catch (Exception e) {
            response.setException(e);
            callback.invoke(response, null);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private void onPostExecute(TextResponse response) {
        new Handler(Looper.getMainLooper()).post(() -> {
            textCallback.invoke(request, response);
            dispose();
        });
    }

    private void onPostExecute(FileResponse response) {
        new Handler(Looper.getMainLooper()).post(() -> {
            fileCallback.invoke(request, response);
            dispose();
        });
    }

    private void dispose() {
        request = null;
        textCallback = null;
        destFile = null;
        fileCallback = null;
        configurations = null;
    }

}
