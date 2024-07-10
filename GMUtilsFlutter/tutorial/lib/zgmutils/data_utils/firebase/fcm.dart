import 'dart:convert';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:http/http.dart' as http;
import '../../../zgmutils/data_utils/storages/locale_preference.dart';
import '../../../zgmutils/utils/notifications.dart';
import 'package:shared_preferences/shared_preferences.dart' as sharedPrefLib;
import '../../utils/logs.dart';

import '../../../main.dart' as main;


///https://firebase.google.com/docs/cli?authuser=0#mac-linux-auto-script
///https://firebase.flutter.dev/docs/messaging/overview/
///https://pub.dev/packages/flutter_local_notifications#scheduled-notifications-and-daylight-saving-time
///
/// add those to android manifest
//         <meta-data
//             android:name="com.google.firebase.messaging.default_notification_channel_id"
//             android:value="@string/default_channel" />
//
//         <meta-data
//             android:name="com.google.firebase.messaging.default_notification_icon"
//             android:resource="@drawable/notif_icon" />
abstract class FCMFunctions {
  Future<String?> get deviceToken;

  ///must user on main screen starts
  void redirectToPendingScreen();

  Future<void> subscribeToTopics(List<String> topics);

  Future<void> unsubscribeFromTopics();

  Future<bool> sendMessageToSpecificDevice({
    required String deviceToken,
    required String notificationId,
    required String title,
    required String message,
    required String payload,
    AndroidNotificationChannel? channel,
    bool dataNotification = false,
  });

  Future<bool> sendMessageToTopic({
    required String topic,
    required String notificationId,
    required String title,
    required String message,
    required String payload,
    AndroidNotificationChannel? channel,
    bool dataNotification = false,
  });

  int showNotificationProperties(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    LocalNotificationChannelInfo? channelInfo,
    AndroidNotificationChannel? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  });
}

//------------------------------------------------------------------------------

class FCM extends FCMFunctions {
  static String _sentNotificationId = '';

  FirebaseMessaging messaging = FirebaseMessaging.instance;
  FCMConfigurations? fcmConfigurations;

  INotifications localNotifications = Notifications.instance;

  //private constructor
  static FCM? _instance;

  static FCM get instance {
    _instance ??= FCM._();
    return _instance!;
  }

  FCM._();

