import '../utils/mappable.dart';
import 'web_url.dart';

class WebResponse<DATA> {
  Url? url;
  DATA? data;
  String? error;
  String? rawResponse;
  int? httpCode;
  Map<String, String>? responseHeader;

  WebResponse({
    required this.url,
    required this.data,
    required this.error,
    required this.rawResponse,
    required this.httpCode,
    required this.responseHeader,
  });

  WebResponse.success({
    required this.url,
    required this.data,
    this.responseHeader,
  }) : httpCode = 200;

  WebResponse.failed({
    required this.url,
    required this.error,
    required this.rawResponse,
    required this.httpCode,
    this.responseHeader,
  });

  //bool get isSuccess => httpCode == 200;
  bool get isSuccess => ((httpCode ?? 0) ~/ 100) == 2;

  bool get isConnectionFailed => (httpCode == 0 || httpCode == 100 || httpCode == null);

  @override
  String toString() {
    return 'Response{'
        'endPoint: ${url?.endPoint}, '
        'data: $data, '
        'error: $error, '
        'rawResponse: $rawResponse, '
        'httpCode: $httpCode, '
        'responseHeader: $responseHeader'
        '}';
  }
}

class WebResponseMapper<DT> extends Mappable<WebResponse<DT>> {
  final Mappable<DT> dataMapper;

  WebResponseMapper(this.dataMapper);

  @override
  WebResponse<DT> from(values) {
    try {
      final data = dataMapper.from(values);

      return WebResponse(
        url: null,
        data: data,
        error: null,
        rawResponse: null,
        httpCode: values['httpCode'],
        responseHeader: null,
      );
    } catch (_) {
      //Logs.print(() => 'ResponseMapper.from ---> EXCEPTION:: $e');
    }

    //-----------------------------------------------

    if (values is Map) {
      return fromMap(values as Map<String, dynamic>);
    }
    //
    else if (values is List) {
      try {
        var lst = dataMapper.fromMapList(values);

        return WebResponse(
          url: null,
          data: lst as DT?,
          error: null,
          rawResponse: null,
          httpCode: null,
          responseHeader: null,
        );
      } catch (e) {
        return fromMap({'list': values});
      }
    }
    //
    else {
      try {
        return WebResponse(
          url: null,
          data: values,
          error: null,
          rawResponse: null,
          httpCode: null,
          responseHeader: null,
        );
      } catch (_) {
        /*return Response(
          url: null,
          data: null,
          error: '$e',
          rawResponse: '$values',
          httpCode: null,
          responseHeader: null,
        );*/
        rethrow;
      }
    }
  }

  @override
  WebResponse<DT> fromMap(Map<String, dynamic> values) {
    final data = dataMapper.fromMap(values);

    return WebResponse(
      url: null,
      data: data,
      error: null,
      rawResponse: null,
      httpCode: values['httpCode'],
      responseHeader: null,
    );
  }

  @override
  Map<String, dynamic> toMap(WebResponse<DT> object) {
    throw UnimplementedError();
  }
}
