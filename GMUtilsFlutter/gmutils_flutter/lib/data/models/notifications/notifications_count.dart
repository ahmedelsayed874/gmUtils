import 'package:gmutils_flutter/zgmutils/utils/mappable.dart';

class NotificationsCount {
  int allNotificationsCount;
  int unreadMailsCount;
  int unreadChatMessagesCount;

  NotificationsCount({
    required this.allNotificationsCount,
    required this.unreadMailsCount,
    required this.unreadChatMessagesCount,
  });

  @override
  String toString() {
    return 'NotificationsCount{allNotificationsCount: $allNotificationsCount, unreadMailsCount: $unreadMailsCount, unreadChatMessagesCount: $unreadChatMessagesCount}';
  }
}

class NotificationsCountMapper extends Mappable<NotificationsCount> {
  @override
  NotificationsCount fromMap(Map<String, dynamic> values) {
    return NotificationsCount(
      allNotificationsCount: values['allNotificationsCount'],
      unreadMailsCount: values['unreadMailsCount'],
      unreadChatMessagesCount: values['unreadChatMessagesCount'],
    );
  }


  @override
  Map<String, dynamic> toMap(NotificationsCount object) {
    return {
      'allNotificationsCount': object.allNotificationsCount,
      'unreadMailsCount': object.unreadMailsCount,
      'unreadChatMessagesCount': object.unreadChatMessagesCount,
    };
  }

}
