import 'dart:convert';
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:gmutils_flutter/data/models/notifications/notification.dart'
    as notifModel;
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/screens/notifications/notifications_screen.dart';
import 'package:gmutils_flutter/ui/widgets/my_widgets.dart';
import 'package:gmutils_flutter/zgmutils/data_utils/firebase/fcm.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/dialogs/input_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/dialogs/message_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';
import 'package:gmutils_flutter/zgmutils/utils/files/files.dart';
import 'package:gmutils_flutter/zgmutils/utils/launcher.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';
import 'package:gmutils_flutter/zgmutils/utils/notifications/notifications_manager.dart';
import 'package:share_plus/share_plus.dart';

import '../user_profile/user_profile_screen.dart';
import 'sidemenu_driver.dart';

class Sidemenu extends StatefulWidget {
  const Sidemenu({super.key});

  @override
  State<Sidemenu> createState() => _SidemenuState();

  static void updateProfilePhoto() {
    //_SidemenuState.updateProfilePhoto();
  }
}

class _SidemenuState extends BaseState<Sidemenu> implements SidemenuDelegate {
  /*static const String sidemenuProfileObserverCategory = 'sidemenu-profile';
  static const String action_updatePhoto = 'update_photo';
  static void updateProfilePhoto() {
    App.callObservers(
        category: sidemenuProfileObserverCategory, args: action_updatePhoto);
  }*/

  late SidemenuDriverAbs screenDriver;

