package gmutils.firebase.fcm;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public interface FcmMessageHandler {
    @NotNull
    FcmNotificationProperties onMessageReceived(Context context, RemoteMessage message);
}
