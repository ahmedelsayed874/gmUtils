import 'package:flutter/material.dart';

import 'audio.dart';
import 'images.dart';
import 'strings.dart';
import 'themes.dart';

class Res {
  static Res? _instance; //must init on app start

  static void init(BuildContext context, {bool? useLightTheme}) {
    _instance = Res._(
      Strings(context),
      Themes(context, light: useLightTheme),
      Images(context),
      Audio(),
    );
  }

  static Strings get strings => _instance!._strings;

  static Themes get themes => _instance!._themes;

  static Images get images => _instance!._images;

  static Audio get audio => _instance!._audio;

  //----------------------------------------------------------------------------

  Res._(this._strings, this._themes, this._images, this._audio,);

  final Strings _strings;
  final Themes _themes;
  final Images _images;
  final Audio _audio;
}
