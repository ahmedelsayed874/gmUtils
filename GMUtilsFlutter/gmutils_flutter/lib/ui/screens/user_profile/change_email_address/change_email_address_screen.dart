import 'package:flutter/material.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/ui/widgets/page_layout.dart';
import 'package:gmutils_flutter/ui/widgets/toolbar.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';

import 'change_email_address_screen_driver.dart';

class ChangeEmailAddressScreen extends StatefulWidget {
  static Future<bool?> show({required bool forceChange}) {
    return App.navTo(ChangeEmailAddressScreen(forceChange: forceChange));
  }

  final bool forceChange;

  const ChangeEmailAddressScreen({
    required this.forceChange,
    super.key,
  });

  @override
  State<ChangeEmailAddressScreen> createState() =>
      _ChangeEmailAddressScreenState();
}

class _ChangeEmailAddressScreenState extends BaseState<ChangeEmailAddressScreen>
    implements ChangeEmailAddressScreenDelegate {
  late ChangeEmailAddressScreenDriverAbs screenDriver;

  var email = TextEditingController();
  var confirmationCode = TextEditingController();

  @override
  void initState() {
    super.initState();
    screenDriver = ChangeEmailAddressScreenDriver(this);
  }

  @override
  void dispose() {
    email.dispose();
    confirmationCode.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return PageLayout(
      toolbarConfiguration: ToolbarConfiguration(
        showBackButton: true,//to do !widget.forceChange,
        title: Res.strings.change_email_address,
        subtitle: null,
        showNotificationIcon: false,
      ),
      enableScroll: true,
      child: build2,
    );
  }

  Widget build2(BuildContext context) {
    var children = <Widget>[];

    //
    children.add(const SizedBox(height: 50));
    children.add(Center(
      child: Text(
        Res.strings
            .you_need_to_register_your_email_address_to_help_you_restore_your_password_if_forget,
        textAlign: TextAlign.center,
      ),
    ));

    //
    children.add(const SizedBox(height: 50));

    //
    if (!screenDriver.isVerificationCodeSent) {
      children.add(TextField(
        controller: email,
        textAlign: TextAlign.center,
        decoration: InputDecoration(
          prefixIcon: const Icon(Icons.email),
          iconColor: Res.themes.colors.primary,
          label: Text(Res.strings.emailAddress),
          border: OutlineInputBorder(
            borderSide: BorderSide(
              color: Res.themes.colors.primary,
            ),
          ),
          contentPadding: const EdgeInsets.symmetric(horizontal: 15),
          suffixIcon: SizedBox(width: 30),
        ),
        maxLines: 1,
        keyboardType: TextInputType.emailAddress,
        textInputAction: TextInputAction.done,
        onEditingComplete: verifyEmailAddress,
      ));

      //verify btn
      children.add(const SizedBox(height: 30));
      children.add(Center(
        child: ElevatedButton(
          onPressed: verifyEmailAddress,
          child: Text('\t\t${Res.strings.verify}\t\t'),
        ),
      ));
    }

    //
    else {
      children.add(Center(
        child: Text(
          '${Res.strings.verification_code} ${Res.strings.have_sent_to}',
          textAlign: TextAlign.center,
        ),
      ));

      children.add(Center(
        child: Text(
          email.text.trim(),
          textAlign: TextAlign.center,
          style: Res.themes.defaultTextStyle(
            fontWeight: FontWeight.w800,
          ),
        ),
      ));

      children.add(Center(
        child: TextButton(
          onPressed: () {
            screenDriver.allowChangeEmailAddress();
            confirmationCode.text = '';
          },
          style: ButtonStyle(
              foregroundColor: WidgetStatePropertyAll(Colors.blueAccent)),
          child: Text(Res.strings.change),
        ),
      ));

      children.add(SizedBox(
        height: 22,
      ));

      children.add(Center(
        child: Text(
          '${Res.strings.enter} ${Res.strings.verification_code}',
          textAlign: TextAlign.center,
        ),
      ));

      children.add(TextField(
        controller: confirmationCode,
        decoration: InputDecoration(
          prefixIcon: const Icon(Icons.key),
          iconColor: Res.themes.colors.primary,
          border: OutlineInputBorder(
            borderSide: BorderSide(
              color: Res.themes.colors.primary,
            ),
          ),
          contentPadding: const EdgeInsets.symmetric(horizontal: 15),
          suffixIcon: SizedBox(width: 30),
        ),
        maxLines: 1,
        textAlign: TextAlign.center,
        keyboardType: TextInputType.number,
        textInputAction: TextInputAction.done,
        onEditingComplete: confirmEmailAddress,
      ));

      //confirm btn
      children.add(const SizedBox(height: 30));
      children.add(SizedBox(
        width: double.maxFinite,
        child: ElevatedButton(
          onPressed: confirmEmailAddress,
          child: Text('\t\t${Res.strings.verify}\t\t'),
        ),
      ));
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  void verifyEmailAddress() {
    screenDriver.verifyEmailAddress(emailAddress: email.text.trim());
  }

  void confirmEmailAddress() async {
    var changed = await screenDriver.confirmEmailAddress(
      code: confirmationCode.text.trim(),
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
