import 'dart:async';
import 'dart:math';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../../utils/logs.dart';
import '../../utils/mappable.dart';
import '../../utils/pairs.dart';
import '../../utils/result.dart';
import '../../utils/string_set.dart';
import 'response.dart';
import 'web_url.dart';

class WebRequestExecutor {
  Future<http.Response?> openUrl(String link) async {
    try {
      var response = await http.get(Uri.parse(link.trim()));
      Logs.print(
        () => "WebRequestExecutor.openUrl($link) ==> response: {\n"
            ">>> code: ${response.statusCode},\n"
            ">>> body: ${response.body},\n"
            ">>> headers: ${response.headers}\n"
            "}",
      );
      return response;
    } catch (e) {
      Logs.print(
        () => "WebRequestExecutor.checkUrlValidity($link) ==> "
            "EXCEPTION::: $e",
      );
      return null;
    }
  }

  Future<Pair<bool, String>> checkUrlValidity(String link) async {
    try {
      link = link.toLowerCase().trim();

      if (link.split(' ').length > 1) {
        Logs.print(
          () => ""
              "WebRequestExecutor.checkUrlValidity($link) ==> "
              "LINK CONTAINS SPACES",
        );
        return Pair(value1: false, value2: 'Link contains spaces');
      }

      if (!link.startsWith('http://') && !link.startsWith('https://')) {
        Logs.print(
          () => ""
              "WebRequestExecutor.checkUrlValidity($link) ==> "
              "Link not start with 'http://' or 'https://'",
        );
        return Pair(
          value1: false,
          value2: "Link is not start with 'http://' or 'https://'",
        );
      }

      var response = await http.get(Uri.parse(link.trim()));
      Logs.print(
        () => ""
            "WebRequestExecutor.checkUrlValidity($link) ==> response: {\n"
            ">>> code: ${response.statusCode},\n"
            ">>> is valid?: ${response.statusCode > 100}\n"
            "}",
      );

      return Pair(
        value1: /*response.statusCode != 404 &&*/ response.statusCode > 100,
        value2: '',
      );
    } catch (e) {
      Logs.print(
        () => ""
            "WebRequestExecutor.checkUrlValidity($link) ==> "
            "EXCEPTION::: $e",
      );
      return Pair(
        value1: false,
        value2: '$e'.replaceAll("Invalid argument(s): ", ""),
      );
    }
  }

  //---------------------------------------------------------------------------

  Future<Response<DT>> execute<DT>(
    Url<DT> url, {
    int? cacheIntervalInSeconds,
  }) async {
    if (url is PostUrl) {
      return executePost(
        url as PostUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
      );
    }
    //
    else if (url is GetUrl) {
      return executeGet(
        url as GetUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
      );
    }
    //
    else {
      throw 'this method supports only PostUrl and GetUrl only';
    }
  }

  //---------------------------------------------------------------------------

