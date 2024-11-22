package gmutils.firebase.fcm;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.Notifier;
import gmutils.app.BaseApplication;
import gmutils.backgroundWorkers.BackgroundTask;
import gmutils.json.JsonBuilder;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.listeners.ResultCallback3;
import gmutils.logger.Logger;
import gmutils.logger.LoggerAbs;
import gmutils.net.SimpleHTTPRequest;
import gmutils.storage.GeneralStorage;


public class FCM implements FCMFunctions {
    public static final String EXTRA_FCM_TIMESTAMP = "fcm.timestamp";
    public static final String EXTRA_GOOGLE_MESSAGE_ID = "google.message_id";
    public static final String EXTRA_FROM = "from";

    static FCM _instance;

    public static FCM instance() {
        if (_instance == null) _instance = new FCM();
        return _instance;
    }

    //----------------------------------------------------------------

    public final FirebaseMessaging firebaseMessaging;
    private ResultCallback<String> onDeviceTokenRefresh;
    private SendFcmMessageParameters sendFcmMessageParameters;
    private LoggerAbs logger;

    private FCM() {
        firebaseMessaging = FirebaseMessaging.getInstance();

        LoggerAbs loggerTmp = logger;
        logger = Logger.d();
        printInstallationHint();
        logger = loggerTmp;
    }

    private void printLog(LoggerAbs.ContentGetter content) {
        if (logger != null) {
            logger.print(content);
        }
    }

    public FCM setLogger(LoggerAbs logger) {
        this.logger = logger;
        return this;
    }

    public FCM printInstallationHint() {
        printLog(() -> "[Fcm.init()] -> " +
                "don\"t forget to use FCM.instance.redirectToPendingScreen(); " +
                "in your home screen"
        );
        printLog(() -> "[Fcm.init()] -> " +
                "don\"t forget to define this " +
                Notifier.DEFAULT_NOTIFICATION_CHANNEL_ID +
                " as " +
                "default notification channel (com.google.firebase.messaging.default_notification_channel_id)"
        );

        printLog(() -> "add this to Manifest File::\n" +
                "before <application />\n" +
                "<uses-permission android:name=\"android.permission.POST_NOTIFICATIONS\"/>\n" +
                "then those inside <application />" +
                "<service\n" +
                "    android:name=\"{packageName}.{FCMNotificationService extends gmutils.firebase.fcm.GmFirebaseMessagingService}\"\n" +
                "    android:enabled=\"true\"\n" +
                "    android:exported=\"false\">\n" +
                "    <intent-filter>\n" +
                "        <action android:name=\"com.google.firebase.MESSAGING_EVENT\" />\n" +
                "    </intent-filter>\n" +
                "</service>\n" +
                "\n" +
                "<!-- [optional] -->\n" +
                "<meta-data\n" +
                "    android:name=\"com.google.firebase.messaging.default_notification_icon\"\n" +
                "    android:resource=\"@drawable/fcm_notification_icon\" />\n" +
                "<meta-data\n" +
                "    android:name=\"com.google.firebase.messaging.default_notification_color\"\n" +
                "    android:resource=\"@color/colorAccent\" />\n" +
                "<meta-data\n" +
                "    android:name=\"com.google.firebase.messaging.default_notification_channel_id\"\n" +
                "    android:value=\"" + Notifier.DEFAULT_NOTIFICATION_CHANNEL_ID + "\" />\n" +
                "\n" +
                "\n------------------------------------------------------------\n" +
                "\n" +
                "also add this class::\n" +
                "class {FCMNotificationService} : gmutils.firebase.fcm.GmFirebaseMessagingService() {\n" +
                "     //TODO don't do anything here" +
                "}"
        );

        return this;
    }

    //----------------------------------------------------------------

