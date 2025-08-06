import 'package:flutter/material.dart';
import 'package:gmutils_flutter/main.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/login/login_screen.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';

import '../../../zgmutils/ui/utils/base_stateful_state.dart';
import 'splash_screen_driver.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends BaseState<SplashScreen>
    with SingleTickerProviderStateMixin
    implements SplashScreenDelegate {
  late SplashScreenDriverAbs screenDriver;
  late AnimationController _animationController;
  late Animation<int> _animation;

  @override
  void initState() {
    super.initState();

    // pageLayout.selectedBottomNavIconIndex = 0;

    screenDriver = SplashScreenDriver(this);

    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );

    _animation = IntTween(begin: 0, end: 10).animate(_animationController);
    _animation.addListener(() {
      setState(() {});
    });
    _animationController.forward();
  }

  @override
  Widget build(BuildContext context) {
    return MyRootWidget.withoutToolbar(
      awareTopSafeArea: false,
      backButtonColor: Res.themes.colors.secondary,
    ).setBody(build2(context)).build();
  }

  Widget build2(BuildContext context) {
    return Container(
      color: Res.themes.colors.primary,
      child: Stack(
        children: [
          Image.asset(
            Res.images.splash,
            fit: BoxFit.cover,
            width: double.maxFinite,
            height: double.maxFinite,
          ),
          Align(
            alignment: Alignment.center,
            child: Container(
              margin: EdgeInsets.only(bottom: 2),
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 80),
                child: Image.asset(Res.images.appLogoSplash, color: Colors.white,),
              ),
            ),
          ),
          Align(
            alignment: Alignment.center,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 80),
              child: Image.asset(Res.images.appLogoSplash),
            ),
          ),
          Align(
            alignment: Alignment.bottomCenter,
            child: Text(
              'V$appVersion',
              style: Res.themes.defaultTextStyle(
                textColor: Res.themes.colors.secondary,
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  void gotoHomeScreen() {
    HomeScreen.show();
  }

  @override
  void gotoLoginScreen() {
    LoginScreen.show();
  }
}
