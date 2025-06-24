import 'dart:math';

import 'package:gmutils_flutter/zgmutils/data_utils/web/web_request_executors.dart';
import 'package:gmutils_flutter/zgmutils/utils/result.dart';
import 'package:gmutils_flutter/zgmutils/utils/string_set.dart';

import '../../../zgmutils/utils/pairs.dart';
import '../../models/notifications/notification.dart';
import '../../models/notifications/notifications_count.dart';
import '../../models/response.dart';
import 'notifications_datasource.dart';

class NotificationsDataSourceMockup extends NotificationsDataSource {
  List<Notification>? __notifications;

  List<Notification> get _notifications {
    __notifications ??= List.generate(33, (i) {
      var types = [
        Notification.relatedObjectName_Mail,
        Notification.relatedObjectName_Chat,
        Notification.relatedObjectName_ChatMessage,
        Notification.relatedObjectName_ChatMessageReaction,
        Notification.relatedObjectName_ChatMessagePollResultUpdated,
      ];

      var type = types[new Random().nextInt(types.length)];

      return Notification(
        notificationId: i,
        titleEn: '#$i: $type',
        titleAr: '#$i: $type',
        bodyEn: 'Body: #$i: $type',
        bodyAr: 'Body: #$i: $type',
        isRead: new Random().nextBool(),
        relatedObjectName: type,
        relatedObjectId: 1,
        extraParameters: null,
        createdAt: '2025-01-18 13:30:00',
      );
    });

    return __notifications!;
  }

  @override
  Future<Response<void>> doUpdateFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'updateFcmToken(accountId: $accountId, fcmToken: $fcmToken)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

  @override
  Future<Response<void>> doDeleteFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'deleteFcmToken(accountId: $accountId, fcmToken: $fcmToken)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

//------------------------------------------------------------------------------

  @override
  Future<Response<List<Notification>>> doGetNotifications({
    required int accountId,
    required int pageNumber,
    required int pageSize,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName:
          'getNotifications(accountId: $accountId, pageNumber: $pageNumber, pageSize: $pageSize)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                _notifications,
              ),
              value2: NotificationMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

  @override
  Future<Response<void>> doMarkNotificationAsRead({
    required int accountId,
    required int notificationId,
    required String relatedObjectName,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'markNotificationAsRead('
          'accountId: $accountId, '
          'notificationId: $notificationId, '
          'relatedObjectName: $relatedObjectName'
          ')',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          return Pair(
              value1: Result(
                null,
              ),
              value2: null);
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

//------------------------------------------------------------------------------

  @override
  Future<Response<NotificationsCount>> doGetNotificationsCount({
    required int accountId,
  }) async {
    var response = await WebRequestExecutor().createDummyResponse(
      apiName: 'getTeacherNotificationsCount(accountId: $accountId)',
      responseData: () {
        if (Random().nextInt(100) > 97) {
          return Pair(
              value1: Result(
                null,
                message: StringSet('Any Error', 'أي خطأ'),
              ),
              value2: null);
        } else {
          var notifications = _notifications;

          return Pair(
              value1: Result(
                NotificationsCount(
                  allNotificationsCount: notifications.length,
                  unreadMailsCount: notifications
                      .where((element) =>
                          !element.isRead &&
                          element.relatedObjectName == Notification.relatedObjectName_Mail)
                      .length,
                  unreadChatMessagesCount: notifications
                      .where((element) =>
                          !element.isRead &&
                          element.relatedObjectName ==
                              Notification.relatedObjectName_Chat)
                      .length,
                ),
              ),
              value2: NotificationsCountMapper());
        }
      },
    );

    return Response.fromDummyResponse(response);
  }

}