  @override
  void initState() {
    super.initState();

    screenDriver = SidemenuDriver(this);

    /*App.addObserver(
      category: sidemenuProfileObserverCategory,
      observerName: 'sidemenu-$hashCode',
      observer: (observerName, args) {
        try {
          setState(() {});
        } catch (e) {}
      },
    );*/
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> children = [];

    children.add(Container(
      color: Res.themes.colors.secondary,
      child: Column(
        children: [
          SizedBox(height: Platform.isAndroid ? 40 : 57),

          //
          Center(
            child: MyWidgets().userPhotoAvatar(
              photoPath: screenDriver.authUserAccount.personalPhoto,
              size: 90,
              strokeColor: Res.themes.colors.primary,
              onClick: (a) => UserProfileScreen.show(),
            ),
          ),

          //
          SizedBox(height: 10),

          //
          Row(
            children: [
              Expanded(
                child: Text(
                  screenDriver.authUserAccount.fullname,
                  textAlign: TextAlign.center,
                  style: Res.themes.defaultTextStyle(
                    textColor: Res.themes.colors.primaryVariant,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),

          //
          Row(
            children: [
              Expanded(
                child: Text(
                  screenDriver.authUserAccount.job(App.isEnglish),
                  textAlign: TextAlign.center,
                  style: Res.themes.defaultTextStyle(
                    textColor: Colors.grey[100],
                    textSize: 12,
                    //fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),

          //
          SizedBox(height: 10),
        ],
      ),
    ));

    //----------------------------------------------------------------------

    var count = screenDriver.itemsCount;
    if (count == null) {
      children.add(
        const Center(
          child: SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(),
          ),
        ),
      );
    }
    //
    else {
      children.add(Expanded(
        child: Padding(
          padding: const EdgeInsets.only(left: 0, bottom: 10, right: 0),
          child: ListView.builder(
              itemCount: count,
              itemBuilder: (context, idx) {
                return SidemenItemWidget(
                    item: screenDriver.sidemenuItemAt(idx));
              }),
        ),
      ));
    }

    return Container(
      width: 300,
      color: Res.themes.colors.background,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: children,
      ),
    );
  }

  /////////////////////////////////////////////////////////////////////////////

  @override
  void openHomeScreen() {
    HomeScreen.show();
  }

  @override
  void openBlsWebsiteScreen() {
    /*WebViewScreen.show(
      toolbarTitle: Res.strings.bls_website,
      url: 'https://bls.edu.sa/',
    );*/
    Launcher().openUrl('https://bls.edu.sa/');
  }

  @override
  void openBananWebsiteScreen() {
    /*WebViewScreen.show(
      toolbarTitle: Res.strings.banan_website,
      url: 'https://banan-bls.com/',
    );*/
    Launcher().openUrl('https://banan-bls.com/');
  }

  @override
  void openNotificationsScreen() {
    NotificationsScreen.show(initialNotifications: null, pageSize: null);
  }

  @override
  void onReportIssue() async {
    if ((await Logs.hasLogs) == true) {
      showMessage(
        message: App.isEnglish
            ? 'Please repeat the steps that lead to the issue, to help collect data, then finally report the issue'
            : 'برجاء اعادة تكرار نفس الخطوات التي قادت للمشكلة لمساعدتنا في جمع البيانات المطلوبة، وفي النهاية قم بالإبلاغ عن المشكلة',
        actions: [
          MessageDialogActionButton(
            App.isEnglish ? 'OK' : 'حسنا',
            action: () {
              var dateTime = DateTime.now().add(Duration(minutes: 15));
              var dl = DateOp().formatForDatabase(dateTime, dateOnly: false);
              Logs.setLogFileDeadline(
                privateLogFileDeadline: dl,
                saveDate: true,
              );
            },
          ),
          MessageDialogActionButton(App.isEnglish ? 'Back' : 'رجوع'),
        ],
      );

      return;
    }

    showMessage(
      message: App.isEnglish
          ? 'Before continue make sure of repeating the same step which lead to the error to help us find the problem quickly'
          : 'تأكد من تكرار نفس الخطوات التي تقود إلي المشكلة وذلك قبل الاستمرار، حيث أن ذلك يساعدنا في تحديد المشكلة',
      actions: [
        MessageDialogActionButton(
          App.isEnglish ? 'Continue' : 'استمرار',
          action: sendBugEmail,
        ),
        MessageDialogActionButton(App.isEnglish ? 'Back' : 'رجوع'),
      ],
    );
  }

  void sendBugEmail() async {
    var msg = '';
    msg += '---------- DESCRIBE THE ISSUE BELOW ----------';
    msg += '\n\n\n\n\n';

    var logFileContent = await Logs.getLastLogsContent(upTo: 5);
    if (logFileContent?.isNotEmpty == true) {
      msg += '---------- DON\'T CHANGE ANY OF THE FOLLOWING ----------';
      msg += '\n\n';

      var encoded = logFileContent!;
      if (encoded.length > 100) {
        var end = Random().nextInt(100);
        if (end < 1) end = 1;

        msg += encoded.substring(0, end);
        msg += 'AEA';
        msg += encoded.substring(end);
      } else {
        msg += encoded;
      }
    }

    var emailAddress = 'blsschool24@gmail.com';
    var b = await Launcher().sendEmail(
      emailAddress,
      'BUG-${DateTime.now()}',
      msg,
    );

    if (!b) {
      var fileName = 'BUG-${DateTime.now()}';
      var files = Files.public(fileName, 'bls', saveToCacheDir: true);
      var file = await files.write(msg);

      showMessage(
        message: 'Can\'t send mail to: $emailAddress',
        actions: [
          MessageDialogActionButton(
            App.isEnglish ? 'Try Another Way' : 'جرب طريقة أخرى',
            action: () async {
              await Share.shareXFiles(
                [XFile(file.path, mimeType: 'text/plain')],
                subject: fileName,
              );
            },
          ),
          MessageDialogActionButton(App.isEnglish ? 'Cancel' : 'إلغاء'),
        ],
      );
    }
  }

  /////////////////////////////////////////

  @override
  void testNewChatNotification() {
    NotificationsManager.instance.showNotification(
      'TestNewChatNotification',
      'Test New Chat Notification',
      customChannel: notifModel.Notification.channelInfoOf(
        notifModel.Notification.relatedObjectName_Chat,
      ),
    );
  }

  @override
  void testNewChatMessageNotification() {
    var channel = [
      notifModel.Notification.relatedObjectName_ChatMessage,
      notifModel.Notification.relatedObjectName_ChatMessageReaction,
      notifModel.Notification.relatedObjectName_ChatMessagePollResultUpdated
    ][Random().nextInt(3)];

    NotificationsManager.instance.showNotification(
      'TestNewChatMessageNotification',
      'Test New Chat Message Notification ($channel)',
      customChannel: notifModel.Notification.channelInfoOf(channel),
    );
  }

  String? lastNotificationTitle;
  String? lastNotificationMessage;
  String? lastNotificationTopic;

  @override
  void sendGlobalNotification() {
    InputDialog()
        .setTitle('Send Global Notification')
        .setMessage("Enter the notification title")
        .setInputHint("Type here")
        .setInputText(lastNotificationTitle ?? '')
        .setInputHandler((title) {
      //
      lastNotificationTitle = title;
      InputDialog()
          .setTitle('Send Global Notification')
          .setMessage("Enter the message")
          .setInputHint("Type here")
          .setInputText(lastNotificationMessage ?? '')
          .setInputHandler((message) {
        //
        //
        lastNotificationMessage = message;
        InputDialog()
            .setTitle('Send Global Notification')
            .setMessage("Enter FCM TOPIC")
            .setInputHint("Type here")
            .setInputText(lastNotificationTopic ?? '')
            .setInputHandler((topic) {
          //
          lastNotificationTopic = topic;
          FCM.instance.sendMessageToTopic(
            topic: topic,
            title: title,
            message: message,
            payload: null,
          );
          //
        }).show(() => App.context);
        //
      }).show(() => App.context);
      //
    }).show(() => App.context);
  }
}

///////////////////////////////////////////////////////////////////////////////

class SidemenItemWidget extends StatefulWidget {
  final SidemenuItem item;

  const SidemenItemWidget({
    required this.item,
    super.key,
  });

  @override
  State<SidemenItemWidget> createState() => _SidemenItemWidgetState();
}

class _SidemenItemWidgetState extends State<SidemenItemWidget> {
  @override
  Widget build(BuildContext context) {
    List<Widget> children = [];

    children.add(itemWidget(widget.item));

    if (widget.item.isSubmenuDisplayed) {
      widget.item.submenu?.forEach((sub) {
        children.add(itemWidget(sub, indent: 30));
      });
    }

    return Container(
      width: 300,
      color: Res.themes.colors.background,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: children,
      ),
    );
  }

  Widget itemWidget(SidemenuItem item, {int? indent, double iconSize = 25}) {
    return SizedBox(
      height: 50,
      child: TextButton(
        onPressed: () => onItemClick(item),
        child: Row(
          children: [
            if (indent != null) SizedBox(width: indent.toDouble()),

            //
            Container(
              decoration: BoxDecoration(
                  color: Res.themes.colors.secondary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(30)),
              width: iconSize,
              height: iconSize,
              padding: EdgeInsets.all(5),
              child: item.iconRes != null
                  ? Image.asset(
                      item.iconRes!,
                      width: iconSize,
                      height: iconSize,
                      color: Res.themes.colors.secondary,
                    )
                  : Icon(
                      item.iconData,
                      size: iconSize - 9,
                      color: Res.themes.colors.secondary,
                    ),
            ),

            //
            SizedBox(width: 10),
            Expanded(
                child: Text(
              item.text,
              style: Res.themes.defaultTextStyle(
                fontWeight: FontWeight.w600,
                textColor: Res.themes.colors.primary,
              ),
            )),
          ],
        ),
      ),
    );
  }

  void onItemClick(SidemenuItem item) {
    if (item.submenu == null) {
      MyRootWidget.getCurrentScaffoldState(context).closeDrawer();
      item.action?.call();
    }
    //
    else {
      setState(() {
        widget.item.isSubmenuDisplayed = !widget.item.isSubmenuDisplayed;
      });
    }
  }
}
