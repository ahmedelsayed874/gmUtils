import 'dart:ffi';
import 'dart:io';
import 'dart:math';

import 'package:gmutils_flutter/zgmutils/data_utils/web/web_request_executors.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/utils/mappable.dart';
import 'package:gmutils_flutter/zgmutils/utils/collections/string_set.dart';

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
      responseBuilder: () => DummyResponseBuilder(
        dataMappable: AttachmentMapper(),
        data: (Random().nextInt(100) > 97)
            ? null
            : Attachment(
                attachmentFilePath: 'files/$fileName',
                attachmentFileName: fileName,
                attachmentType: attachmentType,
              ),
        error: StringSet('Any Error', 'أي خطأ'),
      ),
    );

    return Response.fromDummyResponse(response);
  }

  @override
  Future<Response<Void>> deleteFile({
    required String filePath,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'deleteFile(filePath: $filePath)',
      responseBuilder: () {
        var e = (Random().nextInt(100) > 97);

        return DummyResponseBuilder(
          dataMappable: VoidMapper(),
          data: null,
          error: e ? StringSet('Any Error', 'أي خطأ') : null,
        );
      },
    );

    return Response.fromDummyResponse(response);
  }
}
