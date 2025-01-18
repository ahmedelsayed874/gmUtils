import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/services/configs/app_configs.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/screens/user_profile/user_profile_screen.dart';
import 'package:gmutils_flutter/ui/widgets/toolbar.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:flutter/material.dart';

import '../screens/zsidemenu/sidemenu.dart';
import 'small_notifications_list.dart';


class PageLayout extends StatefulWidget {
  final ToolbarConfiguration toolbarConfiguration;
  final Type? screenClassType;
  final String? title;
  final String? subtitle;
  final Widget Function(BuildContext context) child;
  final EdgeInsets padding;
  final bool enableScroll;
  final bool resizeToAvoidBottomInset;
  final bool? hideBottomNavBar;
  final Widget? floatingActionButtonChild;
  final String? floatingActionButtonTooltip;
  final VoidCallback? floatingActionButtonAction;

  const PageLayout({
    required this.toolbarConfiguration,
    this.screenClassType,
    this.title,
    this.subtitle,
    required this.child,
    this.floatingActionButtonChild,
    this.floatingActionButtonTooltip,
    this.floatingActionButtonAction,
    this.enableScroll = false,
    this.resizeToAvoidBottomInset = true,
    this.hideBottomNavBar,
    this.padding = const EdgeInsets.all(10),
    super.key,
  });

  @override
  State<PageLayout> createState() => _PageLayoutState();
}

class _PageLayoutState extends State<PageLayout> {
  bool showNotificationList = false;

  late List<_BottomNavIconDesc> bottomNavIconDesc;
  int selectedBottomNavIconIndex = 0;

  @override
  void initState() {
    super.initState();

    var account = UsersDataSource.instance.cachedUserAccount;

    if (widget.hideBottomNavBar == true) {
      bottomNavIconDesc = [];
    }
    //
    else {
      final appConfigsData = AppConfigs.appConfigsData;

      bottomNavIconDesc = [
        //home
        _BottomNavIconDesc(
          icon: Icons.home_filled,
          label: Res.strings.home,
          screenClassType: HomeScreen,
          onTap: () {
            if (selectedBottomNavIconIndex != 0) {
              HomeScreen.show();
            }
          },
        ),

        //profile
        _BottomNavIconDesc(
          icon: Icons.person,
          label: Res.strings.profile,
          screenClassType: UserProfileScreen,
          onTap: () {
            if (selectedBottomNavIconIndex != 0) {
              UserProfileScreen.show();
            }
          },
        ),

      ];

      int idx = bottomNavIconDesc.indexWhere(
        (e) => e.screenClassType == widget.screenClassType,
      );
      selectedBottomNavIconIndex = idx;
    }
  }

  @override
  Widget build(BuildContext context) {
    var root = MyRootWidget.withoutToolbar(
      awareTopSafeArea: true,
      showBackButton: false,
      backButtonColor: Res.themes.colors.background,
    );

    root.configStatusBar(statusBarColor: Res.themes.colors.primary);

    // if (widget.toolbarConfiguration.showMenuButton &&
    //     !widget.toolbarConfiguration.showBackButton) {
    root.setDrawer(const Sidemenu());
    // }

    root.setBody(
      body(context),
      scrollable: false,
    );

    bool hideBottomNavBar = widget.hideBottomNavBar ?? false;
    if (widget.hideBottomNavBar == null) {
      hideBottomNavBar = !widget.toolbarConfiguration.showNotificationIcon;
    }
    if (bottomNavIconDesc.length < 3) {
      hideBottomNavBar = true;
    }

    if (!hideBottomNavBar) {
      root.setBottomNavigationBar(BottomNavigationBar(
        items: bottomNavIconDesc
            .map(
              (e) => BottomNavigationBarItem(
                icon: Icon(e.icon),
                label: e.label,
              ),
            )
            .toList(),
        type: BottomNavigationBarType.fixed,
        backgroundColor: Colors.white.withOpacity(0.99),
        selectedItemColor: selectedBottomNavIconIndex < 0
            ? Res.themes.colors.secondary.withOpacity(0.55)
            : Res.themes.colors.secondary,
        unselectedItemColor: Res.themes.colors.secondary.withOpacity(0.55),
        currentIndex:
            selectedBottomNavIconIndex < 0 ? 0 : selectedBottomNavIconIndex,
        onTap: (index) {
          //Future.delayed(const Duration(milliseconds: 500), () {
          bottomNavIconDesc[index].onTap();
          //selectedBottomNavIconIndex = index;
          //});
        },
      ));
    }

    root.resizeToAvoidBottomInset(widget.resizeToAvoidBottomInset);

    if (widget.floatingActionButtonAction != null) {
      root.setFloatingActionButton(FloatingActionButton(
        onPressed: widget.floatingActionButtonAction,
        backgroundColor: Res.themes.colors.secondary,
        tooltip: widget.floatingActionButtonTooltip,
        child: widget.floatingActionButtonChild ??
            const Icon(Icons.add, color: Colors.white, size: 25),
      ));
    }

    return root.build();
  }

  Widget body(BuildContext context) {
    return Stack(
      children: [
        /*Center(child: Opacity(
            opacity: 0.2,
            child: Image.asset(Res.images.logoColored),
        )),*/

        Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Toolbar(
              toolbarConfiguration: widget.toolbarConfiguration,
              onNotificationBellClicked: () => setState(() {
                showNotificationList = true;
              }),
            ),

            //title
            if (widget.title != null)
              Text(
                widget.title!,
                style: Res.themes.textStyleOfScreenTitle(),
              ),

            //subtitle
            if (widget.title != null && widget.subtitle != null)
              Text(
                widget.subtitle!,
                textAlign: TextAlign.center,
                style: Res.themes.textStyleOfScreenTitle(
                  textSize: 11,
                  textColor: Res.themes.colors.hint,
                ),
              ),

            //
            Expanded(
              child: SizedBox(
                width: double.maxFinite,
                child: widget.enableScroll
                    ? SingleChildScrollView(
                        child: Padding(
                          padding: widget.padding,
                          child: widget.child(context),
                        ),
                      )
                    : Padding(
                        padding: widget.padding,
                        child: widget.child(context),
                      ),
              ),
            ),
          ],
        ),

        //
        if (showNotificationList)
          GestureDetector(
            onTap: () => setState(() {
              showNotificationList = false;
            }),
            child: Container(
              color: Colors.black38,
              width: double.maxFinite,
              height: double.maxFinite,
              child: Stack(
                children: [
                  Positioned(
                    top: 30,
                    left: App.isEnglish
                        ? Res.themes.measurement.screenSize.width - 255
                        : null,
                    right: App.isEnglish
                        ? null
                        : Res.themes.measurement.screenSize.width - 255,
                    child: const SmallNotificationsList(),
                  ),
                ],
              ),
            ),
          ),
      ],
    );
  }
}

class _BottomNavIconDesc {
  final IconData icon;
  final String label;
  final Type screenClassType;
  final VoidCallback onTap;

  _BottomNavIconDesc({
    required this.icon,
    required this.label,
    required this.screenClassType,
    required this.onTap,
  });
}