  Future<Response<DT>> executePost<DT>(
    PostUrl<DT> url, {
    int? cacheIntervalInSeconds,
  }) async {
    Logs.print(() => [
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

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.post(
        url.uri,
        headers: url.headers,
        body: url.postObject,
      ),
    );
  }

  Future<Response<DT>> executePostMultiPartFile<DT>(
    PostMultiPartFileUrl<DT> url,
    int? cacheIntervalInSeconds,
  ) async {
    dynamic fileBytesLength;
    try {
      fileBytesLength = url.fileBytes.length;
    } catch (e) {
      fileBytesLength = e;
    }

    Logs.print(() => [
          'API::Call',
          '[POST / MULTIPART]',
          'url: ${url.uri}',
          //'\n',
          'headers: ${url.headers}',
          //'\n',
          'fileMappedKey: ${url.fileMappedKey}',
          //'\n',
          'fileBytes-length: $fileBytesLength',
          //'\n',
          'fileName: ${url.fileName}',
          //'\n',
          'fileMimeType: ${url.fileMimeType}',
          //'\n',
        ]);

    if (fileBytesLength is int) {
      if (fileBytesLength == 0) {
        Logs.print(() => [
              'API::Response',
              'url: ${url.uri}',
              '\n',
              '<[File size is zero]>',
            ]);
        return Response.failed(
          url: url,
          error: 'File size is zero',
          httpCode: -1,
        );
      }
    }
    //
    else {
      Logs.print(() => [
            'API::Response',
            'url: ${url.uri}',
            '\n',
            '<[File size is unknown]>',
            '\n',
            '<[ERROR: $fileBytesLength]>',
          ]);
      return Response.failed(
        url: url,
        error: 'File size is unknown',
        httpCode: -1,
      );
    }

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async {
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
          Completer<http.Response> completer = Completer();

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

            completer.complete(response);
          });

          return completer.future;
        } else {
          return http.Response(
            response1.reasonPhrase ?? '',
            response1.statusCode,
            headers: response1.headers,
            request: response1.request,
            reasonPhrase: response1.reasonPhrase,
          );
        }
      },
    );
  }

  Future<Response<DT>> executeGet<DT>(
    GetUrl<DT> url, {
    int? cacheIntervalInSeconds,
  }) async {
    if (url is PostUrl) {
      throw 'WebRequestExecutor.executeGet() applied only on GetUrl objects';
    }

    Logs.print(() => [
          'API::Call',
          '[GET]',
          'url: ${url.uri}',
          //'\n',
          'headers: ${url.headers}',
          //'\n',
        ]);

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.get(
        url.uri,
        headers: url.headers,
      ),
    );
  }

  //---------------------------------------------------------------------------

  Future<Response<DT>> _executeWithTries<DT>({
    required Url<DT> url,
    required int? cacheIntervalInSeconds,
    required Future<http.Response> Function() run,
  }) async {
    _Cache<DT>? cache = _createOrGetCacheStorage(
      key: () => url.signature,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
    );
    if (cache != null) {
      Logs.print(() => [
            'API::Response',
            'url: ${url.uri}',
            '\n',
            '<[RESULT WILL RETURN FROM CACHE]>',
            '\n',
            'cachedResponse: ${cache.response}',
            '\n',
          ]);

      return cache.response;
    }

    const supposedTries = 3;

    int tries = 0;
    dynamic exception;

    while (tries < supposedTries) {
      tries++;

      int code = 0;
      try {
        exception = null;
        var response = await run();
        code = response.statusCode;

        if (code != 0) {
          return _resolveResponse(
            url,
            response,
            cache: cache,
            tries: tries,
          );
        }
      } catch (e) {
        exception = e;
      }

      if (tries < supposedTries) {
        if (Logs.inDebugMode || await Logs.writingToLogFileEnabled) {
          Logs.print(() => [
                'API::Response(OF_TRY_NUMBER: $tries)',
                'url: ${url.uri}',
                '\n',
                'code: $code',
                '\n',
                'exception: $exception',
                '\n',
                'A NEW TRY WILL EXECUTE AFTER 1500 MS',
                '\n',
              ]);
        }

        await Future.delayed(const Duration(milliseconds: 1500));
      }
    }

    var error = '';
    if (exception != null) {
      try {
        error = '$exception';
      } catch (e) {}
    } else {
      error = 'Can\'t connect the server';
    }

    return _resolveResponse(
      url,
      http.Response(error, 100),
      cache: cache,
      tries: tries,
    );
  }

  Future<Response<DT>> _resolveResponse<DT>(
    Url<DT> url,
    http.Response response, {
    required _Cache<DT>? cache,
    required int tries,
  }) async {
    final code = response.statusCode;

    Logs.print(() => [
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
          'numberOfTries: $tries',
          '\n',
        ]);

    if (code == 200) {
      try {
        final responseObj = url.encodeResponse(response.body);
        responseObj.httpCode = code;
        responseObj.responseHeader = response.headers;
        cache?.set(responseObj);
        _removeExpiredCaches();
        return responseObj;
      } catch (e) {
        Logs.print(() =>
            'WebRequestExecutor -> Error:: $e ------> Code: $code ------> response: ${response.body}');

        _removeExpiredCaches();
        return Response.failed(
          url: url,
          error: '$e',
          httpCode: code,
          responseHeader: response.headers,
        );
      }
    }
    //
    else {
      _removeExpiredCaches();
      return Response.failed(
        url: url,
        error: response.reasonPhrase ?? response.body,
        httpCode: code,
        responseHeader: response.headers,
      );
    }
  }

  //============================================================================

  Future<Response<Result<DT>>> createDummyResponse<DT>({
    required String apiName,
    required DummyResponseBuilder<DT> Function() responseBuilder,
    int delayInSeconds = 1,
  }) async {
    Logs.print(() => 'API-Dummy-Request:: http://$apiName');

    await Future.delayed(Duration(seconds: delayInSeconds));

    Response<Result<DT>> response;

    final r = Random().nextInt(100);
    if (r > 0 && r < 5) {
      response = Response.failed(
        url: null,
        error: "no connection",
        httpCode: 0,
      );
    }
    //
    else if (r < 10) {
      response = Response.failed(
        url: null,
        error: "Supposed error on server side",
        httpCode: 400,
      );
    }
    //
    else {
      Result<DT>? data;
      String? error;

      var resBldr = responseBuilder();
      if (resBldr.result == null && resBldr.message == null) {
        data = Result(null);
      }
      //
      else if (resBldr.result != null) {
        if (resBldr.result is List) {
          assert(
            resBldr.dataMappable != null,
            'set mapper class if data is exist',
          );

          List<Map<String, dynamic>>? map;

          try {
            map = resBldr.dataMappable!.toMapList(
              resBldr.result as List,
            );
          } catch (e) {
            error = 'Error in toMapList() in '
                '${resBldr.dataMappable!.runtimeType} class for '
                'data class ${resBldr.result.runtimeType}.... details: $e';
          }

          if (error == null) {
            try {
              data = Result(resBldr.dataMappable!.fromMapList(map) as DT?);
            } catch (e) {
              error = 'Error in fromMapList() in '
                  '${resBldr.dataMappable!.runtimeType} class for '
                  'data class ${resBldr.result.runtimeType}.... details: $e';
            }
          }
        }
        //
        else if (resBldr.result is Map) {
          assert(
            resBldr.dataMappable != null,
            'set mapper class if data is exist',
          );

          Map<String, dynamic>? map;
          try {
            map = resBldr.dataMappable!.toMap(resBldr.result);
          } catch (e) {
            error = 'Error in toMap() in '
                '${resBldr.dataMappable!.runtimeType} class for '
                'data class ${resBldr.result.runtimeType}.... details: $e';
          }
          if (error == null) {
            try {
              data = resBldr.dataMappable!.fromMap(map!);
            } catch (e) {
              error = 'Error in fromMap() in '
                  '${resBldr.dataMappable!.runtimeType} class for '
                  'data class ${resBldr.result.runtimeType}.... details: $e';
            }
          }
        }
        //
        else {
          data = Result(resBldr.result);
        }
      }
      //
      else {
        error = resBldr.message?.en;
      }

      if (data != null) {
        response = Response.success(
          url: null,
          data: data,
        );
      }
      //
      else {
        response = Response.failed(
          url: null,
          error: error,
          httpCode: 400,
        );
      }
    }

    Logs.print(() => 'API-Dummy-Response:: http://$apiName -> $response');

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

  if (cache == null) {
    return null;
  } else {
    try {
      return cache as _Cache<DT>?;
    } catch (e) {
      return null;
    }
  }
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

//==============================================================================

class DummyResponseBuilder<D> extends Result<D> {
  final Mappable? dataMappable;

  DummyResponseBuilder({
    required this.dataMappable,
    required D? data,
    required StringSet? error,
    super.extra,
  }) : super(data, message: error);
}
