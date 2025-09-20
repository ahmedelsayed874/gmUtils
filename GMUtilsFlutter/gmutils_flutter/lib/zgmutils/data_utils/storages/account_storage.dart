//shared_preferences: ^2.0.7
import 'dart:async';
import 'dart:convert';

import '../../utils/logs.dart';
import 'package:shared_preferences/shared_preferences.dart' as shared_pref;

import '../../gm_main.dart';
import '../../utils/data_security.dart';
import '../../utils/mappable.dart';
import '../../utils/pairs.dart';

abstract class IAccount {
  get account_id;

  get token_;
}

abstract class IAccountStorage<Account extends IAccount> {
  Mappable<Account> accountMapper;

  IAccountStorage(this.accountMapper);

  Future<bool> saveAccount(
    Account account,
    String username,
    String password,
  );

  Future<bool> saveUserNameAndPassword(String username, String newPassword);

  Future<bool> updatePassword(String newPassword);

  Future<bool> updateAccount(Account account);

  Future<Account?> get account;

  Account? get cachedAccount;

  Future<Pair<String, String>?> getUserNameAndPassword();

  Future<bool> clear();
}

class AccountStorage<Account extends IAccount>
    extends IAccountStorage<Account> {
  static const KEY_ACCOUNT = "AccountStorage.ACCOUNT";
  static const KEY_USER_NAME = "AccountStorage.USER_NAME";
  static const KEY_PASSWORD = "AccountStorage.PASSWORD";

  shared_pref.SharedPreferences? __prefs;
  static IAccount? cached_account;
  static String? cached_username;

  AccountStorage(super.accountMapper);

  Future<shared_pref.SharedPreferences> get _prefs async {
    __prefs ??= await shared_pref.SharedPreferences.getInstance();
    return __prefs!;
  }

  //----------------------------------------------------------------------------

  @override
  Future<bool> saveAccount(
    Account account,
    String username,
    String password,
  ) async {
    var pref = await _prefs;

    cached_account = account;
    cached_username = username;

    var accountJson = jsonEncode(accountMapper.toMap(account));

    var accountJsonEnc = await _enc(accountJson);

    var b1 = await pref.setString(KEY_ACCOUNT, accountJsonEnc);
    var b2 = await saveUserNameAndPassword(username, password);

    AccountStorage.callObservers(account);

    return b1 && b2;
  }

  @override
  Future<bool> saveUserNameAndPassword(
    String username,
    String password,
  ) async {
    cached_username = username;

    var pref = await _prefs;

    var userNameEnc = await _enc(username);

    var b2 = await pref.setString(KEY_USER_NAME, userNameEnc);
    var b3 = await updatePassword(password);

    return b2 && b3;
  }

  @override
  Future<bool> updatePassword(String newPassword) async {
    var pref = await _prefs;
    var passwordEnc = await _enc(newPassword);
    var b3 = await pref.setString(KEY_PASSWORD, passwordEnc);
    return b3;
  }

  @override
  Future<bool> updateAccount(Account account) async {
    Pair? credentials = await getUserNameAndPassword();
    if (credentials != null) {
      return await saveAccount(account, credentials.value1, credentials.value2);
    }
    return false;
  }

  //----------------------------------------------------------------------------

  @override
  Future<Account?> get account async {
    if (cached_account == null) {
      var pref = await _prefs;
      var accountJsonEnc = pref.getString(KEY_ACCOUNT);
      if (accountJsonEnc != null && accountJsonEnc.isNotEmpty) {
        var accountJson = await _dec(accountJsonEnc);
        var map = jsonDecode(accountJson);
        try {
          cached_account = accountMapper.fromMap(map);
        } catch (e) {
          Logs.print(() =>
              'AccountStorage.get account ---> Exception $e ----> at parsing account data ($map)');
        }
      }
    }
    return cached_account as Account?;
  }

  @override
  Account? get cachedAccount => AccountStorage.cached_account as Account?;

  //----------------------------------------------------------------------------

  @override
  Future<Pair<String, String>?> getUserNameAndPassword() async {
    var pref = await _prefs;

    var un = pref.getString(KEY_USER_NAME);
    var pw = pref.getString(KEY_PASSWORD);

    if (un != null && un.isNotEmpty && pw != null && pw.isNotEmpty) {
      un = await _dec(un);
      pw = await _dec(pw);

      cached_username = un;

      return Pair(value1: un, value2: pw);
    }
    //
    else {
      return null;
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<bool> clear() async {
    var pref = await _prefs;

    cached_account = null;
    cached_username = null;

    var b1 = await pref.setString(KEY_ACCOUNT, '');
    var b2 = await pref.setString(KEY_USER_NAME, '');
    var b3 = await pref.setString(KEY_PASSWORD, '');

    AccountStorage.callObservers(null);

    return b1 && b2 && b3;
  }

  //----------------------------------------------------------------------------

  static void addObserver(String name, ObserverDelegate observer) {
    App.addObserver(
        category: 'AccountCache', observerName: name, observer: observer);
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
