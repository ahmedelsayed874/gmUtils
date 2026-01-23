import '../utils/mappable.dart';
import 'web_url.dart';

class Response<DATA> {
  Url? url;
  DATA? data;
  String? error;
  String? rawResponse;
  int? httpCode;
  Map<String, String>? responseHeader;

  Response({
    required this.url,
    required this.data,
    required this.error,
    required this.rawResponse,
    required this.httpCode,
    required this.responseHeader,
  });

  Response.success({
    required this.url,
    required this.data,
    this.responseHeader,
  }) : httpCode = 200;

  Response.failed({
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

class ResponseMapper<DT> extends Mappable<Response<DT>> {
  final Mappable<DT> dataMapper;

  ResponseMapper(this.dataMapper);

  @override
  Response<DT> from(values) {
    try {
      final data = dataMapper.from(values);

      return Response(
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

        return Response(
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
        return Response(
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
  Response<DT> fromMap(Map<String, dynamic> values) {
    final data = dataMapper.fromMap(values);

    return Response(
      url: null,
      data: data,
      error: null,
      rawResponse: null,
      httpCode: values['httpCode'],
      responseHeader: null,
    );
  }

  @override
  Map<String, dynamic> toMap(Response<DT> object) {
    throw UnimplementedError();
  }
}
