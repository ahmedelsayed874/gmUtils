import 'dart:convert';

abstract class Mappable<T> {
  Map<String, dynamic> toMap(T object) {
    throw UnimplementedError(
        'implement this method to handle this object --> $object');
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
  String? key;

  StringMapper({required this.key});

  @override
  String? fromMap(Map<String, dynamic> values) {
    //if (key == null) return jsonEncode(values);

    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'StringMapper.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return null;
    }
    //
    else {
      value = values[key];
    }

    return (value is String
            ? value
            : throw 'StringMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values');
  }

  @override
  Map<String, dynamic> toMap(String? object) {
    return {
      key ?? 'value': object,
    };
  }
}

class IntMapper extends Mappable<int?> {
  String? key;

  IntMapper({required this.key});

  @override
  int? fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'IntMapper.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return null;
    }
    //
    else {
      value = values[key];
    }

    return (int.tryParse('$value') ??
            (throw 'IntMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(int? object) {
    return {key ?? 'value': object,};
  }
}

class DoubleMapper extends Mappable<double?> {
  String? key;

  DoubleMapper({required this.key});

  @override
  double? fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'DoubleMapper.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return null;
    }
    //
    else {
      value = values[key];
    }

    return (double.tryParse('$value') ??
            (throw 'DoubleMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(double? object) {
    return {key ?? 'value': object};
  }
}

class BoolMapper extends Mappable<bool?> {
  String? key;

  BoolMapper({required this.key});

  @override
  bool? fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'BoolMapper.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return null;
    }
    //
    else {
      value = values[key];
    }

    if (value is int) {
      return value == 1;
    }
    //
    else if (value is bool) {
      return value;
    }
    //
    else {
      throw 'BoolMapper.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values';
    }
  }

  @override
  Map<String, dynamic> toMap(bool? object) {
    return {key ?? 'value': object};
  }
}
