import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:bilingual_learning_schools_ksa/zgmutils/data_utils/firebase/firebase_configs.dart';
import 'package:bilingual_learning_schools_ksa/zgmutils/data_utils/storages/general_storage.dart';
import 'package:bilingual_learning_schools_ksa/zgmutils/utils/app_version_check.dart';
import 'package:bilingual_learning_schools_ksa/zgmutils/utils/date_op.dart';
import 'package:bilingual_learning_schools_ksa/zgmutils/utils/logs.dart';
import 'package:bilingual_learning_schools_ksa/main.dart' as main;


class AppConfigs {
  String get currentAppVersion => main.appVersion;
  String get iosAppStoreId => main.iosAppStoreId;

  late final IStorage _storage;

  AppConfigs({IStorage? storage}) {
    _storage = storage ?? GeneralStorage.o('app_configs_cached');
  }

  //----------------------------------------------------------------------------

  AppConfigsData? _appConfigsData;

  AppConfigsData? get appConfigsData => _appConfigsData;

  Future<void> fetch() async {
    _AppConfigsDataHandler handler = _AppConfigsDataHandler();
    await FirebaseConfigs().fetch(handlers: [handler]);
    _appConfigsData = handler.appConfigsData;

    _cacheAppConfigsData(handler);

    Logs.print(
          () => 'AppConfigs/_fetch -> appConfigsData: $_appConfigsData',
    );
  }

  void _cacheAppConfigsData(_AppConfigsDataHandler handler) async {
    var map = handler.toMap();
    var json = jsonEncode(map);
    await _storage.save('data', json);
  }

  Future<AppConfigsData?> get cachedAppConfigsData async {
    var json = await _storage.retrieve('data');
    if (json == null || json.isEmpty) return null;

    try {
      var map = jsonDecode(json);
      var h = _AppConfigsDataHandler();
      h.handle(Map.from(map));
      return h.appConfigsData;
    } catch (e) {
      return null;
    }
  }

  //--------------------------------------------------------------------------

  Stream<String> getServerUrl({required String? username}) async* {
    var cachedData = await cachedAppConfigsData;
    if (cachedData != null) {
      yield cachedData.getServerUrl(username: username);
    }

    await fetch();

    yield appConfigsData!.getServerUrl(username: username);
  }

  //--------------------------------------------------------------------------

  bool needUpdateApp() {
    if (_appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
      return false;
    }

    var checker = _getAppVersionCheckInstance();
    var b = checker.hasNewVersion();
    Logs.print(() => 'AppConfigs -> needUpdate() -> $b');
    return b == true;
  }

  bool mustUpdateApp() {
    if (_appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
      return false;
    }

    String expireDate = '';
    if (Platform.isIOS) {
      expireDate = _appConfigsData!.previousIosAppVersionExpiryDate;
    }
    //
    else if (Platform.isAndroid) {
      expireDate = _appConfigsData!.previousAndroidAppVersionExpiryDate;
    }
    //
    else {
      return false;
    }

    var date = DateOp().parse(expireDate, convertToLocalTime: false);
    if (date == null) {
      Logs.print(
        () => 'AppConfigs -> forceUpdateNow()::\n'
            'expireDate: $expireDate ($date) ..... return false',
      );
      return false;
    }

    var timeNow = DateTime.now();
    var b = timeNow.millisecondsSinceEpoch >= date.millisecondsSinceEpoch;
    Logs.print(
      () => 'AppConfigs -> forceUpdateNow()::\n'
          '$timeNow >= $date ? $b',
    );
    return b;
  }

  void updateApp() {
    if (_appConfigsData == null) {
      Logs.print(
        () => 'AppConfigs.needUpdate() -> must use get() method first',
      );
    }

    var checker = _getAppVersionCheckInstance();

    if (Platform.isIOS) {
      checker.openAppleStore();
    }
    //
    else if (Platform.isAndroid) {
      checker.openPlayStore();
    }
  }

  AppVersionCheck _getAppVersionCheckInstance() {
    return AppVersionCheck(
      runningAndroidVersion: currentAppVersion,
      runningIosVersion: currentAppVersion,
      publishedAndroidVersion: _appConfigsData!.latestAndroidAppVersion,
      publishedIosVersion: _appConfigsData!.latestIosAppVersion,
      iosAppId: iosAppStoreId,
    );
  }
}

class _AppConfigsDataHandler extends FirebaseConfigsHandler {
  late AppConfigsData appConfigsData;

