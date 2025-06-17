import '../../../zgmutils/data_utils/web/response.dart';
import '../../../zgmutils/data_utils/web/web_request_executors.dart';
import '../../../zgmutils/data_utils/web/web_url.dart';
import '../../../zgmutils/utils/mappable.dart';

class UsefulApis {
  Future<Response<_TimeOfAreaModel>> currentTimeInCairo() async {
    Response<_TimeOfAreaModel> res = await WebRequestExecutor().executeGet(
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
