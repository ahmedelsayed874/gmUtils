import 'dart:convert';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart' as sharedPrefLib;

import '../../../main.dart' as main;
import '../../utils/logs.dart';
import '../../utils/notifications_manager.dart';
import '../../utils/result.dart';
import '../../utils/string_set.dart';
import '../storages/app_preferences_storage.dart';
import 'fcm_extension.dart';

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
abstract class IFCM {
  Future<String?> get deviceToken;

  ///must user on main screen starts
  void redirectToPendingScreen();

  Future<Result<bool>> subscribeToTopics(List<String> topics);

  Future<Result<bool>> unsubscribeFromSubscribedTopics();

  Future<Result<bool>> unsubscribeFromTopics(List<String> topics);

  Future<List<String>> subscribedTopics();

  Future<bool> sendMessageToSpecificDevice({
    required String deviceToken,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = false,
  });

  Future<bool> sendMessageToTopic({
    required String topic,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = true,
  });

  int showNotification(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    AndroidNotificationChannelProperties? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  });
}

//------------------------------------------------------------------------------

class FCM extends IFCM {
  FirebaseMessaging messaging = FirebaseMessaging.instance;
  FCMConfigurations? fcmConfigurations;

  INotificationsManager localNotifications = NotificationsManager.instance;

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
      String token;
      try {
        token = await deviceToken ?? '';
      } catch (e) {
        token = '';
      }

      if (token.isNotEmpty) {
        this.fcmConfigurations?.onDeviceTokenRefresh?.call(token);
      }

