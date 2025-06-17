import '../../../zgmutils/utils/logs.dart';
import 'package:flutter/material.dart';

class DateOp {
  static const ONE_MINUTE_MILLISECONDS = 60 * 1000;
  static const ONE_HOUR_MILLISECONDS = 60 * 60 * 1000;
  static const ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000;

  ///"yyyy-MM-dd HH:mm:ss.SSSXXX" ========> 2012-01-23 01:23:45+02:00
  ///"yyyy-MM-dd HH:mm:ssZ" ========> 2012-01-23 01:23:45+0200
  String formatForDatabase(
    DateTime dateTime, {
    required bool dateOnly,
    bool convertToUtc = true,
    bool includeZoneTime = false,
  }) {
    if (convertToUtc) {
      if (!(dateTime.isUtc) && !(dateOnly)) {
        dateTime = dateTime.toUtc();
      }
    }

    if (dateOnly) {
      return formatForDatabase2(
        day: DateOpDayComponent(
          year: dateTime.year,
          month: dateTime.month,
          day: dateTime.day,
        ),
        time: null,
      );
    } else {
      DateOpTimeZoneComponent? timezone;

      if (includeZoneTime) {
        if (convertToUtc) {
          timezone = DateOpTimeZoneComponent(
            isPositive: true,
            hours: 0,
            minutes: 0,
          );
        } else {
          var totalMinutes = dateTime.timeZoneOffset.inMinutes;
          var isPositive = totalMinutes >= 0;
          int timeZoneHours = totalMinutes ~/ 60;
          int timeZoneMinutes = totalMinutes - (timeZoneHours * 60);

          timezone = DateOpTimeZoneComponent(
            isPositive: isPositive,
            hours: timeZoneHours,
            minutes: timeZoneMinutes,
          );
        }
      }

      return formatForDatabase2(
        day: DateOpDayComponent(
          year: dateTime.year,
          month: dateTime.month,
          day: dateTime.day,
        ),
        time: DateOpTimeComponent(
          hour: dateTime.hour,
          minute: dateTime.minute,
          second: dateTime.second,
          timezone: timezone,
        ),
      );
    }
  }

  ///"yyyy-MM-dd HH:mm:ss.SSSXXX" ========> 2012-01-23 01:23:45+02:00
  ///"yyyy-MM-dd HH:mm:ssZ" ========> 2012-01-23 01:23:45+0200
  String formatForDatabase2({
    required DateOpDayComponent day,
    required DateOpTimeComponent? time,
  }) {
    String formattedDate = '';

    //day -----------------------------------
    formattedDate += day.formattedForDb;

    //time -----------------------------------
    if (time != null) {
      formattedDate += ' ${time.formattedForDb}';
    }

    return formattedDate;
  }

