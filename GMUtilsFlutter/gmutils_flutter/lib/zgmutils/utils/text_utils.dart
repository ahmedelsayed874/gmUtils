// import 'package:html/parser.dart';
// import 'package:intl/intl.dart';

import 'package:flutter/material.dart';

class TextUtils {
  String removeExtraSpaces(String text) {
    text = text.trim();
    while (text.contains('  ')) {
      text = text.replaceAll('  ', ' ');
    }
    return text;
  }

  String numberToString(int number, int length) {
    String text = '$number';

    while (text.length < length) {
      text = '0$text';
    }

    return text;
  }

  //----------------------------------------------------------------------------

  bool? isArabicLetter(String letter) {
    if (letter.trim().isEmpty) return null;
    var letterCode = letter[0].codeUnits[0];

    if ('اآإأء'.contains(letter) || 'يىئ'.contains(letter)) return true;
    if (letterCode >= '٠'.codeUnits[0] && letterCode <= '٩'.codeUnits[0])
      return true;
    if (letterCode >= 'ء'.codeUnits[0] && letterCode <= 'ي'.codeUnits[0])
      return true;

    return false;
  }

  bool? isArabicSymbol(String letter) {
    if (letter.trim().isEmpty) return null;

    if ('!@#\$٪^&*)(ـ-+=ًٌٍَُِّْ][}{»«:\"\؛|\\ـ>،.</؟'.contains(letter))
      return true;
    if ('°ٱ∞دذطظ‘’“”ڤەچ…گںٹٰٓپیےڈڑژ,گںٹٰٓپیےڈڑژ'.contains(letter)) return true;

    return false;
  }

  bool? isEnglishLetter(String letter) {
    if (letter.trim().isEmpty) return null;
    var letterCode = letter[0].codeUnits[0];

    if (letterCode >= 'a'.codeUnits[0] && letterCode <= 'z'.codeUnits[0])
      return true;
    if (letterCode >= 'A'.codeUnits[0] && letterCode <= 'Z'.codeUnits[0])
      return true;
    if (letterCode >= '0'.codeUnits[0] && letterCode <= '9'.codeUnits[0])
      return true;

    return false;
  }

  bool? isEnglishSymbol(String letter) {
    if (letter.trim().isEmpty) return null;

    if ('§±!@#\$%^&*()_-+=[{]}\\|\'\";:`~,<.>/?'.contains(letter)) return true;
    if ('§¡™£¢∞§¶•ªº–≠œ∑´®†¥¨ˆøπ“‘åß∂ƒ©˙∆˚¬…æ«`Ω≈ç√∫˜µ≤≥µ'.contains(letter))
      return true;

    return false;
  }

  //----------------------------------------------------------------------------

  bool? isStartWithArabic(String text) {
    // Logs.print(() => [
    //       'TextUtils.isStartWithArabic',
    //       '|$text|',
    //       'length: ${text.length}',
    //     ]);

    if (text.isEmpty) return false;
    var i = 0;
    var b = false;
    while (i < text.length) {
      b = isArabicSymbol(text.substring(i, i + 1)) ?? true;
      if (!b) b = isEnglishSymbol(text.substring(i, i + 1)) ?? true;
      if (!b) break;
      i++;
    }

    // Logs.print(() => [
    //       'TextUtils.isStartWithArabic',
    //       '|$text|',
    //       'length: ${text.length}',
    //       'start index: $i',
    //     ]);

    if (i >= text.length) return false;

    String firstLetter = text.substring(i, i + 1);
    // Logs.print(() => [
    //       'TextUtils.isStartWithArabic',
    //       '|$text|',
    //       'length: ${text.length}',
    //       'firstLetter: $firstLetter',
    //     ]);

    return isArabicLetter(firstLetter) ?? false;
  }

  // String removeHtmlTags(String content) {
  //   final document = parse(content);
  //   String parsedString =
  //       parse(document.body?.text).documentElement?.text ?? '';
  //
  //   if (parsedString.isEmpty) {
  //     parsedString = Bidi.stripHtmlIfNeeded(content);
  //   }
  //
  //   return parsedString;
  // }

  //----------------------------------------------------------------------------

  String convertEnglishNumberToArabicNumber(String number) {
    return number
        .replaceAll('0', '٠')
        .replaceAll('1', '١')
        .replaceAll('2', '٢')
        .replaceAll('3', '٣')
        .replaceAll('4', '٤')
        .replaceAll('5', '٥')
        .replaceAll('6', '٦')
        .replaceAll('7', '٧')
        .replaceAll('8', '٨')
        .replaceAll('9', '٩');
  }

  String convertArabicNumbersToEnglishNumber(String number) {
    return number
        .replaceAll('٠', '0')
        .replaceAll('١', '1')
        .replaceAll('٢', '2')
        .replaceAll('٣', '3')
        .replaceAll('٤', '4')
        .replaceAll('٥', '5')
        .replaceAll('٦', '6')
        .replaceAll('٧', '7')
        .replaceAll('٨', '8')
        .replaceAll('٩', '9');
  }

  //----------------------------------------------------------------------------

  bool hasNonEnglishLetter(String sentence) {
    for (var c in sentence.characters) {
      bool notValid = true;

      if (c.codeUnits[0] >= 'a'.codeUnits[0] && c.codeUnits[0] <= 'z'.codeUnits[0]) {
        notValid = false;
      }
      else if (c.codeUnits[0] >= 'A'.codeUnits[0] && c.codeUnits[0] <= 'Z'.codeUnits[0]) {
        notValid = false;
      }

      if (notValid) return true;
    }

    return false;
  }

  bool hasNonEnglishNumbers(String sentence) {
    for (var c in sentence.characters) {
      bool notValid = true;

      if (c.codeUnits[0] >= '0'.codeUnits[0] && c.codeUnits[0] <= '9'.codeUnits[0]) {
        notValid = false;
      }

      if (notValid) return true;
    }

    return false;
  }

  //----------------------------------------------------------------------------

  String trimEnd(String text, {int endIndex = 100, String suffix = "..."}) {
    if (endIndex < 1) return '';

    if (text.length > endIndex) {
      return text.substring(0, endIndex) + suffix;
    }
    //
    else {
      return text;
    }
  }

  //----------------------------------------------------------------------------

  String convertVariableNameToFriendlyText(String key) {
    //abcDef_JH
    key = key.replaceAll('_', ' ');
    //abcDef JH

    int space = ' '.codeUnits[0]; //32
    int zero = '0'.codeUnits[0]; //48
    int nine = '9'.codeUnits[0]; //57
    int A = 'A'.codeUnits[0]; //65
    int Z = 'Z'.codeUnits[0]; //90
    int a = 'a'.codeUnits[0]; //97
    int z = 'z'.codeUnits[0]; //122

    String txt = '';
    int prevChat = 0;

    for (var c in key.codeUnits) {
      if (c >= A && c <= Z) {
        if (prevChat != 0) txt += ' ';
        txt += String.fromCharCode(c);
      } else if (c >= a && c <= z) {
        var c2 = c;
        if (!(prevChat >= a && prevChat <= z)) {
          //if (prevChat != 0) txt += ' ';
          //c2 = c - 32;
          if (prevChat == 0 || prevChat == space) c2 = c - 32;
        }
        txt += String.fromCharCode(c2);
      } else if (c >= zero && c <= nine) {
        if (!(prevChat >= zero && prevChat <= nine)) txt += ' ';
        txt += String.fromCharCode(c);
      } else {
        if (prevChat != space) txt += ' ';
        txt += String.fromCharCode(c);
      }

      prevChat = c;
    }

    return txt;
  }
}
