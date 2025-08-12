import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

import '../gm_main.dart';
import 'logs.dart';

abstract class INotificationsManager {
  Future<void> init(
    NotificationsConfigurations? _notificationsConfigurations,
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
    AndroidNotificationChannelProperties? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  });

  Future<List<ActiveNotification>> getActiveNotifications();
}

class NotificationsManager extends INotificationsManager {
  static String defaultNotificationIconName = 'notif_icon';
  ///this value must set in android manifest
  ///         <meta-data
  ///             android:name="com.google.firebase.messaging.default_notification_channel_id"
  ///             android:value="@string/default_channel" />
  static String defaultNotificationChannelId = 'default_notification_channel';
  static String defaultNotificationChannelName = 'Default';

  ///sound file name must include extension
  static SoundFile? defaultNotificationChannelSound;

  /////////////////////////////////////////////////////////////////////////////

  NotificationsConfigurations? _notificationsConfigurations;
  NotificationsConfigurations? get notificationsConfigurations => _notificationsConfigurations;

  //private constructor
  static NotificationsManager? _instance;

  static NotificationsManager get instance {
    _instance ??= NotificationsManager._();
    return _instance!;
  }

  NotificationsManager._();

  @override
  Future<void> init(
    NotificationsConfigurations? _notificationsConfigurations,
  ) async {
    Logs.print(() => 'Notifications.init ... follow setup instruction here: https://pub.dev/packages/flutter_local_notifications');

    this._notificationsConfigurations ??= _notificationsConfigurations;

    //region Local Notification Config
    await _setupLocalNotification();
    //endregion

    Logs.print(() => '[Notifications.init()] -> '
        'don\'t forget to use Notifications.instance.redirectToPendingScreen(); '
        'in your list screen');
    Logs.print(() => '[Notifications.init()] -> '
        'don\'t forget to define this "$defaultNotificationChannelId" as '
        'default notification channel (com.google.firebase.messaging.default_notification_channel_id)');
  }

  //region handle Notifications message
  @override
  void openCorrespondingScreenByNotificationJson(dynamic payload) {
    Logs.print(() => 'Notifications.openCorrespondingScreenByNotificationJson');
    try {
      _notificationsConfigurations?.openCorrespondingScreen(payload);
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
        _notificationsConfigurations?.androidNotificationIconName ??
            defaultNotificationIconName,
      ),

      //------------------------------------

      iOS: const DarwinInitializationSettings(
        //onDidReceiveLocalNotification: _onDidReceiveLocalNotificationOfIos,
      ),

    );

    await _flutterLocalNotificationsPlugin.initialize(
      initializationSettings,
      onDidReceiveNotificationResponse: _onDidReceiveNotificationResponse,
      onDidReceiveBackgroundNotificationResponse: _onDidReceiveNotificationResponse,
    );
    //endregion

