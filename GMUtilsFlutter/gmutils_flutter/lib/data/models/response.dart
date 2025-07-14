import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/mappable.dart';
import 'package:gmutils_flutter/zgmutils/utils/result.dart';

import '../../zgmutils/data_utils/web/response.dart' as webResponse;

class Response<T> {
  static const String statusSuccess = 'Success';
  static const String statusFailed = 'Failed';
  static const String statusConnectionError = 'ConnectionError';

  final String status;
  final ResponseMessage? _message1;
  final String? _message2;
  final T? data;
  final int? httpCode;

  Response({
    required this.status,
    // required ResponseMessage? message1,
    // required String? message2,
    required message,
    required this.data,
    required this.httpCode,
  })  : this._message1 = message is ResponseMessage? ? message : null,
        this._message2 = message is String? ? message : null;

  Response.failed({
    String status = statusFailed,
    String? message,
    int? httpCode,
  }) : this(
          status: status,
          message: message,
          data: null,
          httpCode: httpCode,
        );

  bool get isSuccess => status == statusSuccess;

  bool get isConnectionFailed => status == statusConnectionError;

  String? defaultErrorMessage;

  String get errorMessage {
    String? msg = _message2;
    msg ??= _message1?.text(en: App.isEnglish);
    msg ??= (defaultErrorMessage ?? 'Unknown Error');

    if (httpCode != null &&
        httpCode != 200 &&
        httpCode != 100 &&
        httpCode != 0) {
      msg += ' [code: $httpCode]';
    }
    return msg;
  }

  @override
  String toString() {
    return 'Response{status: $status, message1: $_message1, , message2: $_message2, data: $data, httpCode: $httpCode}';
  }

  static Response<T> fromWebResponse<T>(
    webResponse.Response<Response<T>> response,
  ) {
    if (response.isConnectionFailed) {
      return Response(
        status: Response.statusConnectionError,
        // message: response.error == null
        //     ? ResponseMessage(en: 'Connection failed', ar: 'تعذر الاتصال')
        //     : ResponseMessage(
        //         en: response.error!,
        //         ar: response.error!,
        //       ),
        message: response.error ??
            ResponseMessage(en: 'Connection failed', ar: 'تعذر الاتصال')
                .text(en: App.isEnglish),
        data: null,
        httpCode: response.httpCode,
      );
    } else {
      if (response.error != null) {
        return Response(
          status: Response.statusFailed,
          // message: response.error == null
          //     ? null
          //     : ResponseMessage(
          //         en: response.error!,
          //         ar: response.error!,
          //       ),
          message: response.error,
          data: null,
          httpCode: response.httpCode,
        );
      } else {
        return response.data!;
      }
    }
  }

  static Response<T> fromDummyResponse<T>(
    webResponse.Response<Result<T?>> response,
  ) {
    if (response.isSuccess) {
      if (response.error != null) {
        return Response(
          status: Response.statusFailed,
          // message1: ResponseMessage(
          //   en: 'EN: ${response.error}',
          //   ar: 'AR: ${response.error}',
          // ),
          message: response.error,
          data: null,
          httpCode: response.httpCode,
        );
      } else {
        return Response(
          status: Response.statusSuccess,
          message: null,
          data: response.data?.result,
          httpCode: response.httpCode,
        );
      }
    }

    //
    else {
      return Response(
        status: Response.statusConnectionError,
        // message1: response.error == null
        //     ? ResponseMessage(en: 'Connection failed', ar: 'تعذر الاتصال')
        //     : ResponseMessage(
        //         en: response.error!,
        //         ar: response.error!,
        //       ),
        message: response.error ??
            ResponseMessage(en: 'Connection failed', ar: 'تعذر الاتصال').text(
              en: App.isEnglish,
            ),
        data: null,
        httpCode: 0,
      );
    }
  }
}

class ResponseMapper<T> extends Mappable<Response<T>> {
  final Mappable? dataMapper;

  ResponseMapper({required this.dataMapper});

  @override
  Response<T> fromMap(Map<String, dynamic> values) {
    var responseData = values['data'];

    T? data;
    if (responseData != null) {
      if (responseData is Map) {
        assert(dataMapper != null);
        data = dataMapper?.fromMap(Map.from(responseData));
      }
      //
      else if (responseData is List) {
        assert(dataMapper != null);
        data = dataMapper?.fromMapList(responseData) as T?;
      }
      //
      else {
        data = responseData;
      }
    }

    dynamic message = values['message'];
    if (message != null) {
      if (message is Map) {
        message = ResponseMessageMapper().fromMap(Map.from(message));
      }
    }

    return Response(
      status: values['status'],
      message: message,
      data: data,
      httpCode: null,
    );
  }

  @override
  Map<String, dynamic> toMap(Response<T> object) {
    return {
      'status': object.status,
      'message': object.errorMessage,
      // 'message': object._message1 == null
      //     ? null
      //     : ResponseMessageMapper().toMap(object._message),
      'data': object.data == null
          ? null
          : (object.data is Map
              ? dataMapper?.toMap(object.data!)
              : (object.data is List
                  ? dataMapper?.toMapList(object.data as List<T>?)
                  : object.data)),
      'httpCode': object.httpCode,
    };
  }
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

class ResponseMessage {
  final String en;
  final String ar;

  ResponseMessage({required this.en, required this.ar});

  String text({required bool en}) => en ? this.en : this.ar;

  @override
  String toString() {
    return 'ResponseMessage{en: $en, ar: $ar}';
  }
}

class ResponseMessageMapper extends Mappable<ResponseMessage> {
  @override
  ResponseMessage fromMap(Map<String, dynamic> values) {
    return ResponseMessage(
      en: values['en'],
      ar: values['ar'],
    );
  }

  @override
  Map<String, dynamic> toMap(ResponseMessage object) {
    return {
      'en': object.en,
      'ar': object.ar,
    };
  }
}
