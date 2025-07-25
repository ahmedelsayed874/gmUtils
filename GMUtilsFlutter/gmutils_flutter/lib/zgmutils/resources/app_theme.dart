import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_measurement.dart';

class AppTheme {
  static AppColors? appColors;
  static AppMeasurement? appMeasurement;
  static String? toolbarTitleFontFamily;
  static String? defaultFontFamily;

  AppTheme({
    required AppColors appColors,
    required AppMeasurement appMeasurement,
    required String? toolbarTitleFontFamily,
    required String? defaultFontFamily,
  }) {
    AppTheme.appColors = appColors;
    AppTheme.appMeasurement = appMeasurement;
    AppTheme.toolbarTitleFontFamily = toolbarTitleFontFamily;
    AppTheme.defaultFontFamily = defaultFontFamily;
  }

  AppColors get app_colors => AppTheme.appColors!;

  AppMeasurement get app_measurement => AppTheme.appMeasurement!;

  static TextStyle defaultTextStyle({
    Color? textColor,
    double? textSize,
    FontWeight? fontWeight,
    TextDecoration? textDecoration,
    FontStyle? fontStyle,
    String? fontFamily,
    TextOverflow? overflow,
    List<Shadow>? shadows,
    double? letterSpacing,
    double? height,
  }) {
    return TextStyle(
      color: textColor ?? appColors?.text,
      fontSize: textSize ?? appMeasurement?.textSize,
      fontWeight: fontWeight,
      fontFamily: fontFamily ?? defaultFontFamily,
      overflow: overflow,
      shadows: shadows,
      decoration: textDecoration,
      fontStyle: fontStyle,
      letterSpacing: letterSpacing,
      height: height,
    );
  }

  static TextStyle textStyleOfScreenTitle({
    Color? textColor,
    double? textSize,
    String? fontFamily,
    List<Shadow>? shadows,
    double? height,
  }) =>
      defaultTextStyle(
        textColor: textColor ?? appColors?.primary,
        textSize: textSize ?? appMeasurement?.screenTitleSize,
        fontWeight: FontWeight.w800,
        fontFamily: fontFamily ?? defaultFontFamily,
        shadows: shadows,
          height: height,
      );

  static TextStyle textStyleOfSectionTitle({
    Color? textColor,
    double? textSize,
    String? fontFamily,
    List<Shadow>? shadows,
    double? height,
  }) =>
      defaultTextStyle(
        textColor: textColor,
        textSize: textSize,
        fontWeight: FontWeight.w800,
        fontFamily: fontFamily ?? defaultFontFamily,
        shadows: shadows,
          height: height,
      );
}
