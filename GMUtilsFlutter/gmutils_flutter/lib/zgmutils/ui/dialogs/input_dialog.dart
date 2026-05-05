import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../widgets/direction_aware_textfield.dart';

class InputDialog {
  static InputDialog get create => InputDialog();

  String _title = '';
  String _message = '';
  String _inputText = '';
  String _inputHint = '';
  int? _minInputLines;
  int? _maxInputLines;
  String? Function(String input)? _validationInputHandler;
  void Function(String input)? _inputHandler;
  void Function()? _onDismiss;
  final TextEditingController _inputController = TextEditingController();
  TextInputType? _keyboardType;
  bool _obscureText = false;
  String? _okButtonText;
  TextStyle? _okButtonTextStyle;
  String? _skipButtonText;
  TextStyle? _skipButtonTextStyle;
  String? _cancelButtonText;
  TextStyle? _cancelButtonTextStyle;
  bool _enableOuterDismiss = true;
  bool _showSkipButton = false;
  void Function(String input)? _onSkipClick;
  BuildContext Function()? _context;
  bool _dismissed = false;
  Widget? _extraWidget;

  //
  InputDialog setTitle(String title) {
    _title = title;
    return this;
  }

  InputDialog setMessage(String message) {
    _message = message;
    return this;
  }

  //
  InputDialog setInputText(String text) {
    _inputText = text;
    return this;
  }

  InputDialog setInputHint(String hint) {
    _inputHint = hint;
    return this;
  }

  InputDialog setMinInputLines(int minInputLines) {
    _minInputLines = minInputLines;
    return this;
  }

  InputDialog setMaxInputLines(int maxInputLines) {
    _maxInputLines = maxInputLines;
    return this;
  }

  InputDialog setInputKeyboardType(TextInputType keyboardType) {
    _keyboardType = keyboardType;
    return this;
  }

  InputDialog setInputObscureText(bool obscureText) {
    _obscureText = obscureText;
    return this;
  }

  InputDialog setInputHandler(void Function(String input)? inputHandler) {
    _inputHandler = inputHandler;
    return this;
  }

  InputDialog setValidationInputHandler(
    String? Function(String input)? validationInputHandler,
  ) {
    _validationInputHandler = validationInputHandler;
    return this;
  }

  //
  InputDialog setExtraWidget(Widget widget) {
    _extraWidget = widget;
    return this;
  }

  //
  InputDialog setEnableOuterDismiss(bool enable) {
    _enableOuterDismiss = enable;
    return this;
  }

  //
  InputDialog setOkButtonText(String text) {
    _okButtonText = text;
    return this;
  }

  InputDialog setOkButtonTextStyle(TextStyle style) {
    _okButtonTextStyle = style;
    return this;
  }

  InputDialog setSkipButtonText(String text) {
    _skipButtonText = text;
    return this;
  }

  InputDialog setSkipButtonTextStyle(TextStyle style) {
    _skipButtonTextStyle = style;
    return this;
  }

  InputDialog setCancelButtonText(String text) {
    _cancelButtonText = text;
    return this;
  }

  InputDialog setCancelButtonTextStyle(TextStyle style) {
    _cancelButtonTextStyle = style;
    return this;
  }

  //
  InputDialog showSkipButton(void Function(String input)? onSkipClick) {
    _showSkipButton = true;
    _onSkipClick = onSkipClick;
    return this;
  }

  InputDialog setOnDismiss(void Function() onDismiss) {
    _onDismiss = onDismiss;
    return this;
  }

  //
  int _tries = 3;

  InputDialog show(BuildContext Function() context) {
    _tryShow(context);
    return this;
  }

  Future<String?> showAsync(BuildContext Function() context) {
    return _tryShow(context);
  }

  Future<String?> _tryShow(BuildContext Function() context) async {
    bool b = false;

    do {
      try {
        return _show(context);
      } catch (e) {
        b = await Future.delayed(const Duration(milliseconds: 500), () {
          if (--_tries == 0) {
            _context = null;
            return false;
          }

          if (!_dismissed && _context != null) {
            return true;
          } else {
            _context = null;
            return false;
          }
        });
      }
    } while (b);

    return null;
  }

