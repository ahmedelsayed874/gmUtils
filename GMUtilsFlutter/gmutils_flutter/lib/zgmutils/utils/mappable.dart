abstract class Mappable<T> {
  Map<String, dynamic> toMap(T object) {
    throw UnimplementedError('implement this method to handle this object --> $object');
  }

  T fromMap(Map<String, dynamic> values);

  List<Map<String, dynamic>>? toMapList(List<T>? objects) {
    if (objects == null) return null;
    return objects.map((e) => toMap(e)).toList();
  }

  List<T>? fromMapList(List<dynamic>? values) {
    if (values == null) return null;

    List<T> list = [];

    for (var element in values) {
      T t;
      if (element is Map) {
        t = fromMap(Map<String, dynamic>.from(element));
      } else {
        t = element;
      }
      list.add(t);
    }

    return list;
  }
}

//==============================================================================

class VoidMapper extends Mappable<void> {
  @override
  void fromMap(Map<String, dynamic> values) {}

  @override
  Map<String, dynamic> toMap(void object) {
    return {};
  }
}

class StringMapper extends Mappable<String?> {
  String key;

  StringMapper({required this.key});

  @override
  String? fromMap(Map<String, dynamic> values) {
    return values[key] == null
        ? null
        : (values[key] is String
            ? values[key]
            : throw 'StringMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values');
  }

  @override
  Map<String, dynamic> toMap(String? object) {
    return {key: object};
  }
}

class IntMapper extends Mappable<int?> {
  String key;

  IntMapper({required this.key});

   @override
  int? fromMap(Map<String, dynamic> values) {
    var n = int.tryParse('${values[key]}');

    return values[key] == null
        ? null
        : (n ?? (throw 'IntMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(int? object) {
    return {key: object};
  }
}

class DoubleMapper extends Mappable<double?> {
  String key;

  DoubleMapper({required this.key});

  @override
  double? fromMap(Map<String, dynamic> values) {
    var n = double.tryParse('${values[key]}');
    
    return values[key] == null
        ? null
        : (n ?? (throw 'DoubleMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(double? object) {
    return {key: object};
  }
}

class BoolMapper extends Mappable<bool?> {
  String key;

  BoolMapper({required this.key});

  @override
  bool? fromMap(Map<String, dynamic> values) {
    return values[key] == null
        ? null
        : (values[key] is int
            ? values[key]
            : throw 'BoolMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values');
  }

  @override
  Map<String, dynamic> toMap(bool? object) {
    return {key: object};
  }
}
