import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:pallora/zgmutils/utils/logs.dart';

import 'data/preferences/app_info_storage.dart';
import 'resources/_resources.dart';
import 'resources/strings.dart';
import 'ui/screens/scr01_splash/splash_screen.dart';
import 'ui/screens/zzzznotifications/notifications_screen.dart';
import 'ui/utils/notifications_handler.dart';
import 'zgmutils/data_utils/firebase/fcm.dart';
import 'zgmutils/gm_main.dart';
import 'zgmutils/utils/notifications.dart';

const bool useDummyData = true; //todo useDummyData = false

const String currentAppVersion = '1.0.0';

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
    startScreen: SplashScreen(),
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
              NotificationsScreen.show();
            });
          },
        ),
        firebaseProjectMessageKeyForSend: 'AAAAC_-DXlg:APA91bEWg2xfyqW2XxFUktp'
            '_cqtiCK0MwYO10staM17zpGa1OtBBPuTEv22XBMN_wMFMZBsH-632iWVgaWgTiHi'
            '1zqYM_zDgQfVBcX7K3UcE8i7X3kRJdhCjEDlQ3ej2Zg-QCLeXSLT9',
        onDeviceTokenRefresh: (newToken) {
          Logs.print(() => 'main.init.onDeviceTokenRefresh:: $newToken');
          //todo
        },
      ),
      onFcmInitialized: null,
    ),
    onInitialize: (ctx) {
      AppInfoStorage().saveFirstAppLaunchDateTimeIfMissed();
    },
  );
}

@pragma('vm:entry-point')
FcmNotificationProperties resolveNotification(RemoteMessage message) {
  return NotificationsHandler.resolveFcmPayload(message);
}
