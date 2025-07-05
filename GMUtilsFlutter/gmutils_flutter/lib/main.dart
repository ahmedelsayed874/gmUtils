import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart'
    as nm;
import 'package:gmutils_flutter/services/notifications/firebase_options.dart';
import 'package:gmutils_flutter/ui/screens/notifications/notifications_screen.dart';
import 'package:gmutils_flutter/ui/screens/splash/splash_screen.dart';
import 'package:gmutils_flutter/zgmutils/utils/device_info.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

import 'resources/_resources.dart';
import 'resources/strings.dart';
import 'services/notifications/notifications_handler.dart';
import 'zgmutils/data_utils/firebase/fcm.dart';
import 'zgmutils/gm_main.dart';
import 'zgmutils/utils/logs.dart';
import 'zgmutils/utils/notifications.dart';

//------------------------------------------------------------------------------

const bool useProductionData = true; //todo must true

String get appVersion => '1.0.0';

//region serverUrl --------------------------------------------------
String _serverUrl = 'https://gm4s1.blogspot.com/'; //'http://0/';
set serverUrl(String url) => _serverUrl = url;

String get serverUrl => _serverUrl;
//String get serverUrl => 'https://test.example.com/';

const String apisPath = 'api/';

//username/password: BLSAdmin & pa$$word

//endregion

//region iosAppStoreId ----------------------------------------------
const String iosAppStoreId = '000'; //TODO
//endregion

bool isDeviceHasHardwareKeys = true;

//------------------------------------------------------------------------------

void main() {
  GMMain.init(
    defaultAppPreferences: null,
    appName: (ctx) {
      return Strings(ctx).appName;
    },
    measurements: (ctx) {
      Res.init(ctx);
      return Res.themes.measurement;
    },
    appColors: (ctx, isLight) {
      Res.init(ctx, useLightTheme: isLight);
      return Res.themes.colors;
    },
    toolbarTitleFontFamily: () => Res.themes.fonts.toolbarTitle,
    defaultFontFamily: () => Res.themes.fonts.cairo,
    localNotificationsConfigurations: null,
    fcmRequirements: FcmRequirements(
      fcmConfigurations: FCMConfigurations(
        notificationsConfigurations: NotificationsConfigurations(
          androidNotificationIconName: 'notif_icon',
          defaultNotificationSound: null,

          //
          androidExtraNotificationChannels: () =>
              nm.Notification.allChannelInfo(),

          //
          androidNotificationChannelsIdsToDelete: () =>
              nm.Notification.unneedNotificationIds(),

          //
          openCorrespondingScreen: (payload) {
            NotificationsHandler.openCorrespondingScreen(payload, onError: () {
              NotificationsScreen.show(
                initialNotifications: null,
                pageSize: null,
              );
            });
          },
        ),
        sendFcmMessageParameters: SendFcmMessageParameters(
          firebaseProjectId: 'gmutils_flutter',
          firebaseServiceAccountFilePathInAssets:
              'assets/keys/gmutils_flutter-firebase-adminsdk-twoid-599c494587.json',
        ),
        onDeviceTokenRefresh: (newToken) {
          Logs.print(() => 'main.init.onDeviceTokenRefresh:: $newToken');
          NotificationsDataSource.updateFcmTokenForAuthUser(newToken);
        },
      ),
      firebaseOptions: DefaultFirebaseOptions.currentPlatform,
      //
      onFcmInitialized: (fcm) async {
        /*var appConfigs = AppConfigs();

        ///note: same steps repeated in login_driver
        var credentials = await UsersDataSource.instance.savedCredentials;

        var cachedAppConfigsData = await appConfigs.cachedAppConfigsData;
        if (cachedAppConfigsData != null) {
          serverUrl = cachedAppConfigsData.getServerUrl(
            username: credentials?.value1,
          );
        }

        await appConfigs.fetch();

        serverUrl = appConfigs.appConfigs.getServerUrl(
          username: credentials?.value1,
        );

        if (credentials != null) {
          Logs.setLogFileDeadline(appConfigs.appConfigs.getLogFileDeadline(
            username: credentials.value1,
          ));
        }*/
      },
    ),
    onInitialize: (ctx) {
      DeviceInfo((di) {
        isDeviceHasHardwareKeys = di.isDeviceHasHardwareKeys;
      });
      //AppInfoStorage().saveFirstAppLaunchDateTimeIfMissed();
      Logs.print(() => "main.initializing completed");
    },
    startScreen: const SplashScreen(),
    customWaitViewController: null,
  );
}

//------------------------------------------------------------------------------

@pragma('vm:entry-point')
FcmNotificationProperties resolveNotification(RemoteMessage message, bool? en) {
  return NotificationsHandler.resolveNotification(message, en);
}
