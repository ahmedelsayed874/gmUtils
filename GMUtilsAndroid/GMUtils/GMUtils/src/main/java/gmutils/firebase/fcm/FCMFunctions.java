package gmutils.firebase.fcm;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import gmutils.listeners.ResultCallback;

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
            @Nullable String firebaseProjectMessageKey
    );

    void getDeviceToken(ResultCallback<String> callback);

    void subscribeToTopics(List<String> topics, ResultCallback<Boolean> callback);

    void unsubscribeFromTopics(List<String> topics, ResultCallback<Boolean> callback);

    void unsubscribeFromSavedTopics(List<String> exceptedTopics, ResultCallback<Boolean> callback);

    void sendMessageToSpecificDevice(
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
    );

    void sendMessageToTopic(
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
    );

    void onActivityStarted(Bundle intentExtras);
}
