import 'package:flutter/material.dart';

abstract class AppColors {
  final bool isLightMode;

  AppColors({required this.isLightMode});

  Color get primary;

  Color get primaryVariant;

  //
  Color get secondary;

  Color get secondaryVariant;

  //
  Color get toolbar;

  Color get toolbarVariant;

  //
  Color get background;

  //
  Color get bottomNavBar;

  Color get bottomNavBarVariant;

  //
  Color get sideMenu;

  Color get sideMenuVariant;

  //
  Color get title;

  Color get text;

  Color get hint;

  Color get links;

  //
  Color get card;

  //
  Color get white;

  //
  Color get black;

  //
  Color get red;

  //
  Color get blue;

  //
  Color get green;

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
