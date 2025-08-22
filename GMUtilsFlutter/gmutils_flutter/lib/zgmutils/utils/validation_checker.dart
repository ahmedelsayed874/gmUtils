import 'package:flutter/material.dart';

import 'result.dart';
import 'string_set.dart';

class ValidationChecker {
  String _errorsEn = '';
  String _errorsAr = '';

  StringSet get errors => StringSet(_errorsEn, _errorsAr);

  void _appendToErrors(StringSet error) {
    if (_errorsEn.isNotEmpty) {
      _errorsEn += '\n';
      _errorsAr += '\n';
    }

    _errorsEn += '• ${error.en}';
    _errorsAr += '• ${error.ar}';
  }

  Result<bool> check({
    required List<Validator> validators,
  }) {
    for (Validator validator in validators) {
      var result = validator.validate();
      if (result.result == false) {
        if (result.message != null) _appendToErrors(result.message!);
      }
    }

    if (_errorsEn.isEmpty) {
      return Result(true);
    } else {
      return Result(false, message: StringSet(_errorsEn, _errorsAr));
    }
  }
}

abstract class Validator {
  final String _text;

  Validator(this._text);

  String get text => _text;

  ///check input and return error if exist
  Result<bool> validate();
}

class UserNameValidator extends Validator {
  String userName;

  UserNameValidator({required this.userName}) : super(userName) {
    //userName = Text Utils().removeExtraSpaces(userName);
  }

  @override
  Result<bool> validate() {
    if (userName.contains("  ")) {
      StringSet err = StringSet(
        'Enter a valid user name - remove extra spaces',
        'أدخل إسم مستخدم صحيح - أزل المسافات الزائدة',
      );
      return Result(false, message: err);
    }

    if (userName.length < 3) {
      return Result(
        false,
        message: StringSet('Enter a valid user name', 'أدخل إسم مستخدم صحيح'),
      );
    } else {
      return Result(true);
    }
  }
}

class NameValidator extends Validator {
  String targetName;
  String? title;

  NameValidator({required this.targetName, this.title}) : super(targetName) {
    //targetName = Text Utils().removeExtraSpaces(targetName);
  }

  @override
  Result<bool> validate() {
    String errEn = '';
    String errAr = '';

    if (title != null) {
      errEn += '$title: ';
      errAr += '$title: ';
    }

    if (targetName.contains("  ")) {
      errEn += 'Enter a valid name - remove extra spaces';
      errAr += 'أدخل إسم صحيح - أزل المسافات الزائدة';
      return Result(
        false,
        message: StringSet(errEn, errAr),
      );
    }

    if (targetName.length < 2) {
      errEn += 'Enter a valid name.';
      errAr += 'أدخل إسم صحيح.';
      return Result(false, message: StringSet(errEn, errAr));
    } else {
      final charset1min = 'a'.codeUnitAt(0);
      final charset1max = 'z'.codeUnitAt(0);

      final charset2min = 'A'.codeUnitAt(0);
      final charset2max = 'Z'.codeUnitAt(0);

      final charset3min = 'ء'.codeUnitAt(0);
      final charset3max = 'ي'.codeUnitAt(0);

      final spaceCode = ' '.codeUnitAt(0);

      var valid = true;
      for (var char in targetName.characters) {
        var charCode = char.codeUnitAt(0);
        valid = (charCode >= charset1min && charCode <= charset1max) ||
            (charCode >= charset2min && charCode <= charset2max) ||
            (charCode >= charset3min && charCode <= charset3max) ||
            charCode == spaceCode;
        if (!valid) continue;
      }

      if (!valid) {
        errEn += 'Enter a valid name.';
        errAr += 'أدخل إسم صحيح.';
        return Result(false, message: StringSet(errEn, errAr));
      }

      return Result(true);
    }
  }
}

class PasswordValidator extends Validator {
  String password;
  int minimumPasswordLength = 6;

  PasswordValidator({required this.password, this.minimumPasswordLength = 6}) : super(password) {
    //password = Text Utils().removeExtraSpaces(password);
  }

  @override
  Result<bool> validate() {
    if (password.trim().length < minimumPasswordLength) {
      return Result(
        false,
        message: StringSet('Enter a valid password ($minimumPasswordLength-characters at least)',
            'أدخل كلمة مرور صالحة ($minimumPasswordLength-أحرف على الأقل)'),
      );
    } else {
      return Result(true);
    }
  }
}

class EmailValidator extends Validator {
  String email;

  EmailValidator({required this.email}) : super(email);

  @override
  Result<bool> validate() {
    var regex = RegExp(
        r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+");
    if (!regex.hasMatch(email)) {
      return Result(
        false,
        message: StringSet(
            'Enter a valid Email address', 'أدخل عنوان بريد إلكتروني صحيح'),
      );
    } else {
      return Result(true);
    }
  }
}

class MobileValidator extends Validator {
  String mobile;

  MobileValidator({required this.mobile}) : super(mobile);

  @override
  Result<bool> validate() {
    var m = mobile.trim();

    String accepted = '+0123456789';
    bool validNum = true;
    if (m.length > 10) {
      for (var n in m.characters) {
        if (!accepted.contains(n)) {
          validNum = false;
          break;
        }
      }
    } else {
      validNum = false;
    }

    if (validNum) {
      return Result(true);
    } else {
      return Result(
        false,
        message:
            StringSet('Enter a valid mobile number', 'أدخل رقم موبايل صحيح'),
      );
    }
  }
}
