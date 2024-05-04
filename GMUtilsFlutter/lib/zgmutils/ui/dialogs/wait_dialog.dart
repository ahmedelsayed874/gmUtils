import 'package:flutter/material.dart';

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/logs.dart';

class WaitDialog {
  static WaitDialog get create => WaitDialog();

  BuildContext Function()? _context;
  String _message = '';
  bool _shown = false;
  bool _dismissed = false;

  WaitDialog setMessage(String message) {
    _message = message;
    return this;
  }

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
          _context = null;
          return;
        }

        if (!_dismissed && _context != null) {
          show(_context!, delayMs: delayMs);
        } else {
          _context = null;
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
    } else {
      Future.delayed(Duration(milliseconds: delayMs), () {
        if (!_dismissed && _context != null) {
          try {
            _show(_context!);
          } catch (e) {
            Logs.print(() => "WaitDialog.show>> EXCEPTION: $e");
            retryShow();
          }
        } else {
          _context = null;
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
                        child: CircularProgressIndicator(),
                        width: 25,
                        height: 25,
                      ),
                      const SizedBox(width: 10),
                      Text(
                        _message,
                        style: AppTheme.defaultTextStyle(),
                      ),
                    ],
                  ),
                ),
                const Expanded(child: const SizedBox()),
              ],
            ),
          ),
        );
      },
      routeSettings: routeSettings,
    ).then((value) => _context = null);

    _shown = true;

    return this;
  }

  void dismiss() {
    _dismissed = true;
    if (_context != null) {
      if (_shown) {
        Navigator.pop(_context!());
      }
    }
  }
}
