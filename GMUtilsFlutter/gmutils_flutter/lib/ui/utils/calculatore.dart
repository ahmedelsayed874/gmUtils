class Calculator {
  static Calculator? _calculator;
  static Calculator get instance => _calculator ??= Calculator();

  String percent(double p, {String separate = ' '}) {
    //0.4234234
    var per100 = p * 100; //42.34234
    var pi = per100.toInt(); //42
    var d = ((per100 - pi) * 100).toInt() / 100; //0.34

    if (d > 0) {
      return '${pi + d}$separate%';
    } else {
      return '$pi$separate%';
    }
  }

  String valueFormat(double? v) {
    if (v == null) return '--.--';

    int valueI = v.toInt();
    double valueD = v - valueI;
    return valueD > 0 ? '${valueI + valueD}' : '$valueI';
  }
}