
import 'general_storage.dart';

class AppPreferencesStorage {
  final GeneralStorage _storage = GeneralStorage.o('local_prefs');

  Future<bool?> isEn() async {
    var locale = await _storage.retrieve("locale");
    return locale == null ? null : locale == 'en';
  }

  Future<bool> setLocale(bool toEn) async {
    return _storage.save('locale', toEn ? 'en' : 'ar');
  }

  Future<bool?> isLightMode() async {
    var v = await _storage.retrieve("appearance");
    return (v == null || v.isEmpty) ? null : v == 'L';
  }

  Future<bool> setAppearance(bool? light) async {
    return _storage.save(
      'appearance',
      light == null ? '' : (light ? 'L' : 'D'),
    );
  }

  Future<AppPreferences> savedAppPreferences({
    bool enIsDefault = true,
    bool? lightIsDefault,
  }) async {
    return AppPreferences(
      isEn: (await isEn()) ?? enIsDefault,
      isLightMode: await isLightMode(),
    );
  }
}

class AppPreferences {
  bool isEn;
  bool? isLightMode;

  AppPreferences({
    required this.isEn,
    required this.isLightMode,
  });

  @override
  String toString() {
    return 'AppPreferences{isEn: $isEn, isLightMode: $isLightMode}';
  }
}