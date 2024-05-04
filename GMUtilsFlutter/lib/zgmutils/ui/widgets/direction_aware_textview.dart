import 'package:flutter/material.dart';

import '../../utils/text_utils.dart';

class DirectionAwareTextView extends StatelessWidget {
  final bool rich;
  final String text;
  final InlineSpan? textSpan;
  final TextStyle? style;
  final TextAlign? textAlign;
  final bool? softWrap;
  final TextOverflow? overflow;
  final double? textScaleFactor;
  final int? maxLines;
  final Color? selectionColor;

  DirectionAwareTextView(this.text, {
    this.style,
    this.textAlign,
    TextDirection? textDirection,
    this.softWrap,
    this.overflow,
    this.textScaleFactor,
    this.maxLines,
    this.selectionColor,
    Key? key,
  })  : rich = false,
        textSpan = null,
        super(key: key) {
    _initState(textDirection);
  }
  
  DirectionAwareTextView.rich({
    required InlineSpan this.textSpan,
    this.style,
    this.textAlign,
    TextDirection? textDirection,
    this.softWrap,
    this.overflow,
    this.textScaleFactor,
    this.maxLines,
    this.selectionColor,
    Key? key,
  })  : rich = true,
        text = '',
        super(key: key) {
    _initState(textDirection);
  }

  TextDirection? _finalTextDirection;

  void _initState(TextDirection? textDirection) {
    _finalTextDirection = textDirection;

    if (textDirection == null) {
      int? c;
      if (textSpan != null) {
        var s = textSpan!.toStringShort().trim();
        if (s.isNotEmpty) {
          c = s.codeUnitAt(0);
        }
      } else {
        if (text.trim().isNotEmpty) {
          c = text.codeUnitAt(0);
        }
      }

      String firstLetter = '';
      if (c != null) {
        firstLetter = String.fromCharCode(c);
      }

      if (firstLetter.length == 1) {
        var e = TextUtils().isEnglishLetter(firstLetter);
        if (e != null) {
          if (e) {
            if (_finalTextDirection != TextDirection.ltr) {
              //setState(() {
              _finalTextDirection = TextDirection.ltr;
              //});
            }
          } else {
            if (_finalTextDirection != TextDirection.rtl) {
              //setState(() {
              _finalTextDirection = TextDirection.rtl;
              //});
            }
          }
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (rich) {
      return Text.rich(
        textSpan!,
        style: style,
        textAlign: textAlign,
        textDirection: _finalTextDirection,
        softWrap: softWrap,
        overflow: overflow,
        textScaleFactor: textScaleFactor,
        maxLines: maxLines,
        selectionColor: selectionColor,
      );
    } else {
      return Text(
        text,
        style: style,
        textAlign: textAlign,
        textDirection: _finalTextDirection,
        softWrap: softWrap,
        overflow: overflow,
        textScaleFactor: textScaleFactor,
        maxLines: maxLines,
        selectionColor: selectionColor,
      );
    }
  }
}
