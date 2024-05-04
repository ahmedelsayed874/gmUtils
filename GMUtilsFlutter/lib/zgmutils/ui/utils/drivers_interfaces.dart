import 'package:flutter/foundation.dart';

import '../../utils/pairs.dart';


abstract class IScreenDriverDependantDelegate {
  void showWaitView();

  void updateWaitViewMessage(String msg);

  Future<void> hideWaitView({bool forceHide = true});

  void showMessage(
    String? title, {
    required String message,
    List<Pair<String, VoidCallback?>>? actions,
        bool allowOuterDismiss = true,
    VoidCallback? onDismiss,
  });

  void showErrorMessage(String message, {String? title, Function()? onRetry,});

  void updateView();

  void goBack([result]);
}

abstract class IScreenDriver {
  final IScreenDriverDependantDelegate baseDelegate;

  IScreenDriver(this.baseDelegate);

}
