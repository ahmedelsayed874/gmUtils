import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource_production_urls.dart';
import 'package:gmutils_flutter/data/models/response.dart';
import 'package:gmutils_flutter/data/models/users/auth_user_account.dart';
import 'package:gmutils_flutter/main.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/storages/account_storage.dart';
import 'package:gmutils_flutter/zgmutils/utils/pairs.dart';

import '../../models/users/user_account.dart';
import '../../models/users/user_account_header.dart';
import '../../models/users/user_account_identifier.dart';
import 'users_datasource_mockup.dart';
import 'users_datasource_production.dart';

abstract class UsersDataSource {
  static UsersDataSource get instance =>
      useProductionData ? UsersDataSourceProduction() : UsersDataSourceMockup();

  //----------------------------------------------------------------------------

  late final IAccountStorage accountStorage;

  UsersDataSource({IAccountStorage? accountStorage}) {
    this.accountStorage =
        accountStorage ?? AccountStorage(AuthUserAccountMapper());
  }

  //----------------------------------------------------------------------------

  //region login
  Future<Response<AuthUserAccount>> login({
    required String username,
    required String password,
    IFCM? fcm,
  }) async {
    var fcmToken = '';//await fcm.deviceToken;

    var response = await doLogin(
      username: username,
      password: password,
      fcmToken: fcmToken ?? '',
    );
    if (response.data != null) {
      if (response.data?.isActive != true) {
        response = Response(
          status: Response.statusFailed,
          message: ResponseMessage(
            en: 'Your account has been deactivated\nReason: ${response.data?.accountStatusNote ?? '--'}',
            ar: 'تم إلغاء تفعيل حسابك'
                '\السبب: ${response.data?.accountStatusNote ?? '--'}',
          ),
          data: null,
          httpCode: 200,
        );
      }
      //
      else {
        //subscribe to fcm topics
        var topics = ['all', "${response.data!.accountType.toLowerCase()}s"];
        if (username.toLowerCase().startsWith('test') == true) {
          topics.add(username.toLowerCase());
        }
        //fcm.subscribeToTopics(topics);

        accountStorage.saveAccount(
          response.data!,
          username,
          password,
        );
      }
    }
    return response;
  }

  Future<Response<AuthUserAccount>> doLogin({
    required String username,
    required String password,
    required String fcmToken,
  });

  //endregion

  //----------------------------------------------------------------------------

  Future<Pair<String, String>?> get savedCredentials =>
      accountStorage.getUserNameAndPassword();

  Future<AuthUserAccount?> get savedUserAccount async {
    var account = await accountStorage.account;
    return account as AuthUserAccount?;
  }

  AuthUserAccount? get cachedUserAccount =>
      AccountStorage.cached_account as AuthUserAccount?;

  //----------------------------------------------------------------------------

  Future<Response<void>> logout({
    required NotificationsDataSource notificationsDataSource,
    required IFCM fcm,
  }) async {
    var account = cachedUserAccount;
    account ??= (await savedUserAccount);
    if (account == null) {
      return Response(
        status: Response.statusFailed,
        message: ResponseMessage(
          en: 'Logout failed, please reopen the app and try again',
          ar: 'تعذر تسجيل الدخول، يرجى إعادة تشغيل التطبيق والمحاولة مرة أخرى',
        ),
        data: null,
        httpCode: 0,
      );
    }

    var fcmToken = await fcm.deviceToken;

    var response = await notificationsDataSource.deleteFcmToken(
      accountId: account.id,
      fcmToken: fcmToken ?? '',
    );

    if (response.isSuccess) {
      await accountStorage.clear();

      //unsubscribe from fcm topics
      var topics = ['all', "${account.accountType.toLowerCase()}s"];
      var credential = await savedCredentials;
      if (credential?.value1.toLowerCase().startsWith('test') == true) {
        topics.add(credential!.value1.toLowerCase());
      }
      fcm.unsubscribeFromTopics(topics);
    }

    return response;
  }

  //----------------------------------------------------------------------------

  //region reset password
  Future<Response<void>> sendAccountPasswordResetCode({
    required String emailAddress,
  });

  Future<Response<UserAccountIdentifier>> verifyAccountPasswordResetCode({
    required String emailAddress,
    required String code,
  });

  Future<Response<void>> resetAccountPassword({
    required int accountId,
    required String password,
  });

  //endregion

  //----------------------------------------------------------------------------

  //region update email address
  Future<Response<void>> verifyEmailAddress({
    required int accountId,
    required String emailAddress,
  });

