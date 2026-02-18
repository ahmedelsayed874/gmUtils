
import 'general_storage.dart';

class AppPreferencesStorage {
  final GeneralStorage _storage = GeneralStorage.o('local_prefs');

  Future<bool> setLanguage(String langCode) async {
    return _storage.save('locale', langCode.toLowerCase());
  }

  Future<String?> getLanguage() async {
    return _storage.retrieve('locale');
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
    AppPreferences? defaultAppPreferences,
  }) async {
    return AppPreferences(
      langCode: (await getLanguage()) ?? defaultAppPreferences?.langCode,
      isLightMode: (await isLightMode()) ?? defaultAppPreferences?.isLightMode,
    );
  }
}

class AppPreferences {
  ///en: English, ar: Arabic
  String? langCode;
  bool? isLightMode;

  AppPreferences({
    required this.langCode,
    required this.isLightMode,
  });

  @override
  String toString() {
    return 'AppPreferences{langCode: $langCode, isLightMode: $isLightMode}';
  }
}