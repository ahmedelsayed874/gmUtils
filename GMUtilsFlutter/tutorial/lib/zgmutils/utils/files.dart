import 'dart:async';
import 'dart:io';

//path_provider: ^2.0.3
import 'package:bilingual_learning_schools_ksa/zgmutils/utils/logs.dart';
import 'package:path_provider/path_provider.dart' as filesProvider;

///Directory tempDir = await getTemporaryDirectory();
/// String tempPath = tempDir.path;
///Directory appDocDir = await getApplicationDocumentsDirectory();
///String appDocPath = appDocDir.path;
class Files {
  String _fileName;
  String _fileExtension;
  bool _saveToCacheDir;
  bool _downloadDir;
  bool _privateDir = true;

  Files.private(
    this._fileName,
    this._fileExtension, {
    bool saveToCacheDir = false,
  })  : _saveToCacheDir = saveToCacheDir,
        _privateDir = true,
        _downloadDir = false;

  Files.public(
    this._fileName,
    this._fileExtension, {
    bool downloadDir = false,
    bool saveToCacheDir = false,
  })  : _downloadDir = downloadDir,
        _saveToCacheDir = saveToCacheDir,
        _privateDir = false;

  //----------------------------------------------------------------------------

  Future<Directory> get directoryPath async {
    Directory? directory;
    if (Platform.isAndroid) {
      if (_privateDir) {
        directory = _saveToCacheDir
            ? await filesProvider.getApplicationCacheDirectory()
            : await filesProvider.getApplicationDocumentsDirectory();
      }
      //
      else {
        if (_saveToCacheDir) {
          directory =
              (await filesProvider.getExternalCacheDirectories())?.first;
        }
        //
        else {
          if (_downloadDir) {
            try {
              directory = await filesProvider.getDownloadsDirectory();
            } catch (e) {
              directory = await filesProvider.getExternalStorageDirectory();
            }
          }
          //
          else {
            directory = await filesProvider.getExternalStorageDirectory();
          }
        }
      }
    }
    //
    else {
      if (_privateDir) {
        directory = await filesProvider.getApplicationSupportDirectory();
      }
      //
      else {
        if (_downloadDir) {
          try {
            directory = await filesProvider.getDownloadsDirectory();
          } catch (e) {
            directory = await filesProvider.getApplicationDocumentsDirectory();
          }
        }
        //
        else {
          directory = await filesProvider.getApplicationDocumentsDirectory();
        }
      }
    }

    return directory ?? (await filesProvider.getTemporaryDirectory());
  }

  //----------------------------------------------------------------------------

  File? __localFile;

  Future<File> get localFile async {
    if (__localFile == null) {
      final path = (await directoryPath).path;
      __localFile = File('$path/$_fileName.$_fileExtension');
      if (!__localFile!.existsSync()) {
        __localFile!.createSync(recursive: true);
      }
    }

    return __localFile!;
  }

  //----------------------------------------------------------------------------

  Future<String> read() async {
    try {
      final file = await localFile;
      final contents = await file.readAsString();
      return contents;
    } catch (e) {
      return '';
    }
  }

  Future<List<int>> readBytes() async {
    try {
      final file = await localFile;
      final contents = await file.readAsBytes();
      return contents;
    } catch (e) {
      return [];
    }
  }

  Future<File> write(String text) async {
    final file = await localFile;
    return file.writeAsString(text, flush: true);
  }

  Future<File> writeBytes(List<int> bytes) async {
    final file = await localFile;
    return file.writeAsBytes(bytes, flush: true);
  }

  Future<Files> append(String text) async {
    final file = await localFile;
    await file.writeAsString(text, mode: FileMode.append);
    return this;
  }

  Future<Files> appendBytes(List<int> bytes) async {
    final file = await localFile;
    await file.writeAsBytes(bytes, mode: FileMode.append, flush: true);
    return this;
  }

  //============================================================

  static Future<double> computeSize(
    File file,
    FileSizeUnit unit, {
    bool actualSize = false,
  }) async {
    var fl = await file.length();
    Logs.print(() => 'Files.computeSizeInMB --> $fl bytes');

    if (unit == FileSizeUnit.Bytes) {
      return fl.toDouble();
    }
    //
    else {
      double divisor = actualSize ? 1024 : 1000;
      double length = 0;

      if (unit == FileSizeUnit.KB) {
        length = fl / divisor;
      }
      //
      else if (unit == FileSizeUnit.MB) {
        length = fl / divisor / divisor;
      }
      //
      else if (unit == FileSizeUnit.GB) {
        length = fl / divisor / divisor / divisor;
      }

      Logs.print(
        () => 'Files.computeSize --> $length $unit '
            '(actualSize: $actualSize)',
      );

      int i = length.toInt();
      int fraction = ((length - i) * 100).toInt();
      return i + (fraction / 100);
    }
  }

  static String fileName(File file) {
    var txt = '';

    var idx = file.path.lastIndexOf('/');
    if (idx > 0) {
      txt = file.path.substring(idx + 1);
    } else {
      txt = file.path;
    }

    return txt;
  }

  static String fileExtension(File file) {
    var txt = '';

    var idx = file.path.lastIndexOf('.');
    if (idx > 0) {
      txt = file.path.substring(idx + 1).toLowerCase();
    }

    return txt;
  }
}

enum FileSizeUnit { Bytes, KB, MB, GB }