  Future<Response<void>> confirmEmailAddress({
    required int accountId,
    required String emailAddress,
    required String otp,
  }) async {
    var response = await doConfirmEmailAddress(
      accountId: accountId,
      emailAddress: emailAddress,
      otp: otp,
    );

    if (response.isSuccess) {
      var account = await savedUserAccount;
      account!.email = emailAddress;

      var cred = await savedCredentials;
      accountStorage.saveAccount(account, cred!.value1, cred.value2);
    }

    return response;
  }

  Future<Response<void>> doConfirmEmailAddress({
    required int accountId,
    required String emailAddress,
    required String otp,
  });

  //endregion

  //----------------------------------------------------------------------------

  //region changeAccountPhoto
  Future<Response<void>> changeAccountPhoto({
    required int accountId,
    required String photoPath,
  }) async {
    var response = await doChangeAccountPhoto(
      accountId: accountId,
      photoPath: photoPath,
    );

    if (response.isSuccess) {
      var account = await savedUserAccount;
      //if (photoPath.toLowerCase().startsWith('http')) {
      account!.personalPhoto = photoPath;
      // } else {
      //   account!.personalPhoto = serverUrl + photoPath;
      // }

      var cred = await savedCredentials;
      accountStorage.saveAccount(account, cred!.value1, cred.value2);
    }

    return response;
  }

  Future<Response<void>> doChangeAccountPhoto({
    required int accountId,
    required String photoPath,
  });

  //endregion

  //----------------------------------------------------------------------------

  //region changePhoneNumber
  Future<Response<void>> changePhoneNumber({
    required int accountId,
    required String phoneNumber,
  }) async {
    var response = await doChangePhoneNumber(
      accountId: accountId,
      phoneNumber: phoneNumber,
    );

    if (response.isSuccess) {
      var account = await savedUserAccount;
      account?.mobile = phoneNumber;

      var cred = await savedCredentials;
      accountStorage.saveAccount(account!, cred!.value1, cred.value2);
    }

    return response;
  }

  Future<Response<void>> doChangePhoneNumber({
    required int accountId,
    required String phoneNumber,
  });

  //endregion

  //----------------------------------------------------------------------------

  //region changeAccountPassword
  Future<Response<void>> changeAccountPassword({
    required int accountId,
    required String newPassword,
  }) async {
    var response = await doChangeAccountPassword(
      accountId: accountId,
      newPassword: newPassword,
    );

    if (response.isSuccess) {
      var account = await savedUserAccount;
      account?.isFirstLogin = false;

      var cred = await savedCredentials;
      accountStorage.saveAccount(account!, cred!.value1, newPassword);
    }

    return response;
  }

  Future<Response<void>> doChangeAccountPassword({
    required int accountId,
    required String newPassword,
  });

  //endregion

  //----------------------------------------------------------------------------

  //region getAccountInfo
  Future<Response<UserAccount>> getAccountInfo({
    required int accountId,
  }) {
    return doGetAccountInfo(accountId: accountId);
  }

  Future<Response<UserAccount>> doGetAccountInfo({
    required int accountId,
  });

  //endregion

  //----------------------------------------------------------------------------

  SortCriteria sortCriteria = SortCriteria();

  //region getAccountsList
  Future<Response<List<UserAccountHeader>>> getAccountsList({
    required int requesterAccountId,
    required String requesterAccountType,
    required List<String>? targetAccountTypes,
    required String? contains,
    required int pageNumber,
    required int pageSize,
    required String language, //→ [en - ar]
    required String? sortBy, //→ [Name - LastLoginTime]
    required String? sortDirection, //→ [ASC - DESC]
  }) {
    return doGetAccountsList(
      requesterAccountId: requesterAccountId,
      requesterAccountType: requesterAccountType,
      targetAccountTypes: targetAccountTypes,
      contains: contains,
      pageNumber: pageNumber,
      pageSize: pageSize,
      language: language,
      sortBy: sortBy,
      sortDirection: sortDirection,
    );
  }

  Future<Response<List<UserAccountHeader>>> doGetAccountsList({
    required int requesterAccountId,
    required String requesterAccountType,
    required List<String>? targetAccountTypes,
    required String? contains,
    required int pageNumber,
    required int pageSize,
    required String language, //→ [en - ar]
    required String? sortBy, // → [Name - LastLoginTime]
    required String? sortDirection, //→ [ASC - DESC]
  });

//endregion
}
