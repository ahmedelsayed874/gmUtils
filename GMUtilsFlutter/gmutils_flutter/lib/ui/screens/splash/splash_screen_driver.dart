import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/utils/launcher.dart';

import '../../../data/data_source/users/users_datasource.dart';
import '../../../data/models/response.dart';
import '../../../data/models/users/auth_user_account.dart';
import '../../../services/configs/app_configs.dart';
import '../../../zgmutils/ui/dialogs/message_dialog.dart';
import '../../../zgmutils/utils/logs.dart';


abstract class SplashScreenDelegate extends IScreenDriverDependantDelegate {
  void gotoLoginScreen();

  void gotoHomeScreen();
}

abstract class SplashScreenDriverAbs extends IScreenDriver {
  late SplashScreenDelegate delegate;

  bool _isSplashTimeElapsed = false;
  bool _isLoginCompleted = false;
  Response<AuthUserAccount>? _authUserAccountResponse;

  SplashScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate) {
    Future.delayed(const Duration(seconds: 4), () {
      _isSplashTimeElapsed = true;
      _startNextScreen();
    });

    //_loadAppConfigs().then((r) {
      _login();
    //});
  }

  Future<void> _loadAppConfigs() async {
    var appConfigs = AppConfigs();

    ///note: same steps repeated in login_driver
    var credentials = await usersDataSource.savedCredentials;

    var cachedAppConfigsData = await appConfigs.cachedAppConfigsData;
    if (cachedAppConfigsData != null) {
      main.serverUrl = cachedAppConfigsData.getServerUrl(
        username: credentials?.value1,
      );
    }

    await appConfigs.fetch();

    main.serverUrl = appConfigs.appConfigs.getServerUrl(
      username: credentials?.value1,
    );

    if (credentials != null) {
      Logs.setLogFileDeadline(
          privateLogFileDeadline: appConfigs.appConfigs.getLogFileDeadline(
        username: credentials.value1,
      ));
    }
  }

  void _login() async {
    var cred = await usersDataSource.savedCredentials;
    if (cred == null) {
      _isLoginCompleted = true;
      _startNextScreen();
    } else {
      _authUserAccountResponse = await usersDataSource.login(
        username: cred.value1,
        password: cred.value2,
        fcm: FCM.instance,
      );

      _isLoginCompleted = true;
      _startNextScreen();
    }
  }

  //-------------------------------------------------

  void _startNextScreen() {
    if (!_isSplashTimeElapsed) return;
    if (!_isLoginCompleted) return;

    _checkAppVersion(() {
      _checkMessages(() {
        _startNextScreen2();
      });
    });
  }

  void _startNextScreen2() {
    if (_authUserAccountResponse == null) {
      delegate.gotoLoginScreen();
    }
    //
    else if (_authUserAccountResponse?.data != null) {
      if (_authUserAccountResponse?.data?.isActive == true) {
        delegate.gotoHomeScreen();
      }
      //
      else {
        delegate.showMessage(
          title: Res.strings.message,
          message: Res.strings.your_account_has_been_blocked,
          actions: [
            MessageDialogActionButton(
              Res.strings.ok,
              action: () {
                delegate.gotoLoginScreen();
              },
            ),
          ],
          allowOuterDismiss: false,
        );
      }
    }
    //
    else {
      var message = _authUserAccountResponse?.errorMessage;
      if (message == null) {
        message = 'Error occurred while getting your data, '
            'check connection and try again.';
        if (_authUserAccountResponse?.httpCode != null) {
          message += ' [code: ${_authUserAccountResponse?.httpCode}]';
        }
      }

      delegate.showMessage(
        title: Res.strings.error,
        message: message,
        actions: [
          MessageDialogActionButton(Res.strings.retry, action: _login),
          MessageDialogActionButton(
            Res.strings.login,
            action: () {
              delegate.gotoLoginScreen();
            },
          ),
        ],
        allowOuterDismiss: false,
      );
    }
  }

  //-------------------------------------------------

  void _checkAppVersion(Function complete) {
    var appConfigs = AppConfigs();
    //await appConfigs.fetch();

    if (appConfigs.needUpdateApp()) {
      // var forceUpdateNow = appConfigs.mustUpdateApp();

      delegate.showMessage(
          title: Res.strings.alert,
          message: Res.strings.a_new_version_has_been_released_please_update,
          actions: [
            MessageDialogActionButton(
              Res.strings.update,
              color: Res.themes.colors.red,
              action: () {
                appConfigs.updateApp();
              },
            ),

            //
            //if (!forceUpdateNow)
            MessageDialogActionButton(
              Res.strings.dismiss,
              action: () {
                //md.allowManualDismiss(true);
              },
            ),
          ],
          allowOuterDismiss: false,
          onDismiss: (s) {
            complete();
          }
      );
    } else {
      complete();
    }
  }

  var _msgCount = 0;

  void _checkMessages(Function complete) async {
    var cred = await usersDataSource.savedCredentials;
    var msgs = AppConfigs.appConfigsData.getMessages(cred?.value1 ?? '');
    _msgCount = msgs.length;

    if (msgs.isNotEmpty) {
      for (var m in msgs) {
        delegate.showMessage(
            title: Res.strings.message,
            message: m.message,
            actions: m.canDismiss ? [
              MessageDialogActionButton(
                Res.strings.ok,
                action: m.action == null ? null : () {
                  Launcher().openUrl(m.action!);
                },
              ),
            ] : [],
            allowOuterDismiss: false,
            onDismiss: (s) {
              _msgCount--;
              if (_msgCount == 0) complete();
            }
        );
      }
    } else {
      complete();
    }
  }
}

class SplashScreenDriver extends SplashScreenDriverAbs {
  SplashScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}
