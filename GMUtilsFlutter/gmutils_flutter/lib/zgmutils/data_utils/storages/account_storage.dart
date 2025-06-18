//shared_preferences: ^2.0.7
import 'dart:async';
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart' as sharedPrefLib;

import '../../gm_main.dart';
import '../../utils/data_security.dart';
import '../../utils/mappable.dart';
import '../../utils/pairs.dart';


abstract class IAccount {
  get account_id;
  get token_;
}

abstract class IAccountStorage {
  static Mappable<IAccount>? accountMapper;

  IAccountStorage(Mappable<IAccount> accountMapper) {
    IAccountStorage.accountMapper = accountMapper;
  }

  Future<bool> saveAccount(IAccount account, String username, String password,);

  Future<bool> saveUserNameAndPassword(String username, String newPassword);

  Future<bool> updateAccount(IAccount account);

  Future<IAccount?> get account;

  IAccount? get cachedAccount;

  Future<Pair<String, String>?> getUserNameAndPassword();

  Future<bool> clear();
}

class AccountStorage extends IAccountStorage {
  static const KEY_ACCOUNT = "AccountStorage.ACCOUNT";
  static const KEY_USER_NAME = "AccountStorage.USER_NAME";
  static const KEY_PASSWORD = "AccountStorage.PASSWORD";

  sharedPrefLib.SharedPreferences? __prefs;
  static IAccount? cached_account;

  AccountStorage(super.accountMapper);

  Future<sharedPrefLib.SharedPreferences> get _prefs async {
    __prefs ??= await sharedPrefLib.SharedPreferences.getInstance();
    return __prefs!;
  }

  //----------------------------------------------------------------------------

  Future<bool> saveAccount(
    IAccount account,
    String username,
    String password,
  ) async {
    var pref = await _prefs;

    cached_account = account;

    var accountJson = jsonEncode(IAccountStorage.accountMapper!.toMap(account));

    var accountJsonEnc = await _enc(accountJson);

    var b1 = await pref.setString(KEY_ACCOUNT, accountJsonEnc);
    var b2 = await saveUserNameAndPassword(username, password);

    AccountStorage.callObservers(account);

    return b1 && b2;
  }

  Future<bool> saveUserNameAndPassword(
    String username,
    String password,
  ) async {
    var pref = await _prefs;

    var userNameEnc = await _enc(username);
    var passwordEnc = await _enc(password);

    var b2 = await pref.setString(KEY_USER_NAME, userNameEnc);
    var b3 = await pref.setString(KEY_PASSWORD, passwordEnc);

    return b2 && b3;
  }

  Future<bool> updateAccount(
    IAccount account,
  ) async {
    Pair? credentials = await getUserNameAndPassword();
    if (credentials != null) {
      return await saveAccount(account, credentials.value1, credentials.value2);
    }
    return false;
  }

  //----------------------------------------------------------------------------

  Future<IAccount?> get account async {
    if (cached_account == null) {
      var pref = await _prefs;
      var accountJsonEnc = pref.getString(KEY_ACCOUNT);
      if (accountJsonEnc != null && accountJsonEnc.isNotEmpty) {
        var accountJson = await _dec(accountJsonEnc);
        var map = jsonDecode(accountJson);
        cached_account = IAccountStorage.accountMapper!.fromMap(map);
      }
    }
    return cached_account;
  }

  IAccount? get cachedAccount => AccountStorage.cached_account;

  //----------------------------------------------------------------------------

  Future<Pair<String, String>?> getUserNameAndPassword() async {
    var pref = await _prefs;

    var un = pref.getString(KEY_USER_NAME);
    var pw = pref.getString(KEY_PASSWORD);

    if (un != null && un.isNotEmpty && pw != null && pw.isNotEmpty) {
      un = await _dec(un);
      pw = await _dec(pw);
      return Pair(value1: un, value2: pw);
    } else {
      return null;
    }
  }

  //----------------------------------------------------------------------------

  Future<bool> clear() async {
    var pref = await _prefs;

    cached_account = null;

    var b1 = await pref.setString(KEY_ACCOUNT, '');
    var b2 = await pref.setString(KEY_USER_NAME, '');
    var b3 = await pref.setString(KEY_PASSWORD, '');

    AccountStorage.callObservers(null);

    return b1 && b2 && b3;
  }

  //----------------------------------------------------------------------------

  static void addObserver(String name, ObserverDelegate observer) {
    App.addObserver(category: 'AccountCache', observerName: name, observer: observer);
  }

  static void callObservers(IAccount? account) {
    App.callObservers(category: 'AccountCache', args: account);
  }

  static void removeObserver(String name) {
    App.removeObserver(category: 'AccountCache', name: name);
  }

  //----------------------------------------------------------------------------

  DataSecurity? _dataSecurity;

  Future<String> _enc(String text) async {
    _dataSecurity ??= DataSecurity();

    return await _dataSecurity!.encrypt(text);
  }

  Future<String> _dec(String encString) async {
    _dataSecurity ??= DataSecurity();
    return _dataSecurity!.decrypt(encString);
  }
}
