import 'dart:core' as core;

import 'package:flutter/foundation.dart';

class Logs {
  static void print(core.Object? Function() info) {
    if (kDebugMode) {
      final infoData = info();
      const logStart = '*****';
      if (infoData == null) {
        core.print('$logStart null');
      } else {
        final string = infoData.toString();
        var len = string.length;

        const maxLen = 990;
        var start = 0;
        var end = maxLen;
        if (end > string.length) end = string.length;

        while (len > maxLen) {
          core.print('$logStart ${string.substring(start, end)}');
          start += maxLen;
          end += maxLen;
          if (end > string.length) end = string.length;
          len -= maxLen;
        }

        if (len > 0) {
          core.print('$logStart ${string.substring(start)}');
        }

      }
    }
  }
}