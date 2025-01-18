import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource_production_urls.dart';

import '../../../zgmutils/data_utils/web/web_request_executors.dart';
import '../../models/notifications/notification.dart';
import '../../models/notifications/notifications_count.dart';
import '../../models/response.dart';
import 'notifications_datasource.dart';

class NotificationsDataSourceProduction extends NotificationsDataSource {
  @override
  Future<Response<void>> doUpdateFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    var url = UpdateFcmTokenUrl(accountId: accountId, fcmToken: fcmToken);
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<void>> doDeleteFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    var url = DeleteFcmTokenUrl(
      accountId: accountId,
      fcmToken: fcmToken,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

//------------------------------------------------------------------------------

  @override
  Future<Response<List<Notification>>> doGetNotifications({
    required int accountId,
    required int pageNumber,
    required int pageSize,
  }) async {
    var url = GetNotificationsUrl(
      accountId: accountId,
      pageNumber: pageNumber,
      pageSize: pageSize,
    );
    var response = await WebRequestExecutor().executeGet(url);
    return Response.fromWebResponse(response);
  }

  @override
  Future<Response<void>> doMarkNotificationAsRead({
    required int accountId,
    required int notificationId,
    required String relatedObjectName,
  }) async {
    var url = MarkNotificationAsReadUrl(
      accountId: accountId,
      notificationId: notificationId,
        relatedObjectName: relatedObjectName,
    );
    var response = await WebRequestExecutor().executePost(url);
    return Response.fromWebResponse(response);
  }

//------------------------------------------------------------------------------

  @override
  Future<Response<NotificationsCount>> doGetNotificationsCount({
    required int accountId,
  }) async {
    var url = GetNotificationsCountUrl(
      accountId: accountId,
    );
    var response = await WebRequestExecutor().executeGet(url);
    return Response.fromWebResponse(response);
  }

}
