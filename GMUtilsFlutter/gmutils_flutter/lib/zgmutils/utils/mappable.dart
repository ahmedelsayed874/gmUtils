import 'dart:convert';

import 'logs.dart';

abstract class Mappable<T> {
  T? fromJsonObject(String? json) {
    if (json?.isNotEmpty != true) return null;

    dynamic map;
    try {
      map = jsonDecode(json!);
      return fromMap(map);
    } catch (e) {
      Logs.print(
        () =>
            'Mappable.fromJsonObject ----> EXCEPTION:: $e ----> json: $json -----> map: $map',
      );
      throw 'decoding json failed ... json: $json -----> map: $map';
    }
  }

  List<T>? fromJsonArray(String? json) {
    if (json?.isNotEmpty != true) return null;

    dynamic map;
    try {
      map = jsonDecode(json!);
      return fromMapList(map);
    } catch (e) {
      Logs.print(
        () =>
            'Mappable.fromJsonArray ----> EXCEPTION:: $e ----> json: $json -----> map: $map',
      );
      throw 'decoding json failed ... json: $json -----> map: $map';
    }
  }

  T fromMap(Map<String, dynamic> values);

  List<T>? fromMapList(List<dynamic>? values) {
    if (values == null) return null;

    List<T> list = [];

    for (var element in values) {
      T t;
      if (element is Map) {
        t = fromMap(Map<String, dynamic>.from(element));
      }
      //
      else {
        t = element;
      }

      list.add(t);
    }

    return list;
  }

  Map<String, dynamic> toMap(T object) {
    throw UnimplementedError(
        'implement this method to handle this object --> $object');
  }

  List<Map<String, dynamic>>? toMapList(List<T>? objects) {
    if (objects == null) return null;
    return objects.map((e) => toMap(e)).toList();
  }

  String? toJsonObject(T? data) {
    if (data == null) return null;

    dynamic map;
    try {
      map = toMap(data);
      return jsonEncode(map);
    } catch (e) {
      throw '$e\nmap: $map';
    }
  }

  String? toJsonArray(List<T>? data) {
    if (data == null) return null;

    dynamic map;
    try {
      map = toMapList(data);
      return jsonEncode(map);
    } catch (e) {
      throw '$e\nmap: $map';
    }
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

//-------------------------------------------------

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

class StringMapper2 extends Mappable<String> {
  String? key;

  StringMapper2({required this.key});

  @override
  String fromMap(Map<String, dynamic> values) {
    //if (key == null) return jsonEncode(values);

    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'StringMapper2.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return '';
    }
    //
    else {
      value = values[key];
    }

    return (value is String
        ? value
        : throw 'StringMapper2.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values');
  }

  @override
  Map<String, dynamic> toMap(String object) {
    return {
      key ?? 'value': object,
    };
  }
}

//-------------------------------------------------

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
    return {
      key ?? 'value': object,
    };
  }
}

class IntMapper2 extends Mappable<int> {
  String? key;

  IntMapper2({required this.key});

  @override
  int fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'IntMapper2.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      //return -0x8000000000000000;
      return double.infinity.toInt();
    }
    //
    else {
      value = values[key];
    }

    return (int.tryParse('$value') ??
        (throw 'IntMapper2.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(int object) {
    return {
      key ?? 'value': object,
    };
  }
}

//-------------------------------------------------

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

class DoubleMapper2 extends Mappable<double> {
  String? key;

  DoubleMapper2({required this.key});

  @override
  double fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'DoubleMapper2.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return double.infinity;
    }
    //
    else {
      value = values[key];
    }

    return (double.tryParse('$value') ??
        (throw 'DoubleMapper2.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values'));
  }

  @override
  Map<String, dynamic> toMap(double object) {
    return {key ?? 'value': object};
  }
}

//-------------------------------------------------

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

class BoolMapper2 extends Mappable<bool> {
  String? key;

  BoolMapper2({required this.key});

  @override
  bool fromMap(Map<String, dynamic> values) {
    dynamic value;

    if (key == null) {
      if (values.length == 1) {
        value = values.values.firstOrNull;
      }
      //
      else {
        (throw 'BoolMapper2.fromMap(Map<String, dynamic>) can\'t handle the data where values has many entries => $values');
      }
    }
    //
    else if (values[key] == null) {
      return bool.parse('null');
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
      throw 'BoolMapper2.fromMap(Map<String, dynamic>) can\'t handle the data under the key = $key;\nthe data: $values';
    }
  }

  @override
  Map<String, dynamic> toMap(bool object) {
    return {key ?? 'value': object};
  }
}
