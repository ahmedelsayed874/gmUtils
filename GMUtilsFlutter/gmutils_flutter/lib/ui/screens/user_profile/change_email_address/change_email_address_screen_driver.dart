import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/utils/validation_checker.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';


abstract class ChangeEmailAddressScreenDelegate
    extends IScreenDriverDependantDelegate {}

abstract class ChangeEmailAddressScreenDriverAbs extends IScreenDriver {
  late ChangeEmailAddressScreenDelegate delegate;

  ChangeEmailAddressScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  //---------------------------------------------------------------------------

  bool _isVerificationCodeSent = false;

  bool get isVerificationCodeSent => _isVerificationCodeSent;

  String? _emailAddressUnderVerify;

  allowChangeEmailAddress() {
    _isVerificationCodeSent = false;
    _emailAddressUnderVerify = null;
    delegate.updateView();
  }

  //---------------------------------------------------------------------------

  bool _allowVerifyEmailAddress = true;

  void verifyEmailAddress({required String emailAddress}) async {
    if (!_allowVerifyEmailAddress) return;

    var r = ValidationChecker()
        .check(validators: [EmailValidator(email: emailAddress)]);
    if (r.message != null) {
      delegate.showMessage(message: r.message!.get(App.isEnglish));
      return;
    }

    _allowVerifyEmailAddress = false;
    delegate.showWaitView();

    var accountId = usersDataSource.cachedUserAccount!.id;
    var response = await usersDataSource.verifyEmailAddress(
      accountId: accountId,
      emailAddress: emailAddress,
    );

    _allowVerifyEmailAddress = true;
    await delegate.hideWaitView();

    if (response.isSuccess) {
      _isVerificationCodeSent = true;
      _emailAddressUnderVerify = emailAddress;
      delegate.updateView();
    } else {
      delegate.showErrorMessage(response.errorMessage, onRetry: () {
        verifyEmailAddress(emailAddress: emailAddress);
      });
    }
  }

  //---------------------------------------------------------------------------

  bool _allowConfirmEmailAddress = true;

  Future<bool?> confirmEmailAddress({required String code}) async {
    if (!_allowConfirmEmailAddress) return null;

    if (_emailAddressUnderVerify == null) {
      delegate.showMessage(
          message: 'You have to verify the Email address first');
      return null;
    }

    if (code.isEmpty) {
      delegate.showMessage(
          message: '${Res.strings.enter} ${Res.strings.verification_code}');
      return null;
    }

    _allowConfirmEmailAddress = false;
    delegate.showWaitView();

    var accountId = usersDataSource.cachedUserAccount!.id;
    var response = await usersDataSource.confirmEmailAddress(
      accountId: accountId,
      emailAddress: _emailAddressUnderVerify!,
      otp: code,
    );

    _allowConfirmEmailAddress = true;
    await delegate.hideWaitView();

    if (!response.isSuccess) {
      delegate.showErrorMessage(response.errorMessage);
    }

    return response.isSuccess;
  }
}

class ChangeEmailAddressScreenDriver extends ChangeEmailAddressScreenDriverAbs {
  ChangeEmailAddressScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
