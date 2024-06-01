import 'package:flutter/material.dart';

import '../zgmutils/utils/logs.dart';

class Images {
  late Size size;
  late double devicePixelRatio;
  late int density; //1, 2, 3

  Images(BuildContext context) {
    MediaQueryData? mediaQuery;
    try {
      mediaQuery = MediaQuery.of(context);
    } catch (e) {}

    devicePixelRatio = mediaQuery?.devicePixelRatio ?? 1;
    size = mediaQuery?.size ?? const Size(0, 0);
    density = size.width >= 2000 ? 3 : (size.width >= 1000 ? 2 : 1);
    Logs.print(() => [
          'Resources -> Images',
          'devicePixelRatio: $devicePixelRatio',
          'screen size: ${size.width},${size.height}',
          'density: $density',
        ]);
  }

  String rootDir = 'assets/images/';

  String get logo => '${rootDir}logo.png';

}
