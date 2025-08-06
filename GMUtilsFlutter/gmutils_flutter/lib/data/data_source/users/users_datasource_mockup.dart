import 'dart:math';

import '../../models/response.dart';
import '../../models/users/auth_user_account.dart';
import '../../models/users/user_account.dart';
import '../../models/users/user_account_header.dart';
import '../../models/users/user_account_identifier.dart';
import 'users_datasource.dart';

class UsersDataSourceMockup extends UsersDataSource {
  final personsImages = [
    null,
    'https://img.freepik.com/free-photo/portrait-young-male-professor-education-day_23-2150980067.jpg?size=626&ext=jpg&ga=GA1.1.1141335507.1719360000&semt=ais_user',
    'https://img.freepik.com/free-photo/3d-cartoon-style-character_23-2151034005.jpg?size=626&ext=jpg',
    'https://img.freepik.com/premium-photo/cartoon-figure-man-with-glasses-tie-holding-stack-books_7023-105952.jpg?size=626&ext=jpg&ga=GA1.1.901599452.1719513696&semt=ais_user',
    'https://img.freepik.com/premium-photo/3d-illustration-young-businessman-with-glasses-book-his-hands_1057-150129.jpg?size=626&ext=jpg&ga=GA1.1.901599452.1719513696&semt=ais_user',
    'https://img.freepik.com/premium-photo/3d-render-little-schoolboy-with-glasses-library_1057-133029.jpg?size=626&ext=jpg&ga=GA1.1.901599452.1719513696&semt=ais_user',
    'https://img.freepik.com/premium-photo/professional-male-educator-proper-attire_349936-2686.jpg?size=626&ext=jpg&ga=GA1.1.901599452.1719513696&semt=ais_user',
    'https://img.freepik.com/premium-photo/lego-figure-with-glasses-tie-with-tie-it_7023-464462.jpg?size=626&ext=jpg&ga=GA1.1.901599452.1719513696&semt=ais_user',
  ];

  AuthUserAccount authorizedUser({
    String accountType = UserAccount.accountTypeTeacher,
    String accountStatus = AuthUserAccount.accountStatusActive,
    int id = 1,
  }) {
    return AuthUserAccount(
      //isFirstLogin: Random().nextBool(),
      isFirstLogin: false,
      //
      id: id,
      //accountType.hashCode,
      //
      accountType: accountType,
      //
      accountStatus: accountStatus,
      accountStatusNote: accountStatus == AuthUserAccount.accountStatusActive
          ? null
          : 'account has been closed',
      //
      email: 'a.elsayedabdo@gmail.com',
      mobile: null,
      //'01022663988',
      //
      firstName: '$accountType: Ahmed',
      middleName: 'Elsayed',
      lastName: 'Abdo',
      //
      personalPhoto: personsImages[Random().nextInt(personsImages.length)],
      idNumber: null,
      dateOfBirth: '1987-04-20',
      gender: 'male',
      //
      createdAt: '2024-07-01 12:00:00+0200',
      updatedAt: '2024-07-01 12:00:00+0200',
      token: 'token${accountType}token${accountType}'
          'token${accountType}token${accountType}'
          'token${accountType}token${accountType}'
          'token${accountType}token${accountType}',
    );
  }

  List<UserAccountHeader> get userAccountHeaders => List.generate(30, (index) {
        var id = index + 1;
        return userAccountHeader(id);
      });

  UserAccountHeader userAccountHeader(int id) {
    return UserAccountHeader(
        accountId: id,
        accountType: [
          UserAccount.accountTypeManager,
          //UserAccount.accountTypeSupervisor,
          UserAccount.accountTypeTeacher,
          UserAccount.accountTypeStudent,
          UserAccount.accountTypeParent,
        ][Random().nextInt(4)],
        firstName: ['Ahmed', 'Mohammed', 'Mahmoud'][Random().nextInt(3)],
        middleName: ['Ali', 'Saad', 'Mahmoud'][Random().nextInt(3)],
        lastName: ['Abdo', 'Elsayed', 'Hamed'][Random().nextInt(3)],
        personalPhoto: personsImages[Random().nextInt(personsImages.length)],
        teacherInfo: null,
        studentInfo: null,
        parentInfo: null,
        lastLoginTime: '2024-12-01 01:00:00');
  }

