import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/reset_password/inquiry_email/rp_inquiry_email_screen.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/utils/keyboard_manager.dart';
import 'package:flutter/material.dart';

import 'login_screen_driver.dart';

class LoginScreen extends StatefulWidget {
  static void show() {
    App.navTo(const LoginScreen(), singleTop: true);
  }

  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends BaseState<LoginScreen>
    implements LoginScreenDelegate {
  late LoginScreenDriverAbs screenDriver;

  var usernameController = TextEditingController();
  var passwordController = TextEditingController();
  var hidePassword = true;

  @override
  void initState() {
    super.initState();
    screenDriver = LoginScreenDriver(this);
  }

  @override
  void dispose() {
    usernameController.dispose();
    passwordController.dispose();

    super.dispose();
  }

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

    //change language
    children.add(Align(
      alignment: App.isEnglish ? Alignment.topRight : Alignment.topLeft,
      child: TextButton(
        onPressed: () {
          App.changeAppLanguage(
            context: context,
            toEnglish: !App.isEnglish,
          );
          Future.delayed(
              const Duration(milliseconds: 100), () => setState(() {}));
        },
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              App.isEnglish ? 'عربي' : 'English',
              style: Res.themes.defaultTextStyle(
                textColor: Res.themes.colors.primary,
              ),
            ),

            SizedBox(width: 7),

            Icon(Icons.language, color: Res.themes.colors.primary),
          ],
        ),
      ),
    ));

    //logo
    children.add(Center(
      child: Image.asset(
        Res.images.logoColored,
        width: 170,
      ),
    ));

    //username
    children.add(TextField(
      controller: usernameController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.person_outline),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.userName),
        border: OutlineInputBorder(
            borderSide: BorderSide(
          color: Res.themes.colors.primary,
        )),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.text,
      textInputAction: TextInputAction.next,
    ));

    //password
    children.add(const SizedBox(height: 30));
    children.add(TextField(
      controller: passwordController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.lock_outline),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.password),
        border: OutlineInputBorder(
            borderSide: BorderSide(
          color: Res.themes.colors.primary,
        )),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
        suffixIcon: IconButton(
          onPressed: () => setState(() {
            hidePassword = !hidePassword;
          }),
          icon: Icon(hidePassword ? Icons.visibility_off : Icons.visibility),
        ),
      ),
      maxLines: 1,
      obscureText: hidePassword,
      keyboardType: TextInputType.text,
      textInputAction: TextInputAction.done,
      onEditingComplete: login,
    ));

    //forgot password btn
    children.add(
      Align(
        alignment: App.isEnglish ? Alignment.topRight : Alignment.topLeft,
        child: TextButton(
          onPressed: resetPassword,
          child: Text(
            Res.strings.forgotPassword,
            style: Res.themes.defaultTextStyle(
              textColor: Colors.blueAccent,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    );

    //login btn
    children.add(const SizedBox(height: 50));
    children.add(
      SizedBox(
        width: double.maxFinite,
        child: ElevatedButton(
          onPressed: login,
          child: Text(Res.strings.login),
        ),
      ),
    );

    children.add(const SizedBox(height: 100));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  void login() {
    KeyboardManage(context).dismissKeyboard();

    screenDriver.login(
      username: usernameController.text.trim(),
      password: passwordController.text.trim(),
    );
  }

  @override
  void onLoginCompleted() {
    HomeScreen.show();
  }

  void resetPassword() {
    RPInquiryEmailScreen.show();
  }

}
