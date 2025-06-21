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
  final Map<String, String>? queries;
  final Mappable<RDT>? responseMapper;
  final Response<RDT> Function(String response)? responseEncoder;

  Url({
    required this.domain,
    required this.fragments,
    required this.endPoint,
    required Map<String, String>? headers,
    required this.queries,
    //
    required this.responseMapper,
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

  String get uriAsString {
    var url = '$domain$fragments$endPoint';

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

  Uri get uri => Uri.parse(Uri.encodeFull(uriAsString));

  Response<RDT> encodeResponse(String response) {
    if (responseEncoder != null) {
      var response2 = responseEncoder!(response);
      // response2.rawResponse = response;
      return response2;
    }
    //
    else if (responseMapper == null) {
      return Response.failed(
        error: 'Internal error: either "Url.responseEncoder" or '
            '"Url.responseMapper" should has value',
        httpCode: 0,
      );
    }
    //
    else {
      var dataMap = jsonDecode(response);
      final responseObj = ResponseMapper(responseMapper!).fromMap(dataMap);
      // responseObj.rawResponse = response;
      return responseObj;
    }
  }

  String get signature {
    String s = '';
    s += domain.hashCode.toString();
    s += fragments.hashCode.toString();
    s += endPoint.hashCode.toString();

    queries?.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });

    headers.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });

    return s;
  }
}

class GetUrl<RDT> extends Url<RDT> {
  GetUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    required super.responseMapper,
    super.responseEncoder,
  });
}

class PostUrl<RDT> extends Url<RDT> {
  final Map<String, dynamic>? params;
  final bool asJson;

  //final String? body;

  PostUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    this.params,
    this.asJson = true,
    //
    required super.responseMapper,
    super.responseEncoder,
  }) {
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

class PostMultiPartFileUrl<RDT> extends Url<RDT> {
  String fileMappedKey;
  File file;
  String? fileMimeType;
  Map<String, String>? formFields;

  PostMultiPartFileUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    required super.headers,
    super.queries,
    //
    this.formFields,
    required this.fileMappedKey,
    required this.file,
    this.fileMimeType,
    //
    required Mappable<RDT> super.responseMapper,
    super.responseEncoder,
  });

  List<int> get fileBytes => file.readAsBytesSync();

  String get fileName {
    int i = file.path.lastIndexOf('/');
    if (i < 0) i = file.path.lastIndexOf('\\');

    if (i >= 0) {
      return file.path.substring(i + 1);
    } else {
      var d = DateTime.now();
      return 'file'
          '${d.year}'
          '${_compansate(d.month)}'
          '${_compansate(d.day)}'
          '${_compansate(d.hour)}'
          '${_compansate(d.minute)}'
          '${_compansate(d.second)}'
          '.${_compansate(d.millisecond)}'
          '';
    }
  }

  String _compansate(int n) {
    return n < 10 ? '0$n' : '$n';
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