  @override
  Future<Response<AuthUserAccount>> doLogin({
    required String username,
    required String password,
    required String fcmToken,
  }) async {
    return Response.failed();

    /*String accountType;
    if (username.substring(0, 1).toLowerCase() == 'm') {
      accountType = UserAccount.accountTypeManager;
    } else if (username.substring(0, 1).toLowerCase() == 's') {
      accountType = UserAccount.accountTypeStudent;
    } else if (username.substring(0, 1).toLowerCase() == 'p') {
      accountType = UserAccount.accountTypeParent;
    } else {
      //if (username.substring(0, 1).toLowerCase() == 't') {
      accountType = UserAccount.accountTypeTeacher;
      //}
    }

    const accountStatus = AuthUserAccount.accountStatusActive;

    var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          "authenticateUser(username: $username, password: $password, fcmToken: $fcmToken)",
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet(
                  "there is not user match with $username",
                  "لا يوجد مستخدم يطابق اسم المستخدم $username",
                ),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                authorizedUser(
                  accountType: accountType,
                  accountStatus: accountStatus,
                ),
              ),
              value2: AuthUserAccountMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //------------------------------------------------------------------------------

  //region reset password
  @override
  Future<Response<void>> sendAccountPasswordResetCode({
    required String emailAddress,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'sendAccountPasswordResetCode(emailAddress: $emailAddress)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  @override
  Future<Response<UserAccountIdentifier>> verifyAccountPasswordResetCode({
    required String emailAddress,
    required String code,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'verifyAccountPasswordResetCode(emailAddress: $emailAddress, code: $code)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                UserAccountIdentifier(accountId: 1, username: 'ahmed'),
              ),
              value2: UserAccountIdentifierMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  @override
  Future<Response<void>> resetAccountPassword({
    required int accountId,
    required String password,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'resetAccountPassword(accountId: $accountId, password: $password)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //endregion

  //------------------------------------------------------------------------------

  //region update email address
  @override
  Future<Response<void>> verifyEmailAddress({
    required int accountId,
    required String emailAddress,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'verifyEmailAddress(accountId: $accountId, emailAddress: $emailAddress)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  @override
  Future<Response<void>> doConfirmEmailAddress({
    required int accountId,
    required String emailAddress,
    required String otp,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'confirmEmailAddress(accountId: $accountId, emailAddress: $emailAddress, otp: $otp)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //endregion

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangeAccountPhoto({
    required int accountId,
    required String photoPath,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'changeAccountPhoto(accountId: $accountId, photoPath: $photoPath)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangePhoneNumber({
    required int accountId,
    required String phoneNumber,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'doChangePhoneNumber(accountId: $accountId, phoneNumber: $phoneNumber)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<void>> doChangeAccountPassword({
    required int accountId,
    required String newPassword,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'changeAccountPassword(accountId: $accountId, newPassword: $newPassword)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }

  //------------------------------------------------------------------------------

  @override
  Future<Response<UserAccount>> doGetAccountInfo({
    required int accountId,
  }) async {
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'getAccountInfo(accountId: $accountId)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          var u = authorizedUser();
          u.lastLoginTime = '2024-12-01 00:00:00';

          var map = UserAccountMapper().toMap(u);

          return Pair(
            value1: Result(UserAccountMapper().fromMap(map)),
            value2: UserAccountMapper(),
          );
        }
      },
    );

    return Response.fromDummyResponse(response);*/
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
    return Response.failed();

    /*var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'getAccountsList(requesterAccountId: $requesterAccountId, '
          'requesterAccountType: $requesterAccountType, '
          'targetAccountTypes: $targetAccountTypes, '
          'contains: $contains, '
          'pageNumber: $pageNumber, pageSize: $pageSize, '
          'language: $language, '
          'sortBy: $sortBy, '
          'sortDirection: $sortDirection'
          ')',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                pageNumber < 3 ? userAccountHeaders : <UserAccountHeader>[],
              ),
              value2: UserAccountHeaderMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);*/
  }
}
