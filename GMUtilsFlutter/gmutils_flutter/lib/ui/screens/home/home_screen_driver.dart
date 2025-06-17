import 'dart:convert';

import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/notifications/notifications_count.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/ui/utils/iscreen_driver.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/response.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/web_request_executors.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/web/web_url.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';
import 'package:gmutils_flutter/zgmutils/utils/observable_value.dart';
import 'package:gmutils_flutter/zgmutils/utils/pairs.dart';

abstract class HomeScreenDelegate extends IScreenDriverDependantDelegate {}

abstract class HomeScreenDriverAbs extends IScreenDriver {
  late HomeScreenDelegate delegate;
  final NotificationsDataSource notificationsDataSource;

  HomeScreenDriverAbs(
    this.delegate, {
    required super.usersDataSource,
    required this.notificationsDataSource,
  }) : super(delegate) {
    _subscribeNotificationCounterObserver();

    //get notifications count
    if (notificationsDataSource.lastNotificationsCount == null) {
      usersDataSource.savedUserAccount.then((a) {
        if (a != null) {
          notificationsDataSource.getNotificationsCount(
            userAccount: a,
            onComplete: (r) {},
          );
        }
      });
    }
  }

  UserAccount get authUserAccount => usersDataSource.cachedUserAccount!;

  //---------------------------------------------------------------------------

  void _subscribeNotificationCounterObserver() {
    notificationsDataSource.observeCount('home', onUpdate: (n) {
      try {
        delegate.updateView();
      } catch (e) {}
    });
  }

  void unsubscribeNotificationCounterObserver() {
    notificationsDataSource.cancelObserveCount('home');
  }

  NotificationsCount? get lastNotificationsCount =>
      notificationsDataSource.lastNotificationsCount;

  /////////////////////////////////////////////////////////////////////////////

  ObservableValue<Pair<List<String>, String>>? _currentDateTimeObservable;
  List<String>? _hijriDate;
  bool _hijri = true;
  bool _timerStopped = false;

  ObservableValue<Pair<List<String>, String>> get currentDateTimeObservable {
    if (_currentDateTimeObservable == null) {
      _currentDateTimeObservable = ObservableValue();
      _fetchDate();
    }

    return _currentDateTimeObservable!;
  }

  void _fetchDate() async {
    var dt = DateTime.now();
    var d = '${dt.day < 10 ? '0' : ''}${dt.day}';
    var m = '${dt.month < 10 ? '0' : ''}${dt.month}';

    var response = await WebRequestExecutor().executeGet(
      GetUrl(
          domain: 'https://api.aladhan.com/v1/gToH/$d-$m-${dt.year}',
          fragments: '',
          endPoint: '',
          responseMapper: null,
          queries: null,
          responseEncoder: (response) {
            try {
              var map = jsonDecode(response) as Map<String, dynamic>;
              var dateMap = map['data']['hijri'];
              var day = dateMap['day'];
              var month = dateMap['month']['ar'];
              var year = dateMap['year'];

              return Response.success(data: ["$day", "$month", "$year"]);
            } catch (e) {
              return Response.failed(error: e.toString(), httpCode: 400);
            }
          }),
    );

    if (response.isSuccess) {
      _hijriDate = response.data;
      // _dateFetchAt = DateTime.now().millisecondsSinceEpoch;
    } else {
      _hijriDate = null;
      // _dateFetchAt = null;
    }

    _timerStopped = false;
    _startTimer();
  }

  void _startTimer() {
    if (_timerStopped) return;

    final now = DateTime.now();
    List<String> date = [];

    if (_hijri && _hijriDate != null) {
      date.addAll(_hijriDate!);
    }

    _hijri = !_hijri;

    if (date.isEmpty) {
      date = [
        '${now.day < 10 ? '0' : ''}${now.day}',
        DateOp().getMonthName(now.month, App.isEnglish, short: false),
        '${now.year}',
      ];
      //date = DateOp().formatForUser(now, en: App.isEnglish, dateOnly: true);
    }

    var a = now.hour >= 12
        ? (App.isEnglish ? 'PM' : 'م')
        : (App.isEnglish ? 'AM' : 'ص');

    int hour = now.hour;
    if (hour == 0) hour = 12;
    if (hour > 12) hour -= 12;
    var h = '${hour < 10 ? '0' : ''}$hour';

    var m = '${now.minute < 10 ? '0' : ''}${now.minute}';
    //var s = '${now.second < 10 ? '0' : ''}${now.second}';

    _currentDateTimeObservable?.value = Pair(
      value1: date,
      //value2: '$h:$m:$s $a',
      value2: '$h:$m $a',
    );

    Future.delayed(const Duration(seconds: 5), () {
      if (_timerStopped) return;
      _startTimer();
    });
  }

  void dispose() {
    _timerStopped = true;
  }
}

class HomeScreenDriver extends HomeScreenDriverAbs {
  HomeScreenDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
          notificationsDataSource: NotificationsDataSource.instance,
        );
}
