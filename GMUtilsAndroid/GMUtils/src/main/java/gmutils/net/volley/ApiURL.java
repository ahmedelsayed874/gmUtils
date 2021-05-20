package gmutils.net.volley;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.android.volley.Request;
import gmutils.TextHelper;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

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
public final class ApiURL {

    private ApiURL() {
    }

    //-------------------------------------------------------------------------------//

    public static String generateBasicAuthorizationCredential(String identifier, String password) {
        String auth = "Basic " +  TextHelper.createInstance().encodeTextByBase64(identifier + ":" + password);
        return auth;
    }

    //-------------------------------------------------------------------------------//

    public interface HEADER_CONST {
        String KEY_AUTHORIZATION = "Authorization";
        String KEY_ACCEPT = "Accept";
        String KEY_CONTENT_TYPE = "Content-Type";
        String VALUE_URL_ENCODED = "application/x-www-form-urlencoded";
        String VALUE_PLAIN_TEXT = "text/plain";
        String VALUE_HTML = "text/html";
        String VALUE_JSON = "application/json";
        String VALUE_PDF = "application/pdf";
    }

    //-------------------------------------------------------------------------------//

    public abstract static class Parameters {
        private final Map<String, String> params = new ArrayMap<>();
        private final Map<String, Bitmap> imagesParams = new ArrayMap<>();
        private final Map<String, Uri> filesParams = new ArrayMap<>();

        public Parameters put(String key, String value) {
            if (value == null) value = "";
            params.put(key, value);
            return this;
        }

        public Parameters put(String key, Integer value) {
            String valueStr = value + "";
            if (value == null) valueStr = "";
            return put(key, valueStr);
        }

        public Parameters put(String key, Float value) {
            String valueStr = value + "";
            if (value == null) valueStr = "";
            return put(key, valueStr);
        }

        public Parameters put(String key, Double value) {
            String valueStr = value + "";
            if (value == null) valueStr = "";
            return put(key, valueStr);
        }

        public Parameters put(String key, Bitmap image) {
            imagesParams.put(key, image);
            return this;
        }

        public Parameters put(String key, Uri file) {
            filesParams.put(key, file);
            return this;
        }


        public Parameters remove(String key) {
            params.remove(key);
            imagesParams.remove(key);
            filesParams.remove(key);
            return this;
        }


        public Map<String, String> getParams() {
            return params;
        }

        public Map<String, Bitmap> getImagesParams() {
            return imagesParams;
        }

        public Map<String, Uri> getFilesParams() {
            return filesParams;
        }

        public boolean hasMultiPartParams() {
            return imagesParams.size() > 0 || filesParams.size() > 0;
        }

        public String getQueryText() {
            String query = "";

            if (params.size() > 0) {
                query += "?";

                Set<String> keys = params.keySet();
                for (String key : keys) {
                    query += key + "=" + params.get(key) + "&";
                }

                query = query.substring(0, query.length() - 1);
            }

            return query;
        }

        @Override
        public String toString() {
            String keyVal = "";

            Map<String, String> params = getParams();
            for (String key : params.keySet()) {
                keyVal += "\t" + key + ": " + params.get(key) + "\n";
            }

            Map<String, Bitmap> imagesParams = getImagesParams();
            for (String key : imagesParams.keySet()) {
                keyVal += "\t" + key + ": " + imagesParams.get(key) + "\n";
            }

            Map<String, Uri> filesParams = getFilesParams();
            for (String key : filesParams.keySet()) {
                keyVal += "\t" + key + ": " + filesParams.get(key) + "\n";
            }

            return keyVal;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this == obj) return true;

            if (obj instanceof Parameters) {
                return params.equals(((Parameters) obj).params);
            }

