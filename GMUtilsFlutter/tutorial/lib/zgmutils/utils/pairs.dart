
class Pair<V1, V2> {
  final V1 value1;
  final V2 value2;

  Pair({required this.value1, required this.value2});

  @override
  bool operator ==(Object other) {
    if (other is Pair<V1, V2>) {
      return other.value1 == value1 && other.value2 == value2;
    }
    return false;
  }

  @override
  int get hashCode => value1.hashCode + value2.hashCode;

  @override
  String toString() {
    return 'Pair {value1: $value1, value2: $value2}';
  }
}