
import 'general_storage.dart';

class LocalePreference {
  final GeneralStorage _storage = GeneralStorage.o('local_prefs');

  Future<bool?> isEn() async {
    var locale = await _storage.retrieve("locale");
    return locale == null ? null : locale == 'en';
  }

  Future<bool> setLocale(bool toEn) async {
    return _storage.save('locale', toEn ? 'en' : 'ar');
  }
}