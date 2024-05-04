import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_measurement.dart';

class AppTheme {
  static AppColors? appColors;
  static AppMeasurement? appMeasurement;
  static String? toolbarTitleFontFamily;

  AppTheme({
    required AppColors appColors,
    required AppMeasurement appMeasurement,
    String? toolbarTitleFontFamily,
  }) {
    AppTheme.appColors = appColors;
    AppTheme.appMeasurement = appMeasurement;
    AppTheme.toolbarTitleFontFamily = toolbarTitleFontFamily;
  }

  AppColors get app_colors => AppTheme.appColors!;

  AppMeasurement get app_measurement => AppTheme.appMeasurement!;

  static TextStyle defaultTextStyle({
    Color? textColor,
    double? textSize,
    FontWeight? fontWeight,
    String? fontFamily,
    TextOverflow? overflow,
    List<Shadow>? shadows,
  }) {
    return TextStyle(
      color: textColor ?? appColors?.text,
      fontSize: textSize ?? appMeasurement?.textSize,
      fontWeight: fontWeight,
      fontFamily: fontFamily,
      overflow: overflow,
      shadows: shadows,
    );
  }

  static TextStyle textStyleOfScreenTitle({
    Color? textColor,
    double? textSize,
    String? fontFamily,
    List<Shadow>? shadows,
  }) =>
      defaultTextStyle(
        textColor: textColor ?? appColors?.primary,
        textSize: textSize ?? appMeasurement?.screenTitleSize,
        fontWeight: FontWeight.w800,
        fontFamily: fontFamily,
        shadows: shadows,
      );

  static TextStyle textStyleOfSectionTitle({
    Color? textColor,
    double? textSize,
    String? fontFamily,
    List<Shadow>? shadows,
  }) =>
      defaultTextStyle(
        textColor: textColor,
        textSize: textSize,
        fontWeight: FontWeight.w800,
        fontFamily: fontFamily,
        shadows: shadows,
      );
}
