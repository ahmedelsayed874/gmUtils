import 'dart:io';

import 'package:gmutils_flutter/data/models/users/user_account.dart';
import 'package:gmutils_flutter/data/models/users/user_account_header.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/services/app_privilege_checker.dart';
import 'package:gmutils_flutter/ui/screens/auth/login/login_screen.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/screens/user_profile/change_email_address/change_email_address_screen.dart';
import 'package:gmutils_flutter/ui/screens/user_profile/change_password/change_password_screen.dart';
import 'package:gmutils_flutter/ui/screens/zsidemenu/sidemenu.dart';
import 'package:gmutils_flutter/ui/widgets/dropdown_action_Properties.dart';
import 'package:gmutils_flutter/ui/widgets/my_widgets.dart';
import 'package:gmutils_flutter/ui/widgets/page_layout.dart';
import 'package:gmutils_flutter/ui/widgets/toolbar.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/dialogs/input_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/dialogs/message_dialog.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/image_viewer_screen.dart';
import 'package:gmutils_flutter/zgmutils/utils/calculations.dart';
import 'package:gmutils_flutter/zgmutils/utils/date_op.dart';
import 'package:gmutils_flutter/zgmutils/utils/image_cropper.dart';
import 'package:gmutils_flutter/zgmutils/utils/image_picker.dart';
import 'package:gmutils_flutter/zgmutils/utils/launcher.dart';
import 'package:flutter/material.dart';

import '../../../zgmutils/ui/utils/base_stateful_state.dart';
import 'user_profile_screen_driver.dart';

class UserProfileScreen extends StatefulWidget {
  static void show({int? accountId, UserAccount? account}) {
    App.navTo(UserProfileScreen(accountId: accountId, account: account));
  }

  final int? accountId;
  final UserAccount? account;

  const UserProfileScreen({this.accountId, this.account, super.key});

  @override
  State<UserProfileScreen> createState() => _UserProfileScreenState();
}

