import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'string_set.dart';

import '../ui/dialogs/message_dialog.dart';
import 'launcher.dart';
import 'logs.dart';
import 'package_info.dart';

class AppVersionCheck {
  static Future<String> get playStoreLink async {
    var a = 'https://play.google.com/store/apps/details?id=';
    a += await PackageInfo().packageName;
    return a;
  }

  final String runningAndroidVersion;
  final String runningIosVersion;
  final String iosAppId;

  AppVersionCheck({
    required this.runningAndroidVersion,
    required this.runningIosVersion,
    required this.iosAppId,
  }) {
    Logs.print(() => [
          'AppVersionCheck.constructor()',
          'runningAndroidVersion: $runningAndroidVersion',
          'runningIosVersion: $runningIosVersion',
          'iosAppId: $iosAppId',
        ]);
  }

  Future<String?> getPlayStoreVersion() async {
    var link = await playStoreLink;
    var response = await http.get(Uri.parse(link));
    Logs.print(() => [
          'AppVersionCheck.getPlayStoreVersion()',
          'link: $link',
          'statusCode: ${response.statusCode}',
          'reasonPhrase: ${response.reasonPhrase}',
          //'body: ${response.body}',
        ]);
    if (response.statusCode == 200) {
      const String httpElementStart = "<span class=\"htlgb\">";
      const String httpElementEnd = "</span>";

      int end = response.body.length - 1;
      int index1, index2;

      do {
        index1 = response.body.lastIndexOf(httpElementStart, end);
        if (index1 >= 0) {
          index2 = response.body.indexOf(httpElementEnd, index1);
          //print('$index1 .... $index2 ..... (${index1 > index2})');
          String text = response.body.substring(
            index1 + httpElementStart.length,
            index2,
          );
          //print(text);
          if (text.contains(".")) {
            if (int.tryParse(text.replaceAll('.', '')) != null) {
              Logs.print(() => [
                    'AppVersionCheck.getPlayStoreVersion()',
                    'version: $text',
                  ]);
              return text;
            }
          }
        }

        end = index1 - 1;
      } while (index1 >= 0);

      Logs.print(() => [
            'AppVersionCheck.getPlayStoreVersion()',
            'COULD-NOT-FIND-VERSION-DUE-TO-MISSING-START-TAG',
          ]);
    }
    return null;
  }

  Future<String?> getAppStoreVersion() async {
    var link = 'https://apps.apple.com/us/app/id$iosAppId';
    var response = await http.get(Uri.parse(link));
    Logs.print(() => [
          'AppVersionCheck.getAppStoreVersion()',
          'link: $link',
          'statusCode: ${response.statusCode}',
          'reasonPhrase: ${response.reasonPhrase}',
          //'body: ${response.body}',
        ]);
    if (response.statusCode == 200) {
      var targetLineStart =
          '<p class="l-column small-6 medium-12 whats-new__latest__version">Version ';
      var targetLineEnd = '</p>';

      var s = response.body.indexOf(targetLineStart);
      if (s >= 0) {
        var e = response.body.indexOf(
          targetLineEnd,
          s + targetLineStart.length,
        );
        var v = response.body.substring(s + targetLineStart.length, e);
        Logs.print(() => [
              'AppVersionCheck.getAppStoreVersion()',
              'version: $v',
            ]);
        return v;
      } else {
        Logs.print(() => [
              'AppVersionCheck.getAppStoreVersion()',
              'COULD-NOT-FIND-VERSION-DUE-TO-MISSING-START-TAG',
            ]);
      }
    }
    return null;
  }

  //----------------------------------------------------------------------------

  StringSet defaultWarnMessage = StringSet(
    'A new version has been released.',
    'تم اصدار تحديث جديد من التطبيق.',
  );

