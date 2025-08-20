import 'dart:convert';
import 'dart:io';

import '../../utils/mappable.dart';
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
      try {
        var dataMap = jsonDecode(response);
        final responseObj = ResponseMapper(responseMapper!).from(dataMap);
        responseObj.url = this;
        return responseObj;
      } catch (e) {
        final Response<RDT> res = Response.failed(
          url: this,
          error: 'Url.encodeResponse --> Exception at Parsing the response of '
              '($endPoint): $e ------> response=$response',
          rawResponse: response,
          httpCode: 0,

        );
        res.url = this;
        return res;
      }
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
      final v = obscureLogOptionsMap[key]?.obscure('${m[key]}');
      if (v != null) {
        h[key] = v;
      }
      else {
        h[key] = '${m[key]}';
      }
    }

    return h;
  }

  /*Map<String, String> get obscuredHeaders0 {
    if (obscureLogOptions == null) return headers;
    final obscureLogOptionsMap = _obscureLogOptionsMap!;

    Map<String, String> h = {};

    for (var key in headers.keys) {
      final v = obscureLogOptionsMap[key]?.obscure(headers[key] ?? '');
      h[key] = v ?? headers[key] ?? '';
    }

    return h;
  }*/
  Map? get obscuredHeaders => _obscureMap(headers);

  /*Map<String, String>? get obscuredQueries {
    if (obscureLogOptions == null || queries == null) return queries;
    final obscureLogOptionsMap = _obscureLogOptionsMap!;

    Map<String, String> h = {};

    for (var key in queries!.keys) {
      final v = obscureLogOptionsMap[key]?.obscure(queries![key] ?? '');
      h[key] = v ?? queries![key] ?? '';
    }

    return h;
  }*/
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
    super.obscureLogOptions,
  });
}

class PostUrl<RDT> extends Url<RDT> {
  final Map<String, dynamic>? params;
  final bool asJson;

  //final String? body;

  PostUrl({
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
    super.obscureLogOptions,
  }) {
    if (params != null) {
      if (asJson) {
        addHeaderIfNotExist('Content-Type', 'application/json');
        addHeaderIfNotExist('Accept', 'application/json');
      } else {
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

  /*Map<String, dynamic>? get obscuredParams {
    if (obscureLogOptions == null || params == null) return params;
    final obscureLogOptionsMap = _obscureLogOptionsMap!;

    Map<String, String> h = {};

    for (var key in params!.keys) {
      final v = obscureLogOptionsMap[key]?.obscure(params![key] ?? '');
      h[key] = v ?? params![key] ?? '';
    }

    return h;
  }*/
  Map? get obscuredParams => _obscureMap(params);

  Object get obscuredPostObject {
    if (obscuredParams?.isNotEmpty == true) {
      if (asJson) {
        final json = jsonEncode(obscuredParams);
        //Logs.print(() => 'PostUrl -> json: $json');
        return json;
      } else {
        String q = '';
        obscuredParams!.forEach((key, value) {
          if (q.isNotEmpty) q += '&';
          q += '$key=$value';
        });
        //Logs.print(() => 'PostUrl -> parameters: $q');
        return q;
      }
    } else {
      return '';
    }
  }
}

class PostMultiPartFileUrl<RDT> extends Url<RDT> {
  final String fileMappedKey;
  final File file;
  final String? customFileName;
  final String? fileMimeType;
  final Map<String, String>? formFields;

  PostMultiPartFileUrl({
    required super.domain,
    required super.fragments,
    required super.endPoint,
    required super.headers,
    super.queries,
    //
    this.formFields,
    required this.fileMappedKey,
    required this.file,
    this.customFileName,
    this.fileMimeType,
    //
    required Mappable<RDT> super.responseMapper,
    super.responseEncoder,
    //
    super.obscureLogOptions,
  });

  List<int> get fileBytes => file.readAsBytesSync();

  String get fileName {
    if (customFileName?.isNotEmpty == true) {
      return customFileName!;
    }

    int i = file.path.lastIndexOf('/');
    if (i < 0) i = file.path.lastIndexOf('\\');

    if (i >= 0) {
      return file.path.substring(i + 1);
    } else {
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

  @override
  String get signature {
    var s = super.signature;
    s += fileMappedKey.hashCode.toString();
    s += file.path.hashCode.toString();
    try {
      s += file.lengthSync().toString();
      s += file.lastModifiedSync().millisecondsSinceEpoch.toString();
    } catch (e) {
      s += e.toString();
    }
    if (fileMimeType != null) {
      s += fileMimeType.hashCode.toString();
    }
    formFields?.forEach((key, value) {
      s += key.hashCode.toString();
      s += value.hashCode.toString();
    });
    return s;
  }

  //--------------------------------------------

  /*Map<String, String>? get obscuredFormFields {
    if (obscureLogOptions == null || formFields == null) return formFields;
    final obscureLogOptionsMap = _obscureLogOptionsMap!;

    Map<String, String> h = {};

    for (var key in formFields!.keys) {
      final v = obscureLogOptionsMap[key]?.obscure(formFields![key] ?? '');
      h[key] = v ?? formFields![key] ?? '';
    }

    return h;
  }*/
  Map? get obscuredFormFields => _obscureMap(formFields);
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
