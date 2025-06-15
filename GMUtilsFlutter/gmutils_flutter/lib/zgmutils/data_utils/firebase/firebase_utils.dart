// import 'package:http/http.dart' as http;

import '../../utils/logs.dart';

class FirebaseUtils {
  //region refineKeyName
  static String refineKeyName(String name) => name
      .trim()
      .replaceAll(' ', '_')
      .replaceAll('.', '_')
      .replaceAll('\$', '_')
      .replaceAll('#', '_')
      .replaceAll('@', '_')
      .replaceAll('[', '_')
      .replaceAll(']', '_')
      .replaceAll('/', '_');

  //endregion

  //region refinePath
  static String refinePathFragmentNames(String path) {
    var frags = path.split("/");
    path = '';
    for (var value in frags) {
      value = refineKeyName(value);
      if (value.isEmpty) throw Exception("invalid_node_name");

      if (path.isNotEmpty) path = '/';
      path += value;
    }
    return path;
  }

  //endregion

  //region refinePhoneNumber
  static String refinePhoneNumber(String number) => number
      .trim()
      .replaceAll('-', '')
      .replaceAll(' ', '')
      .replaceAll('.', '')
      .replaceAll('+', '')
      .replaceAll('(', '')
      .replaceAll('/', '')
      .replaceAll(')', '')
      .replaceAll('N', '')
      .replaceAll(',', '')
      .replaceAll('*', '')
      .replaceAll(';', '')
      .replaceAll('#', '');

  //endregion

  //region isConnectionAvailable
  /*static int _lastConnectionCheckTime = 0;
  static bool _lastConnectionCheckResult = false;

  static Future<bool> isConnectionAvailable() async {
    try {
      int now = DateTime.now().millisecondsSinceEpoch;
      int diff = now - _lastConnectionCheckTime;
      _lastConnectionCheckTime = now;
      if (diff < 10000 && _lastConnectionCheckResult) return true;

      var response = await http.get(Uri.parse('https://www.google.com'));
      _lastConnectionCheckResult = true;
      Logs.print(() => 'FirebaseUtils.isConnectionAvailable ---> response of: google.com: ${response.statusCode}');
      return true;
    } catch (e) {
      _lastConnectionCheckResult = false;
      return false;
    }
  }*/
//endregion
}
