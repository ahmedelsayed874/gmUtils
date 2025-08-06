import 'package:flutter/material.dart';
import 'package:gmutils_flutter/data/models/users/auth_user_account.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/services/configs/app_configs.dart';
import 'package:gmutils_flutter/ui/screens/user_profile/change_password/change_password_screen.dart';
import 'package:gmutils_flutter/ui/widgets/page_layout.dart';
import 'package:gmutils_flutter/ui/widgets/toolbar.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/dialogs/message_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/observable_widget.dart';
import 'package:gmutils_flutter/zgmutils/utils/launcher.dart';
import 'package:gmutils_flutter/zgmutils/utils/pairs.dart';

import '../../../zgmutils/ui/utils/base_stateful_state.dart';
import '../../widgets/titled_icon_button.dart';
import '../user_profile/change_email_address/change_email_address_screen.dart';
import 'home_screen_driver.dart';

class HomeScreen extends StatefulWidget {
  static void show() {
    App.navTo(const HomeScreen(), singleTop: true);
  }

  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends BaseState<HomeScreen>
    implements HomeScreenDelegate {
  late HomeScreenDriverAbs screenDriver;

  //region init
  @override
  void initState() {
    super.initState();
    screenDriver = HomeScreenDriver(this);
    FCM.instance.redirectToPendingScreen();
  }

  @override
  void dispose() {
    screenDriver.unsubscribeNotificationCounterObserver();
    screenDriver.dispose();

    super.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    checkAppVersion();

    if (screenDriver.authUserAccount is AuthUserAccount) {
      var user = screenDriver.authUserAccount as AuthUserAccount;

      var fl = user.isFirstLogin;
      if (fl == true) {
        Future.delayed(const Duration(seconds: 1), () {
          showMessage(
            title: Res.strings.message,
            message: Res.strings.you_have_to_change_your_password,
            allowOuterDismiss: false,
            actions: [
              MessageDialogActionButton(Res.strings.ok),
            ],
            onDismiss: (s) => forceChangePassword(),
          );
        });
      }

      //
      else if (user.email == null) {
        Future.delayed(const Duration(seconds: 1), () {
          showMessage(
            title: Res.strings.message,
            message: Res.strings
                .you_need_to_register_your_email_address_to_help_you_restore_your_password_if_forget,
            allowOuterDismiss: false,
            actions: [
              MessageDialogActionButton(Res.strings.ok,
                  action: () => forceChangeEmailAddress()),
              MessageDialogActionButton(Res.strings.later),
            ],
            //onDismiss: (s) => forceChangeEmailAddress(),
          );
        });
      }
    }
  }

  void forceChangePassword() async {
    var b = await ChangePasswordScreen.show(forceChange: true);
    if (b != true) {
      forceChangePassword();
    }
  }

  void forceChangeEmailAddress() async {
    var b = await ChangeEmailAddressScreen.show(forceChange: true);
    if (b != true) {
      //to do active this line and test forceChangeEmailAddress();
    }
  }

  void checkAppVersion() async {
    //if (isAppVersionChecked) return;
    //isAppVersionChecked = true;

    var appConfigs = AppConfigs();
    await appConfigs.fetch();

    if (appConfigs.needUpdateApp()) {
      var forceUpdateNow = appConfigs.mustUpdateApp();

      Future.delayed(Duration(seconds: mounted ? 1 : 5), () {
        var md = MessageDialog.create;
        md
            .setTitle(Res.strings.alert)
            .setMessage(
              Res.strings.a_new_version_has_been_released_please_update,
            )
            .addActions([
              MessageDialogActionButton(
                Res.strings.update,
                color: Colors.red,
                action: () {
                  appConfigs.updateApp();
                },
              ),

              //
              if (!forceUpdateNow)
                MessageDialogActionButton(
                  Res.strings.dismiss,
                  action: () {
                    md.allowManualDismiss(true);
                  },
                ),
            ])
            .setEnableOuterDismiss(!forceUpdateNow)
            .allowManualDismiss(false)
            .show(() => context);
      });
    }
  }
  //endregion

  //--------------------------------------------------------------------------

  @override
  Widget build(BuildContext context) {
    return PageLayout(
      screenClassType: HomeScreen,
      toolbarConfiguration: ToolbarConfiguration(
        showBackButton: false,
        title: null,
        subtitle: null,
      ),
      child: body,
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 10, top: 10),
    );
  }

