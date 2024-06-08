package gmutils.firebase.fcm;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import gmutils.json.JsonBuilder;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

///https://firebase.google.com/docs/cli?authuser=0#mac-linux-auto-script
///https://firebase.flutter.dev/docs/messaging/overview/
///https://pub.dev/packages/flutter_local_notifications#scheduled-notifications-and-daylight-saving-time
///
/// add those to android manifest
//         <meta-data
//             android:name="com.google.firebase.messaging.default_notification_channel_id"
//             android:value="@string/default_channel" />
//
//         <meta-data
//             android:name="com.google.firebase.messaging.default_notification_icon"
//             android:resource="@drawable/notif_icon" />
public interface FCMFunctions {
    FCMFunctions init(
            @NotNull Class<? extends FcmMessageHandler> fcmMessageHandlerClass,
            @NotNull FcmMessageHandler fcmMessageHandler,
            @Nullable ResultCallback<String> onDeviceTokenRefresh,
            @Nullable SendFcmMessageParameters sendFcmMessageParameters
    );

    void getDeviceToken(ResultCallback<String> callback);

    void subscribeToTopics(List<String> topics, ResultCallback<Map<String, Boolean>> callback);

    void unsubscribeFromTopics(List<String> topics, ResultCallback<Map<String, Boolean>> callback);

    void unsubscribeFromSavedTopics(List<String> exceptedTopics, ResultCallback<Map<String, Boolean>> callback);

    void sendMessageToSpecificDevice(
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
    );

    void sendMessageToTopic(
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
    );

    void onActivityStarted(Bundle intentExtras);

    class SendFcmCallbackArgs {
        public final boolean success;
        public final int httpCode;
        public final String response;
        public final String error;

        public SendFcmCallbackArgs(boolean success, int httpCode, String response, String error) {
            this.httpCode = httpCode;
            this.success = success;
            this.response = response;
            this.error = error;
        }

        @Override
        public String toString() {
            return "SendFcmCallbackArgs{" +
                    "success=" + success +
                    ", httpCode=" + httpCode +
                    ", response='" + response + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}
