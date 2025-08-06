import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';

import '../../../zgmutils/utils/result.dart';
import '../../../zgmutils/utils/text_utils.dart';
import '../../utils/logs.dart';
import '../../utils/string_set.dart';
import 'firebase_utils.dart';
import 'response.dart';

abstract class IFirebaseAuthManager {
  Future<Response<bool>> registerByNonEmail({
    required String text,
    required String password,
  });

  Future<Response<bool>> registerByEmail({
    required String email,
    required String password,
  });

  Future<Response<User>> loginByNonEmail(String text, String password);

  Future<Response<User>> loginByEmail(String email, String password);

  Future<void> logout();

  //----------------------------------------------------------------------------

  String formatNonEmailToEmail(String text) {
    text = FirebaseUtils.refinePhoneNumber(text);
    text = FirebaseUtils.refineKeyName(text);
    return 'un_$text$DEFAULT_EMAIL_HOST_NAME';
  }

  static String get DEFAULT_EMAIL_HOST_NAME => 'myusers.org';

  //----------------------------------------------------------------------------

  Future<Response<bool>> startPasswordReset({
    required String identifier,
    required bool isEmailIdentifier,
  });

  Future<Response<bool>> verifyResetPasswordCode({
    required String identifier,
    required bool isEmailIdentifier,
    required String resetPasswordCode,
  });

  Future<Response<bool>> changePasswordAfterResetting(
    String resetPasswordCode,
    String newPassword,
  );

  //----------------------------------------------------------------------------

  Future<Response<bool>> changePassword({
    required String identifier,
    required bool isEmailIdentifier,
    required String currentPassword,
    required String newPassword,
  });

  //----------------------------------------------------------------------------

  static const String EMAIL_ALREADY_IN_USE = 'User identifier already in use';

  Response<T> firebaseAuthExceptionMessage<T>(FirebaseAuthException e);
}

class FirebaseAuthManager extends IFirebaseAuthManager {
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

