import '../../utils/collections/string_set.dart';
import '../utils/mappable.dart';

class FBResponse<DATA> {
  final DATA? data;
  final StringSet? error;
  final bool noData;
  final bool connectionFailed;

  FBResponse._({
    this.data,
    this.error,
    this.noData = false,
    this.connectionFailed = false,
  });

  FBResponse.success({
    required this.data,
  }) : error = null, noData = false, connectionFailed = false;

  FBResponse.failed({
    required this.error,
    this.noData = false,
    this.connectionFailed = false,
  }) : data = null;

  @override
  String toString() {
    return 'FBResponse{data: $data, error: $error, noData: $noData, connectionFailed: $connectionFailed}';
  }

}

class FBResponseMapper<DT> extends Mappable<FBResponse<DT>> {
  final Mappable<DT> dataMapper;

  FBResponseMapper(this.dataMapper);

  @override
  FBResponse<DT> fromMap(Map<String, dynamic> values) {
    final data = dataMapper.fromMap(values);

    return FBResponse._(
      data: data,
      error: null,
      connectionFailed: values['connectionFailed'],
    );
  }

  @override
  Map<String, dynamic> toMap(FBResponse<DT> object) {
    throw UnimplementedError();
  }
}
