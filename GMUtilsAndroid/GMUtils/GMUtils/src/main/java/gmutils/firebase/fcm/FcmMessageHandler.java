package gmutils.firebase.fcm;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public interface FcmMessageHandler {
    @NotNull
    FcmNotificationProperties onMessageReceived(@NotNull Context context, @NotNull RemoteMessage message);
}
