import 'dart:ffi';
import 'dart:io';
import 'dart:math';

import 'package:gmutils_flutter/zgmutils/data_utils/web/web_request_executors.dart';
import 'package:gmutils_flutter/zgmutils/utils/pairs.dart';
import 'package:gmutils_flutter/zgmutils/utils/result.dart';
import 'package:gmutils_flutter/zgmutils/utils/string_set.dart';

import '../../models/attachment.dart';
import '../../models/response.dart';
import 'misc_datasource.dart';

class MiscDataSourceMockup extends MiscDataSource {
  @override
  Future<Response<Attachment>> doUploadFile({
    required File file,
    required String fileName,
    required String? attachmentType,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'uploadFile(file: ${file.path}, fileName: $fileName, attachmentType: $attachmentType)',
      onSuccessResponse: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                Attachment(
                  attachmentFilePath: 'files/$fileName',
                  attachmentFileName: fileName,
                  attachmentType: attachmentType,
                ),
              ),
              value2: AttachmentMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

  @override
  Future<Response<Void>> deleteFile({
    required String filePath,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName:
      'deleteFile(filePath: $filePath)',
      onSuccessResponse: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(null),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);
  }
}
