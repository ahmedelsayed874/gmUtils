import 'package:firebase_messaging/firebase_messaging.dart';

import 'resources/_resources.dart';
import 'resources/strings.dart';
import 'services/notifications/notifications_handler.dart';
import 'ui/screens/start/start_screen.dart';
import 'zgmutils/data_utils/firebase/fcm.dart';
import 'zgmutils/gm_main.dart';
import 'zgmutils/utils/logs.dart';
import 'zgmutils/utils/notifications.dart';

const bool useProductionData = false; //todo

void main() {
  GMMain.init(
    isEnglishDefaultLocale: true,
    appName: (ctx) {
      return Strings(ctx).appName;
    },
    measurements: (ctx) {
      Res.init(ctx);
      return Res.themes.measurement;
    },
    appColors: (ctx) {
      Res.init(ctx);
      return Res.themes.colors;
    },
    toolbarTitleFontFamily: () => Res.fonts.toolbarTitle,
    startScreen: const StartScreen(),
    localNotificationsConfigurations: null,
    fcmRequirements: FcmRequirements(
      fcmConfigurations: FCMConfigurations(
        notificationsConfigurations: NotificationsConfigurations(
          androidNotificationIconName: 'notif_icon',
          defaultNotificationSound: null,
          androidExtraNotificationChannels: null,
          androidNotificationChannelsIdsToDelete: null,
          openCorrespondingScreen: (payload) {
            NotificationsHandler.openCorrespondingScreen(payload, onError: () {
              //todo take proper action
            });
          },
        ),
        firebaseProjectMessageKeyForSend: null,
        onDeviceTokenRefresh: (newToken) {
          Logs.print(() => 'main.init.onDeviceTokenRefresh:: $newToken');
          //todo take proper action
        },
      ),
      onFcmInitialized: null, firebaseOptions: null,
    ),
    onInitialize: (ctx) {
      // AppInfoStorage().saveFirstAppLaunchDateTimeIfMissed();
    },
  );

}

@pragma('vm:entry-point')
FcmNotificationProperties resolveNotification(RemoteMessage message) {
  return NotificationsHandler.resolveNotification(message);
}