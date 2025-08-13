import 'package:gmutils_flutter/data/data_source/requests_helper.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/web_url.dart';

import '../../models/response.dart';
import '../../models/users/auth_user_account.dart';
import '../../models/users/user_account_header.dart';
import '../../models/users/user_account_identifier.dart';

class LoginUrl extends PostUrl<Response<AuthUserAccount>> {
  LoginUrl({
    required String username,
    required String password,
    required String fcmToken,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'Login',
          responseMapper: ResponseMapper(dataMapper: AuthUserAccountMapper()),
          queries: null,
          params: {
            'userName': username,
            'password': password,
            // 'fcmToken': fcmToken,
            'fcmToken':
                '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz',
          },
          obscureLogOptions: [
            // ObscureLogOption.allValueOf('fcmToken'),
            // ObscureLogOption.firstHalfOfValueOf('password'),
            ObscureLogOption.secondHalfOfValueOf('password'),
            //ObscureLogOption.firstOfValueOf('password', withPercent: 0.7),
            ObscureLogOption.firstOfValueOf('userName', withPercent: 0.7),
            ObscureLogOption.encryptValueOf('fcmToken', secretKey: 'aAbB'),
          ],
        );
}

//------------------------------------------------------------------------------

//region reset password
class SendAccountPasswordResetCodeUrl extends PostUrl<Response<void>> {
  SendAccountPasswordResetCodeUrl({
    required String emailAddress,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'SendAccountPasswordResetCode',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'emailAddress': emailAddress,
          },
        );
}

class VerifyAccountPasswordResetCodeUrl
    extends PostUrl<Response<UserAccountIdentifier>> {
  VerifyAccountPasswordResetCodeUrl({
    required String emailAddress,
    required String code,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'VerifyAccountPasswordResetCode',
          responseMapper:
              ResponseMapper(dataMapper: UserAccountIdentifierMapper()),
          queries: null,
          params: {
            'emailAddress': emailAddress,
            'code': code,
          },
        );
}

class ResetAccountPasswordUrl extends PostUrl<Response<void>> {
  ResetAccountPasswordUrl({
    required int accountId,
    required String password,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'ResetAccountPassword',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'password': password,
          },
        );
}
//endregion

//------------------------------------------------------------------------------

//region update email address
class VerifyEmailAddressUrl extends PostUrl<Response<void>> {
  VerifyEmailAddressUrl({
    required int accountId,
    required String emailAddress,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'VerifyEmailAddress',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'emailAddress': emailAddress,
          },
        );
}

class ConfirmEmailAddressUrl extends PostUrl<Response<void>> {
  ConfirmEmailAddressUrl({
    required int accountId,
    required String emailAddress,
    required String otp,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'ConfirmEmailAddress',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'emailAddress': emailAddress,
            'otp': otp,
          },
        );
}
//endregion

//------------------------------------------------------------------------------

class ChangeAccountPhotoUrl extends PostUrl<Response<void>> {
  ChangeAccountPhotoUrl({
    required int accountId,
    required String photoPath,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'ChangeAccountPhoto',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'photoPath': photoPath,
          },
        );
}

//------------------------------------------------------------------------------

class ChangePhoneNumberUrl extends PostUrl<Response<void>> {
  ChangePhoneNumberUrl({
    required int accountId,
    required String phoneNumber,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'ChangePhoneNumber',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'phoneNumber': phoneNumber,
          },
        );
}

//------------------------------------------------------------------------------

class ChangeAccountPasswordUrl extends PostUrl<Response<void>> {
  ChangeAccountPasswordUrl({
    required int accountId,
    required String newPassword,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'ChangeAccountPassword',
          responseMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'newPassword': newPassword,
          },
        );
}

//------------------------------------------------------------------------------

class GetAccountInfoUrl extends GetUrl<Response<UserAccount>> {
  GetAccountInfoUrl({
    required int accountId,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'GetAccountInfo',
          responseMapper: ResponseMapper(dataMapper: UserAccountMapper()),
          queries: {
            'accountId': '$accountId',
          },
        );
}

//------------------------------------------------------------------------------

class SortCriteria {
  final String sortByName = 'Name';
  final String sortByLastLoginTime = 'LastLoginTime';
  final String sortDirectionAsc = 'ASC';
  final String sortDirectionDesc = 'DESC';
}

class GetAccountsListUrl extends PostUrl<Response<List<UserAccountHeader>>> {
  GetAccountsListUrl({
    required int requesterAccountId,
    required String requesterAccountType,
    required List<String>? targetAccountTypes,
    required String? contains,
    required int pageNumber,
    required int pageSize,
    required String language, //→ [en - ar]
    required String? sortBy, // → [Name - LastLoginTime]
    required String? sortDirection, //→ [ASC - DESC]
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'GetAccountsList',
          responseMapper: ResponseMapper(dataMapper: UserAccountHeaderMapper()),
          queries: null,
          params: {
            'requesterAccountId': requesterAccountId,
            'requesterAccountType': requesterAccountType,
            'targetAccountTypes': targetAccountTypes,
            'contains': contains,
            'pageNumber': pageNumber,
            'pageSize': pageSize,
            'language': language,
            if (sortBy != null) 'sortBy': sortBy,
            if (sortDirection != null) 'sortDirection': sortDirection,
          },
        );

/*static Map<String, String>? collectQueries({
    required int requesterAccountId,
    required String requesterAccountType,
    required List<String>? targetAccountTypes,
    required String? contains,
    required int pageNumber,
    required int pageSize,
    required String language, //→ [en - ar]
  }) {
    var map = {
      'requesterAccountId': '$requesterAccountId',
      'requesterAccountType': requesterAccountType,
      if (contains != null) 'contains': contains,
      'pageNumber': '$pageNumber',
      'pageSize': '$pageSize',
      'language': language,
    };

    int i = -1;
    targetAccountTypes?.forEach((t) {
      i++;
      map['targetAccountTypes[$i]'] = t;
    });

    return map;
  }*/
}
