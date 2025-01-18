import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:flutter/material.dart';

import '../../zgmutils/gm_main.dart';
import '../../zgmutils/resources/app_theme.dart';
import '../../zgmutils/utils/logs.dart';

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
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                //const Expanded(child: SizedBox()),

                Container(
                  padding: const EdgeInsets.all(10),
                  //color: AppTheme.appColors?.background ?? Colors.white,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      _ImageWidget(),
                      const SizedBox(width: 10),
                      Text(
                        _message,
                        style: AppTheme.defaultTextStyle(
                          textSize: 14,
                          textColor: Res.themes.colors.secondary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),

                //const Expanded(child: const SizedBox()),
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

class _ImageWidget extends StatefulWidget {
  const _ImageWidget({super.key});

  @override
  State<_ImageWidget> createState() => _ImageWidgetState();
}

class _ImageWidgetState extends State<_ImageWidget> {
  int index = 0;
  bool disposed = false;

  @override
  void dispose() {
    disposed = true;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    int n = index % 4; //0, 1, 2, 3, 0, 1, 2, 3, 0
    n++;

    Future.delayed(const Duration(milliseconds: 600), () {
      if (!disposed) {
        setState(() {
          index++;
        });
      }
    });

    return Image.asset(Res.images.progress(n));
  }
}
