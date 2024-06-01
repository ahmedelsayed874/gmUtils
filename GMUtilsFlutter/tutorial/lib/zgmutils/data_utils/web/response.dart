import '../../utils/mappable.dart';

class Response<DATA> {
  DATA? data;
  String? error;
  int? httpCode;
  Map<String, String>? responseHeader;

  // String? rawResponse;

  Response._({
    this.data,
    this.error,
    this.httpCode,
    this.responseHeader,
  });

  Response.success({
    required this.data,
    this.responseHeader,
  }) : httpCode = 200;

  Response.failed({
    required this.error,
    required this.httpCode,
    this.responseHeader,
  });

  bool get isSuccess => httpCode == 200;

  bool get isConnectionFailed => (httpCode ?? 0) == 0;

  @override
  String toString() {
    return 'Response{data: $data, error: $error, httpCode: $httpCode, responseHeader: $responseHeader}';
  }
}

class ResponseMapper<DT> extends Mappable<Response<DT>> {
  final Mappable<DT> dataMapper;

  ResponseMapper(this.dataMapper);

  @override
  Response<DT> fromMap(Map<String, dynamic> values) {
    final data = dataMapper.fromMap(values);

    return Response._(
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
