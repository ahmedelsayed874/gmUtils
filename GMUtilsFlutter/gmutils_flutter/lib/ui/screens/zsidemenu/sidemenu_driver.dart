import 'package:flutter/material.dart';

import '../../../data/data_source/users/users_datasource.dart';
import '../../../data/models/users/user_account.dart';
import '../../../resources/_resources.dart';
import '../../../services/configs/app_configs.dart';
import '../../../zgmutils/gm_main.dart';
import '../../utils/iscreen_driver.dart';

abstract class SidemenuDelegate extends IScreenDriverDependantDelegate {
  void openHomeScreen();

  void openBlsWebsiteScreen();

  void openBananWebsiteScreen();

  void openNotificationsScreen();

  void onReportIssue();

  void testNewChatNotification();

  void testNewChatMessageNotification();

  void sendGlobalNotification();
}

abstract class SidemenuDriverAbs extends IScreenDriver {
  late SidemenuDelegate delegate;

  SidemenuDriverAbs(
    this.delegate, {
    required super.usersDataSource,
  }) : super(delegate) {
    _buildSidemenuItems();
  }

  UserAccount get authUserAccount => usersDataSource.cachedUserAccount!;

  //---------------------------------------------------------------------------

  List<SidemenuItem>? _sidemenuItems;

  void _buildSidemenuItems() {
    var account = authUserAccount;
    var accountId = account.id;

    final key = 'sidemenu_${App.isEnglish}_$accountId';
    var data = App.globalVariables[key];
    _sidemenuItems = data;

    if (_sidemenuItems == null) {
      final appConfigsData = AppConfigs.appConfigsData;

      _sidemenuItems = [];

      //home
      _sidemenuItems!.add(SidemenuItem(
        iconRes: null,
        iconData: Icons.home_filled,
        text: Res.strings.home,
        action: delegate.openHomeScreen,
        submenu: null,
      ));

      // if (_sidemenuItems!.last.submenu!.isEmpty) {
      //   _sidemenuItems!.removeLast();
      // }


      //external links
      _sidemenuItems!.add(SidemenuItem(
          iconRes: null,
          iconData: Icons.link,
          text: Res.strings.external_links,
          action: null,
          submenu: [
            //bls website
            SidemenuItem(
              iconRes: Res.images.logoColoredSmallWithoutText,
              iconData: null,
              text: Res.strings.bls_website,
              action: delegate.openBlsWebsiteScreen,
              submenu: null,
            ),

            //banan website

            SidemenuItem(
              iconRes: Res.images.logoColoredSmallWithoutText,
              iconData: null,
              text: Res.strings.banan_website,
              action: delegate.openBananWebsiteScreen,
              submenu: null,
            ),
          ]));

      //notifications
      _sidemenuItems!.add(SidemenuItem(
        iconRes: null,
        iconData: Icons.notifications_active,
        text: Res.strings.notifications,
        action: delegate.openNotificationsScreen,
        submenu: null,
      ));

      //report issue
      _sidemenuItems!.add(SidemenuItem(
        iconRes: null,
        iconData: Icons.report_problem,
        text: Res.strings.reportIssue,
        action: delegate.onReportIssue,
        submenu: null,
      ));

      //developers
      usersDataSource.savedCredentials.then((v) {
        if (v != null && appConfigsData.isDeveloper(v.value1)) {
          _sidemenuItems!.add(SidemenuItem(
              iconRes: null,
              iconData: Icons.developer_mode,
              text: 'Developer',
              action: null,
              submenu: [
                //test new chat notification
                SidemenuItem(
                  iconRes: null,
                  iconData: Icons.check,
                  text: 'Test New Chat Notification',
                  action: delegate.testNewChatNotification,
                  submenu: null,
                ),

                //test new chat message notification
                SidemenuItem(
                  iconRes: null,
                  iconData: Icons.check,
                  text: 'Test New Message (Chat) Notification',
                  action: delegate.testNewChatMessageNotification,
                  submenu: null,
                ),

                //send broadcast message
                SidemenuItem(
                  iconRes: null,
                  iconData: Icons.send,
                  text: 'Send Notification',
                  action: () => Future.delayed(Duration(milliseconds: 500), () {
                    delegate.sendGlobalNotification();
                  }),
                  submenu: null,
                ),
              ]));
        }
      });

      App.globalVariables[key] = _sidemenuItems;

      Future.delayed(const Duration(milliseconds: 300), () {
        delegate.updateView();
      });
    }
  }

  int? get itemsCount {
    if (_sidemenuItems == null) {
      Future.delayed(Duration(milliseconds: 600), () {
        delegate.updateView();
      });
    }

    return _sidemenuItems?.length;
  }

  SidemenuItem sidemenuItemAt(int index) {
    return _sidemenuItems![index];
  }
}

class SidemenuDriver extends SidemenuDriverAbs {
  SidemenuDriver(super.delegate)
      : super(
          usersDataSource: UsersDataSource.instance,
        );
}

class SidemenuItem {
  final String? iconRes;
  final IconData? iconData;
  final String text;
  final VoidCallback? action;
  final List<SidemenuItem>? submenu;
  bool isSubmenuDisplayed = false;

  SidemenuItem({
    required this.iconRes,
    required this.iconData,
    required this.text,
    required this.action,
    required this.submenu,
  });
}
