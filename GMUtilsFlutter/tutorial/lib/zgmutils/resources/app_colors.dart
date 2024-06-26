import 'package:flutter/material.dart';

abstract class AppColors {
  final bool isLightMode;

  AppColors({required this.isLightMode});

  Color get primary;
  Color get secondary;

  Color get background;

  Color get sideMenu;

  Color get toolbar;
  Color get toolbarTextColor;

  Color get card;

  Color get highlight;

  Color get title;

  Color get text;

  Color get textOnPrimary;

  Color get hint;

  Color get red;

  Color get darkRed;

  MaterialColor get primarySwatch => MaterialColor(
        primary.value,
        {
          50: primary,
          100: primary,
          200: primary,
          300: primary,
          400: primary,
          500: primary,
          600: primary,
          700: primary,
          800: primary,
          900: primary,
        },
      );
}
