import 'dart:io';

import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/zgmutils/data_utils/storages/account_storage.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';

class RequestsHelper {
  static RequestsHelper? _instance;

  static RequestsHelper get instance => _instance ??= RequestsHelper._();

  RequestsHelper._();

  //---------------------------------------------

  String get serverUrl => main.serverUrl;

  String get apisPath => main.apisPath;

  Map<String, String> get headers => {
        'Platform': Platform.isAndroid
            ? 'Android'
            : (Platform.isIOS ? 'iOS' : Platform.operatingSystem),

        //
        'AppVersion': main.appVersion,

        //
        'Lang': App.isEnglish ? 'en' : 'ar',

        //
        if (AccountStorage.cached_account?.token_ != null)
          'Authorization': 'Bearer ${AccountStorage.cached_account?.token_}',
      };
}
