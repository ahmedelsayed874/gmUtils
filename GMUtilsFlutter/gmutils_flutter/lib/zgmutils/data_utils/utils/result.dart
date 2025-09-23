import '../../utils/collections/string_set.dart';

class Result<T> {
  final T? result;
  final StringSet? message;
  final Map<String, dynamic>? extra;

  Result(this.result, {this.message, this.extra});

  @override
  String toString() {
    return 'Result{result: $result, message: $message, extra: $extra}';
  }
}
