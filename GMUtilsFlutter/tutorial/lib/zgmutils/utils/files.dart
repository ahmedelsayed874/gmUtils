import 'dart:async';
import 'dart:io';

//path_provider: ^2.0.3
import 'package:path_provider/path_provider.dart' as filesProvider;

///Directory tempDir = await getTemporaryDirectory();
/// String tempPath = tempDir.path;
///Directory appDocDir = await getApplicationDocumentsDirectory();
///String appDocPath = appDocDir.path;
class Files {
  String _fileName;
  String _fileExtension;
  bool _saveToCacheDir;
  bool _privateDir = true;

  Files.private(
    this._fileName,
    this._fileExtension, [
    this._saveToCacheDir = false,
  ]) : _privateDir = true;

  Files.public(
    this._fileName,
    this._fileExtension, [
    this._saveToCacheDir = false,
  ]) : _privateDir = false;

  //----------------------------------------------------------------------------

  Future<String> get _localPath async {
    Directory? directory = _privateDir
        ? (_saveToCacheDir
            ? await filesProvider.getApplicationCacheDirectory()
            : await filesProvider.getApplicationDocumentsDirectory())
        : (_saveToCacheDir
            ? (await filesProvider.getExternalCacheDirectories())?.first
            : await filesProvider.getExternalStorageDirectory());

    return directory?.path ??
        (await filesProvider.getTemporaryDirectory()).path;
  }

  //----------------------------------------------------------------------------

  File? __localFile;

  Future<File> get localFile async {
    if (__localFile == null) {
      final path = await _localPath;
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

  static Future<double> computeSizeInMB(File file) async {
    var fl = await file.length();
    double length = fl / 1024 / 1024 + 0.07;
    int i = length.toInt();
    int fraction = ((length - i) * 100).toInt();
    return i + (fraction / 100);
  }
}
