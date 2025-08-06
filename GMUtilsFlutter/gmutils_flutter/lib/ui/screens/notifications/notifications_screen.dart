import 'package:flutter/material.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart'
    as model;
import 'package:gmutils_flutter/data/models/notifications/notification.dart'
    as n;
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/services/notifications/notifications_handler.dart';
import 'package:gmutils_flutter/ui/widgets/page_layout.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';

import '../../../zgmutils/ui/utils/base_stateful_state.dart';
import '../../widgets/toolbar.dart';
import 'notifications_screen_driver.dart';

class NotificationsScreen extends StatefulWidget {
  static void show({
    required List<model.Notification>? initialNotifications,
    required int? pageSize,
  }) {
    App.navTo(NotificationsScreen(
      initialNotifications: initialNotifications,
      pageSize: pageSize,
    ));
  }

  final List<model.Notification>? initialNotifications;
  final int? pageSize;

  const NotificationsScreen({
    required this.initialNotifications,
    required this.pageSize,
    super.key,
  });

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends BaseState<NotificationsScreen>
    implements NotificationsScreenDelegate {
  late NotificationsScreenDriverAbs screenDriver;
  int selectedIndex = -1;

  @override
  void initState() {
    super.initState();
    screenDriver = NotificationsScreenDriver(
      this,
      initialNotifications: widget.initialNotifications,
      pageSize: widget.pageSize,
    );
  }

  @override
  Widget build(BuildContext context) {
    return PageLayout(
      toolbarConfiguration: ToolbarConfiguration(
        showBackButton: true,
        title: null,
        subtitle: null,
        showNotificationIcon: false,
      ),
      title: Res.strings.notifications,
      hideBottomNavBar: false,
      child: body,
    );
  }

  Widget body(BuildContext context) {
    var children = <Widget>[];

    var count = screenDriver.notificationsCount;

    //wait spinner
    if (count == null) {
      children.add(const Center(
        child: SizedBox(
          width: 20,
          height: 20,
          child: CircularProgressIndicator(),
        ),
      ));
      children.add(const Expanded(child: SizedBox()));
    }

    //no data
    else if (count == 0) {
      children.add(Expanded(
        child: RefreshIndicator(
          child: ListView(
            children: [
              Center(
                child: Text(
                  Res.strings.you_dont_have_notifications,
                  style: Res.themes.defaultTextStyle(
                    textColor: Res.themes.colors.hint,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
          onRefresh: () => screenDriver.refresh(),
        ),
      ));
    }

    //list
    else {
      children.add(Expanded(
        child: RefreshIndicator(
          child: ListView.builder(
            itemCount: count,
            itemBuilder: (context, index) {
              var item = screenDriver.notificationAt(index);
              if (item == null) {
                return const Center(
                  child: SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(),
                  ),
                );
              }
              //
              else {
                return notificationItemWidget(item, index);
              }
            },
          ),
          onRefresh: () => screenDriver.refresh(),
        ),
      ));
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  Widget notificationItemWidget(n.Notification notification, int index) {
    return GestureDetector(
      onTap: () => onNotificationClicked(notification, index),
      child: Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Card(
          elevation: 5,
          surfaceTintColor: selectedIndex == index
              ? Res.themes.colors.secondary.withOpacity(0.1)
              : (notification.isRead
                  ? null
                  : Res.themes.colors.red), //.withOpacity(0.9)),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
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
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      //title
                      Text(
                        App.isEnglish
                            ? notification.titleEn
                            : notification.titleAr,
                        style: Res.themes.defaultTextStyle(
                          fontWeight: FontWeight.bold,
                        ),
                      ),

                      //body
                      Text(
                        App.isEnglish
                            ? notification.bodyEn
                            : notification.bodyAr,
                      ),

                      //time
                      const SizedBox(height: 10),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Text(
                            DateOp().formatForUser2(
                              notification.createdAt,
                              en: App.isEnglish,
                              dateOnly: false,
                            )!,
                            style: Res.themes.defaultTextStyle(
                              textSize: 13,
                              textColor: Res.themes.colors.hint,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
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
      }),
    );
  }
}
