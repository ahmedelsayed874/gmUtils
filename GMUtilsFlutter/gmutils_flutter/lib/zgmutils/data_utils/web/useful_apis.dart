import '../utils/mappable.dart';
import 'web_response.dart';
import 'web_request_executors.dart';
import 'web_url.dart';

class UsefulApis {
  Future<WebResponse<_TimeOfAreaModel>> currentTimeInCairo() async {
    WebResponse<_TimeOfAreaModel> res = await WebRequestExecutor().executeGet(
      _TimeOfAreaUrl(),
    );
    return res;
  }
}

class _TimeOfAreaUrl extends GetUrl<_TimeOfAreaModel> {
  _TimeOfAreaUrl()
      : super(
          domain: 'http://worldtimeapi.org/',
          fragments: 'api/timezone/Africa/Cairo/',
          endPoint: '',
          headers: {},
          responseMapper: _TimeOfAreaModelMapper(),
          queries: null,
        );
}

//----------------------------------------------------------

class _TimeOfAreaModel {
  String datetime;
  String utcDatetime;

  _TimeOfAreaModel({
    required this.datetime,
    required this.utcDatetime,
  });
}

class _TimeOfAreaModelMapper extends Mappable<_TimeOfAreaModel> {
  @override
  _TimeOfAreaModel fromMap(Map<String, dynamic> values) {
    return _TimeOfAreaModel(
        datetime: values['datetime'],
      utcDatetime: values['utc_datetime'],
    );
  }

  @override
  Map<String, dynamic> toMap(_TimeOfAreaModel object) {
    return {
      'datetime' : object.datetime,
      'utc_datetime' : object.utcDatetime,
    };
  }
}
