import 'dart:convert';
import 'dart:io';

import '../utils/mappable.dart';
import 'response.dart';

///RDT: response data type
abstract class Url<RDT> {
  final String domain;
  final String fragments;
  final String endPoint;
  final Map<String, String> headers = {};
  final Map<String, String>? queries;
  final Mappable<RDT>? responseMapper;
  final Response<RDT> Function(String response)? responseEncoder;
  final String? logsName;
  final List<ObscureLogOption>? obscureLogOptions;

  Url({
    required this.domain,
    required this.fragments,
    required this.endPoint,
    required Map<String, String>? headers,
    required this.queries,
    //
    required this.responseMapper,
    required this.responseEncoder,
    //
    required this.logsName,
    required this.obscureLogOptions,
  }) {
    if (headers != null) {
      this.headers.addAll(headers);
    }
  }

  void addHeader(String key, String value) {
    headers[key] = value;
  }

  void addHeaderIfNotExist(String key, String value) {
    if (headers.containsKey(key)) return;
    addHeader(key, value);
  }

  void removeHeader(String key) {
    headers.remove(key);
  }

  String get uriAsString {
    var url = '$domain$fragments$endPoint';

    if (queries?.isNotEmpty == true) {
      String q = '';
      queries!.forEach((key, value) {
        if (q.isNotEmpty) q += '&';
        q += '$key=$value';
      });
      url += '?$q';
    }

    return url;
  }

  Uri get uri => Uri.parse(Uri.encodeFull(uriAsString));

  Response<RDT> encodeResponse(String response) {
    if (responseEncoder != null) {
      var response2 = responseEncoder!(response);
      return response2;
    }
    //
    else if (responseMapper == null) {
      return Response.failed(
        url: this,
        error: 'Internal error: either "Url.responseEncoder" or '
            '"Url.responseMapper" should has value',
        rawResponse: response,
        httpCode: 0,
      );
    }
    //
    else {
      var dataMap = jsonDecode(response);
      final responseObj = ResponseMapper(responseMapper!).from(dataMap);
      responseObj.url = this;
      return responseObj;
    }
  }

  String get signature {
    String s = '';
    s += domain.hashCode.toString();
    s += fragments.hashCode.toString();
    s += endPoint.hashCode.toString();

    queries?.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });

    headers.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });

    return s;
  }

  //-----------------------------------------------

  Map<String, ObscureLogOption>? get _obscureLogOptionsMap {
    if (obscureLogOptions == null) return null;

    Map<String, ObscureLogOption> m = {};

    for (var value in obscureLogOptions!) {
      m[value.keyName] = value;
    }

    return m;
  }

  Map? _obscureMap(Map? m) {
    if (obscureLogOptions == null || m == null) return m;
    final obscureLogOptionsMap = _obscureLogOptionsMap!;

    Map h = {};

    for (var key in m.keys) {
      var value = m[key];

      if (obscureLogOptionsMap.containsKey(key)) {
        final v = obscureLogOptionsMap[key]?.obscure('$value');
        if (v != null) {
          h[key] = v;
        }
        //
        else {
          h[key] = '$value';
        }
      }
      //
      else if (value is Map) {
        h[key] = _obscureMap(value);
      }
      //
      else if (value is List) {
        h[key] = _obscureList(value);
      }
      //
      else {
        h[key] = value;
      }
    }

    return h;
  }

  List _obscureList(List value) {
    List newList = [];

    for (var lstItem in value) {
      if (lstItem is Map) {
        newList.add(_obscureMap(lstItem));
      }
      //
      /*else if (lstItem is List) {
        newList.add(_obscureList(lstItem));
      }*/
      //
      else {
        newList.add(lstItem);
      }
    }

    return newList;
  }

  Map? get obscuredHeaders => _obscureMap(headers);

  Map? get obscuredQueries => _obscureMap(queries);

  String get obscuredUriAsString {
    if (obscureLogOptions == null) return uriAsString;

    var url = '$domain$fragments$endPoint';

    final _obscuredQueries = obscuredQueries;
    if (_obscuredQueries?.isNotEmpty == true) {
      String q = '';
      _obscuredQueries!.forEach((key, value) {
        if (q.isNotEmpty) q += '&';
        q += '$key=$value';
      });
      url += '?$q';
    }

    return url;
  }

  Uri get obscuredUri => Uri.parse(
        Uri.encodeFull(
            obscureLogOptions == null ? uriAsString : obscuredUriAsString),
      );

  @override
  String toString() {
    return '$runtimeType{\n'
        'domain: $domain,\n'
        'fragments: $fragments,\n'
        'endPoint: $endPoint,\n'
        'headers: $headers,\n'
        'queries: $queries'
        '}';
  }
}