  Future<void> init(FCMConfigurations? fcmConfigurations) async {
    Logs.print(() => 'Fcm.init');

    this.fcmConfigurations ??= fcmConfigurations;

    //region Local Notification Config
    await localNotifications.init(
      this.fcmConfigurations?.notificationsConfigurations,
    );

    await messaging.setForegroundNotificationPresentationOptions(
      alert: true,
      badge: false,
      sound: true,
    );
    //endregion

    //--------------------------------------------------------------------------

    //region ios permission
    try {
      NotificationSettings settings = await messaging.requestPermission(
        alert: true,
        announcement: false,
        badge: true,
        carPlay: false,
        criticalAlert: false,
        provisional: false,
        sound: true,
      );

      switch (settings.authorizationStatus) {
        case AuthorizationStatus.authorized:
          ////print('FCM: User granted permission');
          break;
        case AuthorizationStatus.provisional:
          ////print('FCM: User granted provisional permission');
          break;
        default:
        ////print('FCM: User declined or has not accepted permission');
      }
    } catch (e) {}
    //endregion

    //--------------------------------------------------------------------------

    //region handle clicking on (notifications)
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      Logs.print(() => 'Fcm.FirebaseMessaging.onMessageOpenedApp');
      openCorrespondingScreen(message);
    });
    //endregion

    //region receive messages (notifications)
    //foreground message handler
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      _popupNotification(message);
    });

    //background message handler
    FirebaseMessaging.onBackgroundMessage(_handleBackgroundMessage);
    //endregion

    if (fcmConfigurations?.onDeviceTokenRefresh != null) {
      this
          .fcmConfigurations
          ?.onDeviceTokenRefresh
          ?.call((await deviceToken) ?? '');

      FirebaseMessaging.instance.onTokenRefresh.listen((newToken) {
        this.fcmConfigurations?.onDeviceTokenRefresh?.call(newToken);
      });
    }

    Logs.print(() => '[Fcm.init()] -> '
        'don\'t forget to use FCM.instance.redirectToPendingScreen(); '
        'in your home screen');
    Logs.print(() => '[Fcm.init()] -> '
        'don\'t forget to define this "${Notifications.defaultNotificationChannelId}" as '
        'default notification channel (com.google.firebase.messaging.default_notification_channel_id)');
  }

  @override
  Future<String?> get deviceToken => FirebaseMessaging.instance.getToken();

  //region handle FCM message
  void openCorrespondingScreen(RemoteMessage message) {
    Logs.print(() => 'Fcm.openCorrespondingScreen');
    openCorrespondingScreenByNotificationJson(message.data);
  }

  void openCorrespondingScreenByNotificationJson(dynamic payload) {
    Logs.print(() => 'Fcm.openCorrespondingScreenByNotificationJson');
    localNotifications.openCorrespondingScreenByNotificationJson(payload);
  }

  void _popupNotification(RemoteMessage message) {
    try {
      LocalePreference().isEn().then((en) {
        _popupNotification2(message, en);
      });
    } catch (e) {
      _popupNotification2(message, null);
    }
  }

  void _popupNotification2(RemoteMessage message, bool? en) {
    Logs.print(() => [
          'FCM._popupNotification2(message: {id=${message.messageId}, payload=${message.data}, en: $en)'
        ]);

    var title = '•${message.notification?.title ?? 'Notification'}';
    var body = message.notification?.body ?? '';

    var localNotification = resolveNotification(message, en);
    var payload = localNotification.payload;
    if (payload == null) {
      try {
        payload = jsonEncode(message.data);
      } catch (e) {
        Logs.print(() => ['FCM._popupNotification2 ->', e]);
      }
    }

    if (localNotification.allowPopup != false) {
      showNotificationProperties(
        localNotification.title ?? title,
        localNotification.body ?? body,
        payload: payload,
        notificationId: localNotification.notificationId,
        channelInfo: localNotification.channelInfo,
        customChannel: localNotification.customChannel,
        androidInformationStyle: localNotification.androidInformationStyle,
      );
    }
  }

  //endregion

  ///must user on main screen starts
  @override
  void redirectToPendingScreen() async {
    Logs.print(() => 'Fcm.redirectToPendingScreen');

    //gets the notification that pushed by Firebase engine
    var msg = await messaging.getInitialMessage();
    if (msg != null) {
      Logs.print(
          () => 'Fcm.redirectToPendingScreen -> messaging.getInitialMessage -> '
              'title: ${msg.notification?.title}, '
              'body: ${msg.notification?.body}, '
              'data: ${msg.data}');

      openCorrespondingScreen(msg);
    } else {
      await localNotifications.redirectToPendingScreen();
    }
  }

  //----------------------------------------------------------------------------

  //region subscribe/unsubscribe TOPICs
  sharedPrefLib.SharedPreferences? __prefs;

  Future<sharedPrefLib.SharedPreferences> get _prefs async {
    __prefs ??= await sharedPrefLib.SharedPreferences.getInstance();
    return __prefs!;
  }

  @override
  Future<void> subscribeToTopics(List<String> topics) async {
    await unsubscribeFromTopics();

    for (var topic in topics) {
      try {
        await messaging.subscribeToTopic(topic);
        Logs.print(() => "FCM: subscribed to \"$topic\"");
      } catch (e) {
        Logs.print(() => "FCM: failed to subscribe to \"$topic\"");
      }
    }

    (await _prefs).setStringList('FCM_Topics', topics);
  }

  @override
  Future<void> unsubscribeFromTopics() async {
    try {
      var topics = (await _prefs).getStringList('FCM_Topics');
      for (var topic in topics ?? []) {
        try {
          await messaging.unsubscribeFromTopic(topic);
          Logs.print(() => "FCM: unsubscribe from $topic");
        } catch (e) {
          Logs.print(() => "FCM: failed to unsubscribe from $topic");
        }
      }
    } catch (e) {}
  }

  //endregion

  //----------------------------------------------------------------------------

  //region Send FCM message
  final IOS_PAYLOAD_KEY_NAME = 'payload';

  @override
  Future<bool> sendMessageToSpecificDevice({
    required String deviceToken,
    required String notificationId,
    required String title,
    required String message,
    required String payload,
    AndroidNotificationChannel? channel,
    bool dataNotification = false,
  }) async {
    return _sendMessageTo(
      to: deviceToken,
      notificationId: notificationId,
      title: title,
      message: message,
      payload: payload,
    );
  }

  @override
  Future<bool> sendMessageToTopic({
    required String topic,
    required String notificationId,
    required String title,
    required String message,
    required String payload,
    AndroidNotificationChannel? channel,
    bool dataNotification = false,
  }) async {
    return _sendMessageTo(
      to: '/topics/$topic',
      notificationId: notificationId,
      title: title,
      message: message,
      payload: payload,
    );
  }

  Future<bool> _sendMessageTo({
    required String to,
    required String notificationId,
    required String title,
    required String message,
    required String payload,
    AndroidNotificationChannel? channel,
    bool dataNotification = false,
  }) async {
    _sentNotificationId = notificationId;

    //https://firebase.google.com/docs/cloud-messaging/http-server-ref
    //https://firebase.google.com/docs/cloud-messaging/send-message?hl=en&authuser=0#send-messages-to-topics-legacy
    //FIVE TOPICS IN ONE REQUEST

    String? androidChannelId =
        channel?.id ?? Notifications.defaultNotificationChannelId;
    String? sound = channel?.sound?.sound ??
        Notifications.defaultNotificationChannelSound?.sound;

    var android = {
      'title': title,
      'body': message,
    };
    if (sound != null) {
      android['sound'] = sound.substring(0, sound.lastIndexOf('.'));
    }
    android['android_channel_id'] = androidChannelId;
    android['channel_id'] = androidChannelId;

    var apns = {
      'aps': {
        'title': title,
        'body': message,
      },
      'title': title,
      'body': message,
      IOS_PAYLOAD_KEY_NAME: payload,
    };
    if (sound != null) apns['sound'] = sound;

    var notificationBody = {
      'to': to,
      'priority': 'high',
      'data': {
        IOS_PAYLOAD_KEY_NAME: payload,
      },
      'apns': apns,
      'android': android
    };

    if (!dataNotification) {
      notificationBody['notification'] = {
        'title': title,
        'body': message,
      };
    }

    var requestBody = json.encode(notificationBody);

    var url = Uri.parse('https://fcm.googleapis.com/fcm/send');
    var headers = {
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization':
          'key=${fcmConfigurations?.firebaseProjectMessageKeyForSend}',
    };

    var response = await http.post(
      url,
      headers: headers,
      body: requestBody,
    );

    //if (response.statusCode != 200) {}
    //return response; //{ "message_id": 3598509887081198072 }
    return response.statusCode == 200;
  }

  //endregion

  //----------------------------------------------------------------------------

  @override
  int showNotificationProperties(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    LocalNotificationChannelInfo? channelInfo,
    AndroidNotificationChannel? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  }) {
    return localNotifications.showNotification(
      title,
      body,
      payload: payload,
      notificationId: notificationId,
      channelInfo: channelInfo,
      customChannel: customChannel,
      androidInformationStyle: androidInformationStyle,
    );
  }
}

