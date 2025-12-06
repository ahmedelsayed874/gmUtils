package gmutils.firebase;

import gmutils.listeners.ResultCallback;
import gmutils.net.SimpleHTTPRequest;

public class FirebaseUtils {
    //region refineKeyName
    public static String refineKeyName(String name) {
        return name
                .trim()
                .replace(' ', '_')
                .replace('.', '_')
                .replace('$', '_')
                .replace('#', '_')
                .replace('@', '_')
                .replace('[', '_')
                .replace(']', '_')
                .replace('/', '_');
    }

    //endregion

    //region refinePath
    public static String refinePathFragmentNames(String path) throws Exception {
        var frags = path.split("/");//a,b
        StringBuilder pathBuilder = new StringBuilder();
        for (String value : frags) {
            value = refineKeyName(value);
            if (value.isEmpty()) throw new Exception("invalid_node_name");

            if (!pathBuilder.toString().isEmpty()) pathBuilder.append("/");
            pathBuilder.append(value);
        }
        path = pathBuilder.toString();
        return path;
    }

    //endregion

    //region refinePhoneNumber
    public static String refinePhoneNumber(String number) {
        return number
                .trim()
                .replace("-", "")
                .replace(" ", "")
                .replace(".", "")
                .replace("+", "")
                .replace("(", "")
                .replace("/", "")
                .replace(")", "")
                .replace("N", "")
                .replace(",", "")
                .replace("*", "")
                .replace(";", "")
                .replace("#", "");

    }

    //endregion

    //region isConnectionAvailable
    private static long _lastConnectionCheckTime = 0;
    private static boolean _lastConnectionCheckResult = false;

    public static void isConnectionAvailable(ResultCallback<Boolean> callback) {
        try {
            long now = System.currentTimeMillis();
            long diff = now - _lastConnectionCheckTime;
            _lastConnectionCheckTime = now;
            if (diff < 15000 && _lastConnectionCheckResult) callback.invoke(true);

            SimpleHTTPRequest.call(
                    true,
                    new SimpleHTTPRequest.Request(
                            "https://www.google.com",
                            SimpleHTTPRequest.Method.GET
                    ),
                    null,
                    (result1, result2) -> {
                        callback.invoke(result2.getException() == null);
                        _lastConnectionCheckResult = true;
                    }
            );

        } catch (Exception e) {
            _lastConnectionCheckResult = false;
            callback.invoke(false);
        }
    }
    //endregion
}
