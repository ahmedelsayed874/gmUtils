import 'package:flutter/foundation.dart';

import '../dialogs/message_dialog.dart';

abstract class IScreenDriverDependantDelegate {
  void showWaitView([String? message]);

  void updateWaitViewMessage(String msg);

  Future<void> hideWaitView({bool forceHide = true});

  void showQuickMessage(String message);

  void showMessage({
    String? title,
    required String message,
    List<MessageDialogActionButton>? actions,
    bool allowOuterDismiss = true,
    Function(String?)? onDismiss,
  });

  void showErrorMessage(
    String message, {
    String? title,
    Function()? onRetry,
  });

  void updateView();

  void goBack([result]);
}

abstract class IScreenDriver {
  final IScreenDriverDependantDelegate baseDelegate;

  IScreenDriver(this.baseDelegate);
}
