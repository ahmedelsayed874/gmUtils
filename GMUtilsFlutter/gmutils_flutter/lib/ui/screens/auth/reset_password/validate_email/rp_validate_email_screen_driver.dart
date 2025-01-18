import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/users/user_account_identifier.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';

abstract class RPValidateEmailScreenDelegate
    extends IScreenDriverDependantDelegate {
  void onEmailVerifiedSuccessfully(UserAccountIdentifier identifier);
}

abstract class RPValidateEmailScreenDriverAbs extends IScreenDriver {
  late RPValidateEmailScreenDelegate delegate;

  RPValidateEmailScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  void veryCode({required String email, required String code}) async {
    if (code.isEmpty) {
      delegate.showMessage( message: Res.strings.please_enter_the_correct_code_that_sent_to_ + ' $email',);
      return;
    }

    delegate.showWaitView();

    var response = await usersDataSource.verifyAccountPasswordResetCode(
      emailAddress: email,
      code: code,
    );

    await delegate.hideWaitView();

    if (response.data != null) {
      delegate.onEmailVerifiedSuccessfully(response.data!);
    } else {
      delegate.showErrorMessage(response.errorMessage,
          onRetry: () {
        veryCode(email: email, code: code);
      });
    }
  }
}

class RPValidateEmailScreenDriver extends RPValidateEmailScreenDriverAbs {
  RPValidateEmailScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
