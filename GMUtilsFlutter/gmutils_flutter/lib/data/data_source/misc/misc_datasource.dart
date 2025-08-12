import 'dart:ffi';
import 'dart:io';

import 'package:gmutils_flutter/data/models/response.dart';
import 'package:gmutils_flutter/main.dart';
import 'package:gmutils_flutter/zgmutils/utils/files.dart';

import '../../models/attachment.dart';
import 'misc_datasource_mockup.dart';
import 'misc_datasource_production.dart';

abstract class MiscDataSource {
  static MiscDataSource get instance =>
      useProductionData ? MiscDataSourceProduction() : MiscDataSourceMockup();

  //----------------------------------------------------------------------------

  Future<Response<Attachment>> uploadFile({
    required File file,
    required String? fileType,
  }) {
    var fileName = Files.extractFileName(file);
    var fileExtension = Files.extractFileExtension(file);
    String? attachmentType =
        fileType ?? Attachment.fileTypeForFileExtension(fileExtension);

    return doUploadFile(
      file: file,
      fileName: fileName,
      attachmentType: attachmentType,
    );
  }

  Future<Response<Attachment>> doUploadFile({
    required File file,
    required String fileName,
    required String? attachmentType,
  });

  //----------------------------------------------------------------------------

  //todo use this api
  Future<Response<Void>> deleteFile({
    required String filePath,
  });

}
