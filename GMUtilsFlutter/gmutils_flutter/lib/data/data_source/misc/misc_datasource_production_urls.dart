import 'dart:ffi';
import 'dart:io';

import 'package:gmutils_flutter/data/data_source/requests_helper.dart';
import 'package:gmutils_flutter/data/models/attachment.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/web_url.dart';

import '../../models/response.dart';

class UploadFileUrl extends PostMultiPartFileUrl<Response<Attachment>> {
  UploadFileUrl({
    required File file,
    required String fileName,
    required String? attachmentType,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'UploadFile',
          dataMapper: ResponseMapper(dataMapper: AttachmentMapper()),
          queries: null,
          formFields: {
            'fileName': fileName,
            if (attachmentType != null) 'attachmentType': attachmentType,
          },
          fileMappedKey: 'file',
          file: file,
          fileMimeType: null,
        );
}

class DeleteFileUrl extends PostUrl<Response<Void>> {
  DeleteFileUrl({
    required String filePath,
  }) : super(
    domain: RequestsHelper.instance.serverUrl,
    fragments: RequestsHelper.instance.apisPath,
    headers: RequestsHelper.instance.headers,
    endPoint: 'DeleteFile',
    dataMapper: ResponseMapper(dataMapper: null),
    queries: null,
    params: {
      'FilePath': filePath,
    },
  );
}
