import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/services/configs/app_configs.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';

abstract class LoginScreenDelegate extends IScreenDriverDependantDelegate {
  void onLoginCompleted();
}

abstract class LoginScreenDriverAbs extends IScreenDriver {
  late LoginScreenDelegate delegate;

  LoginScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate);

  void login({required String username, required String password}) async {
    if (username.isEmpty) {
      delegate.showMessage(message: Res.strings.please_enter_valid_username);
      return;
    }

    if (password.length < 6) {
      delegate.showMessage(message: Res.strings.enter_password_at_least);
      return;
    }

    delegate.showWaitView();

    // await checkServerUrl(username: username);

    var response = await usersDataSource.login(
      username: username,
      password: password,
      //fcm: FCM.instance,
    );

    await delegate.hideWaitView();

    if (response.data != null) {
      delegate.onLoginCompleted();
    } else {
      delegate.showErrorMessage(response.errorMessage, onRetry: () {
        login(username: username, password: password);
      });
    }
  }

  ///note: same steps repeated in main
  Future<void> checkServerUrl({required String username}) async {
    var appConfigs = AppConfigs();

    var cachedAppConfigsData = await appConfigs.cachedAppConfigsData;
    if (cachedAppConfigsData != null) {
      main.serverUrl = cachedAppConfigsData.getServerUrl(username: username);
    }

    await appConfigs.fetch();

    main.serverUrl = appConfigs.appConfigs.getServerUrl(
      username: username,
    );

    Logs.setLogFileDeadline(
      privateLogFileDeadline: appConfigs.appConfigs.getLogFileDeadline(
        username: username,
      ),
    );
  }
}

class LoginScreenDriver extends LoginScreenDriverAbs {
  LoginScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
