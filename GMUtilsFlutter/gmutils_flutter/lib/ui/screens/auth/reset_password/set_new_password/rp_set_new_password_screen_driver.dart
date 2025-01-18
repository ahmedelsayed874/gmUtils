import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/users/user_account_identifier.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';

abstract class RPSetNewPasswordScreenDelegate
    extends IScreenDriverDependantDelegate {
  void onNewPasswordSavedSuccessfully();
}

abstract class RPSetNewPasswordScreenDriverAbs extends IScreenDriver {
  late RPSetNewPasswordScreenDelegate delegate;

  RPSetNewPasswordScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  void saveNewPassword({
    required UserAccountIdentifier identifier,
    required String newPassword,
    required String confirmationPassword,
  }) async {
    if (newPassword.length < 6) {
      delegate.showMessage( message: Res.strings.enter_password_at_least);
      return;
    }
    if (newPassword != confirmationPassword) {
      delegate.showMessage(
          message: Res.strings.the_two_inserted_passwords_are_not_matched,
      );
      return;
    }

    delegate.showWaitView();

    var response = await usersDataSource.resetAccountPassword(
      accountId: identifier.accountId,
      password: newPassword,
    );

    await delegate.hideWaitView();

    if (response.isSuccess) {
      delegate.showMessage(title: Res.strings.reset_password,
          message: Res.strings.your_password_has_been_reset_successfully,
        onDismiss: (s) {
          delegate.onNewPasswordSavedSuccessfully();
        },
      );
    } else {
      delegate.showErrorMessage(response.errorMessage,
          onRetry: () {
        saveNewPassword(
          identifier: identifier,
          newPassword: newPassword,
          confirmationPassword: confirmationPassword,
        );
      });
    }
  }
}

class RPSetNewPasswordScreenDriver extends RPSetNewPasswordScreenDriverAbs {
  RPSetNewPasswordScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
