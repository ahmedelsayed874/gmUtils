import 'dart:async';
import 'dart:math';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../../utils/collections/pairs.dart';
import '../../utils/collections/string_set.dart';
import '../../utils/logs.dart';
import '../utils/mappable.dart';
import '../utils/result.dart';
import 'response.dart';
import 'web_url.dart';

class WebRequestExecutor {
  Future<http.Response?> openUrl(String link, {String? logsName}) async {
    try {
      var response = await http.get(Uri.parse(link.trim()));
      Logs.get(logsName).print(
        () => "WebRequestExecutor.openUrl($link) ==> response: {\n"
            ">>> code: ${response.statusCode},\n"
            ">>> body: ${response.body},\n"
            ">>> headers: ${response.headers}\n"
            "}",
      );
      return response;
    } catch (e) {
      Logs.get(logsName).print(
        () => "WebRequestExecutor.checkUrlValidity($link) ==> "
            "EXCEPTION::: $e",
      );
      return null;
    }
  }

  Future<Pair<bool, String>> checkUrlValidity(
    String link, {
    String? logsName,
  }) async {
    try {
      link = link.toLowerCase().trim();

      if (link.split(' ').length > 1) {
        Logs.get(logsName).print(
          () => ""
              "WebRequestExecutor.checkUrlValidity($link) ==> "
              "LINK CONTAINS SPACES",
        );
        return Pair(value1: false, value2: 'Link contains spaces');
      }

      if (!link.startsWith('http://') && !link.startsWith('https://')) {
        Logs.get(logsName).print(
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
      Logs.get(logsName).print(
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
      Logs.get(logsName).print(
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
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    if (url is GetUrl) {
      return executeGet(
        url as GetUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else if (url is PostUrl) {
      return executePost(
        url as PostUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else if (url is PatchUrl) {
      return executePatch(
        url as PatchUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else if (url is PutUrl) {
      return executePut(
        url as PutUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else if (url is DeleteUrl) {
      return executeDelete(
        url as DeleteUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else if (url is MultiPartRequestUrl) {
      return executeMultiPartRequest(
        url as MultiPartRequestUrl<DT>,
        cacheIntervalInSeconds: cacheIntervalInSeconds,
        onEncodeResponseFailed: onEncodeResponseFailed,
      );
    }
    //
    else {
      throw 'provided Url is not supported';
    }
  }

  //---------------------------------------------------------------------------

  Future<Response<DT>> executeGet<DT>(
    GetUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Call',
          '[GET]',
          'url: ${url.obscuredUri}',
          //'\n',
          'headers: ${url.obscuredHeaders}',
          //'\n',
        ]);

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.get(
        url.uri,
        headers: url.headers,
      ),
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> executePost<DT>(
    PostUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Call',
          '[POST]',
          'url: ${url.obscuredUri}',
          //'\n',
          'headers: ${url.obscuredHeaders}',
          //'\n',
          'PostParams: ${url.obscuredParams} ..... asJson: ${url.asJson}',
          //'\n',
          'postObject(body): ${url.obscuredPostObject}',
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
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> executeMultiPartRequest<DT>(
    MultiPartRequestUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() {
      final info = [
        'API::Call',
        '[${url.method.name} / MULTIPART]',
        'url: ${url.obscuredUri}',
        'headers: ${url.obscuredHeaders}',
        'formFields: ${url.obscuredFormFields}',
      ];

      for (var file in url.files) {
        info.add('>>> ');
        info.add('fileMappedKey: ${file.mappedKey}');
        info.add('fileBytes-length: ${file.fileBytesLength}');
        info.add('fileName: ${file.fileName}');
        info.add('fileMimeType: ${file.mimeType}');
      }

      return info;
    });

    for (var file in url.files) {
      if (file.fileBytesLength is int) {
        if (file.fileBytesLength == 0) {
          Logs.get(url.logsName).print(() => [
                'API::Response',
                'url: ${url.uri}',
                '\n',
                '<[File (${file.fileName}) size is zero]>',
              ]);
          return Response.failed(
            url: url,
            error: 'File size is zero',
            rawResponse: '"error":"File size is zero"',
            httpCode: -1,
          );
        }
      }
      //
      else {
        Logs.get(url.logsName).print(() => [
              'API::Response',
              'url: ${url.uri}',
              '\n',
              '<[File (${file.fileName}) size is unknown]>',
              '\n',
              '<[ERROR: ${file.fileBytesLength}]>',
            ]);
        return Response.failed(
          url: url,
          error: 'File size is unknown',
          rawResponse: '"error":"File size is unknown"',
          httpCode: -1,
        );
      }
    }

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async {
        var request = http.MultipartRequest(url.method.name, url.uri);
        request.headers.addAll(url.headers);

        if (url.files.isNotEmpty) {
          for (var file in url.files) {
            final httpImage = http.MultipartFile.fromBytes(
              file.mappedKey,
              file.fileBytes,
              filename: file.fileName,
              contentType: file.mimeType == null
                  ? null
                  : MediaType.parse(file.mimeType!),
            );

            /*final httpImage = await http.MultipartFile.fromPath(
              file.fileMappedKey,
              file.file.path,
              filename: file.fileName,
              contentType: file.fileMimeType == null
                  ? null
                  : MediaType.parse(file.fileMimeType!),
            );*/

            request.files.add(httpImage);
          }
        }

        if (url.formFields != null) {
          request.fields.addAll(url.formFields!);
        }

        final response1 = await request.send();

        /*if (response1.statusCode == 200) {
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
        }
        //
        else {
          String? errorBody;
          try {
            errorBody = await response1.stream.bytesToString();
          } catch (_) {}

          return http.Response(
            errorBody ?? response1.reasonPhrase ?? '',
            response1.statusCode,
            headers: response1.headers,
            request: response1.request,
            reasonPhrase: response1.reasonPhrase,
          );
        }*/

        String? body;
        try {
          body = await response1.stream.bytesToString();
        } catch (_) {}

        return http.Response(
          body ?? response1.reasonPhrase ?? '',
          response1.statusCode,
          headers: response1.headers,
          request: response1.request,
          isRedirect: response1.isRedirect,
          persistentConnection: response1.persistentConnection,
          reasonPhrase: response1.reasonPhrase,
        );
      },
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> executePatch<DT>(
    PatchUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Call',
          '[PATCH]',
          'url: ${url.obscuredUri}',
          //'\n',
          'headers: ${url.obscuredHeaders}',
          //'\n',
          'PatchParams: ${url.obscuredParams} ..... asJson: ${url.asJson}',
          //'\n',
          'patchObject(body): ${url.obscuredPostObject}',
          //'\n',
        ]);

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.patch(
        url.uri,
        headers: url.headers,
        body: url.postObject,
      ),
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> executePut<DT>(
    PutUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Call',
          '[PUT]',
          'url: ${url.obscuredUri}',
          //'\n',
          'headers: ${url.obscuredHeaders}',
          //'\n',
          'PutParams: ${url.obscuredParams} ..... asJson: ${url.asJson}',
          //'\n',
          'putObject(body): ${url.obscuredPostObject}',
          //'\n',
        ]);

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.put(
        url.uri,
        headers: url.headers,
        body: url.postObject,
      ),
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> executeDelete<DT>(
    DeleteUrl<DT> url, {
    int? cacheIntervalInSeconds,
    OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Call',
          '[DELETE]',
          'url: ${url.obscuredUri}',
          //'\n',
          'headers: ${url.obscuredHeaders}',
          //'\n',
        ]);

    return _executeWithTries(
      url: url,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
      run: () async => await http.delete(
        url.uri,
        headers: url.headers,
        body: url.postObject,
      ),
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  //---------------------------------------------------------------------------

  Future<Response<DT>> _executeWithTries<DT>({
    required Url<DT> url,
    required int? cacheIntervalInSeconds,
    required Future<http.Response> Function() run,
    required OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    _Cache<DT>? cache = _createOrGetCacheStorage(
      key: () => url.signature,
      cacheIntervalInSeconds: cacheIntervalInSeconds,
    );
    if (cache != null) {
      Logs.get(url.logsName).print(() => [
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

        return _resolveResponse(
          url,
          response,
          statusCode: code,
          exception: null,
          cache: cache,
          tries: tries,
          onEncodeResponseFailed: onEncodeResponseFailed,
        );
      } catch (e) {
        exception = e;
      }

      if (tries < supposedTries) {
        if (Logs.inDebugMode || await Logs.writingToLogFileEnabled) {
          Logs.get(url.logsName).print(() => [
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

    //in case request failure -------------------------------------
    var error = '';
    if (exception != null) {
      try {
        error = '$exception';
      } catch (_) {}
    }
    //
    else {
      error = 'Can\'t connect the server';
    }

    return _resolveResponse(
      url,
      http.Response(
        '',
        100 /*just for passing exception*/,
        //reasonPhrase: error,
      ),
      statusCode: 0,
      exception: error,
      cache: cache,
      tries: tries,
      onEncodeResponseFailed: onEncodeResponseFailed,
    );
  }

  Future<Response<DT>> _resolveResponse<DT>(
    Url<DT> url,
    http.Response response, {
    required int statusCode,
    required String? exception,
    required _Cache<DT>? cache,
    required int tries,
    required OnEncodeResponseFailed<DT>? onEncodeResponseFailed,
  }) async {
    Logs.get(url.logsName).print(() => [
          'API::Response',
          'url: ${url.obscuredUri}',
          '\n',
          'code: $statusCode',
          '\n',
          'response: ${response.body}',
          '\n',
          'error: ${response.reasonPhrase}',
          '\n',
          'headers: ${response.headers}',
          '\n',
          'numberOfTries: $tries',
          '\n',
          'exception: $exception',
          '\n',
        ]);

    Response<DT>? responseObj;

    dynamic encodingBodyException;
    dynamic encodingBodyStackTrace;

    dynamic encodingErrorException;
    dynamic encodingErrorStackTrace;

    if (exception == null) {
      try {
        responseObj = url.encodeResponse(response.body);
      } catch (e, s) {
        //print('render-exception1: $e');//to do remove
        encodingBodyException = e;
        encodingBodyStackTrace = s;
      }

      if (responseObj == null && response.reasonPhrase?.isNotEmpty == true) {
        try {
          responseObj = url.encodeResponse(response.reasonPhrase!);
        } catch (e, s) {
          //print('render-exception2: $e');//to do remove
          encodingErrorException = e;
          encodingErrorStackTrace = s;
        }
      }

      responseObj ??= await onEncodeResponseFailed?.call(
        statusCode,
        response.body,
        response.reasonPhrase,
        {
          'body': encodingBodyException,
          'error': encodingErrorException,
        },
      );
    }

    if (responseObj != null) {
      responseObj.httpCode = statusCode;
      responseObj.responseHeader = response.headers;

      cache?.set(responseObj);
      _removeExpiredCaches();
    }
    //
    else {
      _removeExpiredCaches();

      String error;

      if (encodingBodyException != null || encodingErrorException != null) {
        Logs.get(url.logsName).print(() {
          String m = 'WebRequestExecutor._resolveResponse '
              '--> Exception on parsing endpoint: ${url.endPoint}\n';

          if (encodingBodyException != null) {
            m += '-----> Body parsing Exception= "$encodingBodyException"\n'
                '-----> StackTrace= $encodingBodyStackTrace\n';
          }

          if (encodingErrorException != null) {
            m += '----------\n'
                '-----> Reason parsing Exception= "$encodingErrorException"\n'
                '-----> StackTrace= $encodingErrorStackTrace\n';
          }

          return m;
        });

        error = 'Exception:\n';
        if (encodingBodyException != null) {
          error += '• Parsing body: ${encodingBodyException ?? 'NONE'}.\n';
        }
        if (encodingErrorException != null) {
          error += '\n• Parsing error: ${encodingErrorException ?? 'NONE'}';
        }
        error += '\n\n';
      }
      //
      else if (exception != null) {
        error = exception;
      }
      //
      else {
        Logs.get(url.logsName)
            .print(() => 'WebRequestExecutor._resolveResponse '
                '-> Unknown error while handling the response');

        error = 'UNKNOWN ERROR: parsing response failed'.toUpperCase();
      }

      responseObj = Response.failed(
        url: url,
        error: error,
        rawResponse: '{'
            '"status": "Fatal error", '
            '"body": "${response.body}", '
            '"reasonPhrase": "${response.reasonPhrase}", '
            '"error": "$error"'
            '}',
        httpCode: statusCode,
        // httpCode: 400,
        responseHeader: response.headers,
      );
    }

    return responseObj;
  }

  //============================================================================

  Future<Response<Result<DT>>> createDummyResponse<DT>({
    required String apiName,
    required DummyResponseBuilder<DT> Function() responseBuilder,
    int delayInSeconds = 1,
    String? logsName,
  }) async {
    Logs.get(logsName).print(() => 'API-Dummy-Request:: http://$apiName');

    await Future.delayed(Duration(seconds: delayInSeconds));

    Response<Result<DT>> response;

    final r = Random().nextInt(100);
    if (r > 0 && r < 5) {
      response = Response.failed(
        url: null,
        error: "no connection",
        rawResponse: null,
        httpCode: 0,
      );
    }
    //
    else if (r < 10) {
      response = Response.failed(
        url: null,
        error: "Supposed error on server side",
        rawResponse: '{"error":"Supposed error on server side"}',
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
          rawResponse: '"error":"$error"',
          httpCode: 400,
        );
      }
    }

    Logs.get(logsName)
        .print(() => 'API-Dummy-Response:: http://$apiName -> $response');

    return response;
  }
}

typedef OnEncodeResponseFailed<DT> = Future<Response<DT>?> Function(
  int statusCode,
  String responseBody,
  String? responseError,
  Map<String, dynamic> exceptions,
);

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
      }
      //
      else if (cache.cacheIntervalInSeconds != cacheIntervalInSeconds) {
        _cacheStorage.remove(key2);
        cache = null;
      }
    }

    cache ??= _Cache(cacheIntervalInSeconds: cacheIntervalInSeconds);
    _cacheStorage[key2] = cache;
  }

  if (cache == null) {
    return null;
  }
  //
  else {
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
