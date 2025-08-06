import 'package:flutter/material.dart';

import '../../data/data_source/notifications/notifications_datasource.dart';
import '../../data/data_source/users/users_datasource.dart';
import '../../data/models/notifications/notification.dart' as n;
import '../../resources/_resources.dart';
import '../../services/notifications/notifications_handler.dart';
import '../../zgmutils/gm_main.dart';
import '../../zgmutils/ui/utils/screen_utils.dart';
import '../screens/notifications/notifications_screen.dart';

class SmallNotificationsList extends StatefulWidget {
  final int width;
  final int height;
  final Color backgroundColor;

  const SmallNotificationsList({
    this.width = 250,
    this.height = 300,
    this.backgroundColor = Colors.white,
    super.key,
  });

  @override
  State<SmallNotificationsList> createState() => _SmallNotificationsListState();

  static void updateList({n.Notification? notification}) {
    if (notification == null) {
      notifications = null;
    } else {
      notifications ??= [];
      notifications?.insert(0, notification);
    }
  }
}

List<n.Notification>? notifications;
bool isNotificationsFetched = false;
int currentAccountId = 0;

class _SmallNotificationsListState extends State<SmallNotificationsList> {
  int pageNumber = 1;
  final int pageSize = 20;
  bool allowLoadMore = true;
  int selectedIndex = -1;

  //---------------------------------------------------------------------------

  void fetchNotifications() async {
    var response = await NotificationsDataSource.instance.getNotifications(
      accountId: accountId,
      pageNumber: pageNumber,
      pageSize: pageSize,
    );

    if (response.isSuccess) {
      currentAccountId = accountId;
      isNotificationsFetched = true;

      if (pageNumber == 1) {
        notifications = [];
      }

      notifications!.addAll(response.data ?? []);
      pageNumber++;
      allowLoadMore = response.data?.length == pageSize;

      setState(() {});
    } else {
      const ScreenUtils().showErrorMessage(response.errorMessage, onRetry: () {
        fetchNotifications();
      });
    }
  }

  Future<void> refreshNotificationList() async {
    pageNumber = 1;
    fetchNotifications();
  }

  int get accountId {
    return UsersDataSource.instance.cachedUserAccount!.id;
  }

  //---------------------------------------------------------------------------

  @override
  Widget build(BuildContext context) {
    if (notifications == null ||
        !isNotificationsFetched ||
        currentAccountId != accountId) {
      fetchNotifications();
    }

    var length = notifications?.length;
    double height;

    var children = <Widget>[];
    if (length == null) {
      height = 40;

      children.add(const Center(
        child: Padding(
          padding: EdgeInsets.all(8.0),
          child: SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(),
          ),
        ),
      ));
    }

    //
    else if (length == 0) {
      notifications = null;

      height = 50;

      children.add(Center(
        child: Padding(
          padding: const EdgeInsets.all(10),
          child: Text(Res.strings.you_dont_have_notifications),
        ),
      ));
    }

    //
    else {
      height = (60.0 * length) + 50;

      children.add(Expanded(
        child: RefreshIndicator(
          onRefresh: refreshNotificationList,
          child: ListView.builder(
              itemCount: length,
              itemBuilder: (context, index) {
                return notificationItemWidget(notifications![index], index);
              }),
        ),
      ));

      children.add(const Divider(height: 4));

      children.add(SizedBox(
        width: double.maxFinite,
        child: TextButton(
          onPressed: gotoNotificationsScreen,
          style: ButtonStyle(
            foregroundColor:
                WidgetStatePropertyAll(Res.themes.colors.secondary),
          ),
          child: Text(Res.strings.see_more),
        ),
      ));
    }

    if (height > widget.height) height = widget.height.toDouble();

    return Container(
      decoration: BoxDecoration(
        color: widget.backgroundColor,
        border: Border.all(),
        borderRadius: BorderRadius.circular(4),
      ),
      width: widget.width.toDouble(),
      height: height,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: children,
      ),
    );
  }

  Widget notificationItemWidget(n.Notification notification, int index) {
    return GestureDetector(
      onTap: () => onNotificationClicked(notification, index),
      child: Container(
        color: selectedIndex == index
            ? Res.themes.colors.secondary.withOpacity(0.1)
            : (notification.isRead
                ? null
                : Res.themes.colors.secondary.withOpacity(0.3)),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const SizedBox(height: 5),
            Row(
              //crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(width: 9),

                Padding(
                  padding: const EdgeInsets.only(top: 5),
                  child: Icon(
                    Icons.notifications_active_outlined,
                    color: Res.themes.colors.primary,
                  ),
                ),

                const SizedBox(width: 9),

                //
                Expanded(
                  child: Text.rich(
                    TextSpan(
                      text:
                          '${App.isEnglish ? notification.titleEn : notification.titleAr}: ',
                      style: Res.themes.defaultTextStyle(
                        fontWeight: FontWeight.bold,
                        textSize: 13,
                      ),
                      children: [
                        TextSpan(
                          text: App.isEnglish
                              ? notification.bodyEn
                              : notification.bodyAr,
                          style: Res.themes.defaultTextStyle(
                            textSize: 12,
                          ),
                        ),
                      ],
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.fade,
                  ),
                ),

                const SizedBox(width: 9),
              ],
            ),
            Container(
              color: Res.themes.colors.secondary.withAlpha(100),
              height: 1,
              margin: const EdgeInsets.only(top: 5),
            ),
          ],
        ),
      ),
    );
  }

  //---------------------------------------------------------------------------

  void onNotificationClicked(n.Notification notification, int index) {
    setState(() {
      notification.isRead = true;
      selectedIndex = index;
    });

    Future.delayed(
        const Duration(milliseconds: 300),
        () => setState(() {
              selectedIndex = -1;

              NotificationsHandler.openCorrespondingScreen(
                notification,
                onError: null,
              );
            }));
  }

  void gotoNotificationsScreen() {
    NotificationsScreen.show(
      initialNotifications: notifications,
      pageSize: pageSize,
    );
  }
}
