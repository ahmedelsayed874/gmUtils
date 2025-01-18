import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';

abstract class ChangePasswordScreenDelegate
    extends IScreenDriverDependantDelegate {}

abstract class ChangePasswordScreenDriverAbs extends IScreenDriver {
  late ChangePasswordScreenDelegate delegate;

  ChangePasswordScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  bool _allowSaveNewPassword = true;

  Future<bool?> saveNewPassword({
    required String currentPassword,
    required String newPassword,
    required String confirmationPassword,
  }) async {
    if (!_allowSaveNewPassword) return null;

    var cred = (await usersDataSource.savedCredentials)!;
    if (cred.value2 != currentPassword) {
      delegate.showMessage(
        message: Res.strings.current_password_doesnt_match_with_your_password,
      );
      return null;
    }

    if (newPassword != confirmationPassword) {
      delegate.showMessage(
        message: Res.strings.confirmationPasswordIsNotMatchWithPassword,
      );
      return null;
    }

    _allowSaveNewPassword = false;
    delegate.showWaitView();

    var accountId = usersDataSource.cachedUserAccount!.id;
    var response = await usersDataSource.changeAccountPassword(
      accountId: accountId,
      newPassword: newPassword,
    );

    _allowSaveNewPassword = true;
    await delegate.hideWaitView();

    if (!response.isSuccess) {
      delegate.showErrorMessage(response.errorMessage);
    }

    return response.isSuccess;
  }
}

class ChangePasswordScreenDriver extends ChangePasswordScreenDriverAbs {
  ChangePasswordScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
