package gmutils.firebase.fcm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import gmutils.listeners.ResultCallback;

public class FCMParameters {
    public final int notificationIconRes;
    public final int iconBackgroundColor;

    public final Class<? extends FcmMessageHandler> fcmMessageHandlerClass;
    public final FcmMessageHandler fcmMessageHandler;
    public final ResultCallback<String> onDeviceTokenRefresh;

    ///get from the firebase console (settings) .... ex:: 'AAAAKbiiUMw:APA91...JRP';
    public final String firebaseProjectMessageKeyForSend;
//    public final ActionCallback4<Context, String, String, FcmNotificationProperties, Integer> showNotificationDelegate;


    public FCMParameters(
            int notificationIconRes,
            int iconBackgroundColor,
            @NotNull Class<? extends FcmMessageHandler> fcmMessageHandlerClass,
            @NotNull FcmMessageHandler fcmMessageHandler,
            @Nullable ResultCallback<String> onDeviceTokenRefresh,
            @Nullable String firebaseProjectMessageKeyForSend
//            @Nullable ActionCallback4<Context, String, String, FcmNotificationProperties, Integer> showNotificationDelegate
    ) {
        this.notificationIconRes = notificationIconRes;
        this.iconBackgroundColor = iconBackgroundColor;
        this.fcmMessageHandlerClass = fcmMessageHandlerClass;
        this.fcmMessageHandler = fcmMessageHandler;
        this.onDeviceTokenRefresh = onDeviceTokenRefresh;
        this.firebaseProjectMessageKeyForSend = firebaseProjectMessageKeyForSend;
//        this.showNotificationDelegate = showNotificationDelegate;
    }
}