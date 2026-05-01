import 'package:flutter/material.dart';
import 'package:flutter_linkify/flutter_linkify.dart';

import '../../resources/app_theme.dart';
import '../../utils/logs.dart';
import '../../utils/rich_text.dart';
import '../widgets/my_linkify.dart';

class MessageDialog {
  static MessageDialog get create => MessageDialog();

  BuildContext Function()? _context;
  double? _borderRadius;
  Color? _backgroundColor;
  BoxConstraints? _constraints;
  Widget? _topIcon;
  String? _title;
  TextStyle? _titleStyle;
  String? _message;
  RichSentence? _richMessage;
  TextAlign? _textAlign;
  TextStyle? _messageStyle;
  bool _enableLinks = false;
  bool _enableTextSelect = false;
  Widget? _extraWidget;
  final List<Widget> _actions = [];
  MainAxisAlignment? _actionsAlignment;
  bool _enableOuterDismiss = true;
  bool _dismissed = false;
  String? _dismissedBy;
  Function(String? dismissedBy)? _onDismiss;

  MessageDialog setBoxStyle({
    Color? backgroundColor,
    double? borderRadius,
    BoxConstraints? constraints,
  }) {
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

  MessageDialog setMessage(
    String message, {
    TextAlign? textAlign,
    TextStyle? style,
  }) {
    _message = message;
    _textAlign = textAlign;
    _messageStyle = style;
    return this;
  }

  MessageDialog setRichMessage(
    RichSentence message, {
    TextAlign? textAlign,
  }) {
    _richMessage = message;
    _textAlign = textAlign;
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

  MessageDialog addActions(
    List<MessageDialogActionButton> actions, {
    MainAxisAlignment? alignment,
  }) {
    int i = 0;

    for (var action in actions) {
      i++;

      void onPressed() {
        if (action.autoDismiss) _dismiss(action.title);
        action.action?.call();
      }

      final widget = action.useTextButton
          ? TextButton(
              onPressed: onPressed,
              style: action.buttonStyle,
              child: Text(action.title, style: action.textStyle(i)),
            )
          : ElevatedButton(
              onPressed: onPressed,
              style: action.buttonStyle,
              child: Text(action.title, style: action.textStyle(i)),
            );

      _actions.insert(
        0,
        action.width == null
            ? widget
            : SizedBox(
                width: action.width,
                child: widget,
              ),
      );
    }

    _actionsAlignment = alignment;

    return this;
  }

  List<MessageDialogActionButton>? _actionsCache;

  MessageDialog addAction(
    String title, {
    bool autoDismiss = true,
    Function()? action,
  }) {
    _actions.clear();

    _actionsCache ??= [];
    _actionsCache?.add(
      MessageDialogActionButton(
        title,
        autoDismiss: autoDismiss,
        action: action,
      ),
    );
    addActions(_actionsCache!);

    return this;
  }

  MessageDialog addActionIf(
    bool Function() condition, {
    required String title,
    bool autoDismiss = true,
    Function()? action,
  }) {
    if (condition()) {
      addAction(title, autoDismiss: autoDismiss, action: action);
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

  //===========================================================================
  
  int _tries = 3;

  /*MessageDialog show(BuildContext Function() context) {
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
  }*/

  void show(BuildContext Function() context) {
    _tryShow(false, context);
  }

  Future<dynamic> showAsync(BuildContext Function() context) async {
    return _tryShow(true, context);
  }

  Future<dynamic> _tryShow(
    bool forResult,
    BuildContext Function() context,
  ) async {
    Logs.print(
      () => "MessageDialog.tryShow(forResult: $forResult)",
    );

    bool b = false;

    do {
      try {
        return await _show(context);
      } catch (e) {
        Logs.print(() => "MessageDialog.show:[_tries: $_tries]>> EXCEPTION: $e");

        b = await Future.delayed(
          Duration(
            milliseconds: _tries > 2 ? 500 : (_tries > 1 ? 800 : 1300),
          ),
          () {
            if (_tries-- == 0) {
              _context = null;
              return false;
            }

            if (!_dismissed && _context != null) {
              return true;
            }
            //
            else {
              _context = null;
              return false;
            }
          },
        );
      }
    } while (b);

    return this;
  }

  Future<dynamic> _show(BuildContext Function() context) async {
    _context = context;

    Widget textWidget;
    if (_richMessage != null) {
      textWidget = Text.rich(
        _richMessage!.asTextSpan(),
        textAlign: _textAlign,
        style: _richMessage!.style ?? AppTheme.defaultTextStyle(),
      );
    }
    //
    else if (_enableLinks) {
      textWidget = MyLinkify(
        text: _message ?? '',
        textAlign: _textAlign,
        enableSelect: _enableTextSelect,
        options:
            _enableTextSelect ? null : const LinkifyOptions(humanize: false),
        textStyle: _messageStyle,
      );
    }
    //
    else {
      textWidget = _enableTextSelect
          ? SelectableText(
              _message ?? '',
              textAlign: _textAlign,
              style: _messageStyle ?? AppTheme.defaultTextStyle(),
            )
          : Text(
              _message ?? '',
              textAlign: _textAlign,
              style: _messageStyle ?? AppTheme.defaultTextStyle(),
            );
    }

    var r = await showDialog(
      context: context(),
      barrierDismissible: _enableOuterDismiss,
      builder: (context) {
        return AlertDialog(
          icon: _topIcon,
          //title
          title: _title == null
              ? null
              : Padding(
                  padding: const EdgeInsets.only(left: 7, right: 7, top: 5),
                  child: Text(
                    _title!,
                    style: _titleStyle ?? AppTheme.textStyleOfScreenTitle(),
                  ),
                ),
          //body
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
          actionsAlignment: _actionsAlignment,
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
      },
    );

    _context = null;
    if (_onDismiss != null) {
      Future.delayed(const Duration(milliseconds: 200), () {
        _onDismiss?.call(_dismissedBy);
      });
    }

    return r;
  }

  void _dismiss(String dismissedBy) {
    _dismissedBy = dismissedBy;
    _dismissed = true;
    if (_context != null) {
      Navigator.pop(_context!(), _dismissedBy);
    }
  }
}

class MessageDialogActionButton {
  final String title;
  final bool useTextButton;
  final ButtonStyle? buttonStyle;
  TextStyle? _textStyle;
  final Color? _textColor;
  final FontWeight? _textFontWeight;
  final double? width;
  final bool autoDismiss;
  final Function()? action;

  MessageDialogActionButton(
    this.title, {
    this.useTextButton = true,
    Color? color,
    FontWeight? fontWeight,
    this.width,
    this.autoDismiss = true,
    this.action,
  })  : buttonStyle = null,
        _textStyle = null,
        _textColor = color,
        _textFontWeight = fontWeight;

  MessageDialogActionButton.customStyle(
    this.title, {
    this.useTextButton = true,
    this.buttonStyle,
    TextStyle? textStyle,
    this.width,
    this.autoDismiss = true,
    this.action,
  })  : _textStyle = textStyle,
        _textColor = null,
        _textFontWeight = null;

  TextStyle? textStyle(int idx) {
    _textStyle ??= AppTheme.defaultTextStyle(
      textColor: _textColor ?? AppTheme.appColors?.secondary,
      fontWeight:
          idx == 1 ? (_textFontWeight ?? FontWeight.bold) : _textFontWeight,
    );

    return _textStyle;
  }
}