@pragma('vm:entry-point')
Future<void> _handleBackgroundMessage(RemoteMessage message) async {
  Logs.print(() => 'FCM._handleBackgroundMessage('
      'title: ${message.notification?.title}, '
      'body: ${message.notification?.body}, '
      'data: ${message.data}'
      ')');

  await Firebase.initializeApp();
  FCM.instance.init(FCM.instance.fcmConfigurations);
  FCM.instance._popupNotification(message);
}

@pragma('vm:entry-point')
FcmNotificationProperties resolveNotification(RemoteMessage message, bool? en) {
  /*add this method to main file
    @pragma('vm:entry-point')
    FcmNotificationProperties resolveNotification(RemoteMessage message, bool? en) {}
  */
  return main.resolveNotification(message, en);
}

//------------------------------------------------------------------------------

class FCMConfigurations {
  final NotificationsConfigurations notificationsConfigurations;

  ///get from the firebase console (settings) .... ex:: 'AAAAKbiiUMw:APA91...JRP';
  final String? firebaseProjectMessageKeyForSend;
  final void Function(String)? onDeviceTokenRefresh;

  FCMConfigurations({
    required this.notificationsConfigurations,
    required this.firebaseProjectMessageKeyForSend,
    required this.onDeviceTokenRefresh,
  });
}

class FcmNotificationProperties {
  bool allowPopup;
  int? notificationId;
  String? title;
  String? body;
  String? payload;
  LocalNotificationChannelInfo? channelInfo;
  AndroidNotificationChannel? customChannel;
  DefaultStyleInformation? androidInformationStyle;

  FcmNotificationProperties({
    this.allowPopup = true,
    this.notificationId,
    required this.title,
    required this.body,
    this.payload,
    this.channelInfo,
    this.customChannel,
    this.androidInformationStyle,
  });
}
