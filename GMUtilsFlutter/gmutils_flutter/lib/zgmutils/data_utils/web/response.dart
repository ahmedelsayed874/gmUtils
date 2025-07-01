import '../../utils/mappable.dart';
import 'web_url.dart';

class Response<DATA> {
  Url? url;
  DATA? data;
  String? error;
  int? httpCode;
  Map<String, String>? responseHeader;

  // String? rawResponse;

  Response._({
    required this.url,
    this.data,
    this.error,
    this.httpCode,
    this.responseHeader,
  });

  Response.success({
    required this.url,
    required this.data,
    this.responseHeader,
  }) : httpCode = 200;

  Response.failed({
    required this.url,
    required this.error,
    required this.httpCode,
    this.responseHeader,
  });

  bool get isSuccess => httpCode == 200;

  bool get isConnectionFailed => (httpCode == 0 || httpCode == 100 || httpCode == null);

  @override
  String toString() {
    return 'Response{endPoint: ${url?.endPoint}, data: $data, error: $error, httpCode: $httpCode, responseHeader: $responseHeader}';
  }
}

class ResponseMapper<DT> extends Mappable<Response<DT>> {
  final Mappable<DT> dataMapper;

  ResponseMapper(this.dataMapper);

  @override
  Response<DT> fromMap(Map<String, dynamic> values) {
    final data = dataMapper.fromMap(values);

    return Response._(
      url: null,
      data: data,
      error: null,
      httpCode: values['httpCode'],
      responseHeader: null,
    );
  }

  @override
  Map<String, dynamic> toMap(Response<DT> object) {
    throw UnimplementedError();
  }
}