  ///
  /// if publishedAndroidVersion is null, getPlayStoreVersion will run to try find the version
  /// if publishedIosVersion is null, getAppStoreVersion will run to try find the version
  ///
  void checkAndWarn(
    BuildContext Function() context, {
    required String? publishedAndroidVersion,
    required String? publishedIosVersion,
    required bool en,
    required bool forceSelectAction,
    required String? message,
    String? title,
  }) async {
    Logs.print(() => [
          'AppVersionCheck.check ---> ',
          'publishedAndroidVersion: $publishedAndroidVersion',
          'publishedIosVersion: $publishedIosVersion',
          if (publishedAndroidVersion == null)
            'android version is going to check on playstore',
          if (publishedIosVersion == null)
            'ios version is going to check on appstore',
        ]);

    publishedAndroidVersion ??= await getPlayStoreVersion();
    publishedIosVersion ??= await getAppStoreVersion();

    var b = hasNewVersion(
      publishedAndroidVersion: publishedAndroidVersion,
      publishedIosVersion: publishedIosVersion,
    );

    if (b == true) {
      MessageDialog? md;

      md = MessageDialog.create
          .setTitle(title ?? (en ? 'Alert' : 'تنبيه'))
          .setMessage(message ?? defaultWarnMessage.get(en))
          .setEnableLinks(true)
          .addActions([
            MessageDialogActionButton(
              en ? 'Update' : 'تحديث',
              action: () {
                md?.allowManualDismiss(true);
                md?.dismiss();

                if (Platform.isIOS) {
                  openAppleStore();
                } else {
                  openPlayStore();
                }
              },
            ),
            if (!forceSelectAction)
              MessageDialogActionButton(
                en ? 'Later' : 'لاحقا',
                action: null,
              ),
          ])
          .setEnableOuterDismiss(!forceSelectAction)
          .allowManualDismiss(!forceSelectAction)
          .show(context);
    }
  }

  bool? hasNewVersion({
    required String? publishedAndroidVersion,
    required String? publishedIosVersion,
  }) {
    String? localVersion;
    String? globalVersion;

    if (Platform.isAndroid) {
      localVersion = runningAndroidVersion;
      globalVersion = publishedAndroidVersion;
    }
    //
    else if (Platform.isIOS) {
      localVersion = runningIosVersion;
      globalVersion = publishedIosVersion;
    }

    Logs.print(() => [
          'AppVersionCheck.hasNewVersion ---> ',
          'platform: ${Platform.operatingSystem}',
          'localVersion: $localVersion',
          'globalVersion: $globalVersion',
        ]);

    if (localVersion == null || globalVersion == null) return null;

    var localVersionSplit = localVersion.split('.'); //1.0.0
    var globalVersionSplit = globalVersion.split('.'); //1.0.1

    var len = max(localVersionSplit.length, globalVersionSplit.length); //3
    while (len > localVersionSplit.length) {
      localVersionSplit.add('');
    }
    while (len > globalVersionSplit.length) {
      globalVersionSplit.add('');
    }

    String localVersion2 = '';
    String globalVersion2 = '';

    for (var i = 0; i < len; i++) {
      var localPart = localVersionSplit[i];
      var globalPart = globalVersionSplit[i];

      var ls = max(localPart.length, globalPart.length);
      while (ls > localPart.length) {
        localPart = '0$localPart';
      }
      while (ls > globalPart.length) {
        globalPart = '0$globalPart';
      }

      localVersion2 += localPart;
      globalVersion2 += globalPart;
    }

    //localVersion2 100, globalVersion2: 101
    var compare = globalVersion2.compareTo(localVersion2); //160<>100

    Logs.print(() => [
          'AppVersionCheck.hasNewVersion --->',
          'after converting (localVersion2: $localVersion2, globalVersion2: $globalVersion2)',
          'compare result: $compare'
        ]);

    return compare > 0;
  }

  void openPlayStore() async {
    Launcher().openUrl(await playStoreLink);
  }

  void openAppleStore() {
    Launcher().openUrl("itms-apps://itunes.apple.com/app/id$iosAppId");
  }
}
