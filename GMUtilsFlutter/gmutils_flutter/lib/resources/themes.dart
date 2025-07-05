import 'package:flutter/material.dart';

import '../zgmutils/resources/app_colors.dart';
import '../zgmutils/resources/app_measurement.dart';
import '../zgmutils/resources/app_theme.dart';
import 'fonts.dart';

class Themes {
  bool isLightTheme = true;

  Themes(BuildContext context, {bool? light}) {
    if (light != null) {
      isLightTheme = light;
    } else {
      isLightTheme = Theme.of(context).brightness == Brightness.light;
    }

    AppTheme(
      appColors: isLightTheme ? _LightColors() : _DarkColors(),
      appMeasurement: _AppMeasurement(
        screenSize: MediaQuery.of(context).size,
      ),
      toolbarTitleFontFamily: fonts.toolbarTitle,
      defaultFontFamily: fonts.cairo,
    );
  }

  _AppColors get colors => AppTheme.appColors! as _AppColors;

  AppMeasurement get measurement => AppTheme.appMeasurement!;

  final Fonts fonts = Fonts();

  TextStyle defaultTextStyle({
    Color? textColor,
    double? textSize,
    FontWeight? fontWeight,
    TextDecoration? textDecoration,
    FontStyle? fontStyle,
    String? fontFamily,
    TextOverflow? overflow,
    List<Shadow>? shadows,
    double? letterSpacing,
  }) =>
      AppTheme.defaultTextStyle(
        textColor: textColor,
        textSize: textSize,
        fontWeight: fontWeight,
        textDecoration: textDecoration,
        fontStyle: fontStyle,
        fontFamily: fontFamily,
        overflow: overflow,
        shadows: shadows,
        letterSpacing: letterSpacing,
      );

  TextStyle textStyleOfScreenTitle({
    Color? textColor,
    double? textSize,
    String? fontFamily,
    List<Shadow>? shadows,
  }) =>
      AppTheme.textStyleOfScreenTitle(
        textColor: textColor ?? colors.primary,
        textSize: textSize ?? 20,
        fontFamily: fontFamily,
        shadows: shadows ?? [Shadow(blurRadius: 0.2)],
      );

  TextStyle textStyleOfSectionTitle({
    Color? textColor,
    double? fontSize,
    String? fontFamily,
    List<Shadow>? shadows,
  }) =>
      AppTheme.textStyleOfSectionTitle(
        textColor: textColor,
        textSize: fontSize,
        fontFamily: fontFamily,
        shadows: shadows,
      );
}

//==============================================================================

abstract class _AppColors extends AppColors {
  _AppColors({required super.isLightMode});

  Color darkGray = const Color(0xff28333f);
  Color green = const Color(0xff00b634);
  Color green2 = const Color(0xff029f33);
  Color darkGreen = const Color(0xff10430d);
  Color orange = const Color(0xfff5b81f);
  Color cyan = const Color(0xff1bb6b6);
  Color red0 = const Color(0xfff50f0a);
}

class _LightColors extends _AppColors {
  _LightColors() : super(isLightMode: true);

  @override
  Color get primary => const Color(0xff3bbc04);

  @override
  Color get secondary => const Color(0xffD94A19);

  @override
  Color background = const Color(0xffffffff);
  @override
  Color sideMenu = const Color(0xd3050505);

  @override
  Color get toolbar => primary;

  @override
  Color get toolbarTextColor => secondary;

  @override
  Color card = Colors.white;
  @override
  Color highlight = const Color(0xff777676);

  @override
  Color title = const Color(0xff525151);
  @override
  Color text = const Color(0xff000000);
  @override
  Color textOnPrimary = const Color(0xffffffff);
  @override
  Color hint = const Color(0xffB8B6B6);

  @override
  Color red = const Color(0xffdc3d4a); //0xfff6540e
  @override
  Color darkRed = const Color(0xff822906);

  @override
  Color get chatCardLeft => Color(0xFFEFEFEF);

  //Color get chatCardLeft => Colors.yellow.withOpacity(0.7);

  @override
  Color get chatCardRight => secondary;

  //Color get chatCardRight => const Color(0xfff68a66);

  @override
  Color get links => const Color(0xff0b54f1);

  @override
  Color get white => Colors.white;

  @override
  Color get black => Colors.black;

  @override
  // TODO: implement blue
  Color get blue => throw UnimplementedError();

  @override
  // TODO: implement bottomNavBar
  Color get bottomNavBar => throw UnimplementedError();

  @override
  // TODO: implement bottomNavBarVariant
  Color get bottomNavBarVariant => throw UnimplementedError();

  @override
  // TODO: implement primaryVariant
  Color get primaryVariant => throw UnimplementedError();

  @override
  // TODO: implement secondaryVariant
  Color get secondaryVariant => throw UnimplementedError();

  @override
  // TODO: implement sideMenuVariant
  Color get sideMenuVariant => throw UnimplementedError();

  @override
  // TODO: implement toolbarVariant
  Color get toolbarVariant => throw UnimplementedError();
}

class _DarkColors extends _AppColors {
  _DarkColors() : super(isLightMode: false);

  @override
  Color primary = const Color(0xff144201);

  @override
  Color get secondary => const Color(0xff5e1f0a);

  @override
  Color background = const Color(0xff7c7878);
  @override
  Color sideMenu = const Color(0xff424141);
  @override
  Color toolbar = const Color(0xff787878);
  @override
  Color toolbarTextColor = const Color(0xffe3e2e2);

  @override
  Color card = const Color(0xff7C7979);
  @override
  Color highlight = Colors.grey[800]!;

  @override
  Color title = const Color(0xffDDDADA);
  @override
  Color text = const Color(0xffffffff);
  @override
  Color textOnPrimary = const Color(0xff090000);
  @override
  Color hint = const Color(0xffB8B6B6);

  Color chatBg = const Color(0xff082c52);

  @override
  Color red = const Color(0xfff6540e);
  @override
  Color darkRed = const Color(0xff822906);

  @override
  Color get chatCardLeft => Colors.yellow.withOpacity(0.7);

  @override
  Color get chatCardRight => secondary.withAlpha(80);

  @override
  Color get links => const Color(0xff0b54f1);

  @override
  Color get white => Colors.black87;

  @override
  Color get black => Colors.white70;

  @override
  // TODO: implement blue
  Color get blue => throw UnimplementedError();

  @override
  // TODO: implement bottomNavBar
  Color get bottomNavBar => throw UnimplementedError();

  @override
  // TODO: implement bottomNavBarVariant
  Color get bottomNavBarVariant => throw UnimplementedError();

  @override
  // TODO: implement primaryVariant
  Color get primaryVariant => throw UnimplementedError();

  @override
  // TODO: implement secondaryVariant
  Color get secondaryVariant => throw UnimplementedError();

  @override
  // TODO: implement sideMenuVariant
  Color get sideMenuVariant => throw UnimplementedError();

  @override
  // TODO: implement toolbarVariant
  Color get toolbarVariant => throw UnimplementedError();
}

//==============================================================================

class _AppMeasurement extends AppMeasurement {
  _AppMeasurement({required super.screenSize});

  @override
  double toolbarTitleSize = 17;

  @override
  double screenTitleSize = 17;

  @override
  double screenPaddingTop = 0.0;

  @override
  double screenPaddingLeft = 0.1;

  @override
  double screenPaddingRight = 0.1;

  @override
  double screenPaddingBottom = 0.1;

  @override
  double paragraphTextSize = 16;

  @override
  double textSize = 14;
}
