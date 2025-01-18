import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../dialogs/message_dialog.dart';
import 'drivers_interfaces.dart';
import 'screen_utils.dart';

abstract class BaseState<W extends StatefulWidget> extends State<W>
    implements IScreenDriverDependantDelegate {
  final ScreenUtils _screenUtils = const ScreenUtils();

  bool _isBuildCalled = false;

  bool get isBuildCalled => _isBuildCalled;

  String? get defaultWaitViewMessage => null;

  @override
  void initState() {
    super.initState();
    // Future.delayed(const Duration(seconds: 1), () => _isBuildCalled = true);
  }

  @override
  void didChangeDependencies() {
    _isBuildCalled = true;
    super.didChangeDependencies();
  }

  @override
  void showWaitView([String? message]) {
    if (!_isBuildCalled) {
      return;
    }

    _screenUtils.showWaitView(message ?? defaultWaitViewMessage);
  }

  @override
  bool get isWaitDialogShown => _screenUtils.isWaitDialogShown;

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
    if (!_isBuildCalled) {
      return;
    }

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
    Function()? onDismiss,
  }) {
    if (!_isBuildCalled) {
      return;
    }

    _screenUtils.showErrorMessage(
      message,
      title: title ?? defaultRetryMessageTitle,
      onRetry: onRetry,
      onDismiss: onDismiss,
    );
  }

  @override
  void updateView() {
    try {
      setState(() {});
    } catch (e) {}
  }

  void updateViewLater(int delay) {
    Future.delayed(Duration(milliseconds: delay), updateView);
  }

  @override
  void goBack([result]) {
    try {
      App.navBack(result);
    } catch (e) {
      App.navBack();
    }
  }
}
