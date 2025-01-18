import 'package:gmutils_flutter/data/data_source/users/users_datasource_production_urls.dart';

import '../../../zgmutils/data_utils/web/web_request_executors.dart';
import '../../models/response.dart';
import '../../models/users/auth_user_account.dart';
import '../../models/users/user_account.dart';
import '../../models/users/user_account_header.dart';
import '../../models/users/user_account_identifier.dart';
import 'users_datasource.dart';

class UsersDataSourceProduction extends UsersDataSource {
  @override
  Future<Response<AuthUserAccount>> doLogin({
    required String username,
    required String password,
    required String fcmToken,
  }) async {
    var url = LoginUrl(
      username: username,
      password: password,
      fcmToken: fcmToken,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //------------------------------------------------------------------------------

  //region reset password
  @override
  Future<Response<void>> sendAccountPasswordResetCode({
    required String emailAddress,
  }) async {
    var url = SendAccountPasswordResetCodeUrl(emailAddress: emailAddress);
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<UserAccountIdentifier>> verifyAccountPasswordResetCode({
    required String emailAddress,
    required String code,
  }) async {
    var url = VerifyAccountPasswordResetCodeUrl(
      emailAddress: emailAddress,
      code: code,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<void>> resetAccountPassword({
    required int accountId,
    required String password,
  }) async {
    var url = ResetAccountPasswordUrl(accountId: accountId, password: password);
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //endregion

  //------------------------------------------------------------------------------

  //region update email address
  @override
  Future<Response<void>> verifyEmailAddress({
    required int accountId,
    required String emailAddress,
  }) async {
    var url = VerifyEmailAddressUrl(
      accountId: accountId,
      emailAddress: emailAddress,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<void>> doConfirmEmailAddress({
    required int accountId,
    required String emailAddress,
    required String otp,
  }) async {
    var url = ConfirmEmailAddressUrl(
      accountId: accountId,
      emailAddress: emailAddress,
      otp: otp,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //endregion

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangeAccountPhoto({
    required int accountId,
    required String photoPath,
  }) async {
    var url = ChangeAccountPhotoUrl(accountId: accountId, photoPath: photoPath);
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangePhoneNumber({
    required int accountId,
    required String phoneNumber,
  }) async {
    var url = ChangePhoneNumberUrl(
      accountId: accountId,
      phoneNumber: phoneNumber,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangeAccountPassword({
    required int accountId,
    required String newPassword,
  }) async {
    var url = ChangeAccountPasswordUrl(
      accountId: accountId,
      newPassword: newPassword,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<UserAccount>> doGetAccountInfo({
    required int accountId,
  }) async {
    var url = GetAccountInfoUrl(accountId: accountId);
    var response = await WebRequestExecutor().executeGet(url);
    return Response.fromWebResponse(response);
  }

  //------------------------------------------------------------------------------

  @override
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
  }) async {
    var url = GetAccountsListUrl(
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
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

}
