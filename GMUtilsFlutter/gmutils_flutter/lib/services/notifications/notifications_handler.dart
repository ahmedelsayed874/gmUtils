import 'dart:convert';
import 'dart:io';

import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart';
import 'package:gmutils_flutter/ui/widgets/small_notifications_list.dart';
import 'package:gmutils_flutter/zgmutils/utils/launcher.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import '../../zgmutils/data_utils/firebase/fcm.dart';

class NotificationsHandler {
  static FcmNotificationProperties resolveNotification(
    RemoteMessage message,
    bool? en,
  ) {
    Logs.print(() => 'NotificationsHandler.resolveNotification('
        'message: {'
        'notification: ${message.notification}, '
        'data: ${message.data} '
        '}'
        ')');

    Notification? notification;
    try {
      notification = NotificationMapper().fromMap(message.data);
      Logs.print(() => 'NotificationsHandler.resolveNotification ---> '
          'ParsedNotification:: $notification',
      );
    } catch (e) {
      Logs.print(
          () => 'NotificationsHandler.resolveNotification ---> EXCEPTION: $e');
    }

    if (notification?.notificationId == 0) {
      notification = null;
    }

    SmallNotificationsList.updateList(notification: notification);

    bool popup = true;

    String? title = en == null ? notification?.titleEn : notification?.titleAr;
    String? body = en == null ? notification?.bodyEn : notification?.bodyAr;

    return FcmNotificationProperties(
      allowPopup: popup,
      title: title,
      body: body,
      payload: jsonEncode(message.data),
      customChannel: notification?.relatedObjectName == null
          ? null
          : Notification.channelInfoOf(
              notification!.relatedObjectName!,
            ),
    );
  }

  static void openCorrespondingScreen(
    payload, {
    required void Function()? onError,
  }) {
    Logs.print(() => 'NotificationsHandler.openCorrespondingScreen('
        'payload: $payload'
        ')');

    Notification? notification;
    String? link;
    String? linkAndroid;
    String? linkIos;

    if (payload is Notification) {
      notification = payload;
    }

    //
    else if (payload is Map) {
      try {
        notification = NotificationMapper().fromMap(Map.from(payload));
      } catch (e) {
        Logs.print(
          () =>
              'NotificationsHandler.openCorrespondingScreen ---> EXCEPTION: $e',
        );
      }
    }

    //
    else if (payload is String) {
      try {
        var map = jsonDecode(payload);
        notification = NotificationMapper().fromMap(Map.from(map));

        if (notification.notificationId == 0) {
          notification = null;

          if (map.containsKey('link')) {
            link = map['link'];
          }
          //
          else if (map.containsKey('linkAndroid') &&
              map.containsKey('linkIos')) {
            linkAndroid = map['linkAndroid'];
            linkIos = map['linkIos'];
          }
          //
          else {
            onError?.call();
            return;
          }
        }
      } catch (e) {
        Logs.print(() =>
            'NotificationsHandler.openCorrespondingScreen ---> EXCEPTION: $e');
        onError?.call();
        return;
      }
    }

    //
    else {
      onError?.call();
      return;
    }

    //////////////////////////////////////////////

    if (notification != null) {
      //markNotificationAsRead
      if (notification.notificationId != 0) {
        markNotificationAsRead(notification);
      }

      //
      else {
        //to do openCorrespondingScreen [virtualRooms]
      }
    }

    //
    else if (link != null) {
      Launcher().openUrl(link);
    }

    //
    else if (linkAndroid != null || linkIos != null) {
      if (Platform.isAndroid) {
        var url = linkAndroid ?? linkIos ?? '';
        Launcher().openUrl(url);
      } else {
        var url = linkIos ?? linkAndroid ?? '';
        Launcher().openUrl(url);
      }
    }
  }

  static Future<bool> markNotificationAsRead(Notification notification) async {
    var accountId = (await UsersDataSource.instance.savedUserAccount)?.id;
    if (accountId == null) return false;

    if (notification.relatedObjectName != null) {
      var r = await NotificationsDataSource.instance.markNotificationAsRead(
        accountId: accountId,
        notificationId: notification.notificationId,
        relatedObjectName: notification.relatedObjectName!,
      );

      return r.isSuccess;
    } else {
      return true;
    }
  }
}
