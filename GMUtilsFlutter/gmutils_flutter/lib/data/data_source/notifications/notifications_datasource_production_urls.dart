import 'package:gmutils_flutter/data/data_source/requests_helper.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/web_url.dart';

import '../../models/notifications/notifications_count.dart';
import '../../models/response.dart';

class UpdateFcmTokenUrl extends PostUrl<Response<void>> {
  UpdateFcmTokenUrl({
    required int accountId,
    required String fcmToken,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'UpdateFcmToken',
          dataMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'fcmToken': fcmToken,
          },
        );
}

class DeleteFcmTokenUrl extends PostUrl<Response<void>> {
  DeleteFcmTokenUrl({
    required int accountId,
    required String fcmToken,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'DeleteFcmToken',
          dataMapper: ResponseMapper(dataMapper: null),
          queries: null,
          params: {
            'accountId': accountId,
            'fcmToken': fcmToken,
          },
        );
}

//------------------------------------------------------------------------------

class GetNotificationsUrl extends GetUrl<Response<List<Notification>>> {
  GetNotificationsUrl({
    required int accountId,
    required int pageNumber,
    required int pageSize,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'GetNotifications',
          dataMapper: ResponseMapper(dataMapper: NotificationMapper()),
          queries: {
            'accountId': accountId.toString(),
            'pageNumber': pageNumber.toString(),
            'pageSize': pageSize.toString(),
          },
        );
}

class MarkNotificationAsReadUrl extends PostUrl<Response<void>> {
  MarkNotificationAsReadUrl({
    required int accountId,
    required int notificationId,
    required String relatedObjectName,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'MarkNotificationAsRead',
          dataMapper: ResponseMapper(dataMapper: null),
          queries: {
            'accountId': accountId.toString(),
            'notificationId': notificationId.toString(),
          },
          params: {
            'accountId': accountId,
            'notificationId': notificationId,
            'relatedObjectName': relatedObjectName,
          },
        );
}

//------------------------------------------------------------------------------

class GetNotificationsCountUrl
    extends GetUrl<Response<NotificationsCount>> {
  GetNotificationsCountUrl({
    required int accountId,
  }) : super(
          domain: RequestsHelper.instance.serverUrl,
          fragments: RequestsHelper.instance.apisPath,
          headers: RequestsHelper.instance.headers,
          endPoint: 'GetNotificationsCount',
          dataMapper: ResponseMapper(dataMapper: NotificationsCountMapper()),
          queries: {
            'accountId': accountId.toString(),
          },
        );
}
