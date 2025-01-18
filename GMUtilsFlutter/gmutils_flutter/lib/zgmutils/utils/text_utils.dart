import 'package:html/parser.dart';
import 'package:intl/intl.dart';

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

  String removeHtmlTags(String content) {
    final document = parse(content);
    String parsedString =
        parse(document.body?.text).documentElement?.text ?? '';

    if (parsedString.isEmpty) {
      parsedString = Bidi.stripHtmlIfNeeded(content);
    }

    return parsedString;
  }

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
}