  Widget body(BuildContext context) {
    var children = <Widget>[];

    //external Links
    var icons = externalLinksIcons();
    if (icons.isNotEmpty) {
      children.add(const SizedBox(height: 20));
      children.add(
        iconsContainer(
          title: Res.strings.external_links,
          icons: icons,
        ),
      );
    }

    return Column(
      children: [
        //
        Expanded(
          child: Center(
            child: SingleChildScrollView(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: children,
              ),
            ),
          ),
        ),

        //
        const SizedBox(height: 10),

        //
        todayDate(),

        //if (!isDeviceHasHardwareKeys) const SizedBox(height: 5)
      ],
    );
  }

  Widget iconsContainer({
    required String title,
    required List<TitledIconButton> icons,
  }) {
    var frameWidth = Res.themes.measurement.screenSize.width - 50;
    var iconTotalWidth = icons.length * TitledIconButton.defaultWidth;

    double space = 0;
    if (frameWidth > iconTotalWidth) {
      space = (frameWidth - iconTotalWidth) / (icons.length + 1) - 10;
    }

    List<Widget> children = [];
    if (space == 0) {
      children = icons;
    } else {
      final total = icons.length * 2 + 1;
      var iconIdx = 0;

      for (var i = 0; i < total; i++) {
        if (i % 2 == 0) {
          children.add(SizedBox(width: space));
        } else {
          children.add(icons[iconIdx]);
          iconIdx++;
        }
      }
    }

    return Stack(
      children: [
        /*Container(
          color: Colors.red,
          height: 20,
          width: w,
        ),*/

        //frame & icons
        Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(15),
            border: Border.all(
              color: Res.themes.colors.secondary,
              width: 1.5,
            ),
          ),
          width: double.maxFinite,
          margin: const EdgeInsets.only(top: 14),
          padding:
              const EdgeInsets.only(left: 15, right: 15, bottom: 6, top: 15),
          child: Center(
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: children,
              ),
            ),
          ),
        ),

        //title
        Align(
          alignment: Alignment.topCenter,
          child: Container(
            decoration: BoxDecoration(
              color: Res.themes.colors.secondary,
              borderRadius: BorderRadius.circular(30),
              border: Border.all(color: Res.themes.colors.secondary),
            ),
            height: 28,
            padding: const EdgeInsets.symmetric(horizontal: 10),
            child: Text(
              title,
              style: Res.themes.defaultTextStyle(
                textColor: Res.themes.colors.primaryVariant,
                fontWeight: FontWeight.bold,
                textSize: 13,
              ),
            ),
          ),
        ),
      ],
    );
  }
  
  Widget todayDate() {
    return ObservableWidget(
      child: (context, b, o) => Center(
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            //day
            Text(
              (o as Pair?)?.value1[0].toString() ?? '',
              style: Res.themes.defaultTextStyle(
                fontWeight: FontWeight.w700,
                textSize: 14,
                textColor: Res.themes.colors.secondary,
              ),
            ),
            const SizedBox(width: 5),

            //month
            Text(
              (o as Pair?)?.value1[1].toString() ?? '',
              style: Res.themes.defaultTextStyle(
                fontWeight: FontWeight.w700,
                textSize: 14,
                textColor: Res.themes.colors.secondary,
              ),
            ),
            const SizedBox(width: 5),

            //year
            Text(
              (o as Pair?)?.value1[2].toString() ?? '',
              style: Res.themes.defaultTextStyle(
                fontWeight: FontWeight.w700,
                textSize: 14,
                textColor: Res.themes.colors.secondary,
              ),
            ),
            const SizedBox(width: 15),

            //time
            Text(
              (o as Pair?)?.value2.toString() ?? '',
              style: Res.themes.defaultTextStyle(
                fontWeight: FontWeight.w700,
                textSize: 14,
                textColor: Res.themes.colors.secondary,
              ),
            ),
          ],
        ),
      ),
      observableValue: screenDriver.currentDateTimeObservable,
    );
  }
  
  //////////////////////////////////////////////////////////////////////////////

  List<TitledIconButton> externalLinksIcons() {
    List<TitledIconButton> lst = [];

    lst.addAll([
      TitledIconButton(
        icon: Res.images.appLogoToolbar,
        title: 'Link 1',
        notificationCount: 0,
        onClick: openBlsWebsiteScreen,
      ),
      TitledIconButton(
        icon: Res.images.appLogoToolbar,
        title: 'Link 2',
        notificationCount: 0,
        onClick: openBananWebsiteScreen,
      ),
    ]);

    return lst;
  }

  //===========================================================================

  void openBlsWebsiteScreen() {
    /*WebViewScreen.show(
      toolbarTitle: Res.strings.bls_website,
      url: 'https://bls.edu.sa/',
    );*/
    Launcher().openUrl('https://bls.edu.sa/');
  }

  void openBananWebsiteScreen() {
    /*WebViewScreen.show(
      toolbarTitle: Res.strings.banan_website,
      url: 'https://banan-bls.com/',
    );*/
    Launcher().openUrl('https://banan-bls.com/');
  }

}
