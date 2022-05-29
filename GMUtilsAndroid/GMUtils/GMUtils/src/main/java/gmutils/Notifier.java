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
import org.jetbrains.annotations.Nullable;

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
    private Context context;
    private NotificationCompat.Builder notificationBuilder;
    private final int notificationIconRes;
    private int iconBackgroundColor = Color.WHITE;
    private String createdChannelId = null;

    private Uri soundUri;
    private boolean vibrate = false;


    public static Notifier getInstance(Context context, int notificationIconRes, int iconBackgroundColor) {
        return new Notifier(context, notificationIconRes, iconBackgroundColor);
    }

    private Notifier(Context context, int notificationIconRes, int iconBackgroundColor) {
        this.context = context;
        this.notificationIconRes = notificationIconRes;
        this.iconBackgroundColor = iconBackgroundColor;

        this.soundUri =  RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
    }

    //---------------------------------

    //region setNotificationChannel
    public Notifier setNotificationChannel(String channelId, String channelName) {
        return setNotificationChannel(channelId, channelName, null, NotificationManager.IMPORTANCE_DEFAULT, soundUri);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription) {
        return setNotificationChannel(channelId, channelName, channelDescription, NotificationManager.IMPORTANCE_DEFAULT, soundUri);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance) {
        return setNotificationChannel(channelId, channelName, channelDescription, importance, soundUri);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance, int rawSoundId) {
        return setNotificationChannel(channelId, channelName, channelDescription, importance, rawSoundId, vibrate);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance, int rawSoundId, boolean vibrate) {
        Uri soundUri = null;
        
        if (rawSoundId != 0) {
            //"android.resource://" + applicationContext.packageName + "/" + soundId
            soundUri = Utils.createInstance().getResourceUri(context, rawSoundId);
        }
        
        return setNotificationChannel(channelId, channelName, channelDescription, importance, soundUri, vibrate);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance, Uri soundUri) {
        return setNotificationChannel(channelId, channelName, channelDescription, importance, soundUri, vibrate);
    }

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance, Uri soundUri, boolean vibrate) {
        this.soundUri = soundUri;
        this.vibrate = vibrate;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationBuilder != null) throw new IllegalStateException("Can't create channel after create notification");

            channelId = TextUtils.isEmpty(channelId) ? "default" : channelId;
            channelName = TextUtils.isEmpty(channelName) ? "Default" : channelName;

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

                this.soundUri = soundUri;
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

    public Notifier setNotificationChannel(String channelId, String channelName, String channelDescription, int importance, boolean vibrate) {
        return setNotificationChannel(channelId, channelName, channelDescription, importance, soundUri, vibrate);
    }
    //endregion

    //---------------------------------

    public Notifier removeNotificationChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(channelId);
        }
        return this;
    }

    //---------------------------------

    public Notifier createNotification(CharSequence title, CharSequence body, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        title = TextUtils.isEmpty(title) ? context.getString(R.string.app_name) : title;

        if (TextUtils.isEmpty(createdChannelId)) {
            setNotificationChannel(null, null, null);
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
                .setSound(soundUri);

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

    public Notifier addActionForUrl(String title, String url) {
        if (notificationBuilder == null) throw new NullPointerException();
        if (TextUtils.isEmpty(url)) return this;


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_CANCEL_CURRENT);

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

    public void release() {
        int i = new Random(100).nextInt(1000);
        release(i);
    }

    public void release(int notificationId) {
        if (notificationBuilder == null) throw new NullPointerException();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notificationBuilder.build());

        this.context = null;
    }
}
