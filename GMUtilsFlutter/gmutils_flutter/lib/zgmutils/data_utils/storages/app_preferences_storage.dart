import '../../utils/logs.dart';
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

  Future<bool> updateAppPreferences(AppPreferences appPreferences) async {
    Logs.print(
      () =>
          '💡$runtimeType.updateAppPreferences('
          'langCode: ${appPreferences.langCode},'
          'isLightMode: ${appPreferences.isLightMode},'
          ')',
    );

    bool b1 = true;
    if (appPreferences.langCode != null) {
      b1 = await setLanguage(appPreferences.langCode!);
    }

    bool b2 = true;
    if (appPreferences.isLightMode != null) {
      b2 = await setAppearance(appPreferences.isLightMode!);
    }

    return b1 && b2;
  }

  Future<AppPreferences> savedAppPreferences({
    AppPreferences? defaultAppPreferences,
  }) async {
    var _langCode = await getLanguage();
    if (_langCode == null) {
      _langCode = defaultAppPreferences?.langCode;
      if (_langCode != null) setLanguage(_langCode);
    }

    var _isLightMode = await isLightMode();
    if (_isLightMode == null) {
      _isLightMode = defaultAppPreferences?.isLightMode;
      if (_isLightMode != null) setAppearance(_isLightMode);
    }

    return AppPreferences(langCode: _langCode, isLightMode: _isLightMode);
  }
}

class AppPreferences {
  ///en: English, ar: Arabic
  String? langCode;
  bool? isLightMode;

  AppPreferences({required this.langCode, required this.isLightMode});

  @override
  String toString() {
    return 'AppPreferences{langCode: $langCode, isLightMode: $isLightMode}';
  }
}
