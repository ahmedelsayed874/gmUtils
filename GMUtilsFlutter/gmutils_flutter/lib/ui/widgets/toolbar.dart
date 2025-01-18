import 'package:gmutils_flutter/data/data_source/notifications/notifications_datasource.dart';
import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/screens/user_profile/user_profile_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:flutter/material.dart';

import 'dropdown_action_Properties.dart';
import 'my_widgets.dart';

class Toolbar extends StatefulWidget {
  final ToolbarConfiguration toolbarConfiguration;
  final VoidCallback onNotificationBellClicked;

  const Toolbar({
    required this.toolbarConfiguration,
    required this.onNotificationBellClicked,
    super.key,
  });

  @override
  State<Toolbar> createState() => _ToolbarState();

  static void updateProfilePhoto() {
    _ToolbarState.updateProfilePhoto();
  }

  static const double toolbarHeight = 70.0;
}

class _ToolbarState extends State<Toolbar> {
  static const String toolbarProfileObserverCategory = 'toolbar-profile';
  static const String action_updatePhoto = 'update_photo';

  static void updateProfilePhoto() {
    App.callObservers(
        category: toolbarProfileObserverCategory, args: action_updatePhoto);
  }

  int notificationCount = 0;

  UsersDataSource get usersDataSource => UsersDataSource.instance;

  UserAccount? get userAccount => usersDataSource.cachedUserAccount;

  NotificationsDataSource get notificationsDataSource =>
      NotificationsDataSource.instance;

  @override
  void initState() {
    super.initState();

    if (widget.toolbarConfiguration.showNotificationIcon) {
      var lnc = notificationsDataSource.lastNotificationsCount;
      notificationCount = lnc?.allNotificationsCount ?? 0;

      notificationsDataSource.observeCount('toolbar-$hashCode', onUpdate: (n) {
        notificationCount = n.allNotificationsCount;
        try {
          setState(() {});
        } catch (e) {}
      });

      //get notifications count
      if (notificationsDataSource.lastNotificationsCount == null) {
        notificationsDataSource.getNotificationsCount(
          userAccount: userAccount!,
          onComplete: (r) {},
        );
      }
    }

    App.addObserver(
      category: toolbarProfileObserverCategory,
      observerName: 'toolbar-$hashCode',
      observer: (observerName, args) {
        try {
          setState(() {});
        } catch (e) {}
      },
    );
  }

