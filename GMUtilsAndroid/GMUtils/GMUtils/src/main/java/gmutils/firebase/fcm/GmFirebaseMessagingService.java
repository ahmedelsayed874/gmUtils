package gmutils.firebase.fcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import gmutils.Notifier;
import gmutils.R;
import gmutils.app.BaseApplication;
import gmutils.logger.Logger;
import gmutils.storage.GeneralStorage;
import gmutils.storage.SettingsStorage;

public class GmFirebaseMessagingService extends FirebaseMessagingService {
//    public interface FcmMessageHandler {
//        void onMessageReceived(Context context, RemoteMessage message);
//    }

    public interface FcmTokenChangeHandler {
        void onNewToken(Context context, String token);
    }

    //---------------------------------------------------------------------------------------------

    public static FcmMessageHandler onMessage;
    public static FcmTokenChangeHandler onNewToken;

    public static void registerBackgroundMessageHandler(Class<? extends FcmMessageHandler> handlerClass) {
        try {
            if (handlerClass.isAnonymousClass()) throw new IllegalStateException();
            handlerClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Handler class must extend \"" + FcmMessageHandler.class.getName() + "\" " +
                            "and have zero argument constructor .. also anonymous class not accepted"
            );
        }

        var storage = GeneralStorage.getInstance(GmFirebaseMessagingService.class.getSimpleName());
        storage.save("hc", handlerClass.getName());
    }

    private void dispose() {
        onMessage = null;
        onNewToken = null;
    }

    //---------------------------------------------------------------------------------------------
    
    @Override
    public void onNewToken(@NonNull String token) {
        Logger.d().printMethod(() -> token);
        super.onNewToken(token);

        if (onNewToken != null) {
            onNewToken.onNewToken(this, token);
            BaseApplication.current().registerOnDispose(this.getClass(), this::dispose);
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Logger.d().printMethod(() -> {
            StringBuilder sb = new StringBuilder();
            
            sb.append("MessageId: ").append(message.getMessageId()).append("\n");
            
            if (message.getNotification() != null) {
                sb.append("Title: ").append(message.getNotification().getTitle()).append("\n");
                sb.append("Body: ").append(message.getNotification().getBody()).append("\n");
            }

            sb.append("Data: ");
            for (String key : message.getData().keySet()) {
                sb.append(key).append(": ").append(message.getData().get(key)).append("\n");
            }
            
            return sb;
        });

        super.onMessageReceived(message);

        FcmNotificationProperties fcmNotificationProperties = null;

        if (onMessage != null) {
            fcmNotificationProperties = onMessage.onMessageReceived(this, message);
            BaseApplication.current().registerOnDispose(this.getClass(), this::dispose);
        } else {
            var storage = GeneralStorage.getInstance(GmFirebaseMessagingService.class.getSimpleName());
            FcmMessageHandler classHandler = null;
            try {
                var classPath = storage.retrieve("hc", "");
                Class<?> aClass = Class.forName(classPath);
                classHandler = (FcmMessageHandler) aClass.newInstance();
            } catch (Exception e) {
                Logger.d().printMethod(() -> e);
            }

            if (classHandler != null) {
                fcmNotificationProperties = classHandler.onMessageReceived(this, message);
            }
        }

        if (fcmNotificationProperties == null) {
            fcmNotificationProperties = new FcmNotificationProperties(
                    0,
                    R.color.gmAccent
            );
        }

            boolean en = SettingsStorage.Language.usingEnglish();
            releaseNotification(this, message, fcmNotificationProperties, en);

    }

    protected int releaseNotification(
            Context context,
            RemoteMessage message,
            FcmNotificationProperties notificationProperties,
            boolean en
    ) {
        Logger.d().printMethod(() ->
                "FCM._popupNotification(message: {" +
                        "id=" + message.getMessageId() + ", " +
                        "payload= " + message.getData() + "}, " +
                        "en: " + en + ")"
        );

        String title;
        if (!TextUtils.isEmpty(notificationProperties.getCustomTitle())) {
            title = notificationProperties.getCustomTitle();
        }
        //
        else if (message.getNotification() != null && !TextUtils.isEmpty(message.getNotification().getTitle())) {
            title = message.getNotification().getTitle();
        }
        //
        else {
            title = en ? "• New Notification:" : "• تنبيه جديد:";
        }

        String body;
        if (!TextUtils.isEmpty(notificationProperties.getCustomBody())) {
            body = notificationProperties.getCustomBody();
        }
        //
        else if (message.getNotification() != null && !TextUtils.isEmpty(message.getNotification().getBody())) {
            body = message.getNotification().getBody();
        }
        //
        else {
            body = "";
        }

        if (notificationProperties.isAllowPopup()) {
            return releaseNotification(context, title, body, notificationProperties);
        }

        return 0;
    }

    protected int releaseNotification(
            Context context,
            @NotNull String title,
            @NotNull String body,
            FcmNotificationProperties notificationProperties
    ) {
        Notifier notifier = Notifier.getInstance(
                notificationProperties.notificationIconRes,
                notificationProperties.iconBackgroundColor
        );

        notifier.setNotificationChannel(
                context,
                notificationProperties.getChannelId(),
                notificationProperties.getChannelName()
        );

        var onClick = notificationProperties.getOnClick();
        Intent intent;
        PendingIntent pendingIntent = null;

        if (onClick != null) {
            intent = onClick.intent;
            intent.putExtra(FCM.PAYLOAD_KEY_NAME, notificationProperties.getPayload());
            intent.putExtra(FCM.EXTRA_FCM_TIMESTAMP, System.currentTimeMillis());

            if (onClick.intentTarget != FcmNotificationProperties.OnClick.IntentTarget.Activity) {
                int pendingIntentFlag = PendingIntent.FLAG_CANCEL_CURRENT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntentFlag |= PendingIntent.FLAG_IMMUTABLE;
                }

                if (onClick.intentTarget == FcmNotificationProperties.OnClick.IntentTarget.Service) {
                    pendingIntent = PendingIntent.getService(
                            context,
                            intent.getAction().hashCode(),
                            intent,
                            pendingIntentFlag
                    );
                }
                //
                else if (onClick.intentTarget == FcmNotificationProperties.OnClick.IntentTarget.Broadcast) {
                    pendingIntent = PendingIntent.getService(
                            context,
                            intent.getAction().hashCode(),
                            intent,
                            pendingIntentFlag
                    );
                }
                //
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (onClick.intentTarget == FcmNotificationProperties.OnClick.IntentTarget.ForegroundService) {
                            pendingIntent = PendingIntent.getForegroundService(
                                    context,
                                    intent.getAction().hashCode(),
                                    intent,
                                    pendingIntentFlag
                            );
                        }
                    }
                }
            }
        } else {
            intent = context.getPackageManager().getLaunchIntentForPackage(
                    context.getPackageName()
            );
            if (intent != null) {
                intent.putExtra(FCM.PAYLOAD_KEY_NAME, notificationProperties.getPayload());
                intent.putExtra(FCM.EXTRA_FCM_TIMESTAMP, System.currentTimeMillis());
            }
        }

        if (pendingIntent != null) {
            notifier.createNotification(context, title, body, pendingIntent);
        } else {
            notifier.createNotification(context, title, body, intent);
        }

        notifier.setVisibility(notificationProperties.getVisibility());
        notifier.setPriority(notificationProperties.getPriority());
        notifier.setAutoCancel(notificationProperties.canDismiss());

        if (notificationProperties.getNotificationStyle() != null) {
            notifier.setStyle(notificationProperties.getNotificationStyle());
        }

        if (notificationProperties.getActions() != null) {
            for (Pair<String, PendingIntent> action : notificationProperties.getActions()) {
                notifier.addAction(action.first, action.second);
            }
        }

        return notifier.release(
                context,
                notificationProperties.getNotificationId()
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onMessageSent(@NonNull String msgId) {
        Logger.d().printMethod(() -> msgId);
        super.onMessageSent(msgId);
    }

    @Override
    public void onDeletedMessages() {
        Logger.d().printMethod();
        super.onDeletedMessages();
    }
}
