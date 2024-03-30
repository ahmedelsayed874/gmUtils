package gmutils.firebase.fcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import gmutils.Notifier;

public class FcmNotificationProperties {
    public static class OnClick {
        public enum IntentTarget {
            Activity,
            Service,
            @RequiresApi(Build.VERSION_CODES.O)
            ForegroundService,
            Broadcast
        }

        public final Intent intent;
        public final IntentTarget intentTarget;

        public OnClick(@NotNull Intent intent, @NotNull IntentTarget intentTarget) {
            this.intent = intent;
            this.intentTarget = intentTarget;
        }
    }

    public final int notificationIconRes;
    public final int iconBackgroundColor;

    private boolean allowPopup = true;
    private int notificationId = 1;
    private String title;
    private String body;
    private String payload;
    private String channelId = Notifier.DEFAULT_NOTIFICATION_CHANNEL_ID;
    private String channelName = Notifier.DEFAULT_NOTIFICATION_CHANNEL_Name;
    private NotificationCompat.Style notificationStyle;
    private List<Pair<String, PendingIntent>> actions;
    private OnClick onClick;
    private int priority = NotificationCompat.PRIORITY_HIGH;
    private int visibility = NotificationCompat.VISIBILITY_PUBLIC;
    private boolean canDismiss = true;


    public FcmNotificationProperties(
            int notificationIconRes,
            int iconBackgroundColor
    ) {
        this.notificationIconRes = notificationIconRes;
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public FcmNotificationProperties setAllowPopup(boolean allowPopup) {
        this.allowPopup = allowPopup;
        return this;
    }

    public FcmNotificationProperties setNotificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public FcmNotificationProperties setTitle(String title) {
        this.title = title;
        return this;
    }

    public FcmNotificationProperties setBody(String body) {
        this.body = body;
        return this;
    }

    public FcmNotificationProperties setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public FcmNotificationProperties setChannel(String channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
        return this;
    }

    public FcmNotificationProperties setNotificationStyle(NotificationCompat.Style notificationStyle) {
        this.notificationStyle = notificationStyle;
        return this;
    }

    public FcmNotificationProperties setActions(List<Pair<String, PendingIntent>> actions) {
        this.actions = actions;
        return this;
    }

    public FcmNotificationProperties setOnClick(OnClick onClick) {
        this.onClick = onClick;
        return this;
    }

    public FcmNotificationProperties setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public FcmNotificationProperties setVisibility(int visibility) {
        this.visibility = visibility;
        return this;
    }

    public FcmNotificationProperties setCanDismiss(boolean canDismiss) {
        this.canDismiss = canDismiss;
        return this;
    }

    //----------------------------------------------------------------------


    public boolean isAllowPopup() {
        return allowPopup;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getPayload() {
        return payload;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public NotificationCompat.Style getNotificationStyle() {
        return notificationStyle;
    }

    public List<Pair<String, PendingIntent>> getActions() {
        return actions;
    }

    public OnClick getOnClick() {
        return onClick;
    }

    public int getPriority() {
        return priority;
    }

    public int getVisibility() {
        return visibility;
    }

    public boolean canDismiss() {
        return canDismiss;
    }
}