class GetUrl<RDT> extends Url<RDT> {
  GetUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });
}

class _PostUrl<RDT> extends Url<RDT> {
  final Map<String, dynamic>? params;
  final bool asJson;

  //final String? body;

  _PostUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    this.params,
    this.asJson = true,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  }) {
    if (params != null) {
      if (asJson) {
        addHeaderIfNotExist('Content-Type', 'application/json');
        addHeaderIfNotExist('Accept', 'application/json');
      }
      //
      else {
        addHeaderIfNotExist(
            'Content-Type', 'application/x-www-form-urlencoded');
      }
    }
  }

  Object get postObject {
    if (params?.isNotEmpty == true) {
      if (asJson) {
        final json = jsonEncode(params);
        //Logs.print(() => 'PostUrl -> json: $json');
        return json;
      }
      //
      else {
        String q = '';
        params!.forEach((key, value) {
          if (q.isNotEmpty) q += '&';
          q += '$key=$value';
        });
        //Logs.print(() => 'PostUrl -> parameters: $q');
        return q;
      }
    }
    //
    else {
      return '';
    }
  }

  @override
  String get signature {
    var s = super.signature;
    params?.forEach((key, value) {
      s += key.hashCode.toString();
      s += '$value'.hashCode.toString();
    });
    s += '$asJson'.hashCode.toString();
    return s;
  }

  //-----------------------------------------

  Map? get obscuredParams => _obscureMap(params);

  Object get obscuredPostObject {
    if (obscuredParams?.isNotEmpty == true) {
      if (asJson) {
        final json = jsonEncode(obscuredParams);
        //Logs.print(() => 'PostUrl -> json: $json');
        return json;
      }
      //
      else {
        String q = '';
        obscuredParams!.forEach((key, value) {
          if (q.isNotEmpty) q += '&';
          q += '$key=$value';
        });
        //Logs.print(() => 'PostUrl -> parameters: $q');
        return q;
      }
    }
    //
    else {
      return '';
    }
  }

  @override
  String toString() {
    var t = super.toString();
    t = t.substring(0, t.length - 1);
    t += ',\n'
        'params: $params,\n'
        'asJson: $asJson'
        '}';
    return t;
  }
}

class PostUrl<RDT> extends _PostUrl<RDT> {
  PostUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    super.params,
    super.asJson,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });
}

//-----------------------------------------------------

class PatchUrl<RDT> extends _PostUrl<RDT> {
  PatchUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    super.params,
    super.asJson,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });
}

class PutUrl<RDT> extends _PostUrl<RDT> {
  PutUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    super.params,
    super.asJson,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });
}

//-----------------------------------------------------

enum MultiPartMethod { POST, PATCH, PUT }

class MultiPartFile {
  final String mappedKey;
  final File file;
  final String? customFileName;
  final String? mimeType;

  MultiPartFile({
    required this.mappedKey,
    required this.file,
    this.customFileName,
    this.mimeType,
  });

  List<int>? _fileBytes;
  List<int> get fileBytes => _fileBytes ??= file.readAsBytesSync();

  dynamic get fileBytesLength {
    try {
      return fileBytes.length;
    } catch (e) {
      return e;
    }
  }

  String get fileName {
    if (customFileName?.isNotEmpty == true) {
      return customFileName!;
    }

    int i = file.path.lastIndexOf('/');
    if (i < 0) i = file.path.lastIndexOf('\\');

    if (i >= 0) {
      return file.path.substring(i + 1);
    }
    //
    else {
      var d = DateTime.now();
      return 'file'
          '${d.year}'
          '${_compensate(d.month)}'
          '${_compensate(d.day)}'
          '${_compensate(d.hour)}'
          '${_compensate(d.minute)}'
          '${_compensate(d.second)}'
          '.${_compensate(d.millisecond)}'
          '';
    }
  }

