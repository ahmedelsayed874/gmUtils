package gmutils.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

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
        private int connectionTimeOut = 30_000;
        private int readTimeOut = 60_000;
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

        protected Response(Response other) {
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

        TextResponse() {
        }

        TextResponse(Response other) {
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

        FileResponse() {
        }

        FileResponse(Response other) {
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
        new TextRequestExecutor(new Request(url, Method.GET, null)).executeAsynchronously(callback);
    }

    public static void get(String url, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.GET, null), configurations).executeAsynchronously(callback);
    }

    public static void get(String url, Map<String, String> headers, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.GET, headers), configurations).executeAsynchronously(callback);
    }


    public static void post(String url, Map<String, Object> parameters, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, null, parameters)).executeAsynchronously(callback);
    }

    public static void post(String url, Map<String, Object> parameters, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, null, parameters), configurations).executeAsynchronously(callback);
    }

    public static void post(String url, Map<String, String> headers, Map<String, Object> parameters, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, headers, parameters), configurations).executeAsynchronously(callback);
    }


    public static void post(String url, String body, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, null, body)).executeAsynchronously(callback);
    }

    public static void post(String url, String body, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, null, body), configurations).executeAsynchronously(callback);
    }

    public static void post(String url, Map<String, String> headers, String body, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        new TextRequestExecutor(new Request(url, Method.POST, headers, body), configurations).executeAsynchronously(callback);
    }


    public static void downloadFile(String url, @NotNull File destFile, @NotNull ResultCallback2<Request, FileResponse> callback) {
        new FileDownloadRequestExecutor(url, null, destFile).executeAsynchronously(callback);
    }

    public static void downloadFile(String url, @NotNull File destFile, Configurations configurations, @NotNull ResultCallback2<Request, FileResponse> callback) {
        new FileDownloadRequestExecutor(url, null, destFile, configurations).executeAsynchronously(callback);
    }

    public static void downloadFile(String url, Map<String, String> headers, @NotNull File destFile, Configurations configurations, @NotNull ResultCallback2<Request, FileResponse> callback) {
        new FileDownloadRequestExecutor(url, headers, destFile, configurations).executeAsynchronously(callback);
    }


    public static void uploadFile(String url, String fieldName, File uploadingFile, ResultCallback2<Request, Integer> progressCallback, ResultCallback2<Request, TextResponse> callback) {
        uploadFile(url, null, fieldName, uploadingFile, null, progressCallback, callback);
    }

    public static void uploadFile(String url, String fieldName, File uploadingFile, Configurations configurations, ResultCallback2<Request, Integer> progressCallback, ResultCallback2<Request, TextResponse> callback) {
        uploadFile(url, null, fieldName, uploadingFile, configurations, progressCallback, callback);
    }

    public static void uploadFile(String url, Map<String, String> headers, String fieldName, File uploadingFile, ResultCallback2<Request, Integer> progressCallback, ResultCallback2<Request, TextResponse> callback) {
        uploadFile(url, headers, fieldName, uploadingFile, null, progressCallback, callback);
    }

    public static void uploadFile(String url, Map<String, String> headers, String fieldName, File uploadingFile, Configurations configurations, ResultCallback2<Request, Integer> progressCallback, ResultCallback2<Request, TextResponse> callback) {
        new FileUploadRequestExecutor(url, headers, fieldName, uploadingFile, configurations, progressCallback)
                .executeAsynchronously(callback);
    }


    public static Response create(boolean synchronously, @NotNull Request request, @NotNull ResultCallback2<Request, TextResponse> callback) {
        return create(synchronously, request, null, callback);
    }

    public static Response create(boolean synchronously, @NotNull Request request, Configurations configurations, @NotNull ResultCallback2<Request, TextResponse> callback) {
        TextRequestExecutor executor = new TextRequestExecutor(request, configurations);

        if (synchronously) {
            return executor.executeSynchronously();
        } else {
            executor.executeAsynchronously(callback);
            return null;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static abstract class RequestExecutor<R extends Response> {
        private Request request;
        private Configurations configurations;
        private boolean isDisposed = false;


        public RequestExecutor(Request request) {
            this(request, null);
        }

        public RequestExecutor(Request request, Configurations configurations) {
            this.request = request;

            if (configurations != null)
                this.configurations = configurations;
            else
                this.configurations = new Configurations();
        }

        public Request getRequest() {
            return request;
        }

        public Configurations getConfigurations() {
            return configurations;
        }

        public boolean isDisposed() {
            return isDisposed;
        }

        //------------------------------------------------------------------------------------------

        public Response executeSynchronously() {
            return doRequest();
        }

        public void executeAsynchronously(ResultCallback2<Request, R> callback) {
            new Thread(() -> {
                R response = doRequest();

                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.invoke(getRequest(), response);
                    });
                }
            }).start();
        }

        //------------------------------------------------------------------------------------------

        protected abstract R doRequest();

        protected Pair<Response, InputStream> executeRequestInternally() {
            return executeRequestInternally(null);
        }

        protected Pair<Response, InputStream> executeRequestInternally(ResultCallback<OutputStream> writeDataDelegate) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Response response = new Response();
            Pair<Response, InputStream> returningData;

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
                        urlConnection.setRequestProperty(CONTENT_LENGTH, "" + postDataBytes.length);
                        //urlConnection.setRequestProperty(CONTENT_LANGUAGE, "en-US");
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

                if (writeDataDelegate != null) {
                    outputStream = urlConnection.getOutputStream();
                    writeDataDelegate.invoke(outputStream);
                }

                response.setCode(urlConnection.getResponseCode());

                inputStream = urlConnection.getInputStream();

                returningData = new Pair<>(response, inputStream);

            } catch (Exception e) {
                response.setException(e);
                returningData = new Pair<>(response, null);

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

                dispose();
            }

            return returningData;
        }

        //------------------------------------------------------------------------------------------

        protected void dispose() {
            request = null;
            configurations = null;
            isDisposed = true;
        }

        protected abstract void onDispose();
    }

    public static class TextRequestExecutor extends RequestExecutor<TextResponse> {

        public TextRequestExecutor(Request request) {
            super(request);
        }

        public TextRequestExecutor(Request request, Configurations configurations) {
            super(request, configurations);
        }

        @Override
        protected TextResponse doRequest() {
            return fetchText();
        }

        private TextResponse fetchText() {
            Pair<Response, InputStream> res = executeRequestInternally();

            TextResponse response = new TextResponse(res.first);
            String text = null;

            try {
                text = new Helpers().readInputStream(res.second);
            } catch (IOException e) {
                response.setException(e);
            }

            response.setText(text);

            return response;
        }

        @Override
        protected void onDispose() {
        }
    }

    public static class FileDownloadRequestExecutor extends RequestExecutor<FileResponse> {
        private File destFile;

        public FileDownloadRequestExecutor(String url, Map<String, String> headers, File destFile) {
            super(new Request(url, Method.GET, headers));
            this.destFile = destFile;
        }

        public FileDownloadRequestExecutor(String url, Map<String, String> headers, File destFile, Configurations configurations) {
            super(new Request(url, Method.GET, headers), configurations);
            this.destFile = destFile;
        }

        @Override
        protected FileResponse doRequest() {
            return fetchFile();
        }

        private FileResponse fetchFile() {
            Pair<Response, InputStream> res = executeRequestInternally();

            FileResponse response = new FileResponse(res.first);
            FileOutputStream os = null;

            try {
                byte[] data = new byte[res.second.available()];
                res.second.read(data);

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

            return response;
        }

        @Override
        protected void onDispose() {
            this.destFile = null;
        }
    }

    public static class FileUploadRequestExecutor extends RequestExecutor<TextResponse> {
        private final String fieldName;
        private File uploadingFile;
        private ResultCallback2<Request, Integer> progressCallback;
        private final String boundary;
        private static final String LINE_FEED = "\r\n";


        public FileUploadRequestExecutor(String url, Map<String, String> headers, String fieldName, File uploadingFile, Configurations configurations, ResultCallback2<Request, Integer> progressCallback) {
            super(new Request(url, Method.POST, headers), configurations);

            this.fieldName = fieldName;
            this.uploadingFile = uploadingFile;
            this.progressCallback = progressCallback;

            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";

            headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);

        }

        @Override
        protected TextResponse doRequest() {
            return uploadFile();
        }

        private TextResponse uploadFile() {
            Pair<Response, InputStream> res = executeRequestInternally(outputStream -> {
                try {
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, getConfigurations().charEncoding), true);

                    addFilePart(writer, fieldName, uploadingFile);

                    readFileAndWriteToStream(uploadingFile, outputStream);

                    writer.append(LINE_FEED);
                    writer.flush();

                    writer.append(LINE_FEED);
                    writer.flush();

                    writer.append("--" + boundary + "--");
                    writer.append(LINE_FEED);
                    writer.close();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            TextResponse response = new TextResponse(res.first);
            String text = null;

            try {
                text = new Helpers().readInputStream(res.second);
            } catch (IOException e) {
                response.setException(e);
            }

            response.setText(text);

            return response;
        }

        private void addFilePart(PrintWriter writer, String fieldName, File uploadFile) throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary)
                    .append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary")
                    .append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
        }

        private void readFileAndWriteToStream(File uploadingFile, OutputStream outputStream) throws Exception {
            FileInputStream inputStream = new FileInputStream(uploadingFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = uploadingFile.length();
            Handler handler = new Handler(Looper.getMainLooper());

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                if (progressCallback != null) {
                    int finalPercentCompleted = percentCompleted;
                    handler.post(() -> {
                        progressCallback.invoke(getRequest(), finalPercentCompleted);
                    });
                }
            }

            inputStream.close();
            outputStream.flush();
        }

        @Override
        protected void onDispose() {
            uploadingFile = null;
        }
    }

    private static class Helpers {

        String readInputStream(InputStream inputStream) throws IOException {
            String text = null;

            if (inputStream != null) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder stringBuffer = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }


                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                text = stringBuffer.toString();
            }

            return text;
        }
    }
}
