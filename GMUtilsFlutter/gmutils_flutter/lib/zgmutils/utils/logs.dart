import 'dart:core' as core;
//import 'dart:core';
import 'dart:io';

import 'package:flutter/foundation.dart';

import '../data_utils/storages/general_storage.dart';
import 'date_op.dart';
import 'files.dart';
import 'result.dart';

class Logs {
  static core.int? _logFileDeadline;
  static Result<core.DateTime>? _savedDeadline;

  static void setLogFileDeadline(
    core.String? logFileDeadline, {
        core.bool saveDate = false,
  }) {
    try {
      var dt = DateOp().parse(logFileDeadline ?? '', convertToLocalTime: true);
      _logFileDeadline = dt?.millisecondsSinceEpoch;
      if (dt != null && saveDate) {
        GeneralStorage.o('logs').save('deadline', logFileDeadline!);
        _savedDeadline = Result(dt);
      }
    } catch (e) {
      if (kDebugMode) {
        core.print(
            'Logs.setLogFileDeadline(logFileDeadline: $logFileDeadline) ---> Exception: $e');
      }
    }
  }

  static core.Future<core.DateTime?> get _savedLogsDeadline async {
    if (_savedDeadline != null) return _savedDeadline!.result;

    try {
      var d = await GeneralStorage.o('logs').retrieve('deadline');
      if (d?.isNotEmpty == true) {
        var dt = DateOp().parse(d ?? '', convertToLocalTime: true);
        _savedDeadline = Result(dt);
        return dt;
      } else {
        _savedDeadline = Result(null);
        return null;
      }
    } catch (e) {
      _savedDeadline = Result(null);
      return null;
    }
  }

  static core.Future<core.bool> get allowLogs async {
    return (kDebugMode || (await allowLogToFile));
  }

  static core.Future<core.bool> get allowLogToFile async {
    var now = core.DateTime.now();
    var printToFile = now.millisecondsSinceEpoch <
        (_logFileDeadline ?? core.DateTime(2024, 5, 15).millisecondsSinceEpoch);

    if (!printToFile) {
      var dl = await _savedLogsDeadline;
      if (dl != null) {
        printToFile = now.millisecondsSinceEpoch < dl.millisecondsSinceEpoch;
      }
    }

    return printToFile;
  }

  //----------------------------------------------------------------------------

  static void print(core.Object? Function() info) async {
    if (await allowLogs) {
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
  static core.List<core.String>? _fileTextCache;
  static core.bool _fileWriterBussy = false;
  static core.int _x = 0;

  static void _print(core.String text) async {
    if (await allowLogToFile) {
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

  static core.Future<void> _printToFile(core.String log) async {
    if (_fileWriterBussy) {
      _fileTextCache ??= [];
      _fileTextCache?.add(log);

      return;
    }

    _fileWriterBussy = true;
    var order = '000000${_x++}'.substring('$_x'.length);//0000 001234
    _files?.append('[$order]:: $log').then((v) {
      if (_fileTextCache?.isNotEmpty == true) {
        _fileWriterBussy = true;

        var log2 = _fileTextCache!.removeAt(0);
        if (_fileTextCache!.isEmpty) _fileTextCache = null;

        _fileWriterBussy = false;
        _printToFile(log2);
      } else {
        _fileWriterBussy = false;
      }
    });
  }

  static core.Future<Directory>? get logsDirPath => _files?.directoryPath;

  static core.Future<File>? get currentLogFile => _files?.localFile;

  static core.Future<core.String>? get currentLogFileContent => _files?.read();
}
