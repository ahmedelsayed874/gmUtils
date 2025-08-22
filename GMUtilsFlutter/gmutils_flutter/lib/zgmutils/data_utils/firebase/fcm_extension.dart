import 'package:flutter/services.dart' show rootBundle;
import "package:googleapis_auth/auth_io.dart";
import 'package:googleapis_auth/googleapis_auth.dart' as google_auth;
import 'package:http/http.dart' as http;

import '../../utils/result.dart';
import '../../utils/string_set.dart';
import 'fcm.dart';

class FCM_Extension {
  ///https://pub.dev/packages/googleapis_auth
  ///googleapis_auth: ^1.6.0
  Future<Result<String>> getAccessToken({
    required FCMConfigurations? fcmConfigurations,
  }) {
    return _getAccessToken(fcmConfigurations: fcmConfigurations);
    //return Future.value(Result(null, message: StringSet('enable FCM_Extension class')));
  }

  Future<Result<String>> _getAccessToken({
    required FCMConfigurations? fcmConfigurations,
  }) async {
    const MESSAGING_SCOPE =
        "https://www.googleapis.com/auth/firebase.messaging";
    final List<String> scopes = [MESSAGING_SCOPE];

    var path = fcmConfigurations
        ?.sendFcmMessageParameters?.firebaseServiceAccountFilePathInAssets;
    if (path?.isNotEmpty != true) {
      return Result(
        null,
        message: StringSet('Path to Service Account File is missing'),
      );
    }

    var bytes = await rootBundle.load(path!);
    var json = String.fromCharCodes(bytes.buffer.asInt8List());

    ServiceAccountCredentials accountCredentials;
    try {
      accountCredentials = google_auth.ServiceAccountCredentials.fromJson(json);
    } catch (e) {
      //return Result(null, message: StringSet('Creating ServiceAccountCredentials from json failed$e'),);
      rethrow;
    }

    AccessCredentials? credentials;
    StringSet? exception;

    var client = http.Client();
    try {
      credentials = await obtainAccessCredentialsViaServiceAccount(
        accountCredentials,
        scopes,
        client,
      );
    } catch (e) {
      exception = StringSet('$e');
    }

    client.close();

    return Result(credentials?.accessToken.data, message: exception);
  }
}
