import 'package:flutter/material.dart';

import '../zgmutils/utils/logs.dart';

class Images {
  late int _density; //1, 2, 3

  Images(BuildContext context) {
    MediaQueryData? mediaQuery;
    try {
      mediaQuery = MediaQuery.of(context);
    } catch (e) {}

    var devicePixelRatio = mediaQuery?.devicePixelRatio ?? 1;
    var size = mediaQuery?.size ?? const Size(0, 0);
    _density = size.width >= 2000 ? 3 : (size.width >= 1000 ? 2 : 1);
    Logs.print(() => [
          'Resources -> Images',
          'devicePixelRatio: $devicePixelRatio',
          'screen size: ${size.width},${size.height}',
          'density: $_density',
        ]);
  }

  String rootDir = 'assets/images/';

  String get logoWhite => '${rootDir}logo_white@${_density}x.png';
  String get logoColored => '${rootDir}logo_colored@${_density}x.png';
  String get logoColoredSmall => '${rootDir}logo_colored_small.png';
  String get logoColoredSmallWithoutText => '${rootDir}logo_colored_small_without_txt.png';

}
