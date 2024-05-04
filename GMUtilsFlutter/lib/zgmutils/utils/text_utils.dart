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

  bool? isEnglishLetter(String letter) {
    if (letter.trim().isEmpty) return null;
    var letterCode = letter[0].codeUnits[0];

    if (letterCode >= 'a'.codeUnits[0] && letterCode <= 'z'.codeUnits[0]) return true;
    if (letterCode >= 'A'.codeUnits[0] && letterCode <= 'Z'.codeUnits[0]) return true;
    if (letterCode >= '0'.codeUnits[0] && letterCode <= '9'.codeUnits[0]) return true;

    return false;
  }
}
