import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';

import '../../utils/collections/string_set.dart';
import '../../utils/logs.dart';
import '../../utils/text/text_utils.dart';
import '../utils/result.dart';
import 'fb_response.dart';
import 'firebase_utils.dart';

abstract class IFirebaseAuthManager {
  Future<FBResponse<bool>> registerByNonEmail({
    required String text,
    required String password,
  });

  Future<FBResponse<bool>> registerByEmail({
    required String email,
    required String password,
  });

  Future<FBResponse<User>> loginByNonEmail(String text, String password);

  Future<FBResponse<User>> loginByEmail(String email, String password);

  Future<void> logout();

  //----------------------------------------------------------------------------

  String formatNonEmailToEmail(String text) {
    text = FirebaseUtils.refinePhoneNumber(text);
    text = FirebaseUtils.refineKeyName(text);
    return 'un_$text$nonEmailHostName';
  }

  String get nonEmailHostName;

  //----------------------------------------------------------------------------

  Future<FBResponse<bool>> startPasswordReset({
    required String identifier,
    required bool isEmailIdentifier,
  });

  Future<FBResponse<bool>> verifyResetPasswordCode({
    required String identifier,
    required bool isEmailIdentifier,
    required String resetPasswordCode,
  });

  Future<FBResponse<bool>> changePasswordAfterResetting(
    String resetPasswordCode,
    String newPassword,
  );

  //----------------------------------------------------------------------------

  Future<FBResponse<bool>> changePassword({
    required String identifier,
    required bool isEmailIdentifier,
    required String currentPassword,
    required String newPassword,
  });

  //----------------------------------------------------------------------------

  static const String EMAIL_ALREADY_IN_USE = 'User identifier already in use';

  FBResponse<T> firebaseAuthExceptionMessage<T>(FirebaseAuthException e);
}

class FirebaseAuthManager extends IFirebaseAuthManager {
  final String _nonEmailHostName;

  FirebaseAuthManager({String nonEmailHostName = 'myusers.org'})
      : _nonEmailHostName = nonEmailHostName;

  FirebaseAuth? _auth;

  Future<FirebaseAuth> get fbAuth async {
    if (_auth == null) {
      try {
        _auth = FirebaseAuth.instance;
      } catch (e) {
        await Firebase.initializeApp();
        _auth = FirebaseAuth.instance;
      }
    }

    return _auth!;
  }

  //----------------------------------------------------------------------------

  @override
  String get nonEmailHostName => _nonEmailHostName;

