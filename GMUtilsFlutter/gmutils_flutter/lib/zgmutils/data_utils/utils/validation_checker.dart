import 'package:flutter/material.dart';

import 'result.dart';
import '../../utils/collections/string_set.dart';

class ValidationChecker {
  String _errorsEn = '';
  String _errorsAr = '';

  StringSet get errors => StringSet(_errorsEn, _errorsAr);

  void _appendToErrors(
    StringSet error, {
    String linePrefix = '•',
    String lineSuffix = '',
  }) {
    if (_errorsEn.isNotEmpty) {
      _errorsEn += '\n';
      _errorsAr += '\n';
    }

    _errorsEn += '$linePrefix${linePrefix.isEmpty ? '' : ' '}'
        '${error.en}$lineSuffix';

    _errorsAr += '$linePrefix${linePrefix.isEmpty ? '' : ' '}'
        '${error.ar}$lineSuffix';
  }

  Result<bool> check({
    required List<Validator> validators,
    String titlePrefix = '',
    String titleSuffix = ':',
    String linePrefix = ' ',
  }) {
    for (Validator validator in validators) {
      var result = validator.validate();
      if (result.result == false) {
        if (validators.length > 1) {
          _appendToErrors(
            validator.fieldName,
            linePrefix: titlePrefix,
            lineSuffix: titleSuffix,
          );
        }

        _appendToErrors(
          StringSet(
            '${result.message?.en}${validators.length > 1 ? '\n' : ''}',
            '${result.message?.ar}${validators.length > 1 ? '\n' : ''}',
          ),
          linePrefix: linePrefix,
        );
      }
    }

    if (_errorsEn.isEmpty) {
      return Result(true);
    }
    //
    else {
      return Result(false,
          message: StringSet(
            _errorsEn,
            _errorsAr,
          ));
    }
  }
}

abstract class Validator {
  StringSet get fieldName;

  final String _text;

  Validator(this._text);

  String get text => _text;

  ///check input and return error if exist
  Result<bool> validate();
}

class UserNameValidator extends Validator {
  final StringSet? _fieldName;
  final String userName;

  UserNameValidator({
    required this.userName,
    StringSet? fieldName,
  })  : _fieldName = fieldName,
        super(userName);

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

  @override
  StringSet get fieldName =>
      _fieldName ??
      StringSet(
        'User Name',
        'اسم المستخدم',
      );
}

class NameValidator extends Validator {
  final StringSet? _fieldName;
  final String targetName;

  NameValidator({
    required this.targetName,
    StringSet? fieldName,
  })  : _fieldName = fieldName,
        super(targetName);

  @override
  Result<bool> validate() {
    String errEn = '';
    String errAr = '';

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
    }
    //
    else {
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

  @override
  StringSet get fieldName => _fieldName ?? StringSet('Name', 'الإسم');
}

class PasswordValidator extends Validator {
  final StringSet? _fieldName;

  final String password;
  final int _minimumPasswordLength;
  final bool shouldBeComplex;

  PasswordValidator({
    required this.password,
    int minimumPasswordLength = 6,
    this.shouldBeComplex = false,
    StringSet? fieldName,
  })  : _fieldName = fieldName,
        _minimumPasswordLength = minimumPasswordLength,
        super(password);

  @override
  Result<bool> validate() {
    if (shouldBeComplex) {
      return isPasswordStrong(password)
          ? Result(true)
          : Result(
              false,
              message: StringSet(
                //en
                'Enter a stronger password which have to follow those rules:\n'
                    '   - At least $minimumPasswordLength characters long.\n'
                    '   - Contains at least one uppercase letter.\n'
                    '   - Contains at least one lowercase letter.\n'
                    '   - Contains at least one digit.\n'
                    '   - Contains at least one special character.',

                //ar
                'ادخل كلمة مرور قوية والتي يجب ان تتبع القواعد التالية:\n'
                    '- تحتوي على $minimumPasswordLength حروف على الأقل.\n'
                    '- تحتوى على حرف كبير على الأقل.\n'
                    '- تحتوي على حرف صغير على الأقل.\n'
                    '- تحتوي على رقم واحد على الأقل.\n'
                    'تحتوي على رمز خاص واحد على الأقل.',
              ),
            );
    }
    //
    else {
      if (password.trim().length < minimumPasswordLength) {
        return Result(
          false,
          message: StringSet(
            //en
            'Enter a valid password ($minimumPasswordLength-characters at least)',

            //ar
            'أدخل كلمة مرور صالحة ($minimumPasswordLength-أحرف على الأقل)',
          ),
        );
      } else {
        return Result(true);
      }
    }
  }

