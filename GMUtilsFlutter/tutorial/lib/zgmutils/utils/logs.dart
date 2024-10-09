import 'dart:core' as core;
import 'dart:core';

import 'package:flutter/foundation.dart';

import 'date_op.dart';
import 'files.dart';

class Logs {
  static int? _logFileDeadline;

  static void setLogFileDeadline(core.String? logFileDeadline) {
    try {
      var dt = DateOp().parse(logFileDeadline ?? '', convertToLocalTime: true);
      _logFileDeadline = dt?.millisecondsSinceEpoch;
    } catch (e) {
      if (kDebugMode) {
        core.print(
            'Logs.setLogFileDeadline(logFileDeadline: $logFileDeadline) ---> Exception: $e');
      }
    }
  }

  static bool get allowLogs {
    return (kDebugMode || allowLogToFile);
  }

  static bool get allowLogToFile {
    var now = core.DateTime.now();
    var printToFile = now.millisecondsSinceEpoch <
        (_logFileDeadline ?? core.DateTime(2024, 5, 15).millisecondsSinceEpoch);
    return printToFile;
  }

  //----------------------------------------------------------------------------

  static void print(core.Object? Function() info) {
    if (allowLogs) {
      final infoData = info();
      const logStart = '*****';
      if (infoData == null) {
        _print('$logStart null');
      } else {
        final string = infoData.toString();
        var len = string.length;

        const maxLen = 990;
        var start = 0;
        var end = maxLen;
        if (end > string.length) end = string.length;

        while (len > maxLen) {
          _print('$logStart ${string.substring(start, end)}');
          start += maxLen;
          end += maxLen;
          if (end > string.length) end = string.length;
          len -= maxLen;
        }

        if (len > 0) {
          _print('$logStart ${string.substring(start)}');
        }
      }
    }
  }

  //----------------------------------------------------------------------------

  static Files? _files;
  static List<core.String>? _fileTextCache;
  static bool _fileWriterBussy = false;

  static void _print(String text) {
    if (allowLogToFile) {
      var now = core.DateTime.now();

      if (_files == null) {
        _files = Files.public(
          'log_${DateOp().formatForDatabase2(day: DateOpDayComponent(year: now.year, month: now.month, day: now.day), time: DateOpTimeComponent(
                hour: now.hour,
                minute: now.minute,
                second: 0,
                timezone: null,
              ))}',
          'txt',
        );
        if (kDebugMode) {
          _files?.localFile.then((file) {
            core.print('****** LOG-FILE-PATH: ${file.path}');
          });
        }
      }

      _printToFile('>>>> $now ----\n$text \r\n\r\n');
    }

    if (kDebugMode) {
      core.print(text);
    }
  }

  static Future<void> _printToFile(String log) async {
    if (_fileWriterBussy) {
      _fileTextCache ??= [];
      _fileTextCache?.add(log);
    }

    _fileWriterBussy = true;
    await _files?.append(log);
    _fileWriterBussy = false;

    if (_fileTextCache?.isNotEmpty == true) {
      _fileWriterBussy = true;

      var log2 = _fileTextCache!.removeAt(0);
      if (_fileTextCache!.isEmpty) _fileTextCache = null;

      _fileWriterBussy = false;
      _printToFile(log2);
    }
  }
}
