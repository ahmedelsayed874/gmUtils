// import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:tutorial/ui/screens/start/start_screen.dart';
import 'package:tutorial/zgmutils/data_utils/firebase/fcm.dart';
import 'package:tutorial/zgmutils/gm_main.dart';
import 'package:tutorial/zgmutils/utils/notifications.dart';

import 'resources/_resources.dart';
import 'resources/strings.dart';
import 'zgmutils/utils/logs.dart';

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
    startScreen: StartScreen(),
    localNotificationsConfigurations: null,
    // fcmRequirements: FcmRequirements(
    //   fcmConfigurations: FCMConfigurations(
    //     notificationsConfigurations: NotificationsConfigurations(
    //       androidNotificationIconName: 'notif_icon',
    //       defaultNotificationSound: null,
    //       androidExtraNotificationChannels: null,
    //       androidNotificationChannelsIdsToDelete: null,
    //       openCorrespondingScreen: (payload) {
    //         // NotificationsHandler.openCorrespondingScreen(payload, onError: () {
    //         //   NotificationsScreen.show();
    //         // });
    //       },
    //     ),
    //     firebaseProjectMessageKeyForSend: null,
    //     onDeviceTokenRefresh: (newToken) {
    //       Logs.print(() => 'main.init.onDeviceTokenRefresh:: $newToken');
    //       //todo
    //     },
    //   ),
    //   onFcmInitialized: null, firebaseOptions: null,
    // ),
    onInitialize: (ctx) {
      // AppInfoStorage().saveFirstAppLaunchDateTimeIfMissed();
    },
  );

}

// @pragma('vm:entry-point')
// FcmNotificationProperties resolveNotification(RemoteMessage message) {
//   return FcmNotificationProperties(title: 'title', body: 'body');
// }