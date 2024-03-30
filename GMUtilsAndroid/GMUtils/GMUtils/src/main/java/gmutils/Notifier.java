package gmutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import gmutils.listeners.ResultCallback;
import gmutils.utils.Utils;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class Notifier {
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_Name = "Default";

    private NotificationCompat.Builder notificationBuilder;
    private final int notificationIconRes;
    private int iconBackgroundColor = Color.WHITE;
    private String createdChannelId = null;

    private Uri notificationSoundUri;
    private boolean vibrate = false;
    
    public static Notifier getInstance(int notificationIconRes, int iconBackgroundColor) {
        return new Notifier(notificationIconRes, iconBackgroundColor);
    }

    private Notifier(int notificationIconRes, int iconBackgroundColor) {
        this.notificationIconRes = notificationIconRes;
        this.iconBackgroundColor = iconBackgroundColor;
    }
    
    //---------------------------------
    
    private Uri getNotificationSound(Context context) {
        if (this.notificationSoundUri == null) {
            this.notificationSoundUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        }
        return this.notificationSoundUri;
    }
    
    //---------------------------------

    //region setNotificationChannel
    public Notifier setNotificationChannel(Context context, String channelId, String channelName) {
        return setNotificationChannel(
                context,
                channelId,
                channelName,
                null,
                NotificationManager.IMPORTANCE_DEFAULT,
                getNotificationSound(context)
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription) {
        return setNotificationChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                NotificationManager.IMPORTANCE_DEFAULT,
                getNotificationSound(context)
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance) {
        return setNotificationChannel(
                context,
                channelId, 
                channelName,
                channelDescription,
                importance,
                getNotificationSound(context)
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance, int rawSoundId) {
        return setNotificationChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                importance,
                rawSoundId,
                vibrate
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance, int rawSoundId, boolean vibrate) {
        Uri soundUri = null;

        if (rawSoundId != 0) {
            //"android.resource://" + applicationContext.packageName + "/" + soundId
            soundUri = Utils.createInstance().getResourceUri(context, rawSoundId);
        }

        return setNotificationChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                importance,
                soundUri,
                vibrate
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance, Uri soundUri) {
        return setNotificationChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                importance,
                soundUri,
                vibrate
        );
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance, Uri soundUri, boolean vibrate) {
        this.notificationSoundUri = soundUri;
        this.vibrate = vibrate;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationBuilder != null)
                throw new IllegalStateException("Can't create channel after create notification");

            channelId = TextUtils.isEmpty(channelId) ? DEFAULT_NOTIFICATION_CHANNEL_ID : channelId;
            channelName = TextUtils.isEmpty(channelName) ? DEFAULT_NOTIFICATION_CHANNEL_Name : channelName;

            this.createdChannelId = channelId;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    importance
            );

            if (channelDescription != null) {
                channel.setDescription(channelDescription);
            }

            if (soundUri != null) {
                AudioAttributes audioAttr = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                channel.setSound(soundUri, audioAttr);

                this.notificationSoundUri = soundUri;
            }

            this.vibrate = vibrate;
            if (vibrate) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            }

            notificationManager.createNotificationChannel(channel);
        }

        return this;
    }

    public Notifier setNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance, boolean vibrate) {
        return setNotificationChannel(
                context,
                channelId,
                channelName,
                channelDescription,
                importance,
                getNotificationSound(context),
                vibrate
        );
    }
    //endregion

    //---------------------------------

    public Notifier removeNotificationChannel(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(channelId);
        }
        return this;
    }

    //---------------------------------

    public Notifier createNotification(Context context, CharSequence title, CharSequence body) {
        return createNotification(context, title, body, (PendingIntent) null);
    }

    public Notifier createNotification(Context context, CharSequence title, CharSequence body, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            //int intentFlag = Intent.FLAG_ACTIVITY_SINGLE_TOP;
            int intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

            int pendingIntentFlag = PendingIntent.FLAG_CANCEL_CURRENT;
            //int pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntentFlag |= PendingIntent.FLAG_IMMUTABLE;
            }

            intent.addFlags(intentFlag);
            pendingIntent = PendingIntent.getActivity(
                    context,
                    intent.getAction().hashCode() /* Request code */,
                    intent,
                    pendingIntentFlag
            );

        }
        
        return createNotification(context, title, body, pendingIntent);
    }
    
    public Notifier createNotification(Context context, CharSequence title, CharSequence body, PendingIntent pendingIntent) {
        title = TextUtils.isEmpty(title) ? context.getString(R.string.app_name) : title;

        if (TextUtils.isEmpty(createdChannelId)) {
            setNotificationChannel(context, null, null, null);
        }

        notificationBuilder = new NotificationCompat.Builder(context, createdChannelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(notificationIconRes)
                .setTicker(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(iconBackgroundColor)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(notificationSoundUri);

        if (vibrate) enableVibrate();

        return this;
    }

    //---------------------------------

    public Notifier setLargeIcon(Bitmap icon) {
        if (notificationBuilder == null) throw new NullPointerException();
        if (icon == null) return this;
        notificationBuilder.setLargeIcon(icon);
        return this;
    }

    public Notifier setSoundUri(Uri soundUri) {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setSound(soundUri);
        return this;
    }

    public Notifier enableVibrate() {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        return this;
    }

    /**
     * @param priority: NotificationCompat
     */
    public Notifier setPriority(int priority) {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setPriority(priority);
        return this;
    }

    /**
     * @param visibility: NotificationCompat
     */
    public Notifier setVisibility(int visibility) {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setVisibility(visibility);
        return this;
    }

    public Notifier setAutoCancel(boolean autoCancel) {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setAutoCancel(autoCancel);
        return this;
    }

    public Notifier setStyle(NotificationCompat.Style style) {
        if (notificationBuilder == null) throw new NullPointerException();
        notificationBuilder.setStyle(style);
        return this;
    }

    public Notifier addActionForUrl(Context context, String title, String url) {
        if (notificationBuilder == null) throw new NullPointerException();
        if (TextUtils.isEmpty(url)) return this;


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                10,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        return addAction(title, pendingIntent);
    }

    public Notifier addAction(String title, PendingIntent pendingIntent) {
        if (notificationBuilder == null) throw new NullPointerException();

        notificationBuilder.addAction(
                android.R.drawable.ic_media_play,
                title,
                pendingIntent
        );

        return this;
    }

    public Notifier updateNotificationBuilder(@NotNull ResultCallback<NotificationCompat.Builder> builder) {
        builder.invoke(notificationBuilder);
        return this;
    }

    //---------------------------------

    Integer notificationIdToRemove;
    boolean removeAllPreviousNotifications = false;

    public Notifier removeNotification(int notificationId) {
        notificationIdToRemove = notificationId;
        return this;
    }

    public Notifier removeAllNotifications() {
        removeAllPreviousNotifications = true;
        return this;
    }

    //---------------------------------

    public Notification buildNotification() {
        return notificationBuilder.build();
    }

    public int release(Context context) {
        Notification notification = buildNotification();
        return release(context, notification, notification.hashCode());
    }

    public int release(Context context, int notificationId) {
        return release(context, buildNotification(), notificationId);
    }

    public int release(Context context, Notification notification, int notificationId) {
        if (notificationBuilder == null) throw new NullPointerException();

        NotificationManagerCompat notificationManager = getNotificationManagerCompat(context);

        if (removeAllPreviousNotifications) {
            notificationManager.cancelAll();
        }
        //
        else if (notificationIdToRemove != null) {
            notificationManager.cancel(notificationIdToRemove);
        }

        notificationManager.notify(notificationId, notification);

        Notifier.lastReleasedNotificationId = notificationId;

        return notificationId;
    }

    //---------------------------------

    private static Integer lastReleasedNotificationId = null;

    public static NotificationManager getNotificationManager(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    public static NotificationManagerCompat getNotificationManagerCompat(Context context) {
        return NotificationManagerCompat.from(context);
    }

    public static void removeNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = getNotificationManagerCompat(context);
        notificationManager.cancel(notificationId);
    }

    public static void removeAllNotifications(Context context) {
        NotificationManagerCompat notificationManager = getNotificationManagerCompat(context);
        notificationManager.cancelAll();
    }

    public static void removeLastReleasedNotification(Context context) {
        if (Notifier.lastReleasedNotificationId == null) return;
        removeNotification(context, Notifier.lastReleasedNotificationId);
    }


}
