import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

import '../gm_main.dart';
import 'logs.dart';

abstract class INotifications {

  Future<void> init(
    NotificationsConfigurations? notificationsConfigurations,
  );

  ///handle Notifications message
  void openCorrespondingScreenByNotificationJson(dynamic payload);

  ///must use on main screen starts
  Future<bool> redirectToPendingScreen();

  //----------------------------------------------------------------------------

  /// Local Notification
  int showNotification(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    LocalNotificationChannelInfo? channelInfo,
    AndroidNotificationChannel? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  });

  Future<List<ActiveNotification>> getActiveNotifications();
}

class Notifications extends INotifications {
  ///this value must set in android manifest
  ///         <meta-data
  ///             android:name="com.google.firebase.messaging.default_notification_channel_id"
  ///             android:value="@string/default_channel" />
  static String defaultNotificationChannelId = 'default_notification_channel';
  static String defaultNotificationChannelName = 'Default';

  ///sound file name must include extension
  static RawResourceAndroidNotificationSound? defaultNotificationChannelSound;

  NotificationsConfigurations? notificationsConfigurations;

  //private constructor
  static Notifications? _instance;

  static Notifications get instance {
    _instance ??= Notifications._();
    return _instance!;
  }

  Notifications._();

  @override
  Future<void> init(
    NotificationsConfigurations? notificationsConfigurations,
  ) async {
    Logs.print(() => 'Notifications.init');

    this.notificationsConfigurations ??= notificationsConfigurations;

    //region Local Notification Config
    await _setupLocalNotification();
    //endregion

    Logs.print(() => '[Notifications.init()] -> '
        'don\'t forget to use Notifications.instance.redirectToPendingScreen(); '
        'in your home screen');
    Logs.print(() => '[Notifications.init()] -> '
        'don\'t forget to define this "$defaultNotificationChannelId" as '
        'default notification channel (com.google.firebase.messaging.default_notification_channel_id)');
  }

  //region handle Notifications message
  @override
  void openCorrespondingScreenByNotificationJson(dynamic payload) {
    Logs.print(() => 'Notifications.openCorrespondingScreenByNotificationJson');
    try {
      notificationsConfigurations?.openCorrespondingScreen(payload);
    } catch (e) {}
  }

  //endregion

  ///must use on main screen starts
  @override
  Future<bool> redirectToPendingScreen() async {
    Logs.print(() => 'Notifications.redirectToPendingScreen');

    var notificationAppLaunchDetails = await _flutterLocalNotificationsPlugin
        .getNotificationAppLaunchDetails();

    final notificationId =
        notificationAppLaunchDetails?.notificationResponse?.id;
    if (notificationId != null &&
        lastOpenedNotificationId?.contains(notificationId) != true) {
      lastOpenedNotificationId ??= {};
      lastOpenedNotificationId?.add(notificationId);

      Logs.print(
        () =>
            'Notifications.redirectToPendingScreen -> notificationAppLaunchDetails ->'
            'lastOpenedNotificationId: $lastOpenedNotificationId, '
            'notificationId: $notificationId',
      );
      Logs.print(
        () =>
            'Notifications.redirectToPendingScreen -> notificationAppLaunchDetails ->'
            'didNotificationLaunchApp: ${notificationAppLaunchDetails?.didNotificationLaunchApp}',
      );

      if (notificationAppLaunchDetails?.didNotificationLaunchApp == true) {
        var notificationResponse =
            notificationAppLaunchDetails!.notificationResponse;
        Logs.print(
          () =>
              'Notifications.redirectToPendingScreen -> notificationAppLaunchDetails ->'
              'notificationResponse('
              'payload: ${notificationResponse?.payload}, '
              'input: ${notificationResponse?.input}, '
              'notificationResponseType: ${notificationResponse?.notificationResponseType.name}'
              ')',
        );

        openCorrespondingScreenByNotificationJson(
          notificationResponse?.payload,
        );

        return true;
      } else {
        return false;
      }
    } else {
      Logs.print(
        () =>
            'Notifications.redirectToPendingScreen -> notificationAppLaunchDetails ->'
            '(notificationId: $notificationId) opened before',
      );
      return false;
    }
  }

  static Set<int>? lastOpenedNotificationId;

  //----------------------------------------------------------------------------

  //region Local Notification
  final FlutterLocalNotificationsPlugin _flutterLocalNotificationsPlugin =
      FlutterLocalNotificationsPlugin();