  Future<Result<UserCredential>> _userCredential({
    required Future<UserCredential> Function() fbMethod,
  }) async {
    try {
      UserCredential userCredential;

      userCredential = await Future.sync(() async {
        return fbMethod();
      }).timeout(Duration(seconds: 10));

      return Result(userCredential);
    } on TimeoutException catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager._userCredential ---> Exception: $e');
      return Result(null,
          message: StringSet(
            'Request timeout. please check your connection.',
            'انتهى الوقت المتوقع، يرجى التأكد من الاتصال بالانترنت.',
          ));
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager._userCredential ---> FirebaseAuthException:: $e');
      var r = firebaseAuthExceptionMessage(e);
      return Result(null, message: r.error);
    } catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager._userCredential ---> Exception: $e');
      return Result(null, message: StringSet(e.toString()));
    }
  }

  @override
  Future<Response<bool>> registerByNonEmail({
    required String text,
    required String password,
  }) async {
    Logs.print(() => 'FirebaseAuthManager.registerByNonEmail('
        'text: $text, '
        'password: ********'
        ')');

    return registerByEmail(
      email: formatNonEmailToEmail(text),
      password: password,
    );
  }

  @override
  Future<Response<bool>> registerByEmail({
    required String email,
    required String password,
  }) async {
    try {
      var res = await _userCredential(fbMethod: () async {
        return await (await fbAuth).createUserWithEmailAndPassword(
          email: email,
          password: password,
        );
      });

      if (res.result == null) {
        return Response.failed(error: res.message);
      }

      UserCredential userCredential = res.result!;

      /*userCredential = await (await fbAuth).createUserWithEmailAndPassword(
        email: email,
        password: password,
      );*/

      var registered = userCredential.additionalUserInfo?.isNewUser == true;

      Logs.print(() => 'FirebaseAuthManager.registerByEmail('
          'email: $email, '
          'password: ********'
          ') ----> registered: $registered');

      return Response.success(data: registered);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.registerByEmail ---> FirebaseAuthException: $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager.registerByEmail ---> Exception: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<Response<User>> loginByNonEmail(String text, String password) {
    Logs.print(() => 'FirebaseAuthManager.loginByNonEmail('
        'text: $text, '
        'password: ********');

    return loginByEmail(formatNonEmailToEmail(text), password);
  }

  @override
  Future<Response<User>> loginByEmail(String email, String password) async {
    try {
      var res = await _userCredential(fbMethod: () async {
        return await (await fbAuth).signInWithEmailAndPassword(
          email: email,
          password: password,
        );
      });

      if (res.result == null) {
        return Response.failed(error: res.message);
      }

      UserCredential userCredential = res.result!;

      /*userCredential = await (await fbAuth).signInWithEmailAndPassword(
        email: email,
        password: password,
      );*/

      Response<User> response;

      if (userCredential.user != null) {
        response = Response.success(data: userCredential.user);
      }
      //
      else {
        response = Response.failed(
          error: StringSet(
            "login failed",
            "تعذر تسجيل الدخول",
          ),
        );
      }

      Logs.print(() => 'FirebaseAuthManager.loginByEmail('
          'email: $email, '
          'password: ********) ---->'
          'response.data: ${TextUtils().trimEnd('${response.data}')}, '
          'response.message: ${response.error}');

      return response;
    } on TimeoutException catch (e) {
      Logs.print(() => 'FirebaseAuthManager.loginByEmail ---> Exception: $e');
      return Response.failed(
          error: StringSet('Request timeout. please check your connection.',
              'انتهى الوقت المتوقع، يرجى التأكد من الاتصال بالانترنت.'));
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.loginByEmail ---> FirebaseAuthException:: $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => 'FirebaseAuthManager.loginByEmail ---> Exception: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<void> logout() async {
    try {
      await (await fbAuth).signOut();
      Logs.print(() => 'FirebaseAuthManager.logout ---> DONE');
    } catch (e) {
      Logs.print(() => 'FirebaseAuthManager.logout ---> Exception:: $e');
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<bool>> startPasswordReset({
    required String identifier,
    required bool isEmailIdentifier,
  }) async {
    try {
      String email =
          isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
      await (await fbAuth).sendPasswordResetEmail(email: email.trim());

      Logs.print(() => 'FirebaseAuthManager.startPasswordReset('
          'identifier: $identifier, '
          'isEmailIdentifier: $isEmailIdentifier'
          ') ---> email: $email ---> DONE');

      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.startPasswordReset ---> FirebaseAuthException:: $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(
          () => 'FirebaseAuthManager.startPasswordReset ---> Exception:: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<Response<bool>> verifyResetPasswordCode({
    required String identifier,
    required bool isEmailIdentifier,
    required String resetPasswordCode,
  }) async {
    try {
      String email =
          isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
      var emailRelatedCode = await (await fbAuth).verifyPasswordResetCode(
        resetPasswordCode,
      );

      Response<bool> response;

      if (emailRelatedCode != email) {
        response = Response.failed(
            error: StringSet(
          'The resetting code is wrong',
          'الكود خاطيء',
        ));
      }
      //
      else {
        response = Response.success(data: true);
      }

      Logs.print(() => 'FirebaseAuthManager.verifyResetPasswordCode('
          'identifier: $identifier, '
          'isEmailIdentifier: $isEmailIdentifier, '
          'resetPasswordCode: ****'
          ') ---> email: $email ---> '
          'response.data: ${response.data}, '
          'response.message: ${response.error}'
          '');

      return response;
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.verifyResetPasswordCode ---> FirebaseAuthException:: $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.verifyResetPasswordCode ---> Exception:: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  @override
  Future<Response<bool>> changePasswordAfterResetting(
    String resetPasswordCode,
    String newPassword,
  ) async {
    try {
      await (await fbAuth).confirmPasswordReset(
        code: resetPasswordCode,
        newPassword: newPassword,
      );

      Logs.print(
          () => 'FirebaseAuthManager.changePasswordAfterResetting ---> DONE');

      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.changePasswordAfterResetting ---> FirebaseAuthException:: $e');
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.changePasswordAfterResetting ---> Exception:: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Future<Response<bool>> changePassword({
    required String identifier,
    required bool isEmailIdentifier,
    required String currentPassword,
    required String newPassword,
  }) async {
    String email =
        isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);

    var fauth = await fbAuth;

    if (fauth.currentUser == null) {
      await fauth.signInWithEmailAndPassword(
        email: email,
        password: currentPassword,
      );
    }

    try {
      await fauth.currentUser!.updatePassword(newPassword);
      Logs.print(() => 'FirebaseAuthManager.changePassword('
          'identifier: $identifier, '
          'isEmailIdentifier: $isEmailIdentifier, '
          'currentPassword: ******, '
          'newPassword: ******'
          ') ---> currentUser.uid: ${fauth.currentUser?.uid} ----> DONE');
      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      Logs.print(() =>
          'FirebaseAuthManager.changePassword ----> FirebaseAuthException:: $e');

      if (e.code == 'requires-recent-login') {
        try {
          await fauth.signInWithEmailAndPassword(
            email: email,
            password: currentPassword,
          );
          await fauth.currentUser!.updatePassword(newPassword);
          return Response.success(data: true);
        } catch (e) {
          return Response.failed(
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
      Logs.print(
          () => 'FirebaseAuthManager.changePassword ----> Exception:: $e');
      return Response.failed(error: StringSet(e.toString()));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Response<T> firebaseAuthExceptionMessage<T>(FirebaseAuthException e) {
    String errEn = e.code.replaceAll('-', ' ');
    String errAr = errEn;

    if (e.code == 'network-request-failed') {
      errEn = "No internet connection";
      errAr = 'لا يوجد اتصال بالإنترنت';
      return Response.failed(
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

    return Response.failed(error: StringSet(errEn, errAr));
  }
}
