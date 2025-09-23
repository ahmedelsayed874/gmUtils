import 'package:gmutils_flutter/main.dart' as main;
import 'package:gmutils_flutter/zgmutils/data_utils/utils/mappable.dart';

class Attachment {
  static const String attachmentTypeImage = 'Image';
  static const String attachmentTypeVideo = 'Video';
  static const String attachmentTypeAudio = 'Audio';
  static const String attachmentTypePDF = 'PDF';
  static const String attachmentTypeWord = 'Word';
  static const String attachmentTypeExcel = 'Excel';
  static const String attachmentTypeOther = 'Other';

  String _attachmentFilePath;
  String attachmentFileName;
  String? attachmentType;

  Attachment({
    required String attachmentFilePath,
    required this.attachmentFileName,
    required this.attachmentType,
  }) : _attachmentFilePath = attachmentFilePath.toLowerCase();

  String getAttachmentFilePath({required bool includeDomain}) {
    if (includeDomain) {
      if (_attachmentFilePath.startsWith('http://') ||
          _attachmentFilePath.startsWith('https://')) {
        return _attachmentFilePath;
      } else {
        return main.serverUrl + _attachmentFilePath;
      }
    } else {
      if (_attachmentFilePath.startsWith('http://') ||
          _attachmentFilePath.startsWith('https://')) {
        return _attachmentFilePath.replaceFirst(main.serverUrl, '');
      } else {
        return _attachmentFilePath;
      }
    }
  }

  static String fileTypeForFileExtension(String fileExtension) {
    if (fileExtension == 'png' ||
        fileExtension == 'jpg' ||
        fileExtension == 'jpeg') {
      return Attachment.attachmentTypeImage;
    }
    //
    else if (fileExtension == 'mp4') {
      return Attachment.attachmentTypeVideo;
    }
    //
    else if (fileExtension == 'mp3' ||
        fileExtension == 'm4a' ||
        fileExtension == 'wav') {
      return Attachment.attachmentTypeAudio;
    }
    //
    else if (fileExtension == 'pdf') {
      return Attachment.attachmentTypePDF;
    }
    //
    else if (fileExtension == 'doc' || fileExtension == 'docx') {
      return Attachment.attachmentTypeWord;
    }
    //
    else if (fileExtension == 'xls' || fileExtension == 'xlsx') {
      return Attachment.attachmentTypeExcel;
    }
    //
    else {
      return Attachment.attachmentTypeOther;
    }
  }

  @override
  String toString() {
    return 'Attachment{attachmentFilePath: $_attachmentFilePath, attachmentFileName: $attachmentFileName, attachmentType: $attachmentType}';
  }
}

class AttachmentMapper extends Mappable<Attachment> {
  @override
  Attachment fromMap(Map<String, dynamic> values) {
    return Attachment(
      attachmentFilePath: values['attachmentFilePath'],
      attachmentFileName: values['attachmentFileName'],
      attachmentType: values['attachmentType'],
    );
  }

  @override
  Map<String, dynamic> toMap(Attachment object) {
    return {
      'attachmentFilePath': object._attachmentFilePath,
      'attachmentFileName': object.attachmentFileName,
      'attachmentType': object.attachmentType,
    };
  }
}
