import 'package:external_app_launcher/external_app_launcher.dart';
import 'package:url_launcher/url_launcher.dart' as url_launcher;

import 'logs.dart';

///https://pub.dev/packages/url_launcher
///https://pub.dev/packages/external_app_launcher
class Launcher {
  void printInstructions() {
    // String ins = '';
    // ins += 'iOS:\n'
    //     'Add any URL schemes passed to canLaunch as LSApplicationQueriesSchemes '
    //     'entries in your Info.plist file.\n'
    //     'Example:\n'
    //     ''
    //     '<key>LSApplicationQueriesSchemes</key>\n'
    //     '<array>\n'
    //     '   <string>https</string>\n'
    //     '   <string>http</string>\n'
    //     '</array>\n'
    //     '========> more info:: https://developer.apple.com/documentation/uikit/uiapplication/1622952-canopenurl';
    //
    // ins += '\n\n-----------------------------------\n\n';
    //
    // ins += 'Android:\n'
    //     'Starting from API 30 Android requires package visibility configuration '
    //     'in your AndroidManifest.xml otherwise canLaunch will return false. '
    //     'A <queries> element must be added to your manifest as a child of the '
    //     'root element.\n\n'
    //     ''
    //     '<queries>\n'
    //     '   <!-- If your app opens https URLs -->\n'
    //     '   <intent>\n'
    //     '      <action android:name="android.intent.action.VIEW" />\n'
    //     '      <data android:scheme="https" />\n'
    //     '   </intent>\n'
    //     '   <!-- If your app makes calls -->\n'
    //     '   <intent>\n'
    //     '      <action android:name="android.intent.action.DIAL" />\n'
    //     '      <data android:scheme="tel" />\n'
    //     '   </intent>\n'
    //     '   <!-- If your sends SMS messages -->\n'
    //     '   <intent>\n'
    //     '      <action android:name="android.intent.action.SENDTO" />\n'
    //     '      <data android:scheme="smsto" />\n'
    //     '   </intent>\n'
    //     '   <!-- If your app sends emails -->\n'
    //     '   <intent>\n'
    //     '      <action android:name="android.intent.action.SEND" />\n'
    //     '      <data android:mimeType="*/*" />\n'
    //     '   </intent>\n'
    //     '   <package android:name="OTHER_APP_PACKAGE" />'
    //     '</queries>';

    //print(ins);
    //throw ins;
  }

  Future<bool> _launchUrl(String url) async {
    ///canLaunch(url);
    bool r = false;
    try {
      r = await url_launcher.launchUrl(Uri.parse(url));
    } catch (_) {}
    if (!r) printInstructions();
    return r;
  }

  //----------------------------------------------------------------------------

  Future<bool> openUrl(String url) async {
    return await _launchUrl(url);
  }

  Future<bool> callPhoneNumber(String phoneNumber) async {
    return await _launchUrl('tel:$phoneNumber');
  }

  Future<bool> sendSms(String phoneNumber) async {
    return await _launchUrl('sms:$phoneNumber');
  }

  Future<bool> sendEmail(String email, String subject, String message) async {
    var subjectEnc = Uri.encodeComponent(subject);
    var messageEnc = Uri.encodeComponent(message);
    return await _launchUrl('mailto:$email?subject=$subjectEnc&body=$messageEnc');
  }

  Future<bool> launchUrl(Uri uri) async {
    ///canLaunch(url);
    bool r = false;
    try {
      r = await url_launcher.launchUrl(uri);
    } catch (_) {}
    if (!r) printInstructions();
    return r;
  }


  Future<void> openOtherApp({
    required String androidPackageName,
    required String iosUrlScheme,
    required String appStoreLink,
  }) async {
    iosUrlScheme = iosUrlScheme.endsWith('://') ? iosUrlScheme : '$iosUrlScheme://';
    Logs.print(() => 'Launcher --> iosUrlScheme: $iosUrlScheme');
    await LaunchApp.openApp(
      androidPackageName: androidPackageName,
      iosUrlScheme: iosUrlScheme,
      appStoreLink: appStoreLink,
      openStore: true,
    );
  }
}
