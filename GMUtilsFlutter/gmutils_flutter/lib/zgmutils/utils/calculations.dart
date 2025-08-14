import 'dart:math';

import '../gm_main.dart';
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
      var y = App.isEnglish ? 'Y' : 'ع';
      var m = App.isEnglish ? 'M' : 'ش';
      var d = App.isEnglish ? 'D' : 'ي';

      String txt = '$years $y';
      if (months > 0) {
        txt += ', $months $m';
      }
      if (days > 0) {
        txt += ', $days $d';
      }
      return txt;
    }
    //
    else {
      var yearsStr = App.isEnglish ? 'years' : 'عام';
      var and = App.isEnglish ? 'and' : 'و';
      var monthsStr = App.isEnglish ? 'months' : 'شهور';
      var daysStr = App.isEnglish ? 'days' : 'يوم';

      String txt = '$years $yearsStr';
      if (months > 0) {
        txt += ' $and $months $monthsStr';
      }
      if (days > 0) {
        txt += ' $and $days $daysStr';
      }

      return txt;
    }
  }

  //----------------------------------------------------------------------------

  /// distance in KM
  double calculateDistanceBetweenPoints({
    required double lat1,
    required double lng1,
    required double lat2,
    required double lng2,
    required bool inKilometers,
  }) {
    var p = 0.017453292519943295;
    var a = 0.5 -
        cos((lat2 - lat1) * p) / 2 +
        cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lng2 - lng1) * p)) / 2;

    var km = 12742 * asin(sqrt(a)); //KM
    return inKilometers ? km : (1000.0 * km);
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

  //----------------------------------------------------------------------------

  String percent(double p, {String suffix = ' %'}) {
    //0.4234234
    var per100 = p * 100; //42.34234
    var pi = per100.toInt(); //42
    var d = ((per100 - pi) * 100).toInt() / 100; //0.34

    if (d > 0) {
      return '${pi + d}$suffix';
    } else {
      return '$pi$suffix';
    }
  }
}
