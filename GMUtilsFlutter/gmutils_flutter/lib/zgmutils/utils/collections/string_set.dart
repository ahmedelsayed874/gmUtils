import '../../gm_main.dart';

class StringSet {
  final Map<String, String> _strings = {};

  StringSet(String en, [String? ar, Map<String, String>? more]) {
    _strings['en'] = en;
    if (ar != null) _strings['ar'] = ar;
    if (more != null && more.isNotEmpty) {
      _strings.addAll(more);
    }
  }

  StringSet.multiple(Map<String, String> strings) {
    _strings.addAll(strings);
  }

  /// Quick getters
  String get en => _strings['en'] ?? '';

  String get ar => _strings['ar'] ?? en;

  String get fr => _strings['fr'] ?? en;

  /// Main getter
  String of(String langCode) {
    final s = _strings[langCode.toLowerCase()];
    return s ?? en;
  }

  String get v => of(App.langCode);

  @override
  String toString() => _strings.toString();

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is StringSet &&
          runtimeType == other.runtimeType &&
          _strings == other._strings;

  @override
  int get hashCode => _strings.hashCode;
}