  @override
  void handle(Map<String, String> configsMap) {
    appConfigsData = AppConfigsData(
      latestAndroidAppVersion: configsMap['appVersion_android_latest'] ?? '',
      previousAndroidAppVersionExpiryDate:
          configsMap['appVersion_android_previousExpiryDate'] ?? '',
      //
      latestIosAppVersion: configsMap['appVersion_ios_latest'] ?? '',
      previousIosAppVersionExpiryDate:
          configsMap['appVersion_ios_previousExpiryDate'] ?? '',
      //
      serverUrl: configsMap['serverUrl'] ?? '',
      //
      specialServerUrl: configsMap['specialServerUrl'] ?? '',
      //
      logFileDeadline: configsMap['zLogFileDeadline'] ?? '',
    );
  }

  Map<String, String> toMap() {
    return {
      'appVersion_android_latest': appConfigsData.latestAndroidAppVersion,
      'appVersion_android_previousExpiryDate': appConfigsData.previousAndroidAppVersionExpiryDate,
      //
      'appVersion_ios_latest': appConfigsData.latestIosAppVersion,
      'appVersion_ios_previousExpiryDate': appConfigsData.previousIosAppVersionExpiryDate,
      //
      'serverUrl': appConfigsData._serverUrl,
      //
      'specialServerUrl': appConfigsData._specialServerUrl,
      //
      'zLogFileDeadline': appConfigsData._logFileDeadline,
    };
  }
}

class AppConfigsData {
  final String latestAndroidAppVersion;
  final String latestIosAppVersion;
  final String previousAndroidAppVersionExpiryDate;
  final String previousIosAppVersionExpiryDate;

  //
  final String _serverUrl;

  /*
  e.g.: {
           "username": {
                 "url":"http://domain.com/",
                 "expireOn": "yyyy-MM-dd HH-mm:dd"
           }
        }
   */
  final String _specialServerUrl;

  /*
  e.g.: {
           "username": {
                 "expireOn": "yyyy-MM-dd HH-mm:dd"
           }
        }
   */
  final String _logFileDeadline;

  //---------------------------------------------------------------------------

  AppConfigsData({
    required this.latestAndroidAppVersion,
    required this.latestIosAppVersion,
    required this.previousAndroidAppVersionExpiryDate,
    required this.previousIosAppVersionExpiryDate,
    //
    required String serverUrl,
    //
    required String specialServerUrl,
    //
    required String logFileDeadline,
  })  : _serverUrl = serverUrl,
        _specialServerUrl = specialServerUrl,
        _logFileDeadline = logFileDeadline;


  @override
  String toString() {
    return 'AppConfigsData{latestAndroidAppVersion: $latestAndroidAppVersion, latestIosAppVersion: $latestIosAppVersion, previousAndroidAppVersionExpiryDate: $previousAndroidAppVersionExpiryDate, previousIosAppVersionExpiryDate: $previousIosAppVersionExpiryDate, _serverUrl: $_serverUrl, _specialServerUrl: $_specialServerUrl, _logFileDeadline: $_logFileDeadline}';
  }

  //---------------------------------------------------------------------------

  String getServerUrl({required String? username}) {
    String? url;
    String? expire;

    if (_specialServerUrl.isNotEmpty && username?.isNotEmpty == true) {
      try {
        /*
        specialServerUrl:
        {
          "sw1":  {    "url": "https://beyti.mnc-control.com/",    "expireOn":"2024-05-16 17:00:00"  },
          "9606": {    "url": "https://beyti.mnc-control.com/",    "expireOn":"2024-05-16 17:00:00"  }
        }
         */
        var jsonAsMap = jsonDecode(_specialServerUrl);
        var usernameMap = jsonAsMap[username];
        url = usernameMap['url'];
        expire = usernameMap['expireOn'];
      } catch (e) {
        Logs.print(() =>
            'AppConfigs.getServerUrlIfExist(username: $username) ---> Exception: $e');
      }
    }

    if (url == null || url.isEmpty) {
      url = _serverUrl;
      expire = '9999-09-09 00:00:00';
    }

    if (url.isNotEmpty) {
      if (_isUrlExpired(expire ?? '')) {
        return '';
      } else {
        return url;
      }
    } else {
      return '';
    }
  }

  bool _isUrlExpired(String date) {
    if (date.isEmpty) return true;
    var d = DateTime.tryParse(date);
    if (d == null) return true;
    return d.millisecondsSinceEpoch < DateTime.now().millisecondsSinceEpoch;
  }

  //----------------------------------------------------------

  String? getLogFileDeadline({required String username}) {
    String? expire;

    if (_logFileDeadline.isNotEmpty) {
      try {
        /*
        logFileDeadline:
        {
          "sw1":  {  "expireOn":"2024-05-16 17:00:00"  },
          "9606": {  "expireOn":"2024-05-16 17:00:00"  }
        }
         */
        var jsonAsMap = jsonDecode(_logFileDeadline);
        var usernameMap = jsonAsMap[username];
        expire = usernameMap['expireOn'];
      } catch (e) {
        Logs.print(() =>
            'AppConfigs.getLogFileDeadline(username: $username) ---> Exception: $e');
      }
    }

    return expire;
  }
}
