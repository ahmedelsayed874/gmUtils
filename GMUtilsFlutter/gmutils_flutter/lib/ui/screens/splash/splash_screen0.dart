import 'package:gmutils_flutter/main.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/ui/screens/auth/login/login_screen.dart';
import 'package:gmutils_flutter/ui/screens/home/home_screen.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/_root_widget.dart';
import 'package:flutter/material.dart';

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
          Align(
            alignment: Alignment.center,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Padding(
                  padding: const EdgeInsets.all(80.0),
                  child: Image.asset(Res.images.appLogoSplash),
                ),
                ...schoolValuesWidgets(from: 0, to: _animation.value),
              ],
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

  final schoolValues = [
    'الوطنية والانتماء',
    'الاحترام والتسامح',
    'العزيمة والمثابرة',
    'التمّيز والتنافسية',
    'الإتقان والانضباط',
    'التشاركّية والعمل بروح الفريق',
    'المرونة والإيجابية',
    'المسؤولية والأمانة',
  ];

  schoolValuesWidgets({required int from, required int to}) {
    if (to > schoolValues.length) to = schoolValues.length;

    var rows = <Widget>[];
    for (int i = from; i < to; i = i + 2) {
      rows.add(
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            children: [
              Expanded(
                child: Text(
                  schoolValues[i],
                  style: Res.themes.defaultTextStyle(
                    textColor: Colors.white,
                    textSize: 11,
                  ),
                  textAlign: App.isEnglish ? TextAlign.right : TextAlign.left,
                ),
              ),
              const SizedBox(width: 5),
              const Icon(Icons.diamond, size: 15, color: Colors.white),

              //
              const SizedBox(width: 15),

              //
              const Icon(Icons.diamond, size: 15, color: Colors.white),
              const SizedBox(width: 5),
              Expanded(
                child: Text(
                  (i < schoolValues.length - 1 ? schoolValues[i + 1] : ""),
                  textAlign: App.isEnglish ? TextAlign.left : TextAlign.right,
                  style: Res.themes.defaultTextStyle(
                    textColor: Colors.white,
                    textSize: 11,
                  ),
                ),
              ),
            ],
          ),
        ),
      );
    }

    return rows;
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
