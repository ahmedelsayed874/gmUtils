import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../dialogs/message_dialog.dart';
import 'drivers_interfaces.dart';
import 'screen_utils.dart';

abstract class BaseState<W extends StatefulWidget> extends State<W>
    implements IScreenDriverDependantDelegate {
  final ScreenUtils _screenUtils = const ScreenUtils();

  String? get defaultWaitViewMessage => null;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
  }

  //-----------------------------------------------------------------

  Future _waitForMount() async {
    int x = 0;
    while (!mounted) {
      await Future.delayed(Duration(milliseconds: 500));
      x++;
      if (x > 6) break;
    }
  }

  //-----------------------------------------------------------------

  @override
  void showWaitView([String? message]) async {
    await _waitForMount();

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
  }) async {
    await _waitForMount();

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
  }) async {
    await _waitForMount();

    _screenUtils.showErrorMessage(
      message,
      title: title ?? defaultRetryMessageTitle,
      onRetry: onRetry,
      onDismiss: onDismiss,
    );
  }

  //-----------------------------------------------------------------

  @override
  void showOptionsDialog<T>({
    required String title,
    required List<T> options,
    required T? selectedOption,
    required void Function(T p1) onOptionSelected,
    required void Function(bool? dissmissedByOk) onDismiss,
    int? maxNumberOfDisplayedItems,
  }) async {
    await _waitForMount();

    _screenUtils.showOptionsDialog(
      title: title,
      options: options,
      selectedOption: selectedOption,
      onOptionSelected: onOptionSelected,
      onDismiss: onDismiss,
    );
  }

  //-----------------------------------------------------------------

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
