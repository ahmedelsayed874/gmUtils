import 'package:gmutils_flutter/data/models/users/user_account_identifier.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/login/login_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/widgtes.dart';
import 'package:flutter/material.dart';

import 'rp_set_new_password_screen_driver.dart';

class RPSetNewPasswordScreen extends StatefulWidget {
  static void show({
    required String email,
    required UserAccountIdentifier identifier,
  }) {
    App.navTo(RPSetNewPasswordScreen(
      email: email,
      identifier: identifier,
    ));
  }

  final String email;
  final UserAccountIdentifier identifier;

  const RPSetNewPasswordScreen({
    required this.email,
    required this.identifier,
    super.key,
  });

  @override
  State<RPSetNewPasswordScreen> createState() => _RPSetNewPasswordScreenState();
}

class _RPSetNewPasswordScreenState extends BaseState<RPSetNewPasswordScreen>
    implements RPSetNewPasswordScreenDelegate {
  late RPSetNewPasswordScreenDriverAbs screenDriver;

  var newPasswordController = TextEditingController();
  var confirmationPasswordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    screenDriver = RPSetNewPasswordScreenDriver(this);
  }

  @override
  void dispose() {
    newPasswordController.dispose();
    confirmationPasswordController.dispose();

    super.dispose();
  }

  @override
  @override
  Widget build(BuildContext context) {
    return MyRootWidget.withoutToolbar(
            awareTopSafeArea: true,
            backButtonColor: Res.themes.colors.background)
        .setScreenPadding(5, 15, 15, 5)
        .setBody(
          build2(context),
          scrollable: true,
        )
        .build();
  }

  Widget build2(BuildContext context) {
    var children = <Widget>[];

    children.add(const SizedBox(height: 20));

    //back btn / logo
    children.add(Stack(
      children: [
        IconButton(
          onPressed: () => App.navBack(),
          icon: Icon(Icons.arrow_back_ios),
        ),

        Center(
          child: Image.asset(
            Res.images.logoColored,
            width: 170,
          ),
        ),
      ],
    ));

    //title
    children.add(Center(
      child: Widgets.title(
        Res.strings.set_new_password,
        textAlign: TextAlign.center,
      ),
    ));

    //new password
    children.add(SizedBox(height: 50));
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
    children.add(SizedBox(height: 20));
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
          child: Text(Res.strings.save),
        ),
      ),
    );

    children.add(const SizedBox(height: 100));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  void saveNewPassword() {
    screenDriver.saveNewPassword(
      identifier: widget.identifier,
      newPassword : newPasswordController.text.trim(),
      confirmationPassword : confirmationPasswordController.text.trim(),
    );
  }

  @override
  void onNewPasswordSavedSuccessfully() {
    LoginScreen.show();
  }
}