  Future<String?> _show(BuildContext Function() context) async {
    _context = context;

    _inputController.text = _inputText;

    var r = await showDialog(
        context: context(),
        barrierDismissible: _enableOuterDismiss,
        builder: (context) {
          return _AlertDialogBody(inputDialog: this);
        });

    _context = null;
    _inputHandler = null;
    _onDismiss?.call();
    _onDismiss = null;

    return r;
  }

  //
  void _dismiss(String? input) {
    _dismissed = true;
    if (_context != null) Navigator.pop(_context!(), input);
  }
}

class _AlertDialogBody extends StatefulWidget {
  InputDialog? inputDialog;

  _AlertDialogBody({required this.inputDialog});

  @override
  State<_AlertDialogBody> createState() => _AlertDialogBodyState();
}

class _AlertDialogBodyState extends State<_AlertDialogBody> {
  String? error;

  @override
  void dispose() {
    super.dispose();
    widget.inputDialog = null;
  }

  @override
  Widget build(BuildContext context) {
    var inputDialog = widget.inputDialog!;

    return Column(
      children: [
        const Expanded(child: Text('')),
        AlertDialog(
          title: Text(
            inputDialog._title,
            style: AppTheme.textStyleOfScreenTitle(),
          ),
          content: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                inputDialog._message,
                style: AppTheme.defaultTextStyle(),
              ),
              DirectionAwareTextField(
                controller: inputDialog._inputController,
                decoration: InputDecoration(
                  hintText: inputDialog._inputHint,
                  hintStyle: AppTheme.defaultTextStyle(
                    fontWeight: FontWeight.normal,
                    textColor: AppTheme.appColors?.hint,
                  ),
                  border: const OutlineInputBorder(),
                  errorText: error,
                ),
                keyboardType: inputDialog._keyboardType,
                minLines: inputDialog._obscureText
                    ? null
                    : inputDialog._minInputLines,
                maxLines: inputDialog._obscureText
                    ? 1
                    : (inputDialog._minInputLines == null
                        ? null
                        : (inputDialog._maxInputLines ??
                            (inputDialog._minInputLines! + 3))),
                obscureText: inputDialog._obscureText,
              ),
              if (inputDialog._extraWidget != null) inputDialog._extraWidget!,
            ],
          ),
          actions: [
            TextButton(
              onPressed: () {
                error = null;

                if (inputDialog._validationInputHandler != null) {
                  error = inputDialog._validationInputHandler!(
                      inputDialog._inputController.text);
                  if (error != null) {
                    setState(() {});
                  }
                }

                if (error == null) {
                  inputDialog._dismiss(
                    inputDialog._inputController.text,
                  );

                  inputDialog._inputHandler?.call(
                    inputDialog._inputController.text,
                  );
                }
              },
              child: Text(
                inputDialog._okButtonText ?? (App.isEnglish ? 'OK' : 'موافق'),
                style: inputDialog._okButtonTextStyle,
              ),
            ),
            if (inputDialog._showSkipButton)
              TextButton(
                onPressed: () {
                  inputDialog._dismiss(null);

                  inputDialog._onSkipClick?.call(
                    inputDialog._inputController.text,
                  );
                },
                child: Text(
                  inputDialog._skipButtonText ??
                      (App.isEnglish ? 'Skip' : 'تخطي'),
                  style: inputDialog._skipButtonTextStyle,
                ),
              ),
            TextButton(
              onPressed: () {
                inputDialog._dismiss(null);
              },
              child: Text(
                inputDialog._cancelButtonText ??
                    (App.isEnglish ? 'Cancel' : 'إلغاء'),
                style: inputDialog._cancelButtonTextStyle,
              ),
            ),
          ],
          backgroundColor: AppTheme.appColors?.background ?? Colors.white,
        ),
        const Expanded(child: Text('')),
      ],
    );
  }
}
