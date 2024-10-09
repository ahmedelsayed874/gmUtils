import 'package:flutter/material.dart';

import '../dialogs/message_dialog.dart';
import 'drivers_interfaces.dart';
import 'screen_utils.dart';

abstract class BaseStatelessWidget extends StatelessWidget
    implements IScreenDriverDependantDelegate {
  final ScreenUtils _screenUtils = const ScreenUtils();

  const BaseStatelessWidget({super.key});

  String? get defaultWaitViewMessage => null;

  @override
  void showWaitView([String? message]) {
    _screenUtils.showWaitView(message ?? defaultWaitViewMessage);
  }

  @override
  void updateWaitViewMessage(String msg) {
    _screenUtils.updateWaitViewMessage(msg);
  }

  @override
  Future<void> hideWaitView({bool forceHide = true}) async {
    return _screenUtils.hideWaitView(forceHide: forceHide);
  }

  //-----------------------------------------------------------------

  @override
  void showQuickMessage(String message) {
    _screenUtils.showQuickMessage(message);
  }

  //-----------------------------------------------------------------

  String? get defaultMessageTitle => null;

  @override
  void showMessage({
    String? title,
    required String message,
    List<MessageDialogActionButton>? actions,
    bool allowOuterDismiss = true,
    Function(String?)? onDismiss,
  }) {
    _screenUtils.showMessage(
      message,
      title: title ?? defaultMessageTitle,
      actions: actions,
      allowOuterDismiss: allowOuterDismiss,
      onDismiss: onDismiss,
    );
  }

  String? get defaultRetryMessageTitle => null;

  @override
  void showErrorMessage(
    String message, {
    String? title,
    Function()? onRetry,
  }) {
    _screenUtils.showErrorMessage(
      message,
      title: title ?? defaultRetryMessageTitle,
      onRetry: onRetry,
    );
  }

  @override
  void updateView() {}
}
