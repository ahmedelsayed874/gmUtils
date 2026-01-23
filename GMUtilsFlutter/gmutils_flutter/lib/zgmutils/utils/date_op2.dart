import 'date_op.dart';

class DateOp2 {
  static const ONE_MINUTE_MILLISECONDS = DateOp.ONE_MINUTE_MILLISECONDS;
  static const ONE_HOUR_MILLISECONDS = DateOp.ONE_HOUR_MILLISECONDS;
  static const ONE_DAY_MILLISECONDS = DateOp.ONE_DAY_MILLISECONDS;

  late final DateTime dateTime;
  final DateOp dateOp = DateOp();

  DateOp2.fromDateTime(this.dateTime);

  DateOp2.fromString(
    String dateTime, {
    bool convertToLocalTime = true,
  }) {
    final dt = dateOp.parse(
      dateTime,
      convertToLocalTime: convertToLocalTime,
      defaultDate: DateTime(0),
    )!;

    this.dateTime = dt;
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  String format({required String pattern, bool? en}) {
    return dateOp.format(dateTime, pattern: pattern, en: en);
  }

  ///"yyyy-MM-dd HH:mm:ss" ========> 2012-01-23 01:23:45
  String formatForDatabase({
    required bool dateOnly,
    bool convertToUtc = true,
    bool includeZoneTime = false,
  }) {
    return dateOp.formatForDatabase(
      dateTime,
      dateOnly: dateOnly,
      convertToUtc: convertToUtc,
      includeZoneTime: includeZoneTime,
    );
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  String getWeekDayName({bool? en, bool short = false}) {
    return dateOp.getWeekDayName(dateTime.weekday, en: en, short: short);
  }

  String getMonthName({bool? en, bool short = false}) {
    return dateOp.getMonthName(dateTime.month, en: en, short: short);
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /// Saturday 22, November 2021 11:00:00 AM
  String formatForUser({
    bool? en,
    bool dateOnly = false,
    bool convertToLocalTime = true,
    bool includeSeconds = false,
    bool useShortNames = false,
    bool inTwoLines = false,
  }) {
    return dateOp.formatForUser(
      dateTime,
      en: en,
      dateOnly: dateOnly,
      convertToLocalTime: convertToLocalTime,
      includeSeconds: includeSeconds,
      useShortNames: useShortNames,
      inTwoLines: inTwoLines,
    );
  }

  //============================================================================

  String sinceNowStatement({
    bool? en,
    bool includeSeconds = false,
  }) {
    return dateOp.sinceNowStatement2(
      dateTime,
      en: en,
      includeSeconds: includeSeconds,
    );
  }

  //============================================================================

  DateOpDuration? remainToNow() {
    return dateOp.remainTo2(dateTime);
  }

  String? remainToAsStatement({
    bool? en,
    bool useShortNames = true,
    bool reportExactDaysCount = false,
    bool includeMinutes = true,
    bool includeSeconds = true,
    bool acceptNegative = false,
  }) {
    return dateOp.remainToAsStatement2(
      dateTime,
      en: en,
      useShortNames: useShortNames,
      reportExactDaysCount: reportExactDaysCount,
      includeMinutes: includeMinutes,
      includeSeconds: includeSeconds,
      acceptNegative: acceptNegative,
    );
  }
}
