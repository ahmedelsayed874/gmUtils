
import 'dart:ui';

abstract class AppMeasurement {
  final Size screenSize;

  AppMeasurement({required this.screenSize});

  abstract double toolbarTitleSize;
  abstract double screenTitleSize;
  abstract double screenPaddingTop;
  abstract double screenPaddingLeft;
  abstract double screenPaddingRight;
  abstract double screenPaddingBottom;

  abstract double paragraphTextSize;
  abstract double textSize;
}