    //region create channels
    if (_notificationsConfigurations != null) {
      var notificationsPlugin = _flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>();
      await notificationsPlugin?.requestNotificationsPermission();

      //mandatory (check init() method)
      defaultNotificationChannelSound =
          _notificationsConfigurations?.defaultNotificationSound;

      AndroidNotificationChannel defaultNotificationChannel =
          AndroidNotificationChannel(
        defaultNotificationChannelId,
        defaultNotificationChannelName,
        description: 'Default notification channel',
        importance: Importance.max,
        playSound: true,
        sound: defaultNotificationChannelSound == null
            ? null
            : RawResourceAndroidNotificationSound(
                defaultNotificationChannelSound!.name,
              ),
      );

      await notificationsPlugin
          ?.createNotificationChannel(defaultNotificationChannel);

      if (_notificationsConfigurations?.androidNotificationChannelsIdsToDelete !=
          null) {
        var cnfg = _notificationsConfigurations!;
        var lst = cnfg.androidNotificationChannelsIdsToDelete!();
        for (var channelId in lst) {
          await notificationsPlugin?.deleteNotificationChannel(channelId);
        }

        _notificationsConfigurations?.androidNotificationChannelsIdsToDelete =
            null;
      }

      if (_notificationsConfigurations?.androidExtraNotificationChannels !=
          null) {
        var cnfg = _notificationsConfigurations!;
        var channel = cnfg.androidExtraNotificationChannels!();

        for (var channel in channel) {
          await notificationsPlugin?.createNotificationChannel(
            AndroidNotificationChannel(
              channel.channelId,
              channel.channelName,
              description: channel.channelDescription,
              importance: channel.importance.importance,
              playSound: true,
              sound: channel.soundFile == null
                  ? null
                  : RawResourceAndroidNotificationSound(
                      channel.soundFile!.name,
                    ),
            ),
          );
        }

        _notificationsConfigurations?.androidExtraNotificationChannels = null;
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
    AndroidNotificationChannelProperties? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  }) {
    var _notificationId = notificationId ?? body.hashCode;
    while (_notificationId > 0x7FFFFFFF || _notificationId < -0x80000000) {
      var v = '$_notificationId';
      var v2 = '$_notificationId'.substring(0, v.length - 1);
      _notificationId = int.parse(v2);
    }

    Logs.print(() => 'Notifications.showLocalNotification('
        'title: $title, '
        'body: $body, '
        'payload: $payload, '
        '_notificationId: $_notificationId (was: $notificationId), '
        'customChannel: ${customChannel?.channelId ?? defaultNotificationChannelId}, '
        'androidInformationStyle: ${androidInformationStyle?.runtimeType}'
        ')');

    SoundFile? sound =
        customChannel?.soundFile ?? defaultNotificationChannelSound;

    _flutterLocalNotificationsPlugin.show(
      _notificationId,
      title,
      body,
      NotificationDetails(
        android: AndroidNotificationDetails(
          customChannel?.channelId ?? defaultNotificationChannelId,
          customChannel?.channelName ?? defaultNotificationChannelName,
          //
          channelDescription: customChannel?.channelDescription,
          importance: customChannel?.importance.importance ??
              Importance.defaultImportance,
          playSound: true,
          sound: sound == null
              ? null
              : RawResourceAndroidNotificationSound(sound.name),
          //
          priority: Priority.high,
          styleInformation: androidInformationStyle ??
              BigTextStyleInformation(
                body,
              ),
        ),
        iOS: DarwinNotificationDetails(
          presentSound: true,
          sound: sound?.fileNameWithExtension,
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
  NotificationsManager.instance.openCorrespondingScreenByNotificationJson(payload);
}

//------------------------------------------------------------------------------

class NotificationsConfigurations {
  final String androidNotificationIconName;
  final SoundFile? defaultNotificationSound;
  List<String> Function()? androidNotificationChannelsIdsToDelete;
  List<AndroidNotificationChannelProperties> Function()?
      androidExtraNotificationChannels;

  ///payload: may be "Map<String, dynamic>" or "String" or "null"
  final void Function(dynamic payload) openCorrespondingScreen;

  NotificationsConfigurations({
    required this.androidNotificationIconName,
    required this.defaultNotificationSound,
    required this.androidNotificationChannelsIdsToDelete,
    required this.androidExtraNotificationChannels,
    required this.openCorrespondingScreen,
  });

  @override
  String toString() {
    return 'NotificationsConfigurations{androidNotificationIconName: $androidNotificationIconName, defaultNotificationSound: $defaultNotificationSound, androidNotificationChannelsIdsToDelete: $androidNotificationChannelsIdsToDelete, androidExtraNotificationChannels: $androidExtraNotificationChannels, openCorrespondingScreen: $openCorrespondingScreen}';
  }
}

class AndroidNotificationChannelProperties {
  final String channelId;
  final String channelName;
  final String? channelDescription;
  final Importance2 importance;
  final SoundFile? soundFile;

  const AndroidNotificationChannelProperties({
    required this.channelId,
    required this.channelName,
    this.channelDescription,
    required this.importance,
    required this.soundFile,
  });

  @override
  String toString() {
    return 'AndroidNotificationChannelProperties{'
        'channelId: $channelId, '
        'channelName: $channelName, '
        'channelDescription: $channelDescription, '
        'importance: $importance, '
        'soundFile: $soundFile'
        '}';
  }
}

class Importance2 {
  static const int unspecified = -1000;
  static const int none = 0;
  static const int min = 1;
  static const int low = 2;
  static const int defaultImportance = 3;
  static const int high = 4;
  static const int max = 5;

  final int value;

  const Importance2(this.value);

  Importance get importance {
    return Importance.values.firstWhere((e) => e.value == value);
  }

  @override
  String toString() {
    return 'Importance2{value: $value}';
  }
}

class SoundFile {
  final String name;
  final String extension;

  const SoundFile({
    required this.name,
    required this.extension,
  });

  String get fileNameWithExtension => '${name}.${extension}';

  @override
  String toString() {
    return 'SoundFile{name: $name, extension: $extension}';
  }
}
