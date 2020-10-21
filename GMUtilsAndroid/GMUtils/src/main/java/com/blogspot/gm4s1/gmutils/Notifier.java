package com.blogspot.gm4s1.gmutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Random;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class Notifier {
    private Context context;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationIconRes;
    private int iconBackgroundColor = Color.WHITE;

    public static Notifier getInstance(Context context, int notificationIconRes, int iconBackgroundColor) {
        return new Notifier(context, notificationIconRes, iconBackgroundColor);
    }

    private Notifier(Context context, int notificationIconRes, int iconBackgroundColor) {
        this.context = context;
        this.notificationIconRes = notificationIconRes;
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public Notifier createNotification(CharSequence title, CharSequence body, String channelId, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        title = TextUtils.isEmpty(title) ? context.getString(R.string.app_name) : title;
        channelId = TextUtils.isEmpty(channelId) ? "default" : channelId;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.app_name));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setTicker(body)
                .setSmallIcon(notificationIconRes)
                //.setLargeIcon(ResourcesHelper.getBitmap(context, android.R.drawable.sym_def_app_icon))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(iconBackgroundColor)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        return this;
    }

    public Notifier setLargeIcon(Bitmap icon) {
        if (notificationBuilder == null) return null;
        if (icon == null) return this;
        notificationBuilder.setLargeIcon(icon);
        return this;
    }

    public Notifier addActionForUrl(String title, String url) {
        if (notificationBuilder == null) return null;
        if (TextUtils.isEmpty(url)) return this;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.addAction(
                android.R.drawable.ic_media_play,
                title,
                pendingIntent
        );

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
    }
}
