import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

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
  //final String? publishedAndroidVersion;
  //final String? publishedIosVersion;
  final String iosAppId;

  AppVersionCheck({
    required this.runningAndroidVersion,
    required this.runningIosVersion,
    //required this.publishedAndroidVersion,
    //required this.publishedIosVersion,
    required this.iosAppId,
  }) {
    Logs.print(() => [
          'AppVersionCheck.constructor()',
          'runningAndroidVersion: $runningAndroidVersion',
          'runningIosVersion: $runningIosVersion',
          //'publishedAndroidVersion: $publishedAndroidVersion',
          //'publishedIosVersion: $publishedIosVersion',
          'iosAppId: $iosAppId',
        ]);
  }

  void check(BuildContext Function() context, bool en) async {
    var b = hasNewVersion(
      publishedAndroidVersion: await getPlayStoreVersion() ?? '0',
      publishedIosVersion: await getAppStoreVersion() ?? '0',
    );

    if (b == true) {
      Future.delayed(
        const Duration(milliseconds: 900),
        () {
          MessageDialog.create
              .setTitle(en ? 'Alert' : 'تنبيه')
              .setMessage(
                en
                    ? 'A new version has been released, please update.'
                    : 'تم اصدار تحديث جديد من التطبيق، يرجى التحديث',
              )
              .addAction(
            en ? 'Update' : 'تحديث',
            () {
              if (Platform.isIOS) {
                openAppleStore();
              } else {
                openPlayStore();
              }
            },
          ).show(context);
        },
      );
    }
  }

  bool? hasNewVersion({
    required String publishedAndroidVersion,
    required String publishedIosVersion,
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
      localVersion2 += localVersionSplit[i];
      globalVersion2 += globalVersionSplit[i];

      var ls = max(localVersion2.length, globalVersion2.length);
      while (ls > localVersion2.length) {
        localVersion2 += '0';
      }
      while (ls > globalVersion2.length) {
        globalVersion2 += '0';
      }
    }

    //localVersion2 100, globalVersion2: 101
    var compare = globalVersion2.compareTo(localVersion2); //160<>100

    Logs.print(() => [
          'AppVersionCheck',
          'hasNewVersion(localVersion: $localVersion, globalVersion: $globalVersion)',
          'after converting (localVersion2: $localVersion2, globalVersion2: $globalVersion2)',
          'compare result: $compare'
        ]);

    return compare > 0;
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

  void openPlayStore() async {
    Launcher().openUrl(await playStoreLink);
  }

  void openAppleStore() {
    Launcher().openUrl("itms-apps://itunes.apple.com/app/id$iosAppId");
  }
}
