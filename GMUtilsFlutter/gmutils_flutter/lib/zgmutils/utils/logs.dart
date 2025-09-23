import 'dart:convert';
import 'dart:core' as core;

//import 'dart:core';
import 'dart:io';

import 'package:flutter/foundation.dart';

import '../data_utils/storages/general_storage.dart';
import 'date_op.dart';
import 'files/files.dart';

class Logs {
  static core.Map<core.String?, LogsManager>? _logs;

  static LogsManager get(
    core.String? name, {
    core.int maxLogsFiles = 10,
    core.bool forceCreateNewInstance = false,
  }) {
    _logs ??= {};

    if (forceCreateNewInstance) {
      _logs![name] = _LogsManagerImpl(
        logsSet: name,
        maxLogsFiles: maxLogsFiles,
      );
    }
    //
    else {
      _logs![name] ??= _LogsManagerImpl(
        logsSet: name,
        maxLogsFiles: maxLogsFiles,
      );
    }

    return _logs![name]!;
  }

  static LogsManager get _defLogs => get(null);

  static core.List<LogsManager> get allLogs {
    var all = _logs?.values.toList() ?? [];
    if (all.isEmpty) all.add(_defLogs);
    return all;
  }

  //////////////////////////////////////////////////////////////////////////////

  static void setLogFileDeadline({
    core.String? publicLogFileDeadline,
    core.String? privateLogFileDeadline,
    core.bool saveDate = false,
  }) =>
      _defLogs.setLogFileDeadline(
        publicLogFileDeadline: publicLogFileDeadline,
        privateLogFileDeadline: privateLogFileDeadline,
        saveDate: saveDate,
      );

  static core.Future<core.DateTime?> get savedPublicLogsDeadline =>
      _defLogs.savedPublicLogsDeadline;

  static core.Future<core.DateTime?> get savedPrivateLogsDeadline =>
      _defLogs.savedPrivateLogsDeadline;

  static core.bool get inDebugMode => _defLogs.inDebugMode;

  static core.Future<core.bool> get writingToLogFileEnabled =>
      _defLogs.writingToLogFileEnabled;

  static core.Future<core.bool> get writingToPublicLogFileEnabled =>
      _defLogs.writingToPublicLogFileEnabled;

  static core.Future<core.bool> get writingToPrivateLogFileEnabled =>
      _defLogs.writingToPrivateLogFileEnabled;

  //----------------------------------------------------------------------------

  static void print(core.Object? Function() info) => _defLogs.print(info);

  //----------------------------------------------------------------------------

  ///fromPublicLogs: null mean read from current logs dir
  static core.Future<core.String?> getLastLogsContent({
    core.int upTo = 1,
    core.bool encrypted = true,
    core.bool? fromPublicLogs,
  }) async {
    core.String content = '';

    for (var logs in allLogs) {
      if (content.isNotEmpty) content += '\n';
      content += '=>${logs.logsSet ?? 'DEF'}::${await logs.getLastLogsContent(
        upTo: upTo,
        encrypted: encrypted,
        fromPublicLogs: fromPublicLogs,
      )}';
    }

    return content;
  }

  //----------------------------------------------------------------------------

  static core.Future<core.bool> get hasPublicLogs => _hasLogs(false);

  static core.Future<core.bool> get hasPrivateLogs => _hasLogs(true);

  static core.Future<core.bool> get hasLogs => _hasLogs(null);

  static core.Future<core.bool> _hasLogs(core.bool? fromPublicLogs) async {
    for (var value in allLogs) {
      final dir = fromPublicLogs == null
          ? (await value.currentLogsDir)
          : (fromPublicLogs
              ? (await value.publicLogsDir)
              : (await value.privateLogsDir));
      final files = dir?.listSync(followLinks: false);

      if (files?.isNotEmpty == true) {
        return true;
      }
    }

    return false;
  }
}

abstract class LogsManager {
  final core.String? logsSet;
  final core.int maxLogsFiles;

  LogsManager({required this.logsSet, required this.maxLogsFiles});

  //----------------------------------------------------------------------------

  core.String _getLogsSetStr({core.String prefix = '_'}) =>
      logsSet?.isNotEmpty == true ? '$prefix$logsSet' : '';

  //----------------------------------------------------------------------------

  core.int? _publicLogFileDeadline;
  core.int? _privateLogFileDeadline;

