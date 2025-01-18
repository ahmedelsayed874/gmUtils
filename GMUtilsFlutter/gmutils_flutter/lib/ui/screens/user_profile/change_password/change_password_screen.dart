import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/widgets/page_layout.dart';
import 'package:gmutils_flutter/ui/widgets/toolbar.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:flutter/material.dart';

import 'change_password_screen_driver.dart';

class ChangePasswordScreen extends StatefulWidget {
  static Future<bool?> show({required bool forceChange}) {
    return App.navTo(ChangePasswordScreen(forceChange: forceChange));
  }

  final bool forceChange;

  const ChangePasswordScreen({required this.forceChange, super.key});

  @override
  State<ChangePasswordScreen> createState() => _ChangePasswordScreenState();
}

class _ChangePasswordScreenState extends BaseState<ChangePasswordScreen>
    implements ChangePasswordScreenDelegate {
  late ChangePasswordScreenDriverAbs screenDriver;

  var currentPasswordController = TextEditingController();
  var newPasswordController = TextEditingController();
  var confirmationPasswordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    screenDriver = ChangePasswordScreenDriver(this);
  }

  @override
  void dispose() {
    currentPasswordController.dispose();
    newPasswordController.dispose();
    confirmationPasswordController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return PageLayout(
      toolbarConfiguration: ToolbarConfiguration(
        showBackButton: !widget.forceChange,
        title: Res.strings.change_password,
        subtitle: null,
        showNotificationIcon: false,
      ),
      child: build2,
      enableScroll: true,
    );
  }

  Widget build2(BuildContext context) {
    var children = <Widget>[];

    //current password
    children.add(const SizedBox(height: 50));
    children.add(TextField(
      controller: currentPasswordController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.lock_outline),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.currentPassword),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Res.themes.colors.primary,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.text,
      textInputAction: TextInputAction.next,
      obscureText: true,
    ));

    //new password
    children.add(const SizedBox(height: 50));
    children.add(TextField(
      controller: newPasswordController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.lock_outline),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.newPassword),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Res.themes.colors.primary,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.text,
      textInputAction: TextInputAction.next,
      obscureText: true,
    ));

    //confirm password
    children.add(const SizedBox(height: 20));
    children.add(TextField(
      controller: confirmationPasswordController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.lock_outline),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.confirmPassword),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Res.themes.colors.primary,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.text,
      textInputAction: TextInputAction.done,
      obscureText: true,
      onEditingComplete: saveNewPassword,
    ));

    //save btn
    children.add(const SizedBox(height: 50));
    children.add(
      SizedBox(
        width: double.maxFinite,
        child: ElevatedButton(
          onPressed: saveNewPassword,
          child: Text(Res.strings.change),
        ),
      ),
    );


    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  void saveNewPassword() async {
    var changed = await screenDriver.saveNewPassword(
      currentPassword: currentPasswordController.text.trim(),
      newPassword: newPasswordController.text.trim(),
      confirmationPassword: confirmationPasswordController.text.trim(),
    );

    if (changed == true) {
      if (widget.forceChange) {
        HomeScreen.show();
      } else {
        App.navBack(changed);
      }
    }
  }

}
