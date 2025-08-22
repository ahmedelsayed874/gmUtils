import 'dart:async';
import 'dart:io';

import 'package:path_provider/path_provider.dart' as files_provider;

//path_provider: ^2.0.3
import 'logs.dart';

///Directory tempDir = await getTemporaryDirectory();
/// String tempPath = tempDir.path;
///Directory appDocDir = await getApplicationDocumentsDirectory();
///String appDocPath = appDocDir.path;
class Files {
  final String fileName;
  final String fileExtension;
  final bool saveToCacheDir;
  final bool downloadDir;
  final bool privateDir;
  final String? subDirName;

  Files.private(
    this.fileName,
    this.fileExtension, {
    this.saveToCacheDir = false,
    this.subDirName,
  })  : privateDir = true,
        downloadDir = false;

  Files.public(
    this.fileName,
    this.fileExtension, {
    this.downloadDir = false,
    this.saveToCacheDir = false,
    this.subDirName,
  }) : privateDir = false;

  //----------------------------------------------------------------------------

  Future<Directory> get directoryPath async {
    Directory? directory;
    if (Platform.isAndroid) {
      if (privateDir) {
        directory = saveToCacheDir
            ? await files_provider.getApplicationCacheDirectory()
            : await files_provider.getApplicationDocumentsDirectory();
      }
      //
      else {
        if (saveToCacheDir) {
          directory =
              (await files_provider.getExternalCacheDirectories())?.first;
        }
        //
        else {
          if (downloadDir) {
            try {
              directory = await files_provider.getDownloadsDirectory();
            } catch (e) {
              directory = await files_provider.getExternalStorageDirectory();
            }
          }
          //
          else {
            directory = await files_provider.getExternalStorageDirectory();
          }
        }
      }
    }
    //
    else {
      if (privateDir) {
        directory = await files_provider.getApplicationSupportDirectory();
      }
      //
      else {
        if (downloadDir) {
          try {
            directory = await files_provider.getDownloadsDirectory();
          } catch (e) {
            directory = await files_provider.getApplicationDocumentsDirectory();
          }
        }
        //
        else {
          directory = await files_provider.getApplicationDocumentsDirectory();
        }
      }
    }

    directory ??= (await files_provider.getTemporaryDirectory());

    if (subDirName?.isNotEmpty == true) {
      directory = Directory('${directory.path}/$subDirName');
      if (!directory.existsSync()) {
        directory = await directory.create(recursive: true);
      }
    }

    return directory;
  }

  //----------------------------------------------------------------------------

  File? __localFile;

  Future<File> get localFile async {
    if (__localFile == null) {
      final path = (await directoryPath).path;
      __localFile = File('$path/$fileName.$fileExtension');
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

  static String extractFileName(File file) {
    var txt = '';

    var idx = file.path.lastIndexOf('/');
    if (idx > 0) {
      txt = file.path.substring(idx + 1);
    } else {
      txt = file.path;
    }

    return txt;
  }

  static String extractFileExtension(File file) {
    var txt = '';

    var idx = file.path.lastIndexOf('.');
    if (idx > 0) {
      txt = file.path.substring(idx + 1).toLowerCase();
    }

    return txt;
  }
}

enum FileSizeUnit { Bytes, KB, MB, GB }
