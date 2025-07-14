import 'dart:math';

import '../../resources/_resources.dart';
import 'date_op.dart';

class Calculations {
  //1987-04-20 = 1987.3287671233
  //2023-07-10 = 2023.5808219178
  double calculateAge({required String birthday}) => calculateAge2(
        birthday: DateOp().parse(
              birthday,
              convertToLocalTime: false,
            ) ??
            DateTime.now(),
      );

  double calculateAge2({required DateTime birthday}) {
    const sumOfDaysPerYear = 365.25;

    //1987-04-20 => 1987.(120.25/365.25) => 1987.3292265572
    //2023-08-19 => 2023.(243.25+19/365.25) => 2023.7180013689
    var birthDate = birthday;
    var birthAsNumber = birthDate.year +
        (_numberOfDaysInYear(birthDate.month, birthDate.day) /
            sumOfDaysPerYear);

    //2023-08-20 => 2023.(243.25+20/365.25) => 2023.7207392197
    var todayDate = DateTime.now();
    var todayAsNumber = todayDate.year +
        (_numberOfDaysInYear(todayDate.month, todayDate.day) /
            sumOfDaysPerYear);

    var ageAsDecimal = todayAsNumber - birthAsNumber;
    //2023.665982204 - 1987.3292265572 = 36.3367556468
    //2023.7207392197 - 2023.7180013689 = 0.0027378508

    //int age = (ageAsDecimal * 100).toInt(); //3633
    //ageAsDecimal = age / 100; //36.33
    return ageAsDecimal;
  }

  //                     1   2      3   4   5   6   7   8   9   10  11  12
  final daysPerMonths = [31, 28.25, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

  int _numberOfDaysInYear(int month, int day) {
    var days = 0.0;
    for (var i = 0; i < month - 1; i++) {
      days += daysPerMonths[i];
    }
    days += day;

    return days.toInt();
  }

  //-----------------

  String calculateAgeAsString({
    required String birthday,
    bool short = false,
  }) {
    return _calculateAgeAsString(
      age: calculateAge(birthday: birthday),
      short: short,
    );
  }

  String calculateAgeAsString2({
    required DateTime birthday,
    bool short = false,
  }) {
    return _calculateAgeAsString(
      age: calculateAge2(birthday: birthday),
      short: short,
    );
  }

  String _calculateAgeAsString({required double age, bool short = false}) {
    //age = 36.3367556468
    //age = 0.0027378508

    int years = age.toInt(); //36    //0

    double monthsInDecimal = (age - years) * 12;
    //(36.3367556468-36)*12 = 4.0410677616
    //(0.0027378508-0)*12 = 0.0328542096

    int months = monthsInDecimal.toInt(); //4      //0

    int daysPerMonth = daysPerMonths[months].toInt();
    int days = ((monthsInDecimal - months) * daysPerMonth).toInt();
    //(4.0410677616 - 4) * 30 = 1.232032848
    //(0.0328542096 - 0) * 31 = 1.0184804976

    if (short) {
      String txt = '$years ${Res.strings.Y}';
      if (months > 0) {
        txt += ', $months ${Res.strings.M}';
      }
      if (days > 0) {
        txt += ', $days ${Res.strings.D}';
      }
      return txt;
    } else {
      String txt = '$years ${Res.strings.years}';
      if (months > 0) {
        txt += ' ${Res.strings.and} $months ${Res.strings.months}';
      }
      if (days > 0) {
        txt += ' ${Res.strings.and} $days ${Res.strings.days}';
      }

      return txt;
    }
  }

  //----------------------------------------------------------------------------

  double calculateDistanceBetweenPoints({
    required double lat1,
    required double lng1,
    required double lat2,
    required double lng2,
  }) {
    var p = 0.017453292519943295;
    var a = 0.5 -
        cos((lat2 - lat1) * p) / 2 +
        cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lng2 - lng1) * p)) / 2;
    return 12742 * asin(sqrt(a));
  }

  //----------------------------------------------------------------------------

  int pixelsInCM(double cm) {
    //An iOS point is equivalent to 1/163 of an inch.

    var oneInchPoints = 163.0; //pt
    var oneCMPoints = oneInchPoints / 2.54; //pt
    var pointCount = oneCMPoints * cm;
    var pixels = pointCount; //* onePointPexils
    return pixels.toInt();
  }
}
