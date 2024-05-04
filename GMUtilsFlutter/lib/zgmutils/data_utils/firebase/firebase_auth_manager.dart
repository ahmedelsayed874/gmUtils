import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:pallora/zgmutils/utils/string_set.dart';

import '../../utils/logs.dart';
import 'firebase_utils.dart';
import 'response.dart';

abstract class IFirebaseAuthManager {
  static String DEFAULT_EMAIL_HOST_NAME = 'myusers.org';
  static const String anonymousUserName = 'anonymous@anonymous.com';
  static const String anonymousPassword = 'pw0fAnonymous123';

  static Future<UserCredential> anonymousLogin() async {
    var auth = await FirebaseAuthManager().fbAuth;
    var userCredential = await auth.signInWithEmailAndPassword(
      email: anonymousUserName,
      password: anonymousPassword,
    );
    return userCredential;
  }

  //----------------------------------------------------------------------------

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

  @override
  Future<Response<bool>> registerByNonEmail({
    required String text,
    required String password,
  }) {
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
      UserCredential userCredential;
      userCredential = await (await fbAuth).createUserWithEmailAndPassword(
        email: email,
        password: password,
      );

      var registered = userCredential.additionalUserInfo?.isNewUser == true;

      return Response.success(data: registered);
    } on FirebaseAuthException catch (e) {
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
    }
  }

  @override
  Future<Response<User>> loginByNonEmail(String text, String password) {
    return loginByEmail(formatNonEmailToEmail(text), password);
  }

  @override
  Future<Response<User>> loginByEmail(String email, String password) async {
    try {
      UserCredential userCredential;
      userCredential = await (await fbAuth).signInWithEmailAndPassword(
        email: email,
        password: password,
      );
      if (userCredential.user != null) {
        return Response.success(data: userCredential.user);
      } else {
        return Response.failed(
          error: StringSet(
            "login failed",
            "تعذر تسجيل الدخول",
          ),
        );
      }
    } on FirebaseAuthException catch (e) {
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
    }
  }

  @override
  Future<void> logout() async {
    return (await fbAuth).signOut();
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
      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
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
      if (emailRelatedCode != email) {
        return Response.failed(
            error: StringSet(
          'The resetting code is wrong',
          'الكود خاطيء',
        ));
      }

      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
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
      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      return firebaseAuthExceptionMessage(e);
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
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
      return Response.success(data: true);
    } on FirebaseAuthException catch (e) {
      if (e.code == 'requires-recent-login') {
        try {
          await fauth.signInWithEmailAndPassword(
            email: email,
            password: currentPassword,
          );
          await fauth.currentUser!.updatePassword(newPassword);
          return Response.success(data: true);
        } catch (e) {
          Logs.print(() => e);
          return Response.failed(
              error: StringSet(
            'failed to change password',
            'تعذر تغيير كلمة المرور',
          ));
        }
      } else {
        return firebaseAuthExceptionMessage(e);
      }
    } catch (e) {
      Logs.print(() => e);
      return Response.failed(error: StringSet(e.toString(), e.toString()));
    }
  }

  //----------------------------------------------------------------------------

  @override
  Response<T> firebaseAuthExceptionMessage<T>(FirebaseAuthException e) {
    Logs.print(() => e);

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
