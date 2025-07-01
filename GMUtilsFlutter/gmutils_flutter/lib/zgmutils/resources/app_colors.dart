import 'package:flutter/material.dart';

abstract class AppColors {
  final bool isLightMode;

  AppColors({required this.isLightMode});

  factory AppColors.def({required bool isLightMode}) => _AppColorsDefault(
        isLightMode: isLightMode,
      );

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

class _AppColorsDefault extends AppColors {
  _AppColorsDefault({required super.isLightMode});

  @override
  Color get background => Colors.white;

  @override
  Color get black => Colors.black;

  @override
  Color get blue => Colors.blueAccent;

  @override
  Color get bottomNavBar => Colors.black;

  @override
  Color get bottomNavBarVariant => Colors.white;

  @override
  Color get card => Colors.grey[100] ?? Colors.grey;

  @override
  Color get green => Colors.green;

  @override
  Color get hint => Colors.grey[400] ?? Colors.grey;

  @override
  Color get links => Colors.blueAccent;

  @override
  Color get primary => Colors.blueAccent;

  @override
  Color get primaryVariant => Colors.white;

  @override
  Color get red => Colors.red;

  @override
  Color get secondary => Colors.red;

  @override
  Color get secondaryVariant => Colors.white;

  @override
  Color get sideMenu => primary;

  @override
  Color get sideMenuVariant => primaryVariant;

  @override
  Color get text => Colors.black;

  @override
  Color get title => Colors.grey;

  @override
  Color get toolbar => primary;

  @override
  Color get toolbarVariant => primaryVariant;

  @override
  Color get white => Colors.white;
}