class _UserProfileScreenState extends BaseState<UserProfileScreen>
    implements UserProfileScreenDelegate {
  late UserProfileScreenDriverAbs screenDriver;

  @override
  void initState() {
    super.initState();

    screenDriver = UserProfileScreenDriver(
      this,
      targetAccountId: widget.accountId,
      targetAccount: widget.account,
    );
  }

  @override
  Widget build(BuildContext context) {
    return PageLayout(
      toolbarConfiguration: ToolbarConfiguration(
        showBackButton: true,
        title:
            '${Res.strings.profile}: ${screenDriver.userFullName ?? Res.strings.loading}',
        subtitle: null,
        moreActions: profileActions,
        onActionSelected: onActionSelected,
      ),
      title: null,
      child: body,
      enableScroll: true,
    );
  }

  List<DropdownActionProperties>? get profileActions {
    if (screenDriver.authUserAccount.isManagerOrSupervisor) {
      var userAccount = screenDriver.desiredUserAccount;

      if (userAccount?.isTeacher == true || userAccount?.isStudent == true) {
        List<DropdownActionProperties> actions =[];

        actions.insert(
          0,
          DropdownActionProperties(
            icon: Icons.insert_chart_sharp,
            text: Res.strings.statistics,
          ),
        );

        return actions;
      }
    }

    return null;
  }

  void onActionSelected(DropdownActionProperties action) {
    var userAccount = screenDriver.desiredUserAccount;
    if (userAccount == null) return;

    if (action.text == Res.strings.statistics) {
        //todo
    }
  }

  //---------------------------------------------------------------------------

  Widget body(BuildContext context) {
    var children = <Widget>[];

    var userAccount = screenDriver.desiredUserAccount;
    if (userAccount == null) {
      children.add(Center(
        child: Text(Res.strings.loading),
      ));
    }

    //
    else {
      //photo
      children.add(Center(
        child: MyWidgets().userPhotoAvatar(
            photoPath: screenDriver.profilePhotoImageFile == null
                ? userAccount.personalPhoto
                : '',
            size: 110,
            defaultWidget: screenDriver.profilePhotoImageFile == null
                ? null
                : Image.file(screenDriver.profilePhotoImageFile!),
            strokeColor: Res.themes.colors.secondary,
            onClick: (a) {
              if (userAccount.personalPhoto != null) {
                ImageViewerScreen.showFromUrl(
                  toolbarTitle: userAccount.fullname,
                  photoUrl: userAccount.personalPhoto!,
                );
              }
            }),
      ));

      //change profile photo btns
      if (screenDriver.isAuthUserProfile) {
        children.add(Center(
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              //camera
              SizedBox(
                width: 30,
                height: 30,
                child: IconButton(
                  onPressed: () => changeProfilePhoto(byCamera: true),
                  icon: const Icon(
                    Icons.camera_alt,
                    size: 20,
                  ),
                  style: const ButtonStyle(
                    padding: WidgetStatePropertyAll(EdgeInsets.zero),
                  ),
                ),
              ),

              //gallery
              SizedBox(
                width: 30,
                height: 30,
                child: IconButton(
                  onPressed: () => changeProfilePhoto(byCamera: false),
                  icon: const Icon(
                    Icons.photo_camera_back_rounded,
                    size: 20,
                  ),
                  style: const ButtonStyle(
                    padding: WidgetStatePropertyAll(EdgeInsets.zero),
                  ),
                ),
              ),
            ],
          ),
        ));
      }

      //fullname
      children.add(Center(
        child: Text(
          userAccount.fullname,
          style: Res.themes.defaultTextStyle(
            fontWeight: FontWeight.bold,
          ),
        ),
      ));

      //accountType
      children.add(Center(
        child: Text(
          userAccount.accountType,
          style: Res.themes.defaultTextStyle(
            textSize: 14,
          ),
        ),
      ));

      //communicate btns
      if (!screenDriver.isAuthUserProfile) {
        children.add(Center(
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              //mail
              if (canSendMail())
                TextButton(
                    onPressed: openEmailEditorScreen,
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const SizedBox(
                          width: 25,
                          height: 25,
                          child: Icon(
                            Icons.mail,
                            size: 20,
                          ),
                        ),
                        Text(Res.strings.send_emil),
                      ],
                    )),

              //chat
              if (canStartChat())
                TextButton(
                    onPressed: openChatTextingScreen,
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const SizedBox(
                          width: 25,
                          height: 25,
                          child: Icon(
                            Icons.chat,
                            size: 20,
                          ),
                        ),
                        Text(Res.strings.chat),
                      ],
                    )),
            ],
          ),
        ));
      }

      children.add(const SizedBox(height: 30));

      //contacts [email - mobile]
      if (screenDriver.isAuthUserCanSeeContacts) {
        //email
        children.add(titleValuePairWidget(
          title: Res.strings.emailAddress,
          value: userAccount.email ?? Res.strings.not_set_yet,
          valueTextColor: userAccount.email?.isNotEmpty == true ? null : Res.themes.colors.hint,
        ));

        children.add(Divider(color: Res.themes.colors.secondary));

        //mobile
        children.add(titleValuePairWidget(
          title: Res.strings.phoneNumber,
          value: userAccount.mobile ?? Res.strings.not_set_yet,
          valueTextColor: userAccount.mobile?.isNotEmpty == true ? null : Res.themes.colors.hint,
          actionIcon: screenDriver.isAuthUserProfile
              ? null
              : (userAccount.mobile == null ? null : Icons.call),
          action: screenDriver.isAuthUserProfile
              ? null
              : (userAccount.mobile == null
                  ? null
                  : () => callPhoneNumber(userAccount.mobile ?? '')),
        ));

        children.add(Divider(color: Res.themes.colors.secondary));
      }

      //gender
      children.add(titleValuePairWidget(
        title: Res.strings.gender,
        value: userAccount.gender == null
            ? Res.strings.not_set_yet
            : (userAccount.isMale ? Res.strings.male : Res.strings.female),
        valueTextColor: userAccount.gender == null ? Res.themes.colors.hint : null,
      ));

      children.add(Divider(color: Res.themes.colors.secondary));

      //birthday
      var dob = DateOp().parse(userAccount.dateOfBirth ?? '');
      children.add(titleValuePairWidget(
        title: Res.strings.birthday,
        value: dob == null
            ? Res.strings.not_set_yet
            : ('${DateOp().formatForUser(
                dob,
                en: App.isEnglish,
                dateOnly: true,
              )} '
                '(${Calculations().calculateAgeAsString2(
                birthday: dob,
                short: true,
              )})'),
        valueTextColor: dob == null ? Res.themes.colors.hint : null,
      ));

      children.add(const SizedBox(height: 50));

      if (screenDriver.isAuthUserProfile) {
        children.add(Divider(color: Res.themes.colors.secondary));

        //change password
        children.add(button(
          icon: Icons.key,
          title: Res.strings.change_password,
          onClick: changePassword,
        ));

        //change email
        children.add(button(
          icon: Icons.mark_email_read,
          title: Res.strings.change_email_address,
          onClick: changeEmail,
        ));

        //change phone
        children.add(button(
          icon: Icons.settings_cell,
          title: Res.strings.changePhoneNumber,
          onClick: changePhoneNumber,
        ));

        //change language
        children.add(button(
          icon: Icons.language,
          title: Res.strings.swapAppLanguage,
          onClick: changeAppLanguage,
        ));

        //logout
        children.add(SizedBox(
          width: double.maxFinite,
          child: OutlinedButton(
              onPressed: logout,
              style: ButtonStyle(
                backgroundColor: WidgetStatePropertyAll(
                  Res.themes.colors.primary.withOpacity(0.7),
                  //Res.themes.colors.red0
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(
                    Icons.logout,
                    color: Colors.white,
                  ),
                  const SizedBox(width: 10),
                  Text(
                    Res.strings.logout,
                    style: Res.themes.defaultTextStyle(
                      //textColor: Res.themes.colors.secondary,
                      textColor: Colors.white,
                    ),
                  ),
                ],
              )),
        ));

        children.add(const SizedBox(height: 30));
      }

      //lastLoginTime
      if (screenDriver.authUserAccount.isManagerOrSupervisor) {
        if (userAccount.lastLoginTime != null) {
          children.add(Center(
            child: Text(
              Res.strings.lastLoginTime,
              style: Res.themes.defaultTextStyle(
                textSize: 10,
                fontWeight: FontWeight.w600,
                textColor: Res.themes.colors.hint,
              ),
            ),
          ));

          children.add(Center(
            child: Text(
              DateOp().formatForUser2(
                userAccount.lastLoginTime!,
                en: App.isEnglish,
                dateOnly: false,
              )!,
              style: Res.themes.defaultTextStyle(
                textSize: 10,
                textColor: Res.themes.colors.hint,
              ),
            ),
          ));

          children.add(const SizedBox(height: 10));
        }
      }
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  Widget titleValuePairWidget({
    required String title,
    Widget? valueWidget,
    String? value,
    Color? valueTextColor,
    IconData? actionIcon,
    VoidCallback? action,
  }) {
    int titleFlex = 30; //out 100

    return Row(
      children: [
        Expanded(
          flex: titleFlex,
          child: Text(
            title,
            style: Res.themes.defaultTextStyle(
              fontWeight: FontWeight.w700,
              textSize: 14,
            ),
          ),
        ),
        Text(
          title.isNotEmpty ? ':  ' : '   ',
          style: Res.themes.defaultTextStyle(
            fontWeight: FontWeight.w700,
            textSize: 14,
          ),
        ),
        Expanded(
          flex: 100 - titleFlex - (actionIcon == null ? 0 : 8),
          child: valueWidget ??
              SelectableText(
                value!,
                maxLines: 1,
                style: Res.themes.defaultTextStyle(
                  textSize: 14,
                  textColor: valueTextColor,
                ),
              ),
        ),

        //
        if (actionIcon != null)
          GestureDetector(
            onTap: action,
            child: SizedBox(
              width: 30,
              height: 25,
              child: Icon(actionIcon, size: 20),
            ),
          ),
      ],
    );
  }

  Widget button({
    required IconData icon,
    required String title,
    required VoidCallback onClick,
  }) {
    return SizedBox(
      width: double.maxFinite,
      child: OutlinedButton(
          onPressed: onClick,
          style: ButtonStyle(
              backgroundColor: WidgetStatePropertyAll(
            //Res.themes.colors.secondary.withOpacity(0.3),
            Res.themes.colors.secondary,
          )),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Icon(
                icon,
                color: Colors.white,
              ),
              Expanded(
                child: Text(
                  title,
                  textAlign: TextAlign.center,
                  style: Res.themes.defaultTextStyle(
                    textColor: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              const SizedBox(
                width: 25,
              ),
            ],
          )),
    );
  }

  Widget textButton(String title, {required VoidCallback action}) {
    return Row(
      children: [
        Container(
          color: Res.themes.colors.secondary,
          width: 1,
          height: 13,
        ),

        //
        TextButton(
          onPressed: action,
          child: Row(
            children: [
              Icon(
                Icons.edit,
                color: Res.themes.colors.secondary,
                size: 19,
              ),
              const SizedBox(width: 10),
              Text(
                title,
                style: Res.themes.defaultTextStyle(
                  textColor: Res.themes.colors.secondary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),

        //
        Container(
          color: Res.themes.colors.secondary,
          width: 1,
          height: 13,
        ),
      ],
    );
  }

  //---------------------------------------------------------------------------

  changeProfilePhoto({required bool byCamera}) async {
    File? file;
    if (byCamera) {
      file = await ImagePicker().takePhoto(maxWidth: 500, maxHeight: 500);
    } else {
      file = await ImagePicker().pickPhoto(maxWidth: 500, maxHeight: 500);
    }

    if (file != null) {
      var toolbarTitle = screenDriver.userFullName ?? Res.strings.appName;

      var croppedFile = await ImageCropper().cropImage(
        imageFile: file,
        toolbarTitle: toolbarTitle,
        toolbarColor: null,
      );
      if (croppedFile != null) {
        completeChangeProfilePhoto(croppedFile);
      } else {
        Future.delayed(const Duration(milliseconds: 500), () {
          showMessage(
            title: toolbarTitle,
            message:
                Res.strings.are_you_still_use_the_selected_image_as_profile,
            actions: [
              MessageDialogActionButton(
                Res.strings.use,
                action: () {
                  completeChangeProfilePhoto(file!);
                },
              ),
              MessageDialogActionButton(Res.strings.cancel),
            ],
          );
        });
      }
    }
  }

  void completeChangeProfilePhoto(File file) async {
    var changed = await screenDriver.changeProfilePhoto(file);
    if (changed) {
      //updateView();
      Toolbar.updateProfilePhoto();
      Sidemenu.updateProfilePhoto();
    }
  }

  //---------------------------------------------------------------------------

  bool canSendMail() {
    return AppPrivilegeChecker.canSendMail(
      senderAccountType: screenDriver.authUserAccount.accountType,
      receiverAccountType: screenDriver.desiredUserAccount?.accountType ?? '',
    );
  }

  bool canStartChat() {
    return AppPrivilegeChecker.canStartChat(
      senderAccountType: screenDriver.authUserAccount.accountType,
      receiverAccountType: screenDriver.desiredUserAccount?.accountType ?? '',
    );
  }

  void openEmailEditorScreen() async {

  }

  void openChatTextingScreen() async {

  }

  //---------------------------------------------------------------------------

  void callPhoneNumber(String mobile) {
    if (mobile.isEmpty) return;
    Launcher().callPhoneNumber(mobile);
  }

  //---------------------------------------------------------------------------

  void changePassword() {
    ChangePasswordScreen.show(forceChange: false);
  }

  void changeEmail() async {
    var changed = await ChangeEmailAddressScreen.show(forceChange: false);
    if (changed == true) {
      updateView();
    }
  }

  void changePhoneNumber() {
    InputDialog.create
        .setTitle(Res.strings.changePhoneNumber)
        .setMessage(Res.strings.changePhoneNumber)
        .setInputKeyboardType(TextInputType.phone)
        .setMinInputLines(1)
        .setInputText(screenDriver.authUserAccount.mobile ?? '')
        .setInputHint(Res.strings.enter_here)
        .setInputHandler((txt) {
      screenDriver.changePhoneNumber(txt);
    }).show(() => context);
  }

  void changeAppLanguage() {
    showMessage(
      title: Res.strings.swapAppLanguage,
      message: App.isEnglish
          ? 'هل ترغب في استخدام اللغة العربية كلغة اساسية؟'
          : 'Do you want to use English as basic language?',
      actions: [
        MessageDialogActionButton('Yes / نعم', action: () {
          if (App.changeAppLanguage(
              context: context, toEnglish: !App.isEnglish)) {
            HomeScreen.show();
          } else {
            MyRootWidget.showSnackBar(context,
                message: Res.strings.changing_failed);
          }
        }),
        MessageDialogActionButton('No / لا', action: null),
      ],
    );
  }

  void logout() {
    showMessage(message: Res.strings.are_you_sure_of_logout, actions: [
      MessageDialogActionButton(Res.strings.logout, action: () {
        screenDriver.logout().then((v) {
          if (v == true) {
            LoginScreen.show();
          }
        });
      }),
      MessageDialogActionButton(Res.strings.cancel),
    ]);
  }
}
