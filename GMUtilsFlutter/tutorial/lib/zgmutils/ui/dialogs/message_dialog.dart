import 'package:flutter/material.dart';
import 'package:flutter_linkify/flutter_linkify.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/launcher.dart';
import '../../utils/logs.dart';

class MessageDialog {
  static MessageDialog get create => MessageDialog();

  BuildContext Function()? _context;
  String _title = '';
  String _message = '';
  Widget? _extraWidget;
  List<Widget> _actions = [];
  bool _enableOuterDismiss = true;
  bool _dismissed = false;
  Function? _onDismiss;
  bool _enableLinks = false;
  bool _enableSelect = false;
  bool _allowManualDismiss = true;

  MessageDialog setTitle(String title) {
    _title = title;
    return this;
  }

  MessageDialog setMessage(String message) {
    _message = message;
    return this;
  }

  MessageDialog setEnableLinks(bool enableLinks) {
    this._enableLinks = enableLinks;
    return this;
  }

  MessageDialog setEnableSelect(bool enableSelect) {
    this._enableSelect = enableSelect;
    return this;
  }

  MessageDialog setExtraWidget(Widget widget) {
    _extraWidget = widget;
    return this;
  }

  MessageDialog allowManualDismiss(bool allow) {
    _allowManualDismiss = allow;
    return this;
  }

  MessageDialog addAction(MessageDialogActionButton action) {
    _actions.insert(
      0,
      TextButton(
        onPressed: () {
          dismiss();
          action.action?.call();
        },
        child: Text(
          action.title,
          style: AppTheme.defaultTextStyle(
            textColor: action.color,
          ),
        ),
      ),
    );

    return this;
  }

  MessageDialog setEnableOuterDismiss(bool enable) {
    _enableOuterDismiss = enable;
    return this;
  }

  MessageDialog setOnDismiss(Function? onDismiss) {
    _onDismiss = onDismiss;
    return this;
  }

  int _tries = 3;

  MessageDialog show(BuildContext Function() context) {
    Logs.print(() => "MessageDialog.show() .. _tries: $_tries");
    try {
      _show(context);
    } catch (e) {
      Logs.print(() => "MessageDialog.show>> EXCEPTION: $e");
      Future.delayed(
        Duration(
          milliseconds: _tries > 2 ? 500 : (_tries > 1 ? 800 : 1300),
        ),
        () {
          if (_tries-- == 0) {
            _context = null;
            return;
          }

          if (!_dismissed && _context != null) {
            show(_context!);
          } else {
            _context = null;
          }
        },
      );
    }

    return this;
  }

  MessageDialog _show(BuildContext Function() context) {
    _context = context;

    if (_actions.isEmpty) {
      addAction(MessageDialogActionButton(App.isEnglish ? 'OK' : 'حسنا'));
    }

    Widget textWidget;
    if (_enableLinks) {
      if (_enableSelect) {
        textWidget = SelectableLinkify(
          onOpen: (link) {
            if (link is EmailLinkifier) {
              Launcher().sendEmail(link.url, '', '');
            } else {
              Launcher().openUrl(link.url);
            }
          },
          text: _message,
          options: const LinkifyOptions(humanize: false),
          style: AppTheme.defaultTextStyle(),
        );
      } else {
        textWidget = Linkify(
          onOpen: (link) {
            if (link is EmailLinkifier) {
              Launcher().sendEmail(link.url, '', '');
            } else {
              Launcher().openUrl(link.url);
            }
          },
          text: _message,
          options: const LinkifyOptions(humanize: false),
          style: AppTheme.defaultTextStyle(),
        );
      }
    } else {
      if (_enableSelect) {
        textWidget = SelectableText(
          _message,
          style: AppTheme.defaultTextStyle(),
        );
      } else {
        textWidget = Text(
          _message,
          style: AppTheme.defaultTextStyle(),
        );
      }
    }

    showDialog(
        context: context(),
        barrierDismissible: _enableOuterDismiss,
        builder: (context) {
          return AlertDialog(
            title: Text(
              _title,
              style: AppTheme.textStyleOfScreenTitle(),
            ),
            content: _extraWidget == null
                ? textWidget
                : Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      textWidget,
                      _extraWidget!,
                    ],
                  ),
            actions: _actions,
            backgroundColor: AppTheme.appColors?.background ?? Colors.white,
          );
        }).then((value) {
      _context = null;
      if (_onDismiss != null) {
        Future.delayed(const Duration(milliseconds: 200), () {
          _onDismiss?.call();
        });
      }
    });

    return this;
  }

  void dismiss() {
    if (!_allowManualDismiss) return;
    _dismissed = true;
    if (_context != null) {
      Navigator.pop(_context!());
    }
  }
}

class MessageDialogActionButton {
  final String title;
  final Color? color;
  final Function? action;

  MessageDialogActionButton(this.title, {this.color, this.action});
}