  String _compensate(int n) {
    return n < 10 ? '0$n' : '$n';
  }

  String get signature {
    var s = '';
    s += mappedKey.hashCode.toString();
    s += file.path.hashCode.toString();
    try {
      s += file.lengthSync().toString();
      s += file.lastModifiedSync().millisecondsSinceEpoch.toString();
    } catch (e) {
      s += e.toString();
    }
    if (mimeType != null) {
      s += mimeType.hashCode.toString();
    }
    return s;
  }
}

class MultiPartRequestUrl<RDT> extends Url<RDT> {
  final MultiPartMethod method;
  final List<MultiPartFile> files;
  final Map<String, String>? formFields;

  MultiPartRequestUrl({
    required this.method,
    required super.domain,
    required super.fragments,
    required super.endPoint,
    required super.headers,
    super.queries,
    //
    required this.files,
    this.formFields,
    //
    required Mappable<RDT> super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });

  @override
  String get signature {
    var s = super.signature;
    for (var file in files) {
      s += file.signature;
    }
    formFields?.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });
    return s;
  }

  //--------------------------------------------

  Map? get obscuredFormFields => _obscureMap(formFields);

  @override
  String toString() {
    var t = super.toString();
    t = t.substring(0, t.length - 1);

    for (var file in files) {
      t += ',\n'
          'fileMappedKey: ${file.mappedKey},\n'
          'file: $file,\n'
          'customFileName: ${file.customFileName},\n'
          'fileMimeType: ${file.mimeType},\n';
    }

    t += 'formFields: $formFields';
    t += '}';
    return t;
  }
}

//-----------------------------------------------------

class DeleteUrl<RDT> extends Url<RDT> {
  DeleteUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    super.headers,
    super.queries,
    //
    required super.responseMapper,
    super.responseEncoder,
    //
    super.logsName,
    super.obscureLogOptions,
  });
}

//-----------------------------------------------------------------------------

class ObscureLogOption {
  final String keyName;
  final bool? fromLeading;
  final double withPercent;
  final String? secretKey;
  final String? replacement;

  ObscureLogOption.allValueOf(this.keyName)
      : fromLeading = true,
        withPercent = 1,
        secretKey = null,
        replacement = null;

  ObscureLogOption.firstHalfOfValueOf(this.keyName)
      : fromLeading = true,
        withPercent = 0.5,
        secretKey = null,
        replacement = null;

  ObscureLogOption.secondHalfOfValueOf(this.keyName)
      : fromLeading = false,
        withPercent = 0.5,
        secretKey = null,
        replacement = null;

  ObscureLogOption.firstOfValueOf(this.keyName, {required this.withPercent})
      : fromLeading = true,
        secretKey = null,
        replacement = null;

  ObscureLogOption.lastOfValueOf(this.keyName, {required this.withPercent})
      : fromLeading = false,
        secretKey = null,
        replacement = null;

  ObscureLogOption.encryptValueOf(this.keyName, {required this.secretKey})
      : fromLeading = null,
        withPercent = 1,
        replacement = null;

  ObscureLogOption.replaceValueOf(this.keyName, {required this.replacement})
      : fromLeading = null,
        secretKey = null,
        withPercent = 1;

  String obscure(value) {
    value = '$value';

    if (fromLeading == true) {
      //password ---> ****word
      int end = (value.length * withPercent).ceil();

      String t = '';

      for (var i = 0; i < end; i++) {
        t += '*';
      }

      if (end < value.length) {
        t += value.substring(end);
      }

      return t;
    }

    //
    else if (fromLeading == false) {
      //password ---> pass****
      int end = (value.length * (1.0 - withPercent)).floor();

      String t = '';

      if (end > 0 && end < value.length) {
        t = value.substring(0, end);
      }

      for (var i = end; i < value.length; i++) {
        t += '*';
      }

      return t;
    }

    //
    else if (secretKey?.isNotEmpty == true) {
      final v = utf8.encode(value);
      return base64Encode(v);
    }

    //
    else if (replacement?.isNotEmpty == true) {
      return replacement!;
    }

    //
    else {
      throw 'obscuring type is unknown';
    }
  }
}
