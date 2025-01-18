import 'dart:ffi';
import 'dart:io';

import 'package:gmutils_flutter/data/data_source/misc/misc_datasource_production_urls.dart';
import '../../../zgmutils/data_utils/web/web_request_executors.dart';
import '../../models/attachment.dart';
import '../../models/response.dart';
import 'misc_datasource.dart';

class MiscDataSourceProduction extends MiscDataSource {

  @override
  Future<Response<Attachment>> doUploadFile({
    required File file,
    required String fileName,
    required String? attachmentType,
  }) async {
    var url = UploadFileUrl(
      file: file,
      fileName: fileName,
      attachmentType: attachmentType,
    );
    var response = await WebRequestExecutor().executePostMultiPartFile(url, 10);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<Void>> deleteFile({
    required String filePath,
  }) async {
    var url = DeleteFileUrl(filePath: filePath);
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }
}
