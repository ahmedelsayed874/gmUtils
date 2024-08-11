import 'dart:io';

import 'package:file_picker/file_picker.dart' as filePicker;

///https://pub.dev/packages/file_picker
class FilePicker {
  Future<File?> pickFile({
    FileType? fileType,
    List<String>? allowedExtensions,
  }) async {
    filePicker.FilePickerResult? result =
        await filePicker.FilePicker.platform.pickFiles(
      type: allowedExtensions == null
          ? _convertFileType(fileType)
          : _convertFileType(FileType.custom),
      allowedExtensions: allowedExtensions,
    );

    if (result?.files.isNotEmpty == true && result!.files.single.path != null) {
      //List<File> files = result.paths.map((path) => File(path!)).toList();

      File file = File(result.files.single.path!);

      //Uint8List fileBytes = result.files.first.bytes;
      //   String fileName = result.files.first.name;

      //print(file.name);
      //   print(file.bytes);
      //   print(file.size);
      //   print(file.extension);
      //   print(file.path);
      return file;
    } else {
      return null;
    }
  }

  Future<List<File>> pickFiles({
    FileType? fileType,
    List<String>? allowedExtensions,
  }) async {
    filePicker.FilePickerResult? result =
        await filePicker.FilePicker.platform.pickFiles(
      allowMultiple: true,
      type: allowedExtensions == null
          ? _convertFileType(fileType)
          : _convertFileType(FileType.custom),
      allowedExtensions: allowedExtensions,
    );

    if (result != null) {
      List<File> files = result.paths.map((path) => File(path!)).toList();
      return files;
    } else {
      return [];
    }
  }

  Future<String?> openSaveDialog({
    String? pickerTitle,
    required String fileName,
  }) async {
    String? outputFile = await filePicker.FilePicker.platform.saveFile(
      dialogTitle: pickerTitle,
      fileName: fileName,
    );

    return outputFile;
  }

  filePicker.FileType _convertFileType(FileType? fileType) {
    if (fileType == FileType.media) return filePicker.FileType.media;
    if (fileType == FileType.image) return filePicker.FileType.image;
    if (fileType == FileType.video) return filePicker.FileType.video;
    if (fileType == FileType.audio) return filePicker.FileType.audio;
    if (fileType == FileType.custom) return filePicker.FileType.custom;

    return filePicker.FileType.any;
  }
}

enum FileType {
  any,
  media,
  image,
  video,
  audio,
  custom,
}
