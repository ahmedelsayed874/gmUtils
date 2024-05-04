import 'dart:async';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:la_vista_city_attendance/zgmutils/utils/result.dart';

import '../../utils/logs.dart';
import 'response.dart';
import 'web_url.dart';

class WebRequestExecutor {
  Future<Response<DT>> executePost<DT>(
    PostUrl<DT> url, {
    int? cacheIntervalInSeconds,
  }) async {
    if (kDebugMode) {
      Logs.print(() => [
            '********',
            'API::Call',
            '[POST]',
            'url: ${url.uri}',
            //'\n',
            'headers: ${url.headers}',
            //'\n',
            'PostParams: ${url.params} ..... asJson: ${url.asJson}',
            //'\n',
            'postObject(body): ${url.postObject}',
            //'\n',
          ]);
    }

    _Cache<DT>? cache = _createOrGetCacheStorage(
      key: () => url.signature,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
    );
    if (cache != null) {
      return cache.response;
    }

    try {
      var response = await http.post(
        url.uri,
        headers: url.headers,
        body: url.postObject,
      );

      return _resolveResponse(url, response, cache: cache);
    } catch (e) {
      return Response.failed(
        httpCode: 0,
        error: e.toString(),
      );
    }
  }

  Future<Response<DT>> executePostMultiPartFile<DT>(
    PostMultiPartFileUrl<DT> url,
    int? cacheIntervalInSeconds,
  ) async {
    if (kDebugMode) {
      Logs.print(() => [
            '********',
            'API::Call',
            '[POST]',
            'url: ${url.uri}',
            //'\n',
            'headers: ${url.headers}',
            //'\n',
            'fileMappedKey: ${url.fileMappedKey}',
            //'\n',
            'fileBytes-length: ${url.fileBytes.length}',
            //'\n',
            'fileName: ${url.fileName}',
            //'\n',
            'fileMimeType: ${url.fileMimeType}',
            //'\n',
          ]);
    }

    _Cache<DT>? cache = _createOrGetCacheStorage(
      key: () => url.signature,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
    );
    if (cache != null) {
      return cache.response;
    }

    try {
      var request = http.MultipartRequest('POST', url.uri);
      final httpImage = http.MultipartFile.fromBytes(
        url.fileMappedKey,
        url.fileBytes,
        filename: url.fileName,
        contentType: url.fileMimeType == null
            ? null
            : MediaType.parse(url.fileMimeType!),
      );

      request.headers.addAll(url.headers);
      request.files.add(httpImage);
      if (url.formFields != null) {
        request.fields.addAll(url.formFields!);
      }

      final response1 = await request.send();

      if (response1.statusCode == 200) {
        Completer<Response<DT>> completer = Completer();

        response1.stream.listen((value) {
          final response2 = String.fromCharCodes(value);

          http.Response response = http.Response(
            response2,
            response1.statusCode,
            request: response1.request,
            headers: response1.headers,
            isRedirect: response1.isRedirect,
            persistentConnection: response1.persistentConnection,
            reasonPhrase: response1.reasonPhrase,
          );

          _resolveResponse(url, response, cache: cache).then((finalResponse) {
            //callback(finalResponse);
            completer.complete(finalResponse);
          });
        });

        return completer.future;
      } else {
        return Response.failed(
          httpCode: response1.statusCode,
          error: response1.reasonPhrase,
          responseHeader: response1.headers,
        );
      }
    } catch (e) {
      if (kDebugMode) {
        Logs.print(() =>
            'WebRequestExecutor.executePostMultiPartFile() -> Error:: $e');
      }
      return Response.failed(
        httpCode: 0,
        error: e.toString(),
      );
    }
  }

  Future<Response<DT>> executeGet<DT>(
    GetUrl<DT> url, {
    int? cacheIntervalInSeconds,
  }) async {
    if (url is PostUrl) {
      throw 'WebRequestExecutor.executeGet() applied only on GetUrl objects';
    }

    if (kDebugMode) {
      Logs.print(() => [
            '********',
            'API::Call',
            '[GET]',
            'url: ${url.uri}',
            //'\n',
            'headers: ${url.headers}',
            //'\n',
          ]);
    }

    _Cache<DT>? cache = _createOrGetCacheStorage(
      key: () => url.signature,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
    );
    if (cache != null) {
      return cache.response;
    }

    try {
      var response = await http.get(
        url.uri,
        headers: url.headers,
      );

      return _resolveResponse(url, response, cache: cache);
    } catch (e) {
      return Response.failed(
        httpCode: 0,
        error: e.toString(),
      );
    }
  }

