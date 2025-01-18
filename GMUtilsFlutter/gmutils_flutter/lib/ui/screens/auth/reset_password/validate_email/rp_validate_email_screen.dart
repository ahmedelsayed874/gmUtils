import 'package:gmutils_flutter/data/models/users/user_account_identifier.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/reset_password/set_new_password/rp_set_new_password_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/widgtes.dart';
import 'package:flutter/material.dart';

import 'rp_validate_email_screen_driver.dart';

class RPValidateEmailScreen extends StatefulWidget {
  static void show({required String email}) {
    App.navTo(RPValidateEmailScreen(email: email));
  }

  final String email;

  const RPValidateEmailScreen({required this.email, super.key});

  @override
  State<RPValidateEmailScreen> createState() => _RPValidateEmailScreenState();
}

class _RPValidateEmailScreenState extends BaseState<RPValidateEmailScreen>
    implements RPValidateEmailScreenDelegate {
  late RPValidateEmailScreenDriverAbs screenDriver;

  var codeController = TextEditingController();

  @override
  void initState() {
    super.initState();
    screenDriver = RPValidateEmailScreenDriver(this);
  }

  @override
  void dispose() {
    codeController.dispose();

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
        Res.strings.verify_emailAddress,
        textAlign: TextAlign.center,
      ),
    ));

    //message
    children.add(SizedBox(height: 15));
    children.add(Center(
      child: Text(
        Res.strings
            .code_sent_to_EMAILADDRESS_plz_check_and_copy_the_code_to_provided_input_box
            .replaceAll('EMAILADDRESS', widget.email),
        style: Res.themes.defaultTextStyle(
          textColor: Res.themes.colors.hint,
          textSize: 11,
        ),
        textAlign: TextAlign.center,
      ),
    ));

    //code
    children.add(SizedBox(height: 30));
    children.add(TextField(
      controller: codeController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.password),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.verification_code),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Res.themes.colors.primary,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.number,
      textInputAction: TextInputAction.done,
      onEditingComplete: veryCode,
    ));

    //next btn
    children.add(const SizedBox(height: 50));
    children.add(
      SizedBox(
        width: double.maxFinite,
        child: ElevatedButton(
          onPressed: veryCode,
          child: Text(Res.strings.next),
        ),
      ),
    );

    children.add(const SizedBox(height: 100));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  void veryCode() {
    screenDriver.veryCode(
      email: widget.email,
      code: codeController.text.trim(),
    );
  }

  @override
  void onEmailVerifiedSuccessfully(UserAccountIdentifier identifier) {
    RPSetNewPasswordScreen.show(email: widget.email, identifier: identifier);
  }
}