  void setLogFileDeadline({
    core.String? publicLogFileDeadline,
    core.String? privateLogFileDeadline,
    core.bool saveDate = false,
  }) {
    if (publicLogFileDeadline == null && privateLogFileDeadline == null) {
      core.print(
        'setLogsFileDeadline '
        'CALLED BUT WITHOUT PROVIDING DATES '
        '(LOGS NAME: ${_getLogsSetStr()})',
      );
    }

    if (publicLogFileDeadline != null) {
      final dt = DateOp().parse(
        publicLogFileDeadline,
        convertToLocalTime: true,
      );
      if (dt != null) {
        _publicLogFileDeadline = dt.microsecondsSinceEpoch;
        if (saveDate) {
          GeneralStorage.o('logs').save(
            'deadline${_getLogsSetStr()}_pub',
            publicLogFileDeadline,
          );
        }
      }
    }

    if (privateLogFileDeadline != null) {
      final dt = DateOp().parse(
        privateLogFileDeadline,
        convertToLocalTime: true,
      );
      if (dt != null) {
        _privateLogFileDeadline = dt.microsecondsSinceEpoch;
        if (saveDate) {
          GeneralStorage.o('logs').save(
            'deadline${_getLogsSetStr()}_prv',
            privateLogFileDeadline,
          );
        }
      }
    }
  }

  core.Future<core.DateTime?> get savedPublicLogsDeadline async {
    try {
      var d = await GeneralStorage.o('logs').retrieve(
        'deadline${_getLogsSetStr()}_pub',
      );
      var dt = DateOp().parse(d, convertToLocalTime: true);
      return dt;
    } catch (e) {
      return null;
    }
  }

  core.Future<core.DateTime?> get savedPrivateLogsDeadline async {
    try {
      var d = await GeneralStorage.o('logs').retrieve(
        'deadline${_getLogsSetStr()}_prv',
      );
      var dt = DateOp().parse(d, convertToLocalTime: true);
      return dt;
    } catch (e) {
      return null;
    }
  }

  core.bool get inDebugMode {
    return kDebugMode;
  }

  core.Future<core.bool> get writingToLogFileEnabled async {
    return (await writingToPrivateLogFileEnabled) ||
        (await writingToPublicLogFileEnabled);
  }

  core.Future<core.bool> get writingToPublicLogFileEnabled async {
    var now = core.DateTime.now();

    if (_publicLogFileDeadline == null) {
      var dl = await savedPublicLogsDeadline;
      _publicLogFileDeadline = dl?.millisecondsSinceEpoch;
    }

    if (_publicLogFileDeadline != null) {
      return now.millisecondsSinceEpoch < _publicLogFileDeadline!;
    }

    _publicLogFileDeadline ??= 0;

    return false;
  }

  core.Future<core.bool> get writingToPrivateLogFileEnabled async {
    var now = core.DateTime.now();

    if (_privateLogFileDeadline == null) {
      var dl = await savedPrivateLogsDeadline;
      _privateLogFileDeadline = dl?.millisecondsSinceEpoch;
    }

    if (_privateLogFileDeadline != null) {
      return now.millisecondsSinceEpoch < _privateLogFileDeadline!;
    }

    _privateLogFileDeadline ??= 0;

    return false;
  }

  //----------------------------------------------------------------------------

  Files? _files;
  core.bool? _intoPublic;
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
    if (await writingToPublicLogFileEnabled) {
      _createLogFileIfNotExist(true);
      _printToFile(text);
    }
    //
    else if (await writingToPrivateLogFileEnabled) {
      _createLogFileIfNotExist(false);
      _printToFile(text);
    }

    if (kDebugMode) {
      core.print('***** $text');
    }
  }

  core.String get _subDirName => 'logs${_getLogsSetStr(prefix: '/')}';

  void _createLogFileIfNotExist(core.bool intoPublic) async {
    if (_files != null && _intoPublic == intoPublic) return;
    _intoPublic = intoPublic;

    final now = core.DateTime.now();
    final fileName =
        'log_${DateOp().format(now, pattern: 'yyyy-MM-dd-HH-mm-ss')}';
    const fileExtension = 'txt';

    if (intoPublic) {
      _files = Files.public(
        fileName,
        fileExtension,
        subDirName: _subDirName,
      );
    }
    //
    else {
      _files = Files.private(
        fileName,
        fileExtension,
        subDirName: _subDirName,
      );
    }

    final dir = await currentLogsDir;
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

  core.Future<Directory?> get publicLogsDir async {
    var file = Files.public('_', '', subDirName: _subDirName);
    return file.directory;
  }

  core.Future<Directory?> get privateLogsDir async {
    var file = Files.private('_', '', subDirName: _subDirName);
    return file.directory;
  }

  core.Future<Directory?> get currentLogsDir async {
    return _files?.directory;
  }

  core.Future<File?> get currentLogFile async {
    return _files?.localFile;
  }

  ///fromPublicLogs: null will get from current logs dir
  core.Future<core.String?> getLastLogsContent({
    core.int upTo = 1,
    core.bool encrypted = true,
    core.bool? fromPublicLogs,
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

    final dir = fromPublicLogs == null
        ? (await currentLogsDir)
        : (fromPublicLogs ? (await publicLogsDir) : (await privateLogsDir));
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

class _LogsManagerImpl extends LogsManager {
  _LogsManagerImpl({required super.logsSet, required super.maxLogsFiles});
}
