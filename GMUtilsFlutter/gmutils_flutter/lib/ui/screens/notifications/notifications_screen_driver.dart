import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';

abstract class NotificationsScreenDelegate
    extends IScreenDriverDependantDelegate {}

abstract class NotificationsScreenDriverAbs extends IScreenDriver {
  late NotificationsScreenDelegate delegate;
  late NotificationsDataSource notificationsDataSource;

  NotificationsScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
    required this.notificationsDataSource,
    required List<Notification>? initialNotifications,
    required int? pageSize,
  }) : super(delegate) {
    this.pageSize = pageSize ?? 20;

    if (initialNotifications?.isNotEmpty == true) {
      _notifications = initialNotifications;
      _allowLoadMore = _notifications?.length == this.pageSize;
    } else {
      _allowLoadMore = true;
      _fetchNotifications();
    }
  }

  UserAccount get authUserAccount => usersDataSource.cachedUserAccount!;

  //---------------------------------------------------------------------------

  List<Notification>? _notifications;
  int _pageNumber = 1;
  late final int pageSize;
  bool _allowLoadMore = true;

  void _fetchNotifications({bool reset = false}) async {
    if (!reset && !_allowLoadMore) return;

    var accountId = authUserAccount.id;

    var response = await notificationsDataSource.getNotifications(
      accountId: accountId,
      pageNumber: reset ? 1 : _pageNumber,
      pageSize: pageSize,
    );

    if (response.isSuccess) {
      if (reset) {
        _pageNumber = 1;
      }

      if (_pageNumber == 1) {
        _notifications = [];
      }

      _notifications!.addAll(response.data ?? []);
      _pageNumber++;
      _allowLoadMore = response.data?.length == pageSize;

      delegate.updateView();
    } else {
      delegate.showErrorMessage(response.errorMessage, onRetry: () {
        _fetchNotifications(reset: reset);
      });
    }
  }

  int? get notificationsCount {
    if (_notifications == null) return null;
    int count = _notifications!.length;
    if (_allowLoadMore) count++;
    return count;
  }

  Notification? notificationAt(int index) {
    if (index == _notifications?.length) {
      _fetchNotifications();
    }

    if (index >= 0 && index < (_notifications?.length ?? 0)) {
      return _notifications![index];
    }

    return null;
  }

  Future<void> refresh() async {
    _fetchNotifications(reset: true);
  }
}

class NotificationsScreenDriver extends NotificationsScreenDriverAbs {
  NotificationsScreenDriver(
    super.delegate, {
    required super.initialNotifications,
    required super.pageSize,
  }) : super(
          usersDataSource: UsersDataSource.instance,
          notificationsDataSource: NotificationsDataSource.instance,
        );
}
