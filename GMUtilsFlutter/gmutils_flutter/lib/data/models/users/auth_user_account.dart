import 'package:gmutils_flutter/zgmutils/data_utils/storages/account_storage.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/utils/mappable.dart';

import 'user_account.dart';

class AuthUserAccount extends UserAccount implements IAccount {
  static const String accountStatusActive = 'Active';
  static const String accountStatusClosed = 'Closed';

  //----------------------------------------------------------------------------

  final String accountStatus; //[Active - Closed]
  final String? accountStatusNote;

  bool? isFirstLogin;

  final String token;

  AuthUserAccount({
    required super.id,
    //
    required super.accountType,
    required this.accountStatus,
    required this.accountStatusNote,
    //
    required this.isFirstLogin,
    //
    required super.email,
    required super.mobile,
    //
    required super.firstName,
    required super.middleName,
    required super.lastName,
    //
    required super.personalPhoto,
    required super.idNumber,
    required super.dateOfBirth,
    required super.gender,
    //
    super.lastLoginTime,
    required super.createdAt,
    required super.updatedAt,
    //
    required this.token,
    //
  });

  bool get isActive => accountStatus == accountStatusActive;

  @override
  get account_id => id;

  @override
  get token_ => token;

  @override
  String toString() {
    return 'AuthUserAccount{accountStatus: $accountStatus, accountStatusNote: $accountStatusNote, isFirstLogin: $isFirstLogin, token: $token, ${super.toString()}';
  }
}

class AuthUserAccountMapper extends Mappable<AuthUserAccount> {
  @override
  AuthUserAccount fromMap(Map<String, dynamic> values) {
    var userAccount = UserAccountMapper().fromMap(values);
    return AuthUserAccount(
      id: userAccount.id,
      //
      accountType: userAccount.accountType,
      accountStatus: values['accountStatus'],
      accountStatusNote: values['accountStatusNote'],
      //
      isFirstLogin: values['isFirstLogin'],
      //
      email: userAccount.email,
      mobile: userAccount.mobile,
      //
      firstName: userAccount.firstName,
      middleName: userAccount.middleName,
      lastName: userAccount.lastName,
      //
      personalPhoto: userAccount.personalPhoto,
      idNumber: userAccount.idNumber,
      dateOfBirth: userAccount.dateOfBirth,
      gender: userAccount.gender,
      //
      lastLoginTime: userAccount.lastLoginTime,
      //
      createdAt: userAccount.createdAt,
      updatedAt: userAccount.updatedAt,
      //
      token: values['token'],
      //
    );
  }

  @override
  Map<String, dynamic> toMap(AuthUserAccount object) {
    var map = UserAccountMapper().toMap(object);

    map['accountStatus'] = object.accountStatus;
    map['accountStatusNote'] = object.accountStatusNote;
    map['isFirstLogin'] = object.isFirstLogin;
    map['token'] = object.token;

    return map;
  }
}
