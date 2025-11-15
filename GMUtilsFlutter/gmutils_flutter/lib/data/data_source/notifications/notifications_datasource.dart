import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource_mockup.dart';
import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource_production.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/response.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/storages/general_storage.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';

import '../../../main.dart';
import '../../models/notifications/notification.dart';
import '../../models/notifications/notifications_count.dart';

abstract class NotificationsDataSource {
  static NotificationsDataSource get instance => useProductionData
      ? NotificationsDataSourceProduction()
      : NotificationsDataSourceMockup();

  //----------------------------------------------------------------------------

  static NotificationsCount? _lastNotificationsCount;
  static int? _accountId;

  NotificationsCount? get lastNotificationsCount {
    if (UsersDataSource.instance.cachedUserAccount?.id != _accountId) {
      _lastNotificationsCount = null;
    }

    return _lastNotificationsCount;
  }

  //-------------------------------------------------------------

  static void dispatchNotificationsCountChanged({
    bool? increased,
    String? relatedObjectName,
  }) {
    Logs.print(
      () => 'NotificationsDataSource'
          '.dispatchNotificationsCountChanged('
          'increased: $increased, '
          'relatedObjectName: $relatedObjectName)',
    );

    var userAccount = UsersDataSource.instance.cachedUserAccount;
    if (userAccount == null) {
      Logs.print(
        () => 'NotificationsDataSource'
            '.dispatchNotificationsCountChanged '
            '---> USER-IS-NULL',
      );
      return;
    }

    if (_lastNotificationsCount == null || _accountId != userAccount.id) {
      _accountId = userAccount.id;

      _lastNotificationsCount = NotificationsCount(
        allNotificationsCount: 0,
        unreadMailsCount: 0,
        unreadChatMessagesCount: 0,
      );
    }

    if (increased != null) {
      var x = 1;
      if (!increased) x = -1;

      _lastNotificationsCount!.allNotificationsCount += x;
      if (relatedObjectName == Notification.relatedObjectName_Chat) {
        _lastNotificationsCount!.unreadChatMessagesCount += x;
      }
    }

    App.callObservers(category: "notifications", args: _lastNotificationsCount);
  }

  void observeCount(
    String observerName, {
    required void Function(NotificationsCount n) onUpdate,
  }) {
    Logs.print(() =>
        'NotificationsDataSource.observeCount(observerName: $observerName)');
    App.addObserver(
      category: "notifications",
      observerName: observerName,
      observer: (on, args) {
        onUpdate(args);
      },
    );
  }

  void cancelObserveCount(String observerName) {
    Logs.print(() =>
        'NotificationsDataSource.cancelObserveCount(observerName: $observerName)');
    App.removeObserver(category: "notifications", observerName: observerName);
  }

  //-------------------------------------------------------------

  static void updateFcmTokenForAuthUser(String newToken) async {
    var user = await UsersDataSource.instance.savedUserAccount;
    if (user != null) {
      instance.updateFcmToken(accountId: user.id, fcmToken: newToken);
    } else {
      Logs.print(() => 'NotificationsDataSource.updateFcmTokenForAuthUser '
          '---> NO-AUTH-USER');
    }
  }

  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  late IStorage _storage;

  NotificationsDataSource({IStorage? storage}) {
    _storage = storage ?? GeneralStorage.o('notifications_configurations');
  }

  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  Future<Response<void>> updateFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    final tokenKey = 'fcm_$accountId';
    final dateKey = 'date_$accountId';

    var prevToken = await _storage.retrieve(tokenKey);
    var date = await _storage.retrieve(dateKey);

    if (prevToken == fcmToken) {
      var dt = DateOp().parse(date ?? '', convertToLocalTime: false);
      if (dt != null) {
        var days = DateTime.now().difference(dt).inDays;
        if (days < 0) days *= -1;
        if (days < 15) {
          Logs.print(() =>
              'NotificationsDataSource.updateFcmToken ---> FCM ALREADY SYNCHRONIZED $days DAYS AGO');
          return Response(
            status: Response.statusSuccess,
            message: null,
            data: null,
            httpCode: 200,
          );
        }
      }
    } else if (prevToken != null) {
      await deleteFcmToken(accountId: accountId, fcmToken: prevToken);
    }

    var response = await doUpdateFcmToken(
      accountId: accountId,
      fcmToken: fcmToken,
    );

    if (response.isSuccess) {
      _storage.save(
        tokenKey,
        fcmToken,
      );
      _storage.save(
        dateKey,
        DateOp().formatForDatabase(DateTime.now(), dateOnly: false),
      );
    }

    return response;
  }

  Future<Response<void>> doUpdateFcmToken({
    required int accountId,
    required String fcmToken,
  });

  Future<Response<void>> deleteFcmToken({
    required int accountId,
    required String fcmToken,
  }) async {
    var response =
        await doDeleteFcmToken(accountId: accountId, fcmToken: fcmToken);

    if (response.isSuccess) {
      final tokenKey = 'fcm_$accountId';
      final dateKey = 'date_$accountId';

      await _storage.remove(tokenKey);
      await _storage.remove(dateKey);
    }

    return response;
  }

  Future<Response<void>> doDeleteFcmToken({
    required int accountId,
    required String fcmToken,
  });

  //----------------------------------------------------------------------------

  Future<Response<List<Notification>>> getNotifications({
    required int accountId,
    required int pageNumber,
    required int pageSize,
  }) {
    return doGetNotifications(
      accountId: accountId,
      pageNumber: pageNumber,
      pageSize: pageSize,
    );
  }

  Future<Response<List<Notification>>> doGetNotifications({
    required int accountId,
    required int pageNumber,
    required int pageSize,
  });

  //---------------------------------------------------------------------------

  Future<Response<void>> markNotificationAsRead({
    required int accountId,
    required int notificationId,
    required String relatedObjectName,
  }) async {
    var response = await doMarkNotificationAsRead(
      accountId: accountId,
      notificationId: notificationId,
      relatedObjectName: relatedObjectName,
    );
    if (response.isSuccess) {
      if (_lastNotificationsCount != null) {
        dispatchNotificationsCountChanged(
          increased: false,
          relatedObjectName: relatedObjectName,
        );
      }
    }
    return response;
  }

  Future<Response<void>> doMarkNotificationAsRead({
    required int accountId,
    required int notificationId,
    required String relatedObjectName,
  });

  //----------------------------------------------------------------------------

  //region notifications count
  static bool _requestingNotifCountBusy = false;
  static List<void Function(Response<NotificationsCount>)>? _pendingNotifCountRequesters;

  void getNotificationsCount({
    required UserAccount userAccount,
    required void Function(Response<NotificationsCount>) onComplete,
  }) async {
    if (_requestingNotifCountBusy) {
      _pendingNotifCountRequesters ??= [];
      _pendingNotifCountRequesters!.add(onComplete);
      return;
    }

    _requestingNotifCountBusy = true;

    Response<NotificationsCount> response;
    response = await doGetNotificationsCount(
      accountId: userAccount.id,
    );

    if (response.data != null) {
      _lastNotificationsCount = response.data;
      _accountId = userAccount.id;
      dispatchNotificationsCountChanged();
    }

    onComplete(response);

    var requesters = _pendingNotifCountRequesters;
    _pendingNotifCountRequesters = null;
    requesters?.forEach((f) {
      try {
        f(response);
      } catch (e) {}
    });

    _requestingNotifCountBusy = false;
  }

  Future<Response<NotificationsCount>> doGetNotificationsCount({
    required int accountId,
  });

//endregion
}
