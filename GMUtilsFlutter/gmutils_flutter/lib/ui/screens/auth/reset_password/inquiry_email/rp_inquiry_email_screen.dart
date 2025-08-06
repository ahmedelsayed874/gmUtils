import 'package:flutter/material.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/reset_password/validate_email/rp_validate_email_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/widgtes.dart';

import 'rp_inquiry_email_screen_driver.dart';

class RPInquiryEmailScreen extends StatefulWidget {
  static void show() {
    App.navTo(const RPInquiryEmailScreen());
  }

  const RPInquiryEmailScreen({super.key});

  @override
  State<RPInquiryEmailScreen> createState() => _RPInquiryEmailScreenState();
}

class _RPInquiryEmailScreenState extends BaseState<RPInquiryEmailScreen>
    implements RPInquiryEmailScreenDelegate {
  late RPInquiryEmailScreenDriverAbs screenDriver;

  var emailController = TextEditingController();

  @override
  void initState() {
    super.initState();
    screenDriver = RPInquiryEmailScreenDriver(this);
  }

  @override
  void dispose() {
    emailController.dispose();

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

    //back btn / logo
    children.add(Stack(
      children: [
        IconButton(
          onPressed: () => App.navBack(),
          icon: Icon(Icons.arrow_back_ios),
        ),

        Center(
          child: Image.asset(
            Res.images.appLogoTop,
            width: 170,
          ),
        ),
      ],
    ));

    //title
    children.add(Center(child: Widgets.title(Res.strings.reset_password)));

    //email
    children.add(SizedBox(height: 50));
    children.add(TextField(
      controller: emailController,
      decoration: InputDecoration(
        prefixIcon: const Icon(Icons.email_outlined),
        iconColor: Res.themes.colors.primary,
        label: Text(Res.strings.emailAddress),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Res.themes.colors.primary,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 15),
      ),
      maxLines: 1,
      keyboardType: TextInputType.emailAddress,
      textInputAction: TextInputAction.done,
      onEditingComplete: startEmailVerification,
    ));

    //next btn
    children.add(const SizedBox(height: 50));
    children.add(
      SizedBox(
        width: double.maxFinite,
        child: ElevatedButton(
          onPressed: startEmailVerification,
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

  void startEmailVerification() {
    screenDriver.startEmailVerification(email: emailController.text);
  }

  @override
  void onVerificationCodeSentToEmail(String email) {
    RPValidateEmailScreen.show(email: email);
  }
}
