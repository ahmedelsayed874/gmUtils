
import 'dart:ui';

abstract class AppMeasurement {
  final Size screenSize;

  AppMeasurement({required this.screenSize});

  factory AppMeasurement.def() => _AppMeasurementDefault();

  abstract double toolbarTitleSize;
  abstract double screenTitleSize;
  abstract double screenPaddingTop;
  abstract double screenPaddingLeft;
  abstract double screenPaddingRight;
  abstract double screenPaddingBottom;

  abstract double paragraphTextSize;
  abstract double textSize;
}

class _AppMeasurementDefault extends AppMeasurement {
  _AppMeasurementDefault() : super(screenSize: Size(1000, 3000));

  @override
  double paragraphTextSize = 15;

  @override
  double screenPaddingBottom = 15;

  @override
  double screenPaddingLeft = 15;

  @override
  double screenPaddingRight = 15;

  @override
  double screenPaddingTop = 15;

  @override
  double screenTitleSize = 18;

  @override
  double textSize = 13;

  @override
  double toolbarTitleSize = 14;

}