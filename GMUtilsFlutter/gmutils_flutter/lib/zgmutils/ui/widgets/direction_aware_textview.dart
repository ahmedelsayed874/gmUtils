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
  final bool selectable;

  DirectionAwareTextView(
    this.text, {
    this.style,
    this.textAlign,
    TextDirection? textDirection,
    this.softWrap,
    this.overflow,
    this.textScaleFactor,
    this.maxLines,
    this.selectionColor,
    this.selectable = false,
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
    this.selectable = false,
    Key? key,
  })  : rich = true,
        text = '',
        super(key: key) {
    _initState(textDirection);
    if (selectable) {
      if (textSpan! is TextSpan)
        throw 'selectable rich text must depend on TextSpan';
    }
  }

  TextDirection? _finalTextDirection;

  void _initState(TextDirection? textDirection) {
    // Logs.print(() => [
    //       "DirectionAwareTextView._initState()",
    //       "textDirection: $textDirection",
    //     ]);

    if (textDirection != null) {
      _finalTextDirection = textDirection;
      return;
    }

    String? startText;

    if (textSpan != null) {
      var i = 0;
      var codes = <int>[];
      int? c;
      while (i < 20) {
        c = textSpan!.codeUnitAt(i);
        if (c == null) break;
        codes.add(c);
        i++;
      }

      if (codes.isNotEmpty) {
        startText = String.fromCharCodes(codes);
      }
    }
    //
    else {
      if (text.length > 20) {
        startText = text.substring(0, 20);
      } else {
        startText = text;
      }
    }

    if (TextUtils().isStartWithArabic(startText ?? '') == true) {
      // Logs.print(() => [
      //       "DirectionAwareTextView._initState()",
      //       "from textSpan? ${textSpan != null}",
      //       "startText: |$startText|",
      //       'start with ar? true',
      //     ]);

      _finalTextDirection = TextDirection.rtl;
    }
    //
    else {
      // Logs.print(() => [
      //       "DirectionAwareTextView._initState()",
      //       "from textSpan? ${textSpan != null}",
      //       "startText: |$startText|",
      //       'start with ar? false',
      //     ]);

      _finalTextDirection = TextDirection.ltr;
    }
  }

  @override
  Widget build(BuildContext context) {
    // Logs.print(() => [
    //   "DirectionAwareTextView.build()",
    //   'rich: $rich, textAlign: $textAlign, '
    //       'finalTextDirection: $_finalTextDirection, '
    // ]);

    if (selectable) {
      if (rich) {
        return SelectableText.rich(
          textSpan as TextSpan,
          style: style,
          textAlign: textAlign,
          textDirection: _finalTextDirection,
          //softWrap: softWrap,
          //overflow: overflow,
          textScaler: textScaleFactor == null
              ? TextScaler.noScaling
              : TextScaler.linear(textScaleFactor!),
          maxLines: maxLines,
          //selectionColor: selectionColor,
        );
      } else {
        return SelectableText(
          text,
          style: style,
          textAlign: textAlign,
          textDirection: _finalTextDirection,
          //softWrap: softWrap,
          //overflow: overflow,
          textScaler: textScaleFactor == null
              ? TextScaler.noScaling
              : TextScaler.linear(textScaleFactor!),
          maxLines: maxLines,
          //selectionColor: selectionColor,
        );
      }
    } else {
      if (rich) {
        return Text.rich(
          textSpan!,
          style: style,
          textAlign: textAlign,
          textDirection: _finalTextDirection,
          softWrap: softWrap,
          overflow: overflow,
          textScaler: textScaleFactor == null
              ? TextScaler.noScaling
              : TextScaler.linear(textScaleFactor!),
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
          textScaler: textScaleFactor == null
              ? TextScaler.noScaling
              : TextScaler.linear(textScaleFactor!),
          maxLines: maxLines,
          selectionColor: selectionColor,
        );
      }
    }
  }
}