      FirebaseMessaging.instance.onTokenRefresh.listen((newToken) {
        this.fcmConfigurations?.onDeviceTokenRefresh?.call(newToken);
      });
    }

    Logs.print(() => '[Fcm.init()] -> '
        'don\'t forget to use FCM.instance.redirectToPendingScreen(); '
        'in your list screen');
    Logs.print(() => '[Fcm.init()] -> '
        'don\'t forget to define this "${NotificationsManager.defaultNotificationChannelId}" as '
        'default notification channel (com.google.firebase.messaging.default_notification_channel_id)');
  }

  @override
  Future<String?> get deviceToken async {
    int tries = 0;
    dynamic exception;
    while (tries < 10) {
      tries++;
      try {
        var token = await FirebaseMessaging.instance.getToken();
        Logs.print(
          () => 'FCM.deviceToken [AFTER TRY#$tries]---> '
              '${token?.isNotEmpty == true ? token?.substring(0, 20) : 'NULL'}...',
        );
        return token;
      } catch (e) {
        exception = e;
        Logs.print(() => 'FCM.deviceToken [TRY#$tries]---> EXCEPTION: $e');
        await Future.delayed(const Duration(milliseconds: 300));
      }
    }

    Logs.print(() =>
        'FCM.deviceToken ---> trying to get token failed $tries times ... LAST_EXCEPTION: $exception');
    return null;
  }

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
      AppPreferencesStorage().isEn().then((en) {
        _popupNotification2(message, en);
      });
    } catch (e) {
      _popupNotification2(message, null);
    }
  }

  void _popupNotification2(RemoteMessage message, bool? en) {
    Logs.print(
      () => ['FCM._popupNotification2(message: ${message.toMap()}, en: $en)'],
    );

    var title = '${message.notification?.title ?? 'Notification'}•';
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
      showNotification(
        localNotification.title ?? title,
        localNotification.body ?? body,
        payload: payload,
        notificationId: localNotification.notificationId,
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
  Future<Result<bool>> subscribeToTopics(List<String> topics) async {
    await unsubscribeFromSubscribedTopics();

    String errors = '';

    for (var topic in topics) {
      try {
        await messaging.subscribeToTopic(topic);
        Logs.print(() => "FCM: subscribed to topic: \"$topic\"");
      } catch (e) {
        Logs.print(() => "FCM: failed to subscribe to topic: \"$topic\"");

        if (errors.isNotEmpty) errors += '\n';
        errors += '- $topic: $e';
      }
    }

    if (errors.isEmpty) {
      (await _prefs).setStringList('FCM_Topics', topics);
      return Result(true);
    } else {
      return Result(false, message: StringSet(errors));
    }
  }

  @override
  Future<Result<bool>> unsubscribeFromSubscribedTopics() async {
    var savedTopics = await subscribedTopics();
    if (savedTopics.isEmpty) return Result(true);

    return _unsubscribeFromTopics(
      savedTopics: savedTopics.toList(),
      targetTopics: savedTopics.toList(),
    );
  }

  @override
  Future<Result<bool>> unsubscribeFromTopics(List<String> topics) async {
    var savedTopics = await subscribedTopics();
    return _unsubscribeFromTopics(
      savedTopics: savedTopics.toList(),
      targetTopics: topics.toList(),
    );
  }

  Future<Result<bool>> _unsubscribeFromTopics({
    required List<String> savedTopics,
    required List<String> targetTopics,
  }) async {
    String errors = '';

    for (var topic in targetTopics) {
      try {
        await messaging.unsubscribeFromTopic(topic);
        savedTopics.remove(topic);
        Logs.print(() => "FCM: unsubscribe from topic: $topic");
      } catch (e) {
        Logs.print(() => "FCM: failed to unsubscribe from topic: $topic");

        if (errors.isNotEmpty) errors += '\n';
        errors += '- $topic: $e';
      }
    }

    if (errors.isEmpty) {
      (await _prefs).setStringList('FCM_Topics', savedTopics);
      return Result(true);
    } else {
      return Result(false, message: StringSet(errors));
    }
  }

  Future<List<String>> subscribedTopics() async {
    return (await _prefs).getStringList('FCM_Topics') ?? [];
  }

  //endregion

  //----------------------------------------------------------------------------

  //region Send FCM message
  final IOS_PAYLOAD_KEY_NAME = 'payload';

  @override
  Future<bool> sendMessageToSpecificDevice({
    required String deviceToken,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = false,
  }) async {
    return _sendMessageTo(
      fcmToken: deviceToken,
      topic: null,
      title: title,
      message: message,
      payload: payload,
      channel: channel,
      dataNotification: dataNotification,
    );
  }

  @override
  Future<bool> sendMessageToTopic({
    required String topic,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = false,
  }) async {
    return _sendMessageTo(
      //to: '/topics/$topic',
      fcmToken: null,
      topic: topic,
      title: title,
      message: message,
      payload: payload,
      channel: channel,
      dataNotification: dataNotification,
    );
  }

  /// https://firebase.google.com/docs/cloud-messaging/migrate-v1?hl=en&authuser=0#java
  /// https://firebase.google.com/docs/cloud-messaging/send-message?authuser=0
  /// https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?authuser=0
  /// <deprecated> https://firebase.google.com/docs/cloud-messaging/http-server-ref
  /// <deprecated> https://firebase.google.com/docs/cloud-messaging/send-message?hl=en&authuser=0#send-messages-to-topics-legacy
  /// FIVE TOPICS IN ONE REQUEST
  Future<bool> _sendMessageTo({
    required String? fcmToken,
    required String? topic,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = false,
  }) async {
    var accessToken = await _getAccessToken();
    if (accessToken.message != null) {
      showNotification(
        'Send Notification Failed',
        accessToken.message?.en ?? 'Send Notification Failed',
      );
      return false;
    }

    var requestBodyMap = _buildRequestData(
      fcmToken: fcmToken,
      topic: topic,
      title: title,
      message: message,
      isDataNotification: dataNotification,
      dataPayload: payload,
      channelId: channel?.channelId,
      //soundFileName: soundFileName,
    );
    var requestBody = jsonEncode(requestBodyMap);

    var url = Uri.parse(
      "https://fcm.googleapis.com/v1/projects/${fcmConfigurations?.sendFcmMessageParameters?.firebaseProjectId}/messages:send",
    );
    var headers = {
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': "Bearer ${accessToken.result}",
    };

    Logs.print(
      () => 'FCM.sendNotification ---> REQUEST:: '
          'headers: $headers, '
          'body: $requestBody',
    );

    var response = await http.post(
      url,
      headers: headers,
      body: requestBody,
    );

    Logs.print(
      () => 'FCM.sendNotification ---> RESPONSE:: '
          'statusCode: ${response.statusCode}, '
          'body: ${response.body}, '
          'error: ${response.reasonPhrase}',
    );

    if (response.statusCode != 200) {
      showNotification(
        'Send Notification Failed',
        'Details: ${response.reasonPhrase}',
      );
    }

    return response.statusCode == 200;
  }

  ///https://pub.dev/packages/googleapis_auth
  ///googleapis_auth: ^1.6.0
  Future<Result<String>> _getAccessToken() {
    return FCM_Extension().getAccessToken(fcmConfigurations: fcmConfigurations);
  }

  Map<String, dynamic> _buildRequestData({
    required String? fcmToken,
    required String? topic,
    //
    required String title,
    required String message,
    //
    required bool isDataNotification,
    //required Map<String, dynamic>? dataPayload,
    required String? dataPayload,
    //
    required String? channelId,
    //required String? soundFileName,
  }) {
    //https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?authuser=0
    Map<String, dynamic> notificationBody = {};
    if (fcmToken?.isNotEmpty == true) {
      notificationBody["token"] = fcmToken;
    }
    //
    else if (topic?.isNotEmpty == true) {
      notificationBody["topic"] = topic;
    }
    //
    else {
      throw "topic or token must set";
    }

    if (!isDataNotification) {
      notificationBody["notification"] = {
        "title": title,
        "body": message,
      };
    }

    notificationBody["android"] = {
      "notification": {
        "title": title,
        "body": message,
        //"notification_priority": "PRIORITY_DEFAULT", //PRIORITY_HIGH - PRIORITY_MAX - PRIORITY_LOW - PRIORITY_MIN
        if (channelId != null) "channel_id": channelId,
        //"sound": soundFileName,
        //"icon": "stock_ticker_update",
        //"color": "#7e55c3",
        //"tag": "",
        //"click_action": "",
        //"ticker": "",
        //"sticky": true,
        //"default_sound": true,
        //"visibility": "PUBLIC", //PRIVATE - SECRET
        //"notification_count": 1
        //"image": "http://....",
        //"direct_boot_ok": false,
      },
      //"data": {},
    };

    notificationBody["apns"] = {
      "payload": {
        "aps": {
          "alert": {
            "title": title,
            "body": message,
          },
          //"sound": soundFileName,
          // "badge": 1,
        },
        //if (dataPayload != null) IOS_PAYLOAD_KEY_NAME: dataPayload, //"data" This was your original iOS custom payload placement
      },
      // "headers": {
      //   "apns-priority": "10", // "5" for low priority
      // }
    };

    if (dataPayload != null) {
      notificationBody["data"] = {IOS_PAYLOAD_KEY_NAME : dataPayload};
    }

    Map<String, dynamic> messageJson = {};
    messageJson["message"] = notificationBody;

    return messageJson;
  }

  Future<bool> sendMessageUsingLegacyApi({
    ///get from the firebase console (settings) .... ex:: 'AAAAKbiiUMw:APA91...JRP';
    required String fcmApiKey,
    required String to,
    required String title,
    required String message,
    required String? payload,
    AndroidNotificationChannelProperties? channel,
    bool dataNotification = false,
  }) async {
    //https://firebase.google.com/docs/cloud-messaging/http-server-ref
    //https://firebase.google.com/docs/cloud-messaging/send-message?hl=en&authuser=0#send-messages-to-topics-legacy
    //FIVE TOPICS IN ONE REQUEST

    String? androidChannelId =
        channel?.channelId ?? NotificationsManager.defaultNotificationChannelId;
    SoundFile? sound =
        channel?.soundFile ?? NotificationsManager.defaultNotificationChannelSound;

    var android = {
      'title': title,
      'body': message,
    };
    if (sound != null) {
      android['sound'] = sound.name;
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
    if (sound != null) {
      apns['sound'] = sound.fileNameWithExtension;
    }

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
      'Authorization': 'key=$fcmApiKey',
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
  int showNotification(
    String title,
    String body, {
    String? payload,
    int? notificationId,
    AndroidNotificationChannelProperties? customChannel,
    DefaultStyleInformation? androidInformationStyle,
  }) {
    return localNotifications.showNotification(
      title,
      body,
      payload: payload,
      notificationId: notificationId,
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
  final SendFcmMessageParameters? sendFcmMessageParameters;
  final void Function(String)? onDeviceTokenRefresh;

  FCMConfigurations({
    required this.notificationsConfigurations,
    required this.sendFcmMessageParameters,
    required this.onDeviceTokenRefresh,
  });
}

class FcmNotificationProperties {
  bool allowPopup;
  int? notificationId;
  String? title;
  String? body;
  String? payload;
  AndroidNotificationChannelProperties? customChannel;
  DefaultStyleInformation? androidInformationStyle;

  FcmNotificationProperties({
    this.allowPopup = true,
    this.notificationId,
    required this.title,
    required this.body,
    this.payload,
    this.customChannel,
    this.androidInformationStyle,
  });
}

class SendFcmMessageParameters {
  final String firebaseProjectId;
  final String firebaseServiceAccountFilePathInAssets;

  SendFcmMessageParameters({
    required this.firebaseProjectId,
    required this.firebaseServiceAccountFilePathInAssets,
  });
}
