//shared_preferences: ^2.0.7
import 'dart:async';

import 'package:shared_preferences/shared_preferences.dart' as sharedPrefLib;

abstract class IStorage {

  Future<bool> save(
    String key,
    String data
  );

  //----------------------------------------------------------------------------

  Future<String?> retrieve(String key);

  //----------------------------------------------------------------------------

  Future<bool> remove(String key);

}

class GeneralStorage extends IStorage {
  final String prefName;

  GeneralStorage.o(this.prefName);

  sharedPrefLib.SharedPreferences? __prefs;

  Future<sharedPrefLib.SharedPreferences> get _prefs async {
    __prefs ??= await sharedPrefLib.SharedPreferences.getInstance();
    return __prefs!;
  }

  //----------------------------------------------------------------------------

  Future<bool> save(
    String key,
    String data,
  ) async {
    var pref = await _prefs;
    var b1 = await pref.setString('${prefName}_$key', data);
    return b1;
  }

  //----------------------------------------------------------------------------

  Future<String?> retrieve(String key) async {
    var pref = await _prefs;
    var data = pref.getString('${prefName}_$key');
    return data;
  }

  //----------------------------------------------------------------------------

  Future<bool> remove(String key) async {
    var pref = await _prefs;
    var b1 = await pref.remove('${prefName}_$key');
    return b1;
  }

}