  int get minimumPasswordLength {
    int len = _minimumPasswordLength;
    if (shouldBeComplex && len < 8) len = 8;
    return len;
  }

  bool isPasswordStrong(String password) {
    int len = minimumPasswordLength;

    // Define the complex password pattern:
    // - At least 8 characters long
    // - Contains at least one uppercase letter (?=.*[A-Z])
    // - Contains at least one lowercase letter (?=.*[a-z])
    // - Contains at least one digit (?=.*\d)
    // - Contains at least one special character (?=.*[!@#\$%^&*()_+=-])

    //final String pattern = r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#\$%^&*()_+=-]).{8,}$';
    final String pattern =
        r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#\$%^&*()_+=-]).{' +
            len.toString() +
            r',}$';
    final RegExp regExp = RegExp(pattern);
    return regExp.hasMatch(password);
  }

  @override
  StringSet get fieldName => _fieldName ?? StringSet('Password', 'كلمة المرور');
}

class EmailValidator extends Validator {
  final StringSet? _fieldName;

  final String email;

  EmailValidator({
    required this.email,
    StringSet? fieldName,
  })  : _fieldName = fieldName,
        super(email);

  @override
  Result<bool> validate() {
    //^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
    var regex = RegExp(
        r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+");
    if (!regex.hasMatch(email)) {
      return Result(
        false,
        message: StringSet(
          'Enter a valid Email address',
          'أدخل عنوان بريد إلكتروني صحيح',
        ),
      );
    } else {
      return Result(true);
    }
  }

  @override
  StringSet get fieldName =>
      _fieldName ?? StringSet('Email', 'البريد الإلكتروني');
}

class MobileValidator extends Validator {
  final StringSet? _fieldName;

  final String mobile;
  final int minLength;
  final bool mustIncludeCountryCode;

  MobileValidator({
    required this.mobile,
    this.minLength = 10,
    this.mustIncludeCountryCode = false,
    StringSet? fieldName,
  })  : _fieldName = fieldName,
        super(mobile);

  /*@override
  Result<bool> validate() {
    var m = mobile.trim();

    String accepted = '+0123456789';
    bool validNum = true;
    if (m.length > minLength) {
      for (var n in m.characters) {
        if (!accepted.contains(n)) {
          validNum = false;
          break;
        }
      }
    }
    //
    else {
      validNum = false;
    }

    if (validNum) {
      return Result(true);
    } else {
      return Result(
        false,
        message: StringSet(
          'Enter a valid mobile number',
          'أدخل رقم موبايل صحيح',
        ),
      );
    }
  }*/

  @override
  Result<bool> validate() {
    var len = minLength + (mustIncludeCountryCode ? 1 : 0);
    //^(\+|00)\d{8,}$
    var pattern = '^${mustIncludeCountryCode ? '(\\+|00)' : ''}\\d{$len,}\$';
    var regex = RegExp(pattern);

    if (!regex.hasMatch(mobile)) {
      return Result(
        false,
        message: StringSet(
          'Enter a valid mobile number',
          'أدخل رقم موبايل صحيح',
        ),
      );
    } else {
      return Result(true);
    }
  }

  @override
  StringSet get fieldName => _fieldName ?? StringSet('Mobile', 'رقم الهاتف');
}