    @Override
    public FCMFunctions init(
            @NotNull Class<? extends FcmMessageHandler> fcmMessageHandlerClass,
            @NotNull FcmMessageHandler fcmMessageHandler,
            @Nullable ResultCallback<String> onDeviceTokenRefresh,
            @Nullable SendFcmMessageParameters sendFcmMessageParameters
    ) {
        printLog(() -> "Fcm.init");

        this.onDeviceTokenRefresh = onDeviceTokenRefresh;
        this.sendFcmMessageParameters = sendFcmMessageParameters;

        //--------------------------------------------------------------------------

        GmFirebaseMessagingService.onMessage = fcmMessageHandler;

        GmFirebaseMessagingService.registerBackgroundMessageHandler(fcmMessageHandlerClass);

        if (onDeviceTokenRefresh != null) {
            getDeviceToken((s) -> this.onDeviceTokenRefresh.invoke(s));

            GmFirebaseMessagingService.onNewToken = (context, newToken) -> {
                this.onDeviceTokenRefresh.invoke(newToken);
            };
        }

        return this;
    }

    @Override
    public void getDeviceToken(ResultCallback<String> callback) {
        firebaseMessaging.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.invoke(task.getResult());
            }
        });
    }

    //----------------------------------------------------------------------------

    //region subscribe/unsubscribe TOPICs
    private GeneralStorage __prefs;

    private GeneralStorage _prefs() {
        if (__prefs == null) __prefs = GeneralStorage.getInstance("fcm-topics");
        return __prefs;
    }

    @NotNull
    private List<String> savedTopics() {
        var savedTopics = _prefs().retrieveList("FCM_Topics");
        if (savedTopics == null) savedTopics = new ArrayList<>(0);
        return savedTopics;
    }

    @Override
    public void subscribeToTopics(List<String> topics, ResultCallback<Map<String, Boolean>> callback) {
        if (topics == null || topics.isEmpty()) {
            if (callback != null) callback.invoke(null);
            return;
        }

        var result = new HashMap<String, Boolean>();

        var savedTopics = savedTopics();
        if (savedTopics.size() > 0) {
            for (int i = 0; i < topics.size(); i++) {
                var topic = topics.get(i);
                var idx = topics.indexOf(topic);
                if (idx >= 0) {
                    topics.remove(idx);
                    result.put(topic, true);
                } else {
                    result.put(topic, false);
                }
            }
        }

        if (topics.isEmpty()) {
            if (callback != null) {
                callback.invoke(result);
            }
            return;
        }

        for (String topic : topics) {
            try {
                firebaseMessaging.subscribeToTopic(topic);
                printLog(() -> "FCM: subscribed to \"" + topic + "\"");
                result.put(topic, true);
            } catch (Exception e) {
                printLog(() -> "FCM: failed to subscribe to \"" + topic + "\"");
                result.put(topic, false);
            }
        }

        if (callback != null) callback.invoke(result);

        _prefs().saveToList("FCM_Topics", topics);
    }

    @Override
    public void unsubscribeFromTopics(List<String> topics, ResultCallback<Map<String, Boolean>> callback) {
        if (topics == null) {
            if (callback != null) callback.invoke(null);
            return;
        }

        var result = new HashMap<String, Boolean>();

        for (String topic : topics) {
            try {
                firebaseMessaging.unsubscribeFromTopic(topic);
                printLog(() -> "FCM: unsubscribe from " + topic);
                result.put(topic, true);
            } catch (Exception e) {
                printLog(() -> "FCM: failed to unsubscribe from " + topic);
                result.put(topic, false);
            }
        }

        if (callback != null) callback.invoke(result);
    }

    @Override
    public void unsubscribeFromSavedTopics(List<String> exceptedTopics, ResultCallback<Map<String, Boolean>> callback) {
        var savedTopics = savedTopics();

        if (exceptedTopics != null && exceptedTopics.size() > 0) {
            savedTopics.removeAll(exceptedTopics);
        }

        if (savedTopics.size() > 0) {
            unsubscribeFromTopics(savedTopics, callback);
            for (var t : savedTopics) {
                _prefs().removeFromList("FCM_Topics", t);
            }
        } else {
            if (callback != null) callback.invoke(null);
        }
    }

    //endregion

    //----------------------------------------------------------------------------

    //region Send FCM message

    @Override
    public void sendMessageToSpecificDevice(
            String deviceToken,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            JSONObject dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<SendFcmCallbackArgs> callback
    ) {
        _sendMessageTo(
                deviceToken + "",
                null,
                //
                title,
                message,
                //
                isDataNotification,
                dataPayload,
                //
                channelId,
                soundFileName,
                //
                callback
        );
    }

    @Override
    public void sendMessageToTopic(
            String topic,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            JSONObject dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<SendFcmCallbackArgs> callback
    ) {
        _sendMessageTo(
                null,
                "" + topic,
                //
                title,
                message,
                //
                isDataNotification,
                dataPayload,
                //
                channelId,
                soundFileName,
                //
                callback
        );
    }

    public static final String PAYLOAD_KEY_NAME = "data";

    /**
     * https://firebase.google.com/docs/cloud-messaging/migrate-v1?hl=en&authuser=0#java
     * https://firebase.google.com/docs/cloud-messaging/send-message?authuser=0
     * https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?authuser=0
     * <deprecated> https://firebase.google.com/docs/cloud-messaging/http-server-ref
     * <deprecated> https://firebase.google.com/docs/cloud-messaging/send-message?hl=en&authuser=0#send-messages-to-topics-legacy
     * FIVE TOPICS IN ONE REQUEST
     */
    private void _sendMessageTo(
            String fcmToken,
            String topic,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            JSONObject dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<SendFcmCallbackArgs> callback
    ) {
        try {
            Class.forName("com.google.auth.oauth2.GoogleCredentials");
        } catch (Exception e) {
            throw new RuntimeException(
                    "this dependency is required 'com.google.api-client:google-api-client:2.4.0' .... " +
                            "add it to gradle .... " +
                            "also add this: packagingOptions { resources.excludes.add(\"META-INF/*\") } " +
                            "inside: android {..}"
            );
        }

        if (sendFcmMessageParameters == null)
            throw new IllegalArgumentException("sendFcmMessageParameters must not be null .. use FCM.init()");

        //----------------------------------------------

        BackgroundTask.run(() -> {
            var url = "https://fcm.googleapis.com/v1/projects/" + sendFcmMessageParameters.firebaseProjectId + "/messages:send";

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; UTF-8");
            headers.put("Authorization", "Bearer " + getAccessToken());

            JsonBuilder messageJson = buildRequestData(
                    fcmToken,
                    topic,
                    title,
                    message,
                    isDataNotification,
                    dataPayload,
                    channelId,
                    soundFileName
            );
            var requestBody = messageJson.toString();

            HttpRequest httpRequest = new HttpRequest(url, headers, requestBody);

            printLog(() -> "sendFcmNotification(" + httpRequest + ")");

            return httpRequest;
        }, (httpRequest) -> {
            httpExecuteDelegate.invoke(httpRequest, callback);
        });
    }

    private JsonBuilder buildRequestData(
            String fcmToken,
            String topic,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            JSONObject dataPayload,
            //
            String channelId,
            String soundFileName
    ) {
        //https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?authuser=0
        JsonBuilder notificationBody = JsonBuilder.ofJsonObject();
        if (!TextUtils.isEmpty(fcmToken)) {
            notificationBody.add("token", fcmToken);
        }
        //
        else if (!TextUtils.isEmpty(topic)) {
            notificationBody.add("topic", topic);
        } else {
            throw new IllegalArgumentException("topic or token must set");
        }

        if (!isDataNotification) {
            notificationBody.add(
                    "notification",
                    JsonBuilder.ofJsonObject()
                            .add("title", title)
                            .add("body", message)
            );
        }

        notificationBody.add(
                "android",
                JsonBuilder.ofJsonObject()
                        //.addString("collapse_key", "")
                        //.addString("priority", "NORMAL" /*or HIGH*/)
                        .add(
                                "notification",
                                JsonBuilder.ofJsonObject()
                                        .add("title", title)
                                        .add("body", message)
                                        //.add("notification_priority", "PRIORITY_DEFAULT") //PRIORITY_HIGH - PRIORITY_MAX - PRIORITY_LOW - PRIORITY_MIN
                                        .add("channel_id", channelId)
                                        .add("sound", soundFileName)
                                //.add("icon", "stock_ticker_update")
                                //.add("color", "#7e55c3")
                                //.add("tag", "")
                                //.add("click_action", "")
                                //.add("ticker", "")
                                //.add("sticky", true)
                                //.add("default_sound", true)
                                //.add("visibility", "PUBLIC") //PRIVATE - SECRET
                                //.add("notification_count", 1)
                                //.add("image", "http://....")
                                //.add("direct_boot_ok", false);
                        )
        );

        notificationBody.add(
                "apns",
                JsonBuilder.ofJsonObject()
                        /*.addSubObject(
                                "headers",
                                JsonBuilder.ofJsonObject()
                        )*/
                        .add(
                                "payload",
                                JsonBuilder.ofJsonObject()
                                        .add(
                                                "aps",
                                                JsonBuilder.ofJsonObject()
                                                        .add("title", title)
                                                        .add("body", message)
                                        )
                        )
                        .add("sound", soundFileName)
        );

        notificationBody.add(PAYLOAD_KEY_NAME, dataPayload);

        JsonBuilder messageJson = JsonBuilder.ofJsonObject();
        messageJson.add("message", notificationBody);

        return messageJson;
    }

    private String getAccessToken() {
        final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
        final String[] SCOPES = {MESSAGING_SCOPE};

        InputStream serviceAccountFile;
        if (sendFcmMessageParameters.firebaseServiceAccountFileSource == SendFcmMessageParameters.FileSource.RawResources) {
            serviceAccountFile = BaseApplication
                    .current().
                    getResources().
                    openRawResource((Integer) sendFcmMessageParameters.firebaseServiceAccountFileAddress);
        } else if (sendFcmMessageParameters.firebaseServiceAccountFileSource == SendFcmMessageParameters.FileSource.Storage) {
            try {
                serviceAccountFile = new FileInputStream((String) sendFcmMessageParameters.firebaseServiceAccountFileAddress);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException(sendFcmMessageParameters.firebaseServiceAccountFileSource + " type is not handled");
        }

        GoogleCredentials googleCredentials = null;
        try {
            googleCredentials = GoogleCredentials
                    .fromStream(serviceAccountFile)
                    .createScoped(Arrays.asList(SCOPES));

            googleCredentials.refresh();

            var value = googleCredentials.getAccessToken().getTokenValue();

            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                serviceAccountFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class HttpRequest {
        public final String url;
        public final Map<String, String> headers;
        public final String body;

        private HttpRequest(String url, Map<String, String> headers, String body) {
            this.url = url;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public String toString() {
            StringBuilder headersStr = new StringBuilder("{");
            if (headers != null) {
                for (String k : headers.keySet()) {
                    if (headersStr.length() > 1) headersStr.append(",");
                    headersStr
                            .append("\n\t")
                            .append(k)
                            .append(": ")
                            .append(headers.get(k));
                }
            }
            headersStr.append("}");

            return "HttpRequest{\n" +
                    "url='" + url + "'\n" +
                    ", headers=" + headersStr + "\n" +
                    ", body='" + body + "'\n" +
                    '}';
        }
    }

    @NotNull
    public ActionCallback2<HttpRequest, ResultCallback<SendFcmCallbackArgs>, Void> httpExecuteDelegate
            = (request, callback) -> {
        SimpleHTTPRequest.post(
                request.url,
                request.headers,
                request.body,
                null,
                (request2, response) -> {
                    //success response: { "name":"projects/{project_id}/messages/{message_id}" }
                    printLog(() -> "sendFcmNotification(RESPONSE::: " + response + ")");

                    callback.invoke(
                            new FCMFunctions.SendFcmCallbackArgs(
                                    200 == response.getCode(),
                                    0 + response.getCode(),
                                    "" + response.getText(),
                                    "" + (response.getException() != null ? response.getException().getMessage() :
                                            response.getError())
                            )
                    );
                }
        );

        return null;
    };

    //endregion

    //------------------------------------------------------------------------------

    private Bundle lastReceivedNotification;

    public Bundle lastReceivedNotification() {
        return lastReceivedNotification;
    }

    @Override
    public void onActivityStarted(Bundle intentExtras) {
        printLog(() -> "Fcm.onActivityStarted -> extras: " + intentExtras);
        if (intentExtras == null) return;

        if (
                intentExtras.get(EXTRA_GOOGLE_MESSAGE_ID) != null ||
                        intentExtras.get(EXTRA_FCM_TIMESTAMP) != null
        ) {
            lastReceivedNotification = intentExtras;
        }
    }

}
