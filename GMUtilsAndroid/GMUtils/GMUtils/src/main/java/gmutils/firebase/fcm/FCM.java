package gmutils.firebase.fcm;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.Notifier;
import gmutils.json.JsonBuilder;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ResultCallback;
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
    private String firebaseProjectMessageKey;//get from the firebase console (settings) .... ex:: 'AAAAKbiiUMw:APA91...JRP';

    private FCM() {

        firebaseMessaging = FirebaseMessaging.getInstance();
    }

    private void printLog(LoggerAbs.ContentGetter content) {
        if (logger != null) {
            logger.print(content);
        }
    }

    public Logger logger;

    //----------------------------------------------------------------

    @Override
    public FCMFunctions init(
            @NotNull Class<? extends FcmMessageHandler> fcmMessageHandlerClass,
            @NotNull FcmMessageHandler fcmMessageHandler,
            @Nullable ResultCallback<String> onDeviceTokenRefresh,
            @Nullable String firebaseProjectMessageKey
    ) {
        printLog(() -> "Fcm.init");

        this.onDeviceTokenRefresh = onDeviceTokenRefresh;
        this.firebaseProjectMessageKey = firebaseProjectMessageKey;

        //--------------------------------------------------------------------------

        GmFirebaseMessagingService.onMessage = fcmMessageHandler;

        GmFirebaseMessagingService.registerBackgroundMessageHandler(fcmMessageHandlerClass);

        if (onDeviceTokenRefresh != null) {
            getDeviceToken((s) -> this.onDeviceTokenRefresh.invoke(s));

            GmFirebaseMessagingService.onNewToken = (context, newToken) -> {
                this.onDeviceTokenRefresh.invoke(newToken);
            };
        }

        //region hints logs
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
        //endregion

        return this;
    }

    @Override
    public void getDeviceToken(ResultCallback<String> callback) {
        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NotNull Task<String> task) {
                if (task.isSuccessful()) {
                    callback.invoke(task.getResult());
                }
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
    public void subscribeToTopics(List<String> topics, ResultCallback<Boolean> callback) {
        unsubscribeFromSavedTopics(topics, null);

        if (topics == null || topics.isEmpty()) return;

        var savedTopics = savedTopics();
        if (savedTopics.size() > 0) {
            topics.removeAll(savedTopics);
        }

        if (topics.isEmpty()) return;

        for (String topic : topics) {
            try {
                firebaseMessaging.subscribeToTopic(topic);
                printLog(() -> "FCM: subscribed to \"" + topic + "\"");
            } catch (Exception e) {
                printLog(() -> "FCM: failed to subscribe to \"" + topic + "\"");
            }
        }

        _prefs().saveToList("FCM_Topics", topics);
    }

    @Override
    public void unsubscribeFromTopics(List<String> topics, ResultCallback<Boolean> callback) {
        if (topics == null) return;

        for (String topic : topics) {
            try {
                firebaseMessaging.unsubscribeFromTopic(topic);
                printLog(() -> "FCM: unsubscribe from " + topic);
            } catch (Exception e) {
                printLog(() -> "FCM: failed to unsubscribe from " + topic);
            }
        }
    }

    @Override
    public void unsubscribeFromSavedTopics(List<String> exceptedTopics, ResultCallback<Boolean> callback) {
        var savedTopics = savedTopics();

        if (exceptedTopics != null && exceptedTopics.size() > 0) {
            savedTopics.removeAll(exceptedTopics);
        }

        if (savedTopics.size() > 0) {
            unsubscribeFromTopics(savedTopics, null);
            for (var t : savedTopics) {
                _prefs().removeFromList("FCM_Topics", t);
            }
        }
    }

    //endregion

    //----------------------------------------------------------------------------

    //region Send FCM message

    @Override
    public void sendMessageToSpecificDevice(
            @Nullable Integer notificationId,
            String deviceToken,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            String dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<Boolean> callback
    ) {
        _sendMessageTo(
                notificationId,
                deviceToken,
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
            @Nullable Integer notificationId,
            String topic,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            String dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<Boolean> callback
    ) {
        _sendMessageTo(
                notificationId,
                "/topics/$topic",
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
     * https://firebase.google.com/docs/cloud-messaging/http-server-ref
     * https://firebase.google.com/docs/cloud-messaging/send-message?hl=en&authuser=0#send-messages-to-topics-legacy
     * FIVE TOPICS IN ONE REQUEST
     */
    private void _sendMessageTo(
            @Nullable Integer notificationId,
            String to,
            //
            String title,
            String message,
            //
            boolean isDataNotification,
            String dataPayload,
            //
            String channelId,
            String soundFileName,
            //
            ResultCallback<Boolean> callback
    ) {
        JsonBuilder notificationBody = new JsonBuilder(new JSONObject());
        notificationBody.addString("to", to);

        notificationBody.addSubObject(
                "android",
                new JsonBuilder(new JSONObject())
                        .addString("title", title)
                        .addString("body", message)
                        .addString("android_channel_id", channelId)
                        .addString("channel_id", channelId)
                        .addString("sound", soundFileName)
        );

        notificationBody.addSubObject(
                "apns",
                new JsonBuilder(new JSONObject())
                        .addSubObject(
                                "aps",
                                new JsonBuilder(new JSONObject())
                                        .addString("title", title)
                                        .addString("body", message)
                        )
                        .addString("title", title)
                        .addString("body", message)
                        .addString(PAYLOAD_KEY_NAME, dataPayload)
                        .addString("sound", soundFileName)
        );

        notificationBody.addString("priority", "high"); //or normal

        notificationBody.addString(
                PAYLOAD_KEY_NAME,//must be "data",
                dataPayload
        );

        if (!isDataNotification) {
            notificationBody.addSubObject(
                    "notification",
                    new JsonBuilder(new JSONObject())
                            .addString("title", title)
                            .addString("body", message)
            );
        }

        //----------------------------------------------

        var url = "https://fcm.googleapis.com/fcm/send";

        var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Authorization", "key=" + firebaseProjectMessageKey);

        var requestBody = notificationBody.toString();

        httpExecuteDelegate.invoke(
                new HttpRequest(
                        url,
                        headers,
                        requestBody
                ),
                callback
        );
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
    }

    @NotNull
    public ActionCallback2<HttpRequest, ResultCallback<Boolean>, Void> httpExecuteDelegate
            = (request, callback) -> {
        SimpleHTTPRequest.post(
                request.url,
                request.headers,
                request.body,
                null,
                (request2, response) -> {
                    //return response; //{ "message_id": 3598509887081198072 }
                    printLog(() -> "" +
                            "sendFcmNotification(notification: " + request.body + ")" +
                            "\n\n-----------------------------------------------------------\n\n" +
                            "RESPONSE::: " + response.getText()
                    );

                    int code = response.getCode();
                    callback.invoke(code == 200);
                });

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
