import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/logs.dart';

class WaitDialog {
  static WaitDialog get create => WaitDialog();

  BuildContext Function()? _context;
  _WaitDialogBodyHorizontal? _waitDialogBodyHor;
  _WaitDialogBodyVertical? _waitDialogBodyVer;

  String _message = '';
  bool _shown = false;
  WaitDialogStyle _style = WaitDialogStyle.vertical;
  bool _dismissed = false;

  //------------------------------------------------

  String get message => _message;

  bool get isShown => _shown;

  WaitDialog setMessage(String message) {
    _message = message;

    _waitDialogBodyHor?.updateMessage(message);
    _waitDialogBodyVer?.updateMessage(message);

    return this;
  }

  WaitDialog setStyle(WaitDialogStyle style) {
    _style = style;
    return this;
  }

  //------------------------------------------------

  int _tries = 3;

  WaitDialog show(BuildContext Function() context, {int? delayMs}) {
    Logs.print(() => "WaitDialog.show() .. _tries: $_tries");

    _context = context;

    retryShow() => Future.delayed(
          Duration(
            milliseconds: _tries > 2 ? 500 : (_tries > 1 ? 800 : 1300),
          ),
          () {
            if (_tries-- == 0) {
              _dispose();
              return;
            }

            if (!_dismissed && _context != null) {
              show(_context!, delayMs: delayMs);
            }
            //
            else {
              _dispose();
            }
          },
        );

    if (delayMs == null) {
      try {
        _show(context);
      } catch (e) {
        Logs.print(() => "WaitDialog.show>> EXCEPTION: $e");
        retryShow();
      }
    }
    //
    else {
      Future.delayed(Duration(milliseconds: delayMs), () {
        if (!_dismissed && _context != null) {
          try {
            _show(_context!);
          } catch (e) {
            Logs.print(() => "WaitDialog.show>> EXCEPTION: $e");
            retryShow();
          }
        }
        //
        else {
          _dispose();
        }
      });
    }

    return this;
  }

  WaitDialog _show(BuildContext Function() context) {
    _context = context;

    if (_message.isEmpty) {
      _message = App.isEnglish ? 'Please wait...' : 'يرجى الانتظار...';
    }

    RouteSettings routeSettings = const RouteSettings(name: 'wait_dialog');

    showDialog(
      context: context(),
      builder: (context) {
        if (_style == WaitDialogStyle.horizontal) {
          _waitDialogBodyHor ??= _WaitDialogBodyHorizontal(message: message);
          return _waitDialogBodyHor!;
        }

        //
        else {
          _waitDialogBodyVer ??= _WaitDialogBodyVertical(message: message);
          return _waitDialogBodyVer!;
        }
      },
      routeSettings: routeSettings,
    ).then((value) {
      _dispose();
    });

    _shown = true;

    return this;
  }

  void dismiss() {
    _dismissed = true;

    if (_context != null) {
      if (_shown) {
        Navigator.pop(_context!());
      }
      //
      else {
        _dispose();
      }
    }
    //
    else {
      _dispose();
    }
  }

  void _dispose() {
    _context = null;
    _waitDialogBodyHor = null;
    _waitDialogBodyVer = null;
  }
}

enum WaitDialogStyle { horizontal, vertical }

//////////////////////////////////////////////////////////////////////////////

class _WaitDialogBodyHorizontal extends StatefulWidget {
  final String message;

  const _WaitDialogBodyHorizontal({
    required this.message,
  });

  @override
  State<_WaitDialogBodyHorizontal> createState() => _WaitDialogBodyHorizontalState();

  void updateMessage(String message) {
    _WaitDialogBodyHorizontalState.updateMessage(message);
  }
}

class _WaitDialogBodyHorizontalState extends State<_WaitDialogBodyHorizontal> {
  static _WaitDialogBodyHorizontalState? _state;

  static void updateMessage(String message) {
    _state?.message = message;
    _state?.setState(() {});
  }

  late String message;

  @override
  void initState() {
    _state = this;
    super.initState();
    message = widget.message;
  }

  @override
  void dispose() {
    _state = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      type: MaterialType.transparency,
      child: Center(
        child: Row(
          children: [
            const Expanded(child: SizedBox()),
            Container(
              padding: const EdgeInsets.all(10),
              color: AppTheme.appColors?.background ?? Colors.white,
              child: Row(
                children: [
                  const SizedBox(
                    width: 25,
                    height: 25,
                    child: CircularProgressIndicator(),
                  ),
                  const SizedBox(width: 10),
                  Text(
                    message,
                    style: AppTheme.defaultTextStyle(),
                  ),
                ],
              ),
            ),
            const Expanded(child: SizedBox()),
          ],
        ),
      ),
    );
  }
}

//////////////////////////////////////////////////////////////////////////////

class _WaitDialogBodyVertical extends StatefulWidget {
  final String message;

  const _WaitDialogBodyVertical({
    required this.message,
  });

  @override
  State<_WaitDialogBodyVertical> createState() => _WaitDialogBodyVerticalState();

  void updateMessage(String message) {
    _WaitDialogBodyVerticalState.updateMessage(message);
  }
}

class _WaitDialogBodyVerticalState extends State<_WaitDialogBodyVertical> {
  static _WaitDialogBodyVerticalState? _state;

  static void updateMessage(String message) {
    _state?.message = message;
    _state?.setState(() {});
  }

  late String message;

  @override
  void initState() {
    _state = this;
    super.initState();
    message = widget.message;
  }

  @override
  void dispose() {
    _state = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      type: MaterialType.transparency,
      child: Center(
        child: Column(
          children: [
            const Expanded(child: SizedBox()),
            Container(
              padding: const EdgeInsets.all(15),
              decoration: BoxDecoration(
                color: AppTheme.appColors?.background ?? Colors.white,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Column(
                children: [
                  const SizedBox(
                    width: 25,
                    height: 25,
                    child: CircularProgressIndicator(),
                  ),
                  const SizedBox(height: 10),
                  Text(
                    message,
                    style: AppTheme.defaultTextStyle(fontWeight: FontWeight.bold,),
                  ),
                ],
              ),
            ),
            const Expanded(child: SizedBox()),
          ],
        ),
      ),
    );
  }
}
