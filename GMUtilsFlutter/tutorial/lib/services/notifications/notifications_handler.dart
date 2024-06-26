
import 'package:firebase_messaging/firebase_messaging.dart';
import '../../zgmutils/data_utils/firebase/fcm.dart';

class NotificationsHandler {
  static FcmNotificationProperties resolveNotification(RemoteMessage message) {
    //todo
    return FcmNotificationProperties(title: null, body: null);
  }

  static void openCorrespondingScreen(payload, {required Null Function() onError}) {
    //todo
  }

}