  Future<Result<UserCredential>> _userCredential({
    required Future<UserCredential> Function() fbMethod,
  }) async {
    try {
      UserCredential userCredential;

      userCredential = await Future.sync(() async {
        return fbMethod();
      }).timeout(const Duration(seconds: 10));

      return Result(userCredential);
    } on TimeoutException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.TimeoutException]._userCredential ---> $e');
      return Result(null,
          message: StringSet(
            'Request timeout. please check your connection.',
            'انتهى الوقت المتوقع، يرجى التأكد من الاتصال بالانترنت.',
          ));
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException]._userCredential ---> $e');
      var r = firebaseAuthExceptionMessage(e);
      return Result(null, message: r.error);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception]._userCredential ---> $e');
      return Result(null, message: StringSet(e.toString()));
    }
  }

  @override
  Future<FBResponse<bool>> registerByNonEmail({
    required String text,
    required String password,
  }) async {
    Logs.print(() => 'FirebaseAuthManager[Call].registerByNonEmail('
        'text: $text, '
        'password: ********'
        ')');

    return registerByEmail(
      email: formatNonEmailToEmail(text),
      password: password,
    );
  }

  @override
  Future<FBResponse<bool>> registerByEmail({
    required String email,
    required String password,
  }) async {
    Logs.print(() => 'FirebaseAuthManager[Call].registerByEmail('
        'text: $email, '
        'password: ********'
        ')');

    try {
      var res = await _userCredential(fbMethod: () async {
        return await (await fbAuth).createUserWithEmailAndPassword(
          email: email,
          password: password,
        );
      });

      if (res.result == null) {
        Logs.print(() => 'FirebaseAuthManager[Response].registerByEmail '
            '----> ERROR:: ${res.message?.en}');

        return FBResponse.failed(error: res.message);
      }

      UserCredential userCredential = res.result!;

      var registered = userCredential.additionalUserInfo?.isNewUser == true;

      Logs.print(() => 'FirebaseAuthManager[Response].registerByEmail '
          '----> registered: $registered');

      return FBResponse.success(data: registered);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].registerByEmail ---> $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception].registerByEmail ---> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<FBResponse<User>> loginByNonEmail(String text, String password) {
    Logs.print(() => 'FirebaseAuthManager[Call].loginByNonEmail('
        'text: $text, '
        'password: ********');

    return loginByEmail(formatNonEmailToEmail(text), password);
  }

  @override
  Future<FBResponse<User>> loginByEmail(String email, String password) async {
    Logs.print(() => 'FirebaseAuthManager[Call].loginByEmail('
        'text: $email, '
        'password: ********');

    try {
      var res = await _userCredential(fbMethod: () async {
        return await (await fbAuth).signInWithEmailAndPassword(
          email: email,
          password: password,
        );
      });

      if (res.result == null) {
        Logs.print(() => 'FirebaseAuthManager[Response].loginByEmail '
            '----> ERROR:: ${res.message?.en}');

        return FBResponse.failed(error: res.message);
      }

      UserCredential userCredential = res.result!;

      FBResponse<User> response;

      if (userCredential.user != null) {
        response = FBResponse.success(data: userCredential.user);
      }
      //
      else {
        response = FBResponse.failed(
          error: StringSet(
            "login failed",
            "تعذر تسجيل الدخول",
          ),
        );
      }

      Logs.print(() => 'FirebaseAuthManager[Response].loginByEmail '
          '----> response.data: ${TextUtils().trimEnd('${response.data}')}, '
          'response.message: ${response.error}');

      return response;
    } on TimeoutException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.TimeoutException].loginByEmail ---> $e');
      return FBResponse.failed(
        error: StringSet('Request timeout. please check your connection.',
            'انتهى الوقت المتوقع، يرجى التأكد من الاتصال بالانترنت.'),
      );
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].loginByEmail ---> $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager[Response.Exception].loginByEmail ---> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<void> logout() async {
    Logs.print(() => 'FirebaseAuthManager[Call].logout');
    try {
      await (await fbAuth).signOut();
      Logs.print(() => 'FirebaseAuthManager[Response].logout ---> DONE');
    } catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager[Response].logout ---> Exception:: $e');
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<FBResponse<bool>> startPasswordReset({
    required String identifier,
    required bool isEmailIdentifier,
  }) async {
    try {
      String email =
          isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
      Logs.print(() => 'FirebaseAuthManager[Call].startPasswordReset('
          'identifier: $identifier, '
          'isEmailIdentifier: $isEmailIdentifier '
          '----> email: $email)');

      await (await fbAuth).sendPasswordResetEmail(email: email.trim());

      Logs.print(() => 'FirebaseAuthManager[Response].startPasswordReset '
          '---> DONE');

      return FBResponse.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].startPasswordReset ---> $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception].startPasswordReset ---> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<FBResponse<bool>> verifyResetPasswordCode({
    required String identifier,
    required bool isEmailIdentifier,
    required String resetPasswordCode,
  }) async {
    try {
      String email =
          isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
      Logs.print(() => 'FirebaseAuthManager[Call].verifyResetPasswordCode('
          'identifier: $identifier, '
          'isEmailIdentifier: $isEmailIdentifier, '
          'resetPasswordCode: **** '
          '---> email: $email'
          ')');

      var emailRelatedCode = await (await fbAuth).verifyPasswordResetCode(
        resetPasswordCode,
      );

      FBResponse<bool> response;

      if (emailRelatedCode != email) {
        response = FBResponse.failed(
            error: StringSet(
          'The resetting code is wrong',
          'الكود خاطيء',
        ));
      }
      //
      else {
        response = FBResponse.success(data: true);
      }

      Logs.print(() => 'FirebaseAuthManager[Response].verifyResetPasswordCode '
          'response.data: ${response.data}, '
          'response.message: ${response.error}'
          '');

      return response;
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].verifyResetPasswordCode ---> $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception].verifyResetPasswordCode ---> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<FBResponse<bool>> changePasswordAfterResetting(
    String resetPasswordCode,
    String newPassword,
  ) async {
    Logs.print(() => 'FirebaseAuthManager[Call].changePasswordAfterResetting');

    try {
      await (await fbAuth).confirmPasswordReset(
        code: resetPasswordCode,
        newPassword: newPassword,
      );

      Logs.print(() =>
          'FirebaseAuthManager[Call].changePasswordAfterResetting ---> DONE');

      return FBResponse.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].changePasswordAfterResetting ---> $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception].changePasswordAfterResetting ---> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<FBResponse<bool>> changePassword({
    required String identifier,
    required bool isEmailIdentifier,
    required String currentPassword,
    required String newPassword,
  }) async {
    String email =
        isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
    Logs.print(() => 'FirebaseAuthManager[Call].changePassword('
        'identifier: $identifier, '
        'isEmailIdentifier: $isEmailIdentifier, '
        'currentPassword: ******, '
        'newPassword: ******'
        ')');

    var fauth = await fbAuth;

    if (fauth.currentUser == null) {
      await fauth.signInWithEmailAndPassword(
        email: email,
        password: currentPassword,
      );
    }

    try {
      await fauth.currentUser!.updatePassword(newPassword);
      Logs.print(() => 'FirebaseAuthManager[Response].changePassword '
          '---> currentUser.uid: ${fauth.currentUser?.uid} ----> DONE');
      return FBResponse.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.FirebaseAuthException].changePassword ----> $e');

      if (e.code == 'requires-recent-login') {
        try {
          await fauth.signInWithEmailAndPassword(
            email: email,
            password: currentPassword,
          );
          await fauth.currentUser!.updatePassword(newPassword);
          return FBResponse.success(data: true);
        } catch (e) {
          return FBResponse.failed(
              error: StringSet(
            'failed to change password',
            'تعذر تغيير كلمة المرور',
          ));
        }
      }
      //
      else {
        return firebaseAuthExceptionMessage(e);
      }
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager[Response.Exception].changePassword ----> $e');
      return FBResponse.failed(error: StringSet(e.toString()));
    }
  }

  //----------------------------------------------------------------------------

  @override
  FBResponse<T> firebaseAuthExceptionMessage<T>(FirebaseAuthException e) {
    String errEn = e.code.replaceAll('-', ' ');
    String errAr = errEn;

    if (e.code == 'network-request-failed') {
      errEn = "No internet connection";
      errAr = 'لا يوجد اتصال بالإنترنت';
      return FBResponse.failed(
        error: StringSet(errEn, errAr),
        connectionFailed: true,
      );
    }

    if (e.code == 'email-already-in-use') {
      errEn = IFirebaseAuthManager.EMAIL_ALREADY_IN_USE;
      errAr = 'اسم المستخدم مسجل سابقا بالفعل';
      //errAr = 'البريد الإلكتروني مستخدم بالفعل';
    }

    //
    else if (e.code == 'invalid-email') {
      errEn = 'Invalid Email';
      errAr = 'بريد إلكتروني غير صحيح';
    }

    //
    else if (e.code == 'operation-not-allowed') {
      errEn = 'Operation not allowed';
      errAr = 'عملية غير مسموح بها';
    }

    //
    else if (e.code == 'weak-password') {
      errEn = 'Weak password';
      errAr = 'كلمة مرور ضعيفة';
    }

    //
    else if (e.code == 'wrong-password') {
      errEn = 'Wrong password';
      errAr = 'كلمة مرور خاطئة';
    }

    //
    else if (e.code == 'user-disabled') {
      errEn = 'User disabled';
      errAr = 'تم تعطيل الحساب';
    }

    //
    else if (e.code == 'user-not-found') {
      errEn = 'User is not found';
      errAr = 'الحساب غير موجود';
    }

    if (e.message != null) {
      errEn += '\n\n<<${e.message}>>';
      errAr += '\n\n<<${e.message}>>';
    }

    return FBResponse.failed(error: StringSet(errEn, errAr));
  }
}
