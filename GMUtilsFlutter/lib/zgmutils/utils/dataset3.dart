
class Dataset3<V1, V2, V3> {
  final V1 value1;
  final V2 value2;
  final V3 value3;

  Dataset3({required this.value1, required this.value2, required this.value3});

  @override
  bool operator ==(Object other) {
    if (other is Dataset3<V1, V2, V3>) {
      return other.value1 == value1 && other.value2 == value2 && other.value3 == value3;
    }
    return false;
  }

  @override
  int get hashCode => value1.hashCode + value2.hashCode + value3.hashCode;

  @override
  String toString() {
    return 'Dataset3 {value1: $value1, value2: $value2, value3: $value3}';
  }
}