import 'package:flutter/material.dart';

import '../zgmutils/resources/app_colors.dart';
import '../zgmutils/resources/app_measurement.dart';
import '../zgmutils/resources/app_theme.dart';

class Themes {
  bool isLightTheme = true;

  Themes(BuildContext? context, {bool? light, String? toolbarTitleFontFamily}) {
    // int density = 1;
    // double textScaleFactor = 1;

    if (context != null) {
      isLightTheme = Theme.of(context).brightness == Brightness.light;

      // try {
      //   var mediaQuery = MediaQuery.of(context);
      //   textScaleFactor = mediaQuery.textScaleFactor;
      // } catch (e) {}
      // density = 1; //to do set it's value based on devicePixelRatio
    }

    if (light != null) {
      isLightTheme = light;
    }

    AppTheme(
      appColors: isLightTheme ? _LightColors() : _DarkColors(),
      appMeasurement: _AppMeasurement(),
      toolbarTitleFontFamily: toolbarTitleFontFamily,
    );
  }

  _AppColors get colors => AppTheme.appColors! as _AppColors;

  AppMeasurement get measurement => AppTheme.appMeasurement!;

  TextStyle defaultTextStyle({
    Color? textColor,
    double? textSize,
    FontWeight? fontWeight,
    TextOverflow? overflow,
    List<Shadow>? shadows,
  }) =>
      AppTheme.defaultTextStyle(
        textColor: textColor,
        textSize: textSize,
        fontWeight: fontWeight,
        overflow: overflow,
        shadows: shadows,
      );

  TextStyle textStyleOfScreenTitle({
    Color? textColor,
    double? textSize,
    List<Shadow>? shadows,
  }) =>
      AppTheme.textStyleOfScreenTitle(
        textColor: textColor,
        textSize: textSize,
        shadows: shadows,
      );

  TextStyle textStyleOfSectionTitle({
    Color? textColor,
    double? fontSize,
    List<Shadow>? shadows,
  }) =>
      AppTheme.textStyleOfSectionTitle(
        textColor: textColor,
        textSize: fontSize,
        shadows: shadows,
      );
}

//==============================================================================

abstract class _AppColors extends AppColors {
  _AppColors({required super.isLightMode});

  Color darkGray = const Color(0xff28333f);
  Color green = const Color(0xff00b634);
  Color darkGreen = const Color(0xff10430d);
  Color orange = const Color(0xfff5b81f);
  Color cyan = const Color(0xff1bb6b6);
  Color red0 = const Color(0xfff50f0a);
}

class _LightColors extends _AppColors {
  _LightColors() : super(isLightMode: true);

  @override
  Color get primary => const Color(0xff31263A);
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
  Color highlight = Colors.grey[300]!;

  @override
  Color title = const Color(0xff525151);
  @override
  Color text = const Color(0xff000000);
  @override
  Color textOnPrimary = const Color(0xff070000);
  @override
  Color hint = const Color(0xffB8B6B6);

  @override
  Color red = const Color(0xfff6540e);
  @override
  Color darkRed = const Color(0xff822906);
}

class _DarkColors extends _AppColors {
  _DarkColors() : super(isLightMode: false);

  @override
  Color primary = const Color(0xff31263A);
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
}

//==============================================================================

class _AppMeasurement extends AppMeasurement {
  @override
  double toolbarTitleSize = 18;

  @override
  double screenTitleSize = 17;

  @override
  double screenPaddingTop = 0.3;

  @override
  double screenPaddingLeft = 0.3;

  @override
  double screenPaddingRight = 0.3;

  @override
  double screenPaddingBottom = 0.3;

  @override
  double paragraphTextSize = 17;

  @override
  double textSize = 15;
}
