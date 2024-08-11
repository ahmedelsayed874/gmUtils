import 'dart:convert';
import 'dart:io';

import '../../utils/mappable.dart';
import 'response.dart';

///RDT: response data type
abstract class Url<RDT> {
  final String domain;
  final String fragments;
  final String endPoint;
  final Map<String, String> headers = {};
  final Mappable<RDT>? dataMapper;
  final Response<RDT> Function(String response)? responseEncoder;

  Url({
    required this.domain,
    required this.fragments,
    required this.endPoint,
    required Map<String, String>? headers,
    required this.dataMapper,
    required this.responseEncoder,
  }) {
    if (headers != null) {
      this.headers.addAll(headers);
    }
  }

  void addHeader(String key, String value) {
    headers[key] = value;
  }

  void addHeaderIfNotExist(String key, String value) {
    if (headers.containsKey(key)) return;
    addHeader(key, value);
  }

  String get uriAsString => '$domain$fragments$endPoint';

  Uri get uri => Uri.parse(Uri.encodeFull(uriAsString));

  Response<RDT> encodeResponse(String response) {
    if (responseEncoder != null) {
      var response2 = responseEncoder!(response);
      // response2.rawResponse = response;
      return response2;
    } else if (dataMapper == null) {
      return Response.failed(
        error: 'Internal error: either "Url.responseEncoder" or '
            '"Url.dataMapper" should has value',
        httpCode: 0,
      );
    } else {
      var dataMap = jsonDecode(response);
      final responseObj = ResponseMapper(dataMapper!).fromMap(dataMap);
      // responseObj.rawResponse = response;
      return responseObj;
    }
  }

  String get signature {
    String s = '';
    s += domain.hashCode.toString();
    s += fragments.hashCode.toString();
    s += endPoint.hashCode.toString();
    headers.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });
    return s;
  }
}

class GetUrl<RDT> extends Url<RDT> {
  final Map<String, String>? queries;

  GetUrl({
    required String domain,
    required String fragments,
    required String endPoint,
    Map<String, String>? headers,
    required Mappable<RDT>? dataMapper,
    required this.queries,
    Response<RDT> Function(String response)? responseEncoder,
  }) : super(
          domain: domain,
          fragments: fragments,
          endPoint: endPoint,
          headers: headers,
          dataMapper: dataMapper,
          responseEncoder: responseEncoder,
        );

  String get uriAsString {
    var url = super.uriAsString;

    if (queries?.isNotEmpty == true) {
      String q = '';
      queries!.forEach((key, value) {
        if (q.isNotEmpty) q += '&';
        q += '$key=$value';
      });
      url += '?$q';
    }

    return url;
  }

  @override
  String get signature {
    var s = super.signature;
    queries?.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });
    return s;
  }
}

class PostUrl<RDT> extends GetUrl<RDT> {
  final Map<String, dynamic>? params;
  final bool asJson;

  //final String? body;

  PostUrl({
    required String domain,
    required String fragments,
    required String endPoint,
    Map<String, String>? headers,
    required Mappable<RDT>? dataMapper,
    Map<String, String>? queries,
    this.params,
    this.asJson = true,
    Response<RDT> Function(String response)? responseEncoder,
  }) : super(
          domain: domain,
          fragments: fragments,
          endPoint: endPoint,
          headers: headers,
          dataMapper: dataMapper,
          queries: queries,
          responseEncoder: responseEncoder,
        ) {
    //assert((params != null && body == null) || (params == null && body != null));

    if (params != null) {
      if (asJson) {
        addHeaderIfNotExist('Content-Type', 'application/json');
        addHeaderIfNotExist('Accept', 'application/json');
      } else {
        addHeaderIfNotExist(
            'Content-Type', 'application/x-www-form-urlencoded');
      }
    }
  }

  Object get postObject {
    if (params?.isNotEmpty == true) {
      if (asJson) {
        final json = jsonEncode(params);
        //Logs.print(() => 'PostUrl -> json: $json');
        return json;
      } else {
        String q = '';
        params!.forEach((key, value) {
          if (q.isNotEmpty) q += '&';
          q += '$key=$value';
        });
        //Logs.print(() => 'PostUrl -> parameters: $q');
        return q;
      }
    } else {
      return '';
    }
  }

  @override
  String get signature {
    var s = super.signature;
    params?.forEach((key, value) {
      s += key.hashCode.toString();
      s += '$value'.hashCode.toString();
    });
    s += '$asJson'.hashCode.toString();
    return s;
  }
}

class PostMultiPartFileUrl<RDT> extends GetUrl<RDT> {
  String fileMappedKey;
  File file;
  String? fileMimeType;
  Map<String, String>? formFields;

  PostMultiPartFileUrl({
    required String domain,
    required String fragments,
    required String endPoint,
    required Map<String, String>? headers,
    required Mappable<RDT> dataMapper,
    Map<String, String>? queries,
    this.formFields,
    required this.fileMappedKey,
    required this.file,
    this.fileMimeType,
    Response<RDT> Function(String response)? responseEncoder,
  }) : super(
          domain: domain,
          fragments: fragments,
          endPoint: endPoint,
          headers: headers,
          dataMapper: dataMapper,
          queries: queries,
          responseEncoder: responseEncoder,
        );

  List<int> get fileBytes => file.readAsBytesSync();

  String get fileName {
    int i = file.path.lastIndexOf('/');
    if (i < 0) i = file.path.lastIndexOf('\\');

    if (i >= 0) {
      return file.path.substring(i + 1);
    } else {
      return 'file';
    }
  }

  @override
  String get signature {
    var s = super.signature;
    s += fileMappedKey.hashCode.toString();
    s += file.path.hashCode.toString();
    s += file.lengthSync().toString();
    s += file.lastModifiedSync().millisecondsSinceEpoch.toString();
    if (fileMimeType != null) {
      s += fileMimeType.hashCode.toString();
    }
    formFields?.forEach((key, value) {
      s += key.hashCode.toString();
      s += '$value'.hashCode.toString();
    });
    return s;
  }
}
