import '../../utils/collections/string_set.dart';
import '../utils/mappable.dart';

class Response<DATA> {
  final DATA? data;
  final StringSet? error;
  final bool noData;
  final bool connectionFailed;

  Response._({
    this.data,
    this.error,
    this.noData = false,
    this.connectionFailed = false,
  });

  Response.success({
    required this.data,
  }) : error = null, noData = false, connectionFailed = false;

  Response.failed({
    required this.error,
    this.noData = false,
    this.connectionFailed = false,
  }) : data = null;

  @override
  String toString() {
    return 'Response{data: $data, error: $error, noData: $noData, connectionFailed: $connectionFailed}';
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
