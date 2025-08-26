import 'dart:convert';
import 'dart:core' as core;

//import 'dart:core';
import 'dart:io';

import 'package:flutter/foundation.dart';

import '../data_utils/storages/general_storage.dart';
import 'date_op.dart';
import 'files.dart';
import 'result.dart';

class Logs {
  static core.Map<core.String?, _LogsManager>? _logs;

  static _LogsManager get(
    core.String? name, {
    core.int maxLogsFiles = 10,
    core.bool forceCreateNewInstance = false,
  }) {
    _logs ??= {};

    if (forceCreateNewInstance) {
      _logs![name] = _LogsManager._(logsSet: name, maxLogsFiles: maxLogsFiles);
    }
    //
    else {
      _logs![name] ??= _LogsManager._(logsSet: name, maxLogsFiles: maxLogsFiles);
    }

    return _logs![name]!;
  }

  static _LogsManager get _defLogs => get(null);

  static core.List<_LogsManager> get allLogs {
    var all = _logs?.values.toList() ?? [];
    if (all.isEmpty) all.add(_defLogs);
    return all;
  }

  //////////////////////////////////////////////////////////////////////////////

  static void setLogFileDeadline(
    core.String? logFileDeadline, {
    core.bool saveDate = false,
  }) =>
      _defLogs.setLogFileDeadline(
        logFileDeadline,
        saveDate: saveDate,
      );

  static core.Future<core.DateTime?> get savedLogsDeadline =>
      _defLogs.savedLogsDeadline;

  static core.bool get inDebugMode => _defLogs.inDebugMode;

  static core.Future<core.bool> get writingToLogFileEnabled =>
      _defLogs.writingToLogFileEnabled;

  //----------------------------------------------------------------------------

  static void print(core.Object? Function() info) => _defLogs.print(info);

  //----------------------------------------------------------------------------

  static core.Future<core.String?> getLastLogsContent({
    core.int upTo = 1,
    core.bool encrypted = true,
  }) async {
    core.String content = '';

    for (var logs in allLogs) {
      if (content.isNotEmpty) content += '\n';
      content += '=>${logs.logsSet ?? 'DEF'}::${await logs.getLastLogsContent(
        upTo: upTo,
        encrypted: encrypted,
      )}';
    }

    return content;
  }

  //----------------------------------------------------------------------------

  static core.Future<core.bool> get hasLogs async {
    for (var value in allLogs) {
      final dir = await value.logsDirPath;
      final files = dir?.listSync(followLinks: false);

      if (files?.isNotEmpty == true) {
        return true;
      }
    }

    return false;
  }
}

class _LogsManager {
  final core.String? logsSet;
  final core.int maxLogsFiles;

  _LogsManager._({required this.logsSet, required this.maxLogsFiles});

  //----------------------------------------------------------------------------

  core.String _getLogsSetStr({core.String prefix = '_'}) =>
      logsSet?.isNotEmpty == true ? '$prefix$logsSet' : '';

  //----------------------------------------------------------------------------

  core.int? _logFileDeadline;
  Result<core.DateTime>? _savedDeadline;

  void setLogFileDeadline(
    core.String? logFileDeadline, {
    core.bool saveDate = false,
  }) {
    try {
      var dt = DateOp().parse(logFileDeadline ?? '', convertToLocalTime: true);
      _logFileDeadline = dt?.millisecondsSinceEpoch;
      if (dt != null && saveDate) {
        GeneralStorage.o('logs').save(
          'deadline${_getLogsSetStr()}}',
          logFileDeadline!,
        );
        _savedDeadline = Result(dt);
      }
    } catch (e) {
      if (kDebugMode) {
        core.print(
            'Logs.setLogFileDeadline(logFileDeadline: $logFileDeadline) ---> Exception: $e');
      }
    }
  }