  @override
  void dispose() {
    notificationsDataSource.cancelObserveCount('toolbar-$hashCode');
    App.removeObserver(
      category: toolbarProfileObserverCategory,
      name: 'toolbar-$hashCode',
    );
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var userPhotoHeight = Toolbar.toolbarHeight - 20;
    var configs = widget.toolbarConfiguration;

    var children = <Widget>[];

    //region menu btn
    if (configs.showMenuButton && !configs.showBackButton) {
      children.add(
        IconButton(
          onPressed: () {
            MyRootWidget.getCurrentScaffoldState(context).openDrawer();
          },
          icon: Icon(
            Icons.menu_sharp,
            color: Res.themes.colors.textOnPrimary,
          ),
        ),
      );
    }
    //endregion

    //region back btn
    if (configs.showBackButton) {
      children.add(
        IconButton(
          onPressed: () {
            if (configs.canNavBack != null) {
              if (configs.canNavBack!()) {
                App.navBack();
              }
            } else {
              App.navBack();
            }
          },
          icon: Icon(
            //App.isEnglish ? Icons.arrow_back_ios : Icons.arrow_forward_ios,
            Icons.arrow_back_ios,
            color: Res.themes.colors.textOnPrimary,
          ),
        ),
      );
    }
    //endregion

    if (!configs.showMenuButton && !configs.showBackButton) {
      children.add(const SizedBox(width: 8));
    }

    //region photo
    if (configs.photo != null) {
      children.add(MyWidgets().userPhotoAvatar(
        photoPath: configs.photo?.photo,
        size: userPhotoHeight,
        orDefaultIcon: configs.photo?.photoAltIcon,
      ));

      children.add(const SizedBox(width: 5));
    }
    //endregion

    //region profile / title
    String line1;
    String line2;
    if (configs.photo == null && configs.title == null && userAccount != null) {
      children.add(MyWidgets().userPhotoAvatar(
        photoPath: userAccount!.personalPhoto,
        size: userPhotoHeight,
        // orDefaultIcon: configs.photoAltIcon,
        onClick: (a) => UserProfileScreen.show(),
      ));

      children.add(const SizedBox(width: 5));

      line1 = userAccount!.fullname;
      line2 = userAccount!.job(App.isEnglish);
    }
    //
    else {
      line1 = configs.title ?? Res.strings.appName;
      line2 = configs.subtitle ?? '';
    }

    const s = '\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t';

    if (line2.length > line1.length) {
      for (var i = 0; i < (line2.length - line1.length); i++) {
        line1 += s;
      }
    }

    children.add(Expanded(
      child: Stack(
        children: [
          Align(
            alignment: App.isEnglish
                ? (line2.isNotEmpty ? Alignment.topLeft : Alignment.centerLeft)
                : (line2.isNotEmpty
                    ? Alignment.topRight
                    : Alignment.centerRight),
            child: Padding(
              padding: EdgeInsets.only(top: line2.isNotEmpty ? 19 : 0),
              child: Text(
                line1,
                maxLines: 1,
                style: Res.themes.defaultTextStyle(
                  textColor: Res.themes.colors.textOnPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          if (line2.isNotEmpty)
            Positioned(
              top: 35,
              child: Text(
                line2,
                maxLines: 1,
                style: Res.themes.defaultTextStyle(
                  textColor: Res.themes.colors.textOnPrimary,
                  textSize: 12,
                ),
              ),
            ),
        ],
      ),
    ));
    //endregion

    //space
    //children.add(const Expanded(child: SizedBox()));

    //region notification bell
    if (widget.toolbarConfiguration.showNotificationIcon) {
      children.add(GestureDetector(
        onTap: widget.onNotificationBellClicked,
        child: Stack(
          children: [
            const Align(
              alignment: Alignment.center,
              child: Icon(
                Icons.notifications,
                //color: Res.themes.colors.secondary,
                color: Colors.white,
              ),
            ),

            //
            if (notificationCount > 0)
              Container(
                margin: EdgeInsets.only(
                  left: App.isEnglish ? 10 : 0,
                  right: App.isEnglish ? 0 : 10,
                  top: 15,
                ),
                padding: EdgeInsets.symmetric(
                  horizontal: notificationCount < 10 ? 7 : 4,
                ),
                decoration: BoxDecoration(
                  color: Res.themes.colors.red,
                  borderRadius: BorderRadius.circular(99),
                ),
                child: Text(
                  notificationCount > 999 ? '+999' : '$notificationCount',
                  style: Res.themes.defaultTextStyle(
                    textColor: Colors.white,
                    textSize: 10,
                  ),
                ),
              ),
          ],
        ),
      ));
    }
    //endregion

    //region more actions
    if (widget.toolbarConfiguration.moreActions?.isNotEmpty == true) {
      children.add(PopupMenuButton<DropdownActionProperties>(
        color: Res.themes.colors.background,
        iconColor: Res.themes.colors.textOnPrimary,
        onSelected: (action) {
          widget.toolbarConfiguration.onActionSelected!(action);
        },
        itemBuilder: (context) =>
            widget.toolbarConfiguration.moreActions!.map((a) {
          return PopupMenuItem(
            value: a,
            child: Row(
              children: [
                Icon(
                  a.icon,
                  size: 20,
                  color: a.iconColor,
                ),
                const SizedBox(width: 7),
                Text(
                  a.text,
                  style: Res.themes.defaultTextStyle(
                    textColor: a.textColor,
                  ),
                ),
              ],
            ),
          );
        }).toList(),
      ));
    }
    //endregion

    //region logo
    children.add(const SizedBox(width: 4));
    children.add(GestureDetector(
      onLongPress: () {
        HomeScreen.show(); //navigate to home on logo long clicked
      },
      child: Image.asset(
        Res.images.logoColoredSmallWithoutText,
        height: 45,
      ),
    ));
    //endregion

    children.add(const SizedBox(width: 4));

    /*return Container(
      height: Toolbar.toolbarHeight,
      decoration: BoxDecoration(
        color: Res.themes.colors.primary,
        boxShadow: [const BoxShadow()],
      ),
      child: Row(children: children),
    );*/

    return SizedBox(
      height: Toolbar.toolbarHeight + 10,
      child: Stack(
        children: [
          //Container(color: Colors.green,),
          SizedBox(
            height: Toolbar.toolbarHeight + 10,
            width: double.maxFinite,
            child: Transform.flip(
              flipX: App.isEnglish,
              child: Image.asset(
                Res.images.toolbar,
                fit: BoxFit.fill,
              ),
            ),
          ),
          //
          Padding(
            padding: const EdgeInsets.only(top: 35),
            child: Opacity(
              opacity: 0.5,
              child: SizedBox(
                height: Toolbar.toolbarHeight + 10,
                width: double.maxFinite,
                child: Transform.flip(
                  flipX: App.isEnglish,
                  child: Image.asset(
                    Res.images.toolbar,
                    fit: BoxFit.fill,
                  ),
                ),
              ),
            ),
          ),
          Row(children: children),
        ],
      ),
    );
  }
}

//-----------------------------------------------------------------------------

class ToolbarConfiguration {
  final bool showMenuButton;
  final bool showBackButton;
  final bool Function()? canNavBack;
  final String? title;
  final String? subtitle;
  final PhotoOnToolbar? photo;
  final bool showNotificationIcon;
  final List<DropdownActionProperties>? moreActions;
  final void Function(DropdownActionProperties)? onActionSelected;

  ToolbarConfiguration({
    this.showMenuButton = true,
    required this.showBackButton,
    this.canNavBack,
    this.title,
    this.subtitle,
    this.photo,
    this.showNotificationIcon = true,
    this.moreActions,
    this.onActionSelected,
  }) {
    if (moreActions != null) {
      assert(onActionSelected != null);
    }
  }
}

class PhotoOnToolbar {
  final String? photo;
  final IconData? photoAltIcon;

  PhotoOnToolbar({
    required this.photo,
    required this.photoAltIcon,
  });
}