  Future<Response<DT>> _resolveResponse<DT>(
    Url<DT> url,
    http.Response response, {
    required _Cache<DT>? cache,
  }) async {
    final code = response.statusCode;

    if (kDebugMode) {
      Logs.print(() => [
            '********',
            'API::Response',
            'url: ${url.uri}',
            '\n',
            'code: $code',
            '\n',
            'response: ${response.body}',
            '\n',
            'error: ${response.reasonPhrase}',
            '\n',
            'headers: ${response.headers}',
            '\n',
          ]);
    }

    if (code == 200) {
      try {
        final responseObj = url.encodeResponse(response.body);
        responseObj.httpCode = code;
        responseObj.responseHeader = response.headers;
        cache?.set(responseObj);
        _removeExpiredCaches();
        return responseObj;
      } catch (e) {
        if (kDebugMode) {
          Logs.print(() =>
              'WebRequestExecutor -> Error:: $e ------> Code: $code ------> response: ${response.body}');
        }
        _removeExpiredCaches();
        return Response.failed(
          error: '$e',
          httpCode: code,
          responseHeader: response.headers,
        );
      }
    } else {
      _removeExpiredCaches();
      return Response.failed(
        error: response.reasonPhrase,
        httpCode: code,
        responseHeader: response.headers,
      );
    }
  }

  //============================================================================

  Future<Response<DT>> createDummyResponse<DT>(
    String apiName,
    Result<DT> Function() data,
  ) async {
    Logs.print(() => 'API-Dummy:: $apiName');

    await Future.delayed(const Duration(seconds: 3));

    final r = Random().nextInt(100);

    Response<DT> response;

    if (r > 0 && r < 5) {
      response = Response.failed(
        error: "no connection",
        httpCode: 0,
      );
    } else if (r < 10) {
      response = Response.failed(
        error: "dummy error",
        httpCode: 400,
      );
    } else {
      var d = data();
      response = Response.success(
        //error: d.message?.en,
        data: d.result,
        //httpCode: 200,
      );
    }

    Logs.print(() => 'API-Dummy:: $apiName -> $response');
    return response;
  }
}

//==============================================================================

class _Cache<DT> {
  final int cacheIntervalInSeconds;
  int? _expireTime;
  Response<DT>? _response;

  _Cache({required this.cacheIntervalInSeconds});

  void set(Response<DT> response) {
    _expireTime = DateTime.now()
        .add(Duration(seconds: cacheIntervalInSeconds))
        .millisecondsSinceEpoch;
    _response = response;
  }

  bool get isExpired {
    if (_expireTime == null) return false;
    return DateTime.now().millisecondsSinceEpoch > _expireTime!;
  }

  Response<DT> get response => _response!;
}

Map<String, _Cache> _cacheStorage = {};

_Cache<DT>? _createOrGetCacheStorage<DT>({
  required String Function() key,
  required int? cacheIntervalInSeconds,
}) {
  _Cache? cache;

  if (cacheIntervalInSeconds != null) {
    var key2 = key();
    cache = _cacheStorage[key2];

    if (cache != null) {
      if (cache.isExpired || cache._response == null) {
        _cacheStorage.remove(key2);
        cache = null;
      } else if (cache.cacheIntervalInSeconds != cacheIntervalInSeconds) {
        _cacheStorage.remove(key2);
        cache = null;
      }
    }

    cache ??= _Cache(cacheIntervalInSeconds: cacheIntervalInSeconds);
    _cacheStorage[key2] = cache;
  }

  return cache as _Cache<DT>?;
}

void _removeExpiredCaches() async {
  List<String> keys = [];
  _cacheStorage.forEach((key, value) {
    if (value.isExpired) {
      keys.add(key);
    }
  });
  for (var k in keys) {
    _cacheStorage.remove(k);
  }
}
