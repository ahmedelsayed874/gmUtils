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

  String get splash => '${rootDir}splash.jpeg';

  String get logoWhite =>
      '${rootDir}logo_white@${_density < 4 ? _density : 3}x.png';

  String get logoColored =>
      '${rootDir}logo_colored@${_density < 4 ? _density : 3}x.png';

  String get logoColoredSmall => '${rootDir}logo_colored_small.png';

  String get logoColoredSmallWithoutText =>
      '${rootDir}logo_colored_small_without_txt.png';

  String get iconCommunications =>
      '${rootDir}icon_communication@${_density < 3 ? _density : 2}x.png';

  String get iconDigitalLearning =>
      '${rootDir}icon_digital_learning@${_density < 3 ? _density : 2}x.png';

  String get iconExam =>
      '${rootDir}icon_exam@${_density < 3 ? _density : 2}x.png';

  String get iconMail =>
      '${rootDir}icon_mail@${_density < 3 ? _density : 2}x.png';

  String get iconChat =>
      '${rootDir}icon_chat@${_density < 3 ? _density : 2}x.png';

  String get classroom =>
      '${rootDir}classroom@${_density < 3 ? _density : 2}x.png';

  String get studentsAll =>
      '${rootDir}students_all@${_density < 4 ? _density : 3}x.png';

  String get studentsGroup =>
      '${rootDir}students_group@${_density < 4 ? _density : 3}x.png';

  String get stuff => '${rootDir}stuff@${_density < 3 ? _density : 2}x.png';

  String get squarePrimary => '${rootDir}square_primary.png';

  String get trianglePrimary => '${rootDir}triangle_primary.png';

  String get lessons => '${rootDir}lessons@${_density < 3 ? _density : 2}x.png';

  String get students =>
      '${rootDir}students@${_density < 3 ? _density : 2}x.png';

  String get questionBank => '${rootDir}question_bank.png';

  String get question => '${rootDir}question.png';

  String get questionColored => '${rootDir}question_colored.png';

  String get podiumRank => '${rootDir}podium_rank@${_density < 4 ? _density : 3}x.png';
  String get podiumWinner => '${rootDir}podium_winner.png';

  String get parentCare => '${rootDir}parent_care.png';

  String get virtualRoomStudents => '${rootDir}virtual_room_students@${_density < 4 ? _density : 3}x.png';
  String get virtualRoomStuff => '${rootDir}virtual_room_stuff@${_density < 4 ? _density : 3}x.png';

  String get vrLogoGoogleMeet => '${rootDir}vr_logo_google_meet.png';
  String get vrLogoTeams => '${rootDir}vr_logo_teams.png';
  String get vrLogoZoom => '${rootDir}vr_logo_zoom.png';

  String get children => '${rootDir}children.png';

  String get toolbar => '${rootDir}toolbar.png';

  String progress(int n) => '${rootDir}progress_bls$n.png';

}
