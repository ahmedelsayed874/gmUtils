import 'package:flutter/material.dart';

import '../gm_main.dart';
import 'collections/string_set.dart';

class RichSentence {
  final List<RichString> strings;
  final TextStyle? style;

  RichSentence({required this.strings, this.style});

  RichString? get first => strings.firstOrNull;

  List<RichString> subtext(int start, [int? end]) {
    if (start >= 0 && start < strings.length) {
      if (end != null) {
        if (end > strings.length) {
          end = strings.length;
        }
        if (end <= start) {
          end = start + 1;
        }
      }

      return strings.sublist(start, end);
    }

    return [];
  }

  int get length => strings.length;

  TextSpan asTextSpan({String? langCode, TextStyle? defaultStyle}) => TextSpan(
        text: first?.string.of(langCode ?? App.langCode),
        style: style ?? defaultStyle,
        children: subtext(1)
            .map((t) => TextSpan(
                  text: t.string.of(langCode ?? App.langCode),
                  style: t.style ?? defaultStyle,
                ))
            .toList(),
      );

  String asString({String? langCode}) => strings
      .map(
        (s) => s.string.of(langCode ?? App.langCode),
      )
      .join("");

  @override
  String toString() {
    return 'RichSentence{strings: $strings, style: $style}';
  }
}

class RichString {
  final StringSet string;
  final TextStyle? style;

  RichString({required this.string, this.style});

  @override
  String toString() {
    return 'RichString{string: $string, style: $style}';
  }
}
