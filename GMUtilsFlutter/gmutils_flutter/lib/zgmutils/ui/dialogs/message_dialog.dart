import 'package:flutter/material.dart';
import 'package:flutter_linkify/flutter_linkify.dart';

import '../../resources/app_theme.dart';
import '../../utils/logs.dart';
import '../widgets/my_linkify.dart';

class MessageDialog {
  static MessageDialog get create => MessageDialog();

  BuildContext Function()? _context;
  double? _borderRadius;
  Color? _backgroundColor;
  BoxConstraints? _constraints;
  Widget? _topIcon;
  String _title = '';
  TextStyle? _titleStyle;
  String _message = '';
  TextStyle? _messageStyle;
  bool _enableLinks = false;
  bool _enableTextSelect = false;
  Widget? _extraWidget;
  final List<Widget> _actions = [];
  bool _enableOuterDismiss = true;
  bool _dismissed = false;
  String? _dismissedBy;
  Function(String? dismissedBy)? _onDismiss;
  bool _allowManualDismiss = true;

  MessageDialog setBoxStyle(
      {Color? backgroundColor,
      double? borderRadius,
      BoxConstraints? constraints}) {
    _backgroundColor = backgroundColor;
    _borderRadius = borderRadius;
    _constraints = constraints;
    return this;
  }

  MessageDialog setTopIcon(Widget topIcon) {
    _topIcon = topIcon;
    return this;
  }

  MessageDialog setTitle(String title, {TextStyle? style}) {
    _title = title;
    _titleStyle = style;
    return this;
  }

  MessageDialog setMessage(String message, {TextStyle? style}) {
    _message = message;
    _messageStyle = style;
    return this;
  }

  MessageDialog setEnableLinks(bool enableLinks) {
    _enableLinks = enableLinks;
    return this;
  }

  MessageDialog setEnableTextSelect(bool enableSelect) {
    _enableTextSelect = enableSelect;
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

  MessageDialog addActions(List<MessageDialogActionButton> actions) {
    int i = 0;

    for (var action in actions) {
      i++;

      void onPressed() {
        _dismiss(action.title);
        action.action?.call();
      }

      _actions.insert(
        0,
        action.useTextButton
            ? TextButton(
                onPressed: onPressed,
                style: action.buttonStyle,
                child: Text(action.title, style: action.textStyle(i)),
              )
            : ElevatedButton(
                onPressed: onPressed,
                style: action.buttonStyle,
                child: Text(action.title, style: action.textStyle(i)),
              ),
      );
    }

    return this;
  }

  MessageDialog setEnableOuterDismiss(bool enable) {
    _enableOuterDismiss = enable;
    return this;
  }

  MessageDialog setOnDismiss(Function(String?)? onDismiss) {
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

    Widget textWidget;
    if (_enableLinks) {
      textWidget = MyLinkify(
        text: _message,
        enableSelect: _enableTextSelect,
        options:
            _enableTextSelect ? null : const LinkifyOptions(humanize: false),
        textStyle: _messageStyle,
      );
    }
    //
    else {
      if (_enableTextSelect) {
        textWidget = SelectableText(
          _message,
          style: _messageStyle ?? AppTheme.defaultTextStyle(),
        );
      }
      //
      else {
        textWidget = Text(
          _message,
          style: _messageStyle ?? AppTheme.defaultTextStyle(),
        );
      }
    }

    showDialog(
        context: context(),
        barrierDismissible: _enableOuterDismiss,
        builder: (context) {
          return AlertDialog(
            icon: _topIcon,
            //
            title: Padding(
              padding: const EdgeInsets.only(left: 7, right: 7, top: 5),
              child: Text(
                _title,
                style: _titleStyle ?? AppTheme.textStyleOfScreenTitle(),
              ),
            ),
            //
            content: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 7),
              child: _extraWidget == null
                  ? textWidget
                  : Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        textWidget,
                        _extraWidget!,
                      ],
                    ),
            ),
            //
            actions: _actions,
            //
            backgroundColor: _backgroundColor ??
                AppTheme.appColors?.background ??
                Colors.white,
            //
            shape: _borderRadius == null
                ? null
                : RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(_borderRadius!),
                  ),
            constraints: _constraints,
          );
        }).then((value) {
      _context = null;
      if (_onDismiss != null) {
        Future.delayed(const Duration(milliseconds: 200), () {
          _onDismiss?.call(_dismissedBy);
        });
      }
    });

    return this;
  }

  void _dismiss(String dismissedBy) {
    if (!_allowManualDismiss) return;
    _dismissedBy = dismissedBy;
    dismiss();
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
  final bool useTextButton;
  final ButtonStyle? buttonStyle;
  TextStyle? _textStyle;
  Color? _textColor;
  FontWeight? _textFontWeight;
  final Function()? action;

  MessageDialogActionButton(
    this.title, {
    this.useTextButton = true,
    Color? color,
    FontWeight? fontWeight,
    this.action,
  }) : buttonStyle = null;

  MessageDialogActionButton.customStyle(
    this.title, {
    this.useTextButton = true,
    this.buttonStyle,
    TextStyle? textStyle,
    this.action,
  }) : _textStyle = textStyle;

  TextStyle? textStyle(int idx) {
    _textStyle ??= AppTheme.defaultTextStyle(
      textColor: _textColor ?? AppTheme.appColors?.secondary,
      fontWeight:
          idx == 1 ? (_textFontWeight ?? FontWeight.bold) : _textFontWeight,
    );

    return _textStyle;
  }
}
