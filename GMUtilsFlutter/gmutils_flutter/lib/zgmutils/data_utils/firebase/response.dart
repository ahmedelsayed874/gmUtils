import '../../utils/mappable.dart';
import '../../utils/string_set.dart';

class Response<DATA> {
  DATA? data;
  StringSet? error;
  bool connectionFailed;

  Response._({
    this.data,
    this.error,
    this.connectionFailed = false,
  });

  Response.success({
    required this.data,
  }) : connectionFailed = false;

  Response.failed({
    required this.error,
    this.connectionFailed = false,
  });

  @override
  String toString() {
    return 'Response{data: $data, error: $error, connectionFailed: $connectionFailed}';
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
      connectionFailed: values['connectionFailed'],
    );
  }

  @override
  Map<String, dynamic> toMap(Response<DT> object) {
    throw UnimplementedError();
  }
}
