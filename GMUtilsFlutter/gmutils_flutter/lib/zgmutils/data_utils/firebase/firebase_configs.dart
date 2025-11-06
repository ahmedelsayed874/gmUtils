import 'package:firebase_remote_config/firebase_remote_config.dart';

import '../../utils/collections/pairs.dart';
import '../../utils/date_op.dart';
import '../../utils/logs.dart';
import '../storages/general_storage.dart';

abstract class IFirebaseConfigs {
  Future<Pair<bool, String?>> fetch(
      {required List<FirebaseConfigsHandler> handlers});

  Future<DateTime?> get lastFetchTime;

  Future<double> get remainMinuteTillNextFetch;
}

class FirebaseConfigs extends IFirebaseConfigs {
  late final int fetchTimeoutSeconds;
  late final int minimumFetchIntervalInMinute;
  late final IStorage _storage;

  FirebaseConfigs({
    this.fetchTimeoutSeconds = 10,
    this.minimumFetchIntervalInMinute = 10,
    IStorage? customStorage,
  }) {
    _storage = customStorage ?? GeneralStorage.o('firebase_configs');
  }

  @override
  Future<Pair<bool, String?>> fetch({
    required List<FirebaseConfigsHandler> handlers,
  }) async {
    assert(handlers.isNotEmpty);

    final rc = FirebaseRemoteConfig.instance;

    rc.setConfigSettings(RemoteConfigSettings(
      fetchTimeout: Duration(seconds: fetchTimeoutSeconds),
      minimumFetchInterval: Duration(
        minutes: minimumFetchIntervalInMinute,
      ),
    ));

    int tries = 0;
    bool success = false;
    String? error;

    while (tries++ < 9) {
      Logs.print(() => 'FirebaseConfigs.fetch --> try #$tries');

      try {
        error = null;
        await rc
            .fetchAndActivate()
            .timeout(Duration(seconds: fetchTimeoutSeconds + 1));
        success = true;
        tries = 10;
      } catch (e) {
        success = false;
        error = e.toString();

        Logs.print(
          () => 'FirebaseConfigs.fetch --> EXCEPTION [at try #$tries]: $e',
        );

        await Future.delayed(const Duration(milliseconds: 300));
        //throw 'you must enable Remote Configuration from Firebase console .... $e';
      }
    }

    if (success) {
      _saveLastFetchTime();

      var configsMap = rc.getAll().map(
            (key, value) => MapEntry(key, value.asString()),
          );

      Logs.print(
        () => 'FirebaseConfigs/FirebaseRemoteConfig -> configsMap: $configsMap',
      );

      for (var handler in handlers) {
        handler.handle(configsMap);
      }
    }

    if (error?.isNotEmpty == true) {
      if (error!.contains('TimeoutException')) {
        error = 'Connection timeout, check device connection and try again';
      }

      //
      else if (error.contains('[firebase_remote_config/internal]')) {
        error = error.replaceAll('[firebase_remote_config/internal]', '').trim();

        if (error.contains('internal remote config fetch error')) {
          error = 'Please check your internet connection';
        }
      }

      //
      else if (error.contains('"Underlying error:')) {
        var s = error.indexOf('"Underlying error:');
        if (s >= 0) {
          var e = error.indexOf('"', s);
          if (e > s) {
            try {
              error = error.substring(s + 11, e);
            } catch (_) {}
          }
        }
      }
    }

    return Pair(value1: success, value2: error);
  }

  void _saveLastFetchTime() {
    _storage.save(
      'last_fetch_time',
      DateOp().formatForDatabase(
        DateTime.now(),
        dateOnly: false,
      ),
    );
  }

  @override
  Future<DateTime?> get lastFetchTime async {
    var dt = await _storage.retrieve('last_fetch_time');
    return DateOp().parse(dt ?? '', convertToLocalTime: false);
  }

  @override
  Future<double> get remainMinuteTillNextFetch async {
    var dt = await lastFetchTime;
    if (dt == null) return 0;
    var ms = DateTime.now().millisecondsSinceEpoch - dt.millisecondsSinceEpoch;
    var min = ms / 1000.0 / 60.0;
    var remain = min - minimumFetchIntervalInMinute;
    if (remain > 0) return 0;
    return min * -1;
  }
}

abstract class FirebaseConfigsHandler {
  void handle(Map<String, String> configsMap);
}