  Future<void> _setupLocalNotification() async {
    Logs.print(() => 'Notifications._setupLocalNotification()');

    //region initialize
    var initializationSettings = InitializationSettings(
      android: AndroidInitializationSettings(
        notificationsConfigurations?.androidNotificationIconName ??
            'notif_icon',
      ),

      //------------------------------------

      iOS: DarwinInitializationSettings(
        onDidReceiveLocalNotification: _onDidReceiveLocalNotificationOfIos,
      ),
    );

    await _flutterLocalNotificationsPlugin.initialize(
      initializationSettings,
      onDidReceiveNotificationResponse: _onDidReceiveNotificationResponse,
      onDidReceiveBackgroundNotificationResponse:
          _onDidReceiveNotificationResponse,
    );
    //endregion

    //region create channels
    if (notificationsConfigurations != null) {
      var notificationsPlugin = _flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>();
      await notificationsPlugin?.requestPermission();

      //mandatory (check init() method)
      defaultNotificationChannelSound =
          notificationsConfigurations?.defaultNotificationSound == null
              ? null
              : RawResourceAndroidNotificationSound(
                  notificationsConfigurations?.defaultNotificationSound,
                );

      AndroidNotificationChannel defaultNotificationChannel =
          AndroidNotificationChannel(
        defaultNotificationChannelId,
        defaultNotificationChannelName,
        description: 'Default notification channel',
        importance: Importance.max,
        playSound: true,
        sound: defaultNotificationChannelSound,
      );

      await notificationsPlugin
          ?.createNotificationChannel(defaultNotificationChannel);

      if (notificationsConfigurations?.androidNotificationChannelsIdsToDelete !=
          null) {
        for (var channelId in notificationsConfigurations!
            .androidNotificationChannelsIdsToDelete!()) {
          await notificationsPlugin?.deleteNotificationChannel(channelId);
        }

        notificationsConfigurations?.androidNotificationChannelsIdsToDelete =
            null;
      }

      if (notificationsConfigurations?.androidExtraNotificationChannels !=
          null) {
        for (var channel in notificationsConfigurations!
            .androidExtraNotificationChannels!()) {
          await notificationsPlugin?.createNotificationChannel(channel);
        }

        notificationsConfigurations?.androidExtraNotificationChannels = null;
      }
    }
    //endregion
  }

  void _onDidReceiveLocalNotificationOfIos(
    int id,
    String? title,
    String? body,
    String? payload,
  ) async {
    Logs.print(() => 'Notifications._onDidReceiveLocalNotificationOfIos('
        'id: $id,'
        'title: $title,'
        'body: $body,'
        'payload: $payload'
        ')');

    BuildContext context;

    try {
      context = App.context;
    } catch (e) {
      return;
    }

    // display a dialog with the notification details, tap ok to go to another page
    showDialog(
      context: context,
      builder: (BuildContext context) => CupertinoAlertDialog(
        title: Text(title ?? 'Notification'),
        content: Text(body ?? 'You have got a notification message'),
        actions: [
          CupertinoDialogAction(
            isDefaultAction: true,
            child: Text(App.isEnglish ? 'Ok' : 'حسنا'),
            onPressed: () async {
              if (payload != null) {
                openCorrespondingScreenByNotificationJson(payload);
              }
            },
          ),
          if (payload != null)
            CupertinoDialogAction(
              isDefaultAction: false,
              onPressed: null,
              child: Text(App.isEnglish ? 'Dismiss' : 'الغاء'),
            ),
        ],
      ),
    );
  }

  @override
  int showNotification(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    LocalNotificationChannelInfo? channelInfo,
    AndroidNotificationChannel? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  }) {
    Logs.print(() => 'Notifications.showLocalNotification('
        'title: $title, '
        'body: $body, '
        'payload: $payload, '
        'channelInfo: ${channelInfo?.id}'
        'customChannel: ${customChannel?.id ?? defaultNotificationChannelId}'
        ')');

    final _notificationId = notificationId ?? body.hashCode;

    _flutterLocalNotificationsPlugin.show(
      _notificationId,
      title,
      body,
      NotificationDetails(
        android: AndroidNotificationDetails(
          channelInfo?.id ?? customChannel?.id ?? defaultNotificationChannelId,
          channelInfo?.name ??
              customChannel?.name ??
              defaultNotificationChannelName,
          //
          channelDescription: customChannel?.description,
          importance: customChannel?.importance ?? Importance.defaultImportance,
          sound: customChannel?.sound ?? defaultNotificationChannelSound,
          //
          priority: Priority.high,
          playSound: true,
          styleInformation: androidInformationStyle ??
              BigTextStyleInformation(
                body,
              ),
        ),
        iOS: DarwinNotificationDetails(
          presentSound: true,
          sound: customChannel?.sound?.sound,
        ),
      ),
      payload: payload,
    );

    return _notificationId;
  }

  @override
  Future<List<ActiveNotification>> getActiveNotifications() {
    return _flutterLocalNotificationsPlugin.getActiveNotifications();
  }
//endregion
}

@pragma('vm:entry-point')
void _onDidReceiveNotificationResponse(NotificationResponse details) {
  Logs.print(() => 'Notifications._onDidReceiveNotificationResponse('
      'payload: ${details.payload}'
      ')');

  var payload = details.payload;
  Notifications.instance.openCorrespondingScreenByNotificationJson(payload);
}

//------------------------------------------------------------------------------

class LocalNotificationChannelInfo {
  String id;
  String name;

  LocalNotificationChannelInfo({required this.id, required this.name});
}

class NotificationsConfigurations {
  final String androidNotificationIconName;
  final String? defaultNotificationSound;
  List<String> Function()? androidNotificationChannelsIdsToDelete;
  List<AndroidNotificationChannel> Function()? androidExtraNotificationChannels;

  ///payload: may be "Map<String, dynamic>" or "String" or "null"
  final void Function(dynamic payload) openCorrespondingScreen;

  NotificationsConfigurations({
    required this.androidNotificationIconName,
    required this.defaultNotificationSound,
    required this.androidNotificationChannelsIdsToDelete,
    required this.androidExtraNotificationChannels,
    required this.openCorrespondingScreen,
  });
}
