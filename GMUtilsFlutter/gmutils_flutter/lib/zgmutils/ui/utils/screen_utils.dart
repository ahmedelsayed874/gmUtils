import 'package:gmutils_flutter/ui/dialogs/wait_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../dialogs/message_dialog.dart';

Map<int, WaitDialog> _waitDialog = {};
Map<int, int> _waitDialogShowCount = {};
Map<int, int> _waitDialogShowTime = {};

class ScreenUtils {
  const ScreenUtils();

  void showWaitView(String? message) {
    message ??= App.isEnglish ? 'Please wait...' : 'يرجى الإنتظار...';

    if (_waitDialogShowCount[hashCode] == null) {
      _waitDialogShowCount[hashCode] = 0;
    }

    _waitDialogShowCount[hashCode] = _waitDialogShowCount[hashCode]! + 1;

    if (_waitDialogShowCount[hashCode] == 1) {
      _waitDialogShowTime[hashCode] = DateTime.now().millisecondsSinceEpoch;
      _waitDialog[hashCode] =
          WaitDialog.create.setMessage(message).show(() => App.context);
    }
  }

  bool get isWaitDialogShown => (_waitDialogShowCount[hashCode] ?? 0) > 0;

  void updateWaitViewMessage(String msg) {
    _waitDialog[hashCode]?.setMessage(msg);
  }

  Future<void> hideWaitView({bool forceHide = true}) async {
    if (_waitDialogShowCount[hashCode] == null) return;

    if (forceHide) {
      _waitDialogShowCount[hashCode] = 0;
    } else {
      _waitDialogShowCount[hashCode] = _waitDialogShowCount[hashCode]! - 1;
    }

    if (_waitDialogShowCount[hashCode] == 0) {
      dismiss() {
        _waitDialogShowCount.remove(hashCode);
        _waitDialogShowTime.remove(hashCode);
        _waitDialog[hashCode]?.dismiss();
        _waitDialog.remove(hashCode);
      }

      var diff = DateTime.now().millisecondsSinceEpoch -
          (_waitDialogShowTime[hashCode] ?? 0);
      if (diff < 1000) {
        return Future.delayed(Duration(milliseconds: 1000 - diff), () {
          dismiss();
        });
      } else {
        dismiss();
      }
    }
  }

  ///root widget must be MyRootWidget to let this method works
  void showQuickMessage(
    String message, {
    SnackBarAction? action,
    Color? backgroundColor,
  }) {
    try {
      MyRootWidget.showSnackBar(
        App.context,
        message: message,
        action: action,
        backgroundColor: backgroundColor,
      );
    } catch (e) {}
  }

  void showMessage(
    String message, {
    String? title,
    List<MessageDialogActionButton>? actions,
    bool allowOuterDismiss = true,
    Function(String?)? onDismiss,
  }) {
    title ??= App.isEnglish ? 'Message' : 'رسالة';

    var md = MessageDialog.create
        .setTitle(title)
        .setMessage(message)
        .setEnableOuterDismiss(allowOuterDismiss)
        .setOnDismiss(onDismiss);

    if (actions == null) {
      md.addActions([
        MessageDialogActionButton(App.isEnglish ? 'Dismiss' : 'إغلاق'),
      ]);
    } else {
      md.addActions(actions);
    }

    md.show(() => App.context);
  }

  void showErrorMessage(
    String message, {
    String? title,
    VoidCallback? onRetry,
    Function()? onDismiss,
  }) {
    title ??= App.isEnglish ? 'Message' : 'رسالة';

    var m = MessageDialog.create
        .setTitle(title)
        .setMessage(message)
        .setEnableOuterDismiss(false);

    if (onRetry != null) {
      m.addActions([
        MessageDialogActionButton(App.isEnglish ? 'Retry' : 'إعادة',
            action: onRetry),
        MessageDialogActionButton(App.isEnglish ? 'Cancel' : 'إلغاء'),
      ]);
    } else {
      m.addActions([
        MessageDialogActionButton(App.isEnglish ? 'Dismiss' : 'إغلاق'),
      ]);
    }

    m.setOnDismiss(onDismiss == null ? null : (r) => onDismiss());
    m.show(() => App.context);
  }
}