            return false;
        }
    }

    public static class Body {
        private byte[] body;
        private String contentType = HEADER_CONST.VALUE_JSON;

        /**
         * @param body
         * @param contentType ({@link HEADER_CONST#VALUE_URL_ENCODED}{@link HEADER_CONST#VALUE_JSON}{@link HEADER_CONST#VALUE_PLAIN_TEXT}}
         */
        public Body(String body, String contentType) {
            setBody(body);
            setContentType(contentType);
        }

        public Body(String body) {
            setBody(body);
        }

        public void setBody(String body) {
            this.body = body.getBytes();
        }

        public void setContentType(String contentType) {
            if (!TextUtils.isEmpty(contentType)) {
                this.contentType = contentType;
            }
        }

        public byte[] getBody() {
            return body;
        }

        public String getContentType() {
            return contentType;
        }

        @Override
        public String toString() {
            return "\t" + new String(body) + "\n\n\tContentType: " + getContentType();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) return false;
            if (this == obj) return true;

            if (obj instanceof Body) {
                if (!Arrays.equals(this.body, ((Body) obj).body)) return false;
                return TextUtils.equals(this.contentType, ((Body) obj).contentType);
            }

            return false;
        }
    }

    //-------------------------------------------------------------------------------//

    public interface IURL {
        int getRequestMethod();

        String getEndPointURL();

        String getFinalURL();

        Map<String, String> getHeaders();

        Parameters getParams();

        Body getBody();
    }

    public abstract static class getURL implements IURL {
        private Parameters params;

        public getURL() {
        }

        public getURL(Parameters params) {
            this.params = params;
            if (params == null)
                throw new RuntimeException("Parameters can not be null for:\n" + getEndPointURL());
        }

        @Override
        public int getRequestMethod() {
            return Request.Method.GET;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new ArrayMap<>();
            headers.put(HEADER_CONST.KEY_AUTHORIZATION, getAuthorizationCredential("Bearer", ""));
            return headers;
        }

        public String getAuthorizationCredential(String type, String value) {
            return type + " " + value;
        }

        @Override
        public Parameters getParams() {
            if (params == null) {
                params = new Parameters() {
                };
            }

            return params;
        }

        @Override
        public Body getBody() {
            return null;
        }

        @Override
        public String getFinalURL() {
            String url = getEndPointURL();

            if (getRequestMethod() == Request.Method.GET) {
                if (getParams() != null) {
                    url += getParams().getQueryText();
                }
            }

            /*try {
                return URLEncoder.encode(url, "UTF-8");
            } catch (Exception e) {
                return url.replace(" ", "%20");
            }*/

            return url.replace(" ", "%20");
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(getFinalURL());
            sb.append("\n");

            sb.append("Method:");
            sb.append("\n");
            sb.append("\t" + getRequestMethod() + "\t {0:GET, 1:POST, 2:PUT, 3:DELETE}");
            sb.append("\n.\n");

            Map<String, String> headers = getHeaders();
            if (headers != null) {
                sb.append("Headers:");
                sb.append("\n");

                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    sb.append("\t" + key + ": " + headers.get(key) + "\n");
                }
                sb.append(".\n");
            }

            Parameters params = getParams();
            if (params != null) {
                sb.append("Parameters:");
                sb.append("\n");
                sb.append(params.toString());
                sb.append(".\n");
            }

            Body body = getBody();
            if (body != null) {
                sb.append("Body:");
                sb.append("\n");
                sb.append(body.toString());
                sb.append("\n.\n");
            }

            return sb.toString();
        }
    }

    public abstract static class postURL extends getURL {
        private Body body;

        public postURL(Parameters params) {
            super(params);
        }

        public postURL(@NonNull Body body) {
            this.body = body;
            if (body == null)
                throw new RuntimeException("Body can not be null for:\n" + getEndPointURL());
        }

        @Override
        public int getRequestMethod() {
            return Request.Method.POST;
        }

        @Override
        public Body getBody() {
            return body;
        }

    }

    public abstract static class putURL extends postURL {

        protected putURL(Parameters params) {
            super(params);
        }

        @Override
        public int getRequestMethod() {
            return Request.Method.PUT;
        }

    }

    public abstract static class deleteURL extends postURL {

        protected deleteURL(Parameters params) {
            super(params);
        }

        @Override
        public int getRequestMethod() {
            return Request.Method.DELETE;
        }

    }


}