  core.Future<core.DateTime?> get savedLogsDeadline async {
    if (_savedDeadline != null) return _savedDeadline!.result;

    try {
      var d = await GeneralStorage.o('logs').retrieve(
        'deadline${_getLogsSetStr()}',
      );
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

  core.bool get inDebugMode {
    return kDebugMode;
  }

  core.Future<core.bool> get writingToLogFileEnabled async {
    var now = core.DateTime.now();
    var printToFile = now.millisecondsSinceEpoch < (_logFileDeadline ?? 0);

    if (!printToFile) {
      var dl = await savedLogsDeadline;
      if (dl != null) {
        printToFile = now.millisecondsSinceEpoch < dl.millisecondsSinceEpoch;
      }
    }

    return printToFile;
  }

  //----------------------------------------------------------------------------

  Files? _files;
  core.List<core.String>? _fileTextCache;
  core.bool _fileWriterBusy = false;
  core.int _x = 0;

  void print(core.Object? Function() info) async {
    if (inDebugMode || await writingToLogFileEnabled) {
      final infoData = info();
      if (infoData == null) {
        _print('null');
      } else {
        final string = infoData.toString();
        var len = string.length;

        const maxLen = 990;
        var start = 0;
        var end = maxLen;
        if (end > string.length) end = string.length;

        while (len > maxLen) {
          _print(string.substring(start, end));
          start += maxLen;
          end += maxLen;
          if (end > string.length) end = string.length;
          len -= maxLen;
        }

        if (len > 0) {
          _print(string.substring(start));
        }
      }
    }
  }

  void _print(core.String text) async {
    if (await writingToLogFileEnabled) {
      _createLogFileIfNotExist();
      _printToFile(text);
    }

    if (kDebugMode) {
      core.print('***** $text');
    }
  }

  void _createLogFileIfNotExist() async {
    if (_files != null) return;

    var now = core.DateTime.now();

    _files = Files.private(
      'log_${DateOp().format(now, pattern: 'yyyy-MM-dd-HH-mm-ss')}',
      'txt',
      subDirName: 'logs${_getLogsSetStr(prefix: '/')}',
    );

    final dir = await logsDirPath;
    if (dir != null) {
      final files = dir
          .listSync(followLinks: false)
          .where((e) => e.path.endsWith('txt'))
          .toList();

      while (files.length > maxLogsFiles) {
        try {
          files.removeAt(0).deleteSync();
        } catch (e) {
          if (kDebugMode) {
            core.print(e);
          }
        }
      }
    }

    if (kDebugMode) {
      _files?.localFile.then((file) {
        if (kDebugMode) {
          core.print('****** LOG-FILE-PATH: ${file.path}');
        }
      });
    }
  }

  core.Future<void> _printToFile(core.String text) async {
    if (_fileWriterBusy) {
      _fileTextCache ??= [];
      _fileTextCache?.add(text);

      return;
    }

    _fileWriterBusy = true;

    var now = core.DateTime.now();
    var order = '000000${_x++}'.substring('$_x'.length); //0000 001234
    final log = '[$order]:: $now::\n$text \r\n\r\n';

    await _files?.append(log);

    if (_fileTextCache?.isNotEmpty == true) {
      _fileWriterBusy = true;

      var log2 = _fileTextCache!.removeAt(0);
      if (_fileTextCache!.isEmpty) _fileTextCache = null;

      _fileWriterBusy = false;
      _printToFile(log2);
    } else {
      _fileWriterBusy = false;
    }
  }

  //----------------------------------------------------------------------------

  core.Future<Directory?> get logsDirPath async {
    return _files?.directoryPath;
  }

  core.Future<File?> get currentLogFile async {
    return _files?.localFile;
  }

  core.Future<core.String?> getLastLogsContent({
    core.int upTo = 1,
    core.bool encrypted = true,
  }) async {
    if (upTo < 1) {
      return null;
    }
    //
    else if (upTo == 1) {
      var file = await currentLogFile;
      if (file == null) return null;

      if (encrypted) {
        return _encodeFileContent(file);
      }
      //
      else {
        return _getFileContent(file);
      }
    }

    final dir = await logsDirPath;
    if (dir == null) return null;

    final files = dir
        .listSync(followLinks: false)
        .where((e) => e.path.endsWith('txt'))
        .toList()
        .reversed;

    var content = '';
    var i = 0;
    while (i < upTo && i < files.length) {
      if (content.isNotEmpty) content += '\n\n';
      
      var file = File(files.elementAt(i).path);

      if (encrypted) {
        content += await _encodeFileContent(file);
      }
      //
      else {
        content += await _getFileContent(file);
      }

      i++;
    }

    return content;
  }

  core.Future<core.String> _getFileContent(File file) async {
    core.String content;
    
    try {
      content = await file.readAsString(); 
    } catch (e) {
      return '';
    }
    
    return '${Files.extractFileName(file)}'
        ':-------------------------\n'
        '$content';
  }
  
  core.Future<core.String> _encodeFileContent(File file) async {
    core.String content = await _getFileContent(file);
    
    core.List<core.int> bytes = utf8.encode(content);
    core.String contentBase64 = base64.encode(bytes);
    return contentBase64;
  }
}
