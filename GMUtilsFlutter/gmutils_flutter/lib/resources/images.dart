import 'package:flutter/material.dart';

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
    /*Logs.print(() => [
          'Resources -> Images',
          'devicePixelRatio: $devicePixelRatio',
          'screen size: ${size.width},${size.height}',
          'density: $_density',
        ]);*/
  }

  String rootDir = 'assets/images/';

  //todo add image
  String get splash =>  '${rootDir}app_splash.png';

  //todo add image
  String get appLogoSplash =>  '${rootDir}app_logo_splash.png';

  //todo add image
  String get appLogoTop =>  '${rootDir}app_logo_top.png';

  //todo add image
  String get appLogoToolbar =>  '${rootDir}app_logo_toolbar.png';

  //todo add image
  String progress(int n) => '${rootDir}progress_$n.png';

  String get children => '${rootDir}children.png';

  String get iconMail =>
      '${rootDir}icon_mail@${_density < 3 ? _density : 2}x.png';

  String get iconChat =>
      '${rootDir}icon_chat@${_density < 3 ? _density : 2}x.png';

  String get parentCare => '${rootDir}parent_care.png';

  String get podiumRank => '${rootDir}podium_rank@${_density < 4 ? _density : 3}x.png';
  String get podiumWinner => '${rootDir}podium_winner.png';

  String get question => '${rootDir}question.png';

  String get questionColored => '${rootDir}question_colored.png';

  String get questionBank => '${rootDir}question_bank.png';

  String get students =>
      '${rootDir}students@${_density < 3 ? _density : 2}x.png';

  String get studentsAll =>
      '${rootDir}students_all@${_density < 4 ? _density : 3}x.png';

  String get studentsGroup =>
      '${rootDir}students_group@${_density < 4 ? _density : 3}x.png';

  String get stuff => '${rootDir}stuff@${_density < 3 ? _density : 2}x.png';

  String get trianglePrimary => '${rootDir}triangle_primary.png';

}
