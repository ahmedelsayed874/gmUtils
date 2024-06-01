import 'package:flutter/material.dart';

import 'fonts.dart';
import 'images.dart';
import 'strings.dart';
import 'themes.dart';

class Res {
  static Res? _instance; //must init on app start

  static void init(BuildContext context) {
    Fonts fonts = Fonts();

    _instance = Res._(
      Strings(context),
      Themes(context, toolbarTitleFontFamily: fonts.toolbarTitle),
      Images(context),
      fonts,
    );
  }

  static Strings get strings => _instance!._strings;

  static Themes get themes => _instance!._themes;

  static Images get images => _instance!._images;

  static Fonts get fonts => _instance!._fonts;

  //----------------------------------------------------------------------------

  Res._(this._strings, this._themes, this._images, this._fonts);

  final Strings _strings;
  final Themes _themes;
  final Images _images;
  final Fonts _fonts;
}
