import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/validation_checker.dart';

abstract class RPInquiryEmailScreenDelegate
    extends IScreenDriverDependantDelegate {
  void onVerificationCodeSentToEmail(String email);
}

abstract class RPInquiryEmailScreenDriverAbs extends IScreenDriver {
  late RPInquiryEmailScreenDelegate delegate;

  RPInquiryEmailScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  void startEmailVerification({required String email}) async {
    var r = ValidationChecker().check(validators: [EmailValidator(email: email)]);
    if (r.result == false) {
      delegate.showMessage( message: r.message?.get(App.isEnglish) ?? 'Enter a valid email address, example: username@domain.com');
      return;
    }

    delegate.showWaitView();

    var response = await usersDataSource.sendAccountPasswordResetCode(
      emailAddress: email,
    );

    await delegate.hideWaitView();

    if (response.isSuccess) {
      delegate.onVerificationCodeSentToEmail(email);
    } else {
      delegate.showErrorMessage(response.errorMessage,
          onRetry: () {
        startEmailVerification(email: email);
      });
    }
  }
}

class RPInquiryEmailScreenDriver extends RPInquiryEmailScreenDriverAbs {
  RPInquiryEmailScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
