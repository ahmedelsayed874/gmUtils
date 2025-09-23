
class Dataset4<V1, V2, V3, V4> {
  final V1 value1;
  final V2 value2;
  final V3 value3;
  final V4 value4;

  Dataset4({required this.value1, required this.value2, required this.value3,  required this.value4, });

  Dataset4.init(this.value1, this.value2, this.value3, this.value4,);

  @override
  bool operator ==(Object other) {
    if (other is Dataset4<V1, V2, V3, V4>) {
      return other.value1 == value1 && other.value2 == value2 && other.value3 == value3 && other.value4 == value4;
    }
    return false;
  }

  @override
  int get hashCode => value1.hashCode + value2.hashCode + value3.hashCode + value4.hashCode;

  @override
  String toString() {
    return 'Dataset4 {value1: $value1, value2: $value2, value3: $value3, value4: $value4}';
  }
}