  String formatTimeForDatabase(TimeOfDay time) {
    return DateOpTimeComponent(
      hour: time.hour,
      minute: time.minute,
      second: 0,
      timezone: null,
    ).formattedForDb;
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  String getDayName(int day, bool en, {bool short = false}) {
    var names = {
      DateTime.saturday: {'en': 'Saturday', 'ar': 'السبت'},
      DateTime.sunday: {'en': 'Sunday', 'ar': 'الأحد'},
      DateTime.monday: {'en': 'Monday', 'ar': 'الإثنين'},
      DateTime.tuesday: {'en': 'Tuesday', 'ar': 'الثلاثاء'},
      DateTime.wednesday: {'en': 'Wednesday', 'ar': 'الأربعاء'},
      DateTime.thursday: {'en': 'Thursday', 'ar': 'الخميس'},
      DateTime.friday: {'en': 'Friday', 'ar': 'الجمعه'},
    };

    var name = names[day]?[en ? 'en' : 'ar'];
    if (en && name != null) {
      if (short) {
        name = name.substring(0, 3);
      }
    }

    return name ?? 'Unknown Day($day)';
  }

  String getMonthName(int month, bool en, {bool short = false}) {
    var names = {
      DateTime.january: {'en': 'January', 'ar': 'يناير'},
      DateTime.february: {'en': 'February', 'ar': 'فبراير'},
      DateTime.march: {'en': 'March', 'ar': 'مارس'},
      DateTime.april: {'en': 'April', 'ar': 'إبريل'},
      DateTime.may: {'en': 'May', 'ar': 'مايو'},
      DateTime.june: {'en': 'June', 'ar': 'يونيو'},
      DateTime.july: {'en': 'July', 'ar': 'يوليو'},
      DateTime.august: {'en': 'August', 'ar': 'أغسطس'},
      DateTime.september: {'en': 'September', 'ar': 'سبتمبر'},
      DateTime.october: {'en': 'October', 'ar': 'أكتوبر'},
      DateTime.november: {'en': 'November', 'ar': 'نوفمبر'},
      DateTime.december: {'en': 'December', 'ar': 'ديسمبر'},
    };

    var name = names[month]?[en ? 'en' : 'ar'];
    if (en && name != null) {
      if (short) {
        name = name.substring(0, 3);
      }
    }

    return name ?? 'Unknown Month($month)';
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /// Saturday 22, November 2021 11:00:00 AM
  String formatForUser(
    DateTime dateTime, {
    required bool en,
    required bool dateOnly,
    bool convertToLocalTime = true,
    bool includeSeconds = false,
    bool useShortNames = false,
    bool inTwoLines = false,
  }) {
    DateTime date_time;
    if (!dateOnly && convertToLocalTime) {
      if (dateTime.isUtc) {
        date_time = dateTime.toLocal();
      } else {
        var now = DateTime.now();
        date_time = dateTime.add(now.timeZoneOffset);
      }
    } else {
      date_time = dateTime;
    }

    String formattedDate = '';

    formattedDate += getDayName(date_time.weekday, en, short: useShortNames);
    formattedDate += ' ${date_time.day}, ';

    formattedDate += getMonthName(date_time.month, en, short: useShortNames);
    formattedDate += ' ${date_time.year}';

    if (!dateOnly) {
      if (inTwoLines) formattedDate += '\n';
      formattedDate += ' ';

      var time = DateOpTimeComponent(
        hour: date_time.hour,
        minute: date_time.minute,
        second: date_time.second,
        timezone: null,
      );
      formattedDate += time.formattedForUser(
        en: en,
        includeSeconds: includeSeconds,
      );
    }

    return formattedDate;
  }

  String formatForUser2(
    String date, {
    required bool en,
    required bool dateOnly,
    bool convertToLocalTime = true,
    bool includeSeconds = false,
    bool useShortNames = false,
    bool inTwoLines = false,
  }) {
    var dateTime = parse(
      date,
      convertToLocalTime: !dateOnly && convertToLocalTime,
    );
    if (dateTime == null) {
      return date;
    }

    return formatForUser(
      dateTime,
      en: en,
      dateOnly: dateOnly,
      convertToLocalTime: false,
      //convertToLocalTime,
      includeSeconds: includeSeconds,
      useShortNames: useShortNames,
      inTwoLines: inTwoLines,
    );
  }

  String formatTimeForUser(
    TimeOfDay time, {
    required bool en,
  }) {
    var time2 = DateOpTimeComponent(
      hour: time.hour,
      minute: time.minute,
      second: 0,
      timezone: null,
    );

    return time2.formattedForUser(
      en: en,
      includeSeconds: false,
    );
  }

  String formatTimeForUser2(
    String time, {
    required bool en,
    bool includeSeconds = false,
  }) {
    var t = time.split(' ');
    if (t.length > 1) return time;

    var p = time.split(':');
    if (p.length >= 2) {
      var h = int.parse(p[0]);
      var m = int.parse(p[1]);
      int s = 0;
      if (p.length > 2) {
        s = int.parse(p[2]);
      }

      var time = DateOpTimeComponent(
        hour: h,
        minute: m,
        second: s,
        timezone: null,
      );
      String formattedTime = time.formattedForUser(
        en: en,
        includeSeconds: includeSeconds,
      );

      return formattedTime;
    }

    return time;
  }

  //============================================================================

  String sinceNowStatement(
    String otherDate,
    bool en, {
    bool convertToLocalTime = true,
    bool includeSeconds = false,
  }) {
    var date = parse(otherDate, convertToLocalTime: convertToLocalTime);

    if (date == null) return otherDate;
    return sinceNowStatement2(date, en, includeSeconds: includeSeconds);
  }

  String sinceNowStatement2(
    DateTime otherDate,
    bool en, {
    bool includeSeconds = false,
  }) {
    var now = DateTime.now();
    var nowMillis = now.millisecondsSinceEpoch;

    int diffTimeMillis = nowMillis - otherDate.millisecondsSinceEpoch;

    int diffTimeDays = diffTimeMillis ~/ ONE_DAY_MILLISECONDS;

    diffTimeMillis = diffTimeMillis - (diffTimeDays * ONE_DAY_MILLISECONDS);
    int diffTimeHours = diffTimeMillis ~/ ONE_HOUR_MILLISECONDS;

    diffTimeMillis = diffTimeMillis - (diffTimeHours * ONE_HOUR_MILLISECONDS);
    int diffTimeMinutes = diffTimeMillis ~/ ONE_MINUTE_MILLISECONDS.toDouble();

    //if other date is in past, then return the full date statement
    if (diffTimeDays < 0 || diffTimeHours < 0 || diffTimeMinutes < 0) {
      return formatForUser(
        otherDate,
        en: en,
        dateOnly: false,
        convertToLocalTime: false,
        includeSeconds: includeSeconds,
      );
    }

    //increase day difference by 1 if the diff = 0 and date now is greater than the other date
    if (diffTimeDays == 0 && now.day > otherDate.day) {
      diffTimeDays = 1;
    }

    //--------------------------- WRITE THE STATEMENT --------------------------
    if (diffTimeDays == 0) {
      String statement = '';

      statement += (en ? 'Today' : 'اليوم');

      if (diffTimeHours == 0) {
        if (diffTimeMinutes == 0) {
          statement += en ? ', Now' : '، الآن';
        } else {
          statement += ' ';
          statement += en ? 'since' : 'منذ';
          statement += ' ';

          if (en) {
            if (diffTimeMinutes == 1) {
              statement += 'a Minute';
            } else {
              statement += '$diffTimeMinutes Minutes';
            }
          } else {
            if (diffTimeMinutes == 1) {
              statement += 'دقيقة';
            } else if (diffTimeMinutes == 2) {
              statement += 'دقيقتان';
            } else if (diffTimeMinutes < 11) {
              statement += '$diffTimeMinutes دقائق ';
            } else {
              statement += '$diffTimeMinutes دقيقة ';
            }
          }
        }
      } else {
        statement += en ? ' At ' : ' ';

        var time = DateOpTimeComponent(
          hour: otherDate.hour,
          minute: otherDate.minute,
          second: otherDate.second,
          timezone: null,
        );
        statement += time.formattedForUser(
          en: en,
          includeSeconds: includeSeconds,
        );

        return statement;
      }

      return statement;
    }

    //
    else if (diffTimeDays == 1) {
      //yesterday
      String statement = '';

      statement += (en ? 'Yesterday' : 'أمس');
      statement += (en ? ' At ' : ' ');

      var time = DateOpTimeComponent(
        hour: otherDate.hour,
        minute: otherDate.minute,
        second: otherDate.second,
        timezone: null,
      );
      statement += time.formattedForUser(
        en: en,
        includeSeconds: includeSeconds,
      );

      return statement;
    }

    //
    else {
      return formatForUser(
        otherDate,
        en: en,
        dateOnly: false,
        convertToLocalTime: false,
        includeSeconds: includeSeconds,
      );
    }
  }

  String nowAsSingleNumber() {
    var now = DateTime.now();

    String s = now.year.toString().substring(2);
    s += (now.month < 10 ? '0' : '') + now.month.toString();
    s += (now.day < 10 ? '0' : '') + now.day.toString();
    s += (now.hour < 10 ? '0' : '') + now.hour.toString();
    s += (now.minute < 10 ? '0' : '') + now.minute.toString();
    s += (now.second < 10 ? '0' : '') + now.second.toString();
    s += (now.millisecond < 10 ? '00' : (now.millisecond < 100 ? '0' : '')) +
        now.millisecond.toString();

    return s;
  }

  DateTime? parse(
    String date, {
    bool convertToLocalTime = true,
    DateTime? defaultDate,
  }) {
    var parsedDate = DateTime.tryParse(date);

    if (parsedDate != null) {
      if (parsedDate.isUtc) {
        parsedDate = parsedDate.toLocal();
      }
      //
      else if (convertToLocalTime) {
        var now = DateTime.now();
        parsedDate = parsedDate.add(now.timeZoneOffset);
      }

      return parsedDate;
    }

    return defaultDate;
  }

  //============================================================================

  DateTime? parseApiHeaderTime(String datetimeAtServer) {
    try {
      var months = [
        '',
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
      ];

      var day = int.parse(datetimeAtServer.substring(5, 7));
      var month = months.indexOf(datetimeAtServer.substring(8, 11));
      var year = int.parse(datetimeAtServer.substring(12, 16));
      var hour = int.parse(datetimeAtServer.substring(17, 19));
      var minute = int.parse(datetimeAtServer.substring(20, 22));
      var second = int.parse(datetimeAtServer.substring(23, 25));

      var dt = DateTime(year, month, day, hour, minute, second);

      return dt;
    } catch (e) {
      return null;
    }
  }

  DateOpDuration? remainTo(String date) {
    var tdt = parse(date);
    if (tdt == null) {
      Logs.print(
        () => 'DateOp.remainTo --> NULL due wrong date format $date',
      );
      return null;
    }

    var remainMillisec =
        tdt.millisecondsSinceEpoch - DateTime.now().millisecondsSinceEpoch;

    int days = remainMillisec ~/ (DateOp.ONE_DAY_MILLISECONDS);
    remainMillisec -= (days * DateOp.ONE_DAY_MILLISECONDS);

    int hours = remainMillisec ~/ (DateOp.ONE_HOUR_MILLISECONDS);
    remainMillisec -= (hours * DateOp.ONE_HOUR_MILLISECONDS);

    int minutes = remainMillisec ~/ (DateOp.ONE_MINUTE_MILLISECONDS);
    remainMillisec -= (minutes * DateOp.ONE_MINUTE_MILLISECONDS);

    int seconds = remainMillisec ~/ 1000;

    Logs.print(
      () => 'DateOp.remainTo --> '
          'days: $days, '
          'hours: $hours, '
          'minutes: $minutes, '
          'seconds: $seconds',
    );

    return DateOpDuration(
      days: days,
      hours: hours,
      minutes: minutes,
      seconds: seconds,
    );
  }

  String? remainToAsStatement(
    String date, {
    required bool en,
    bool useShortNames = true,
    bool reportExactDaysCount = false,
    bool includeMinutes = true,
    bool includeSeconds = true,
    bool acceptNegative = false,
  }) {
    var duration = remainTo(date);
    if (duration == null) return null;

    if (!acceptNegative) {
      if (duration.inPast) {
        return null;
      }
    }

    int days = duration.days;
    int hours = duration.hours;
    int minutes = duration.minutes;
    int seconds = duration.seconds;

    String statement = '';
    if (days != 0) {
      if (en) {
        statement += '${!reportExactDaysCount && days > 99 ? '99+' : '$days'} ';

        if (useShortNames) {
          statement += (en ? 'D' : 'ي');
        } else {
          statement += 'Day${days > 1 ? 's' : ''}';
        }
      } else {
        if (days == 1) {
          statement += 'يوم';
        }
        //
        else if (days == 2) {
          statement += 'يومان';
        }
        //
        else if (days <= 10) {
          statement +=
              '${!reportExactDaysCount && days > 99 ? '99+' : '$days'} ';
          statement += 'أيام';
        }
        //
        else {
          statement +=
              '${!reportExactDaysCount && days > 99 ? '99+' : '$days'} ';
          statement += 'يوم';
        }
      }
    }
    if (hours != 0) {
      if (statement.isNotEmpty) {
        statement += (en ? ', ' : '، ');
      }

      if (en) {
        //statement += '${hours > 9 ? '' : (hours < -9 ? '' : ' ')}$hours ';
        statement += '$hours ';
        if (useShortNames) {
          statement += (en ? 'H' : 'س');
        } else {
          statement += 'Hour${hours > 1 ? 's' : ''}';
        }
      } else {
        if (hours == 1) {
          statement += 'ساعه';
        }
        //
        else if (hours == 2) {
          statement += 'ساعتان';
        }
        //
        else if (hours <= 10) {
          //statement += '${hours > 9 ? '' : (hours < -9 ? '' : ' ')}$hours ';
          statement += '$hours ';
          statement += 'ساعات';
        }
        //
        else {
          //statement += '${hours > 9 ? '' : (hours < -9 ? '' : ' ')}$hours ';
          statement += '$hours ';
          statement += 'ساعه';
        }
      }
    }
    if (minutes != 0 && includeMinutes) {
      if (statement.isNotEmpty) {
        statement += (en ? ', ' : '، ');
      }

      if (en) {
        //statement += '${minutes > 9 ? '' : (minutes < -9 ? '' : ' ')}$minutes ';
        statement += '$minutes ';
        if (useShortNames) {
          statement += (en ? 'M' : 'د');
        } else {
          statement += 'Minute${minutes > 1 ? 's' : ''}';
        }
      } else {
        if (minutes == 1) {
          statement += 'دقيقة';
        }
        //
        else if (minutes == 2) {
          statement += 'دقيقتان';
        }
        //
        else if (minutes <= 10) {
          //statement += '${minutes > 9 ? '' : (minutes < -9 ? '' : ' ')}$minutes ';
          statement += '$minutes ';
          statement += 'دقائق';
        }
        //
        else {
          //statement += '${minutes > 9 ? '' : (minutes < -9 ? '' : ' ')}$minutes ';
          statement += '$minutes ';
          statement += 'دقيقة';
        }
      }
    }
    if (seconds != 0 && includeSeconds) {
      if (statement.isNotEmpty) {
        statement += (en ? ', ' : '، ');
      }

      if (en) {
        statement += '${seconds > 9 ? '' : (seconds < -9 ? '' : ' ')}$seconds ';
        if (useShortNames) {
          statement += (en ? 'S' : 'ث');
        } else {
          statement += 'Second${seconds > 1 ? 's' : ''}';
        }
      } else {
        if (seconds == 1) {
          statement += 'ثانية';
        }
        //
        else if (seconds == 2) {
          statement += 'ثانيتان';
        }
        //
        else if (seconds <= 10) {
          statement +=
              '${seconds > 9 ? '' : (seconds < -9 ? '' : ' ')}$seconds ';
          statement += 'ثوان';
        }
        //
        else {
          statement +=
              '${seconds > 9 ? '' : (seconds < -9 ? '' : ' ')}$seconds ';
          statement += 'ثانية';
        }
      }
    }

    if (statement.isEmpty) {
      statement =
          useShortNames ? (en ? '0 Second' : '٠ ثانية') : (en ? '0 S' : '٠ ث');
    }

    return statement;
  }
}

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

class DateOpDayComponent {
  int year;
  int month;
  int day;

  DateOpDayComponent({
    required this.year,
    required this.month,
    required this.day,
  });

  ///"yyyy-MM-dd" ========> 2012-01-23
  String get formattedForDb {
    String formattedDate = '';

    //year --------------------------------
    formattedDate += '$year';

    //month --------------------------------
    formattedDate += '-';
    if (month < 10) formattedDate += '0';
    formattedDate += '$month';

    //day ---------------------------------
    formattedDate += '-';
    if (day < 10) formattedDate += '0';
    formattedDate += '$day';

    return formattedDate;
  }
}

class DateOpTimeComponent {
  final int hour;
  final int minute;
  final int? second;
  final DateOpTimeZoneComponent? timezone;

  DateOpTimeComponent({
    required this.hour,
    required this.minute,
    required this.second,
    required this.timezone,
  });

  ///"HH:mm:ss.SSSXXX" ========> 01:23:45+02:00
  ///"HH:mm:ssZ" ========> 01:23:45+0200
  String get formattedForDb {
    //if (timezone == null) throw 'timezone is required for formatting function';

    String formattedTime = '';

    //hour ---------------------------------
    if (hour < 10) formattedTime += '0';
    formattedTime += '$hour';

    //minute --------------------------------
    formattedTime += ':';
    if (minute < 10) formattedTime += '0';
    formattedTime += '$minute';

    //seconds --------------------------------
    if (second != null) {
      formattedTime += ':';
      if (second! < 10) formattedTime += '0';
      formattedTime += '$second';
    }

    //timezone --------------------------------
    if (timezone != null) {
      formattedTime += timezone!.formatted;
    }

    return formattedTime;
  }

  String formattedForUser({required bool en, required bool includeSeconds}) {
    var h = hour;
    if (h > 12) {
      h = h - 12;
    } else if (h == 0) {
      h = 12;
    }

    String formattedDate = '';

    //hours ---------------------------------
    if (h < 10) formattedDate += '0';
    formattedDate += '$h';

    //minutes ---------------------------------
    formattedDate += ':';
    if (minute < 10) formattedDate += '0';
    formattedDate += '$minute';

    //seconds ---------------------------------
    if (includeSeconds && second != null) {
      formattedDate += ':';
      if (second! < 10) formattedDate += '0';
      formattedDate += '$second';
    }

    if (hour < 12) {
      formattedDate += en ? ' AM' : ' ص';
    } else {
      formattedDate += en ? ' PM' : ' م';
    }

    return formattedDate;
  }
}

class DateOpTimeZoneComponent {
  final bool isPositive;
  final int hours;
  final int minutes;

  DateOpTimeZoneComponent({
    required this.isPositive,
    required this.hours,
    required this.minutes,
  });

  ///"SSSXXX" ========> +02:00
  ///"Z" ========> +0200
  String get formatted => ''
      '${isPositive ? '+' : '-'}'
      '${hours < 10 ? '0' : ''}$hours'
      '${minutes < 10 ? '0' : ''}$minutes';
}

class DateOpDuration {
  final int days;
  final int hours;
  final int minutes;
  final int seconds;

  DateOpDuration({
    required this.days,
    required this.hours,
    required this.minutes,
    required this.seconds,
  });

  bool get inPast =>
      (days < 0) || (hours < 0) || (minutes < 0) || (seconds < 0);

  double get inDays => inHours / 24;

  double get inHours {
    double h = days * 24;
    h += hours;
    h += (minutes / 60.0);
    h += (seconds / 60.0 / 60.0);

    int i = h.toInt();
    String hs = '${h - i}';
    int length = 5;
    if (i < 0) length++;
    if (hs.length > length) hs = hs.substring(0, length);

    return i.toDouble() + double.parse(hs);
  }

  double get inMinutes => inHours * 60.0;

  double get inSeconds => inHours * 60.0 * 60.0;
}
