
class StringSet {
  Map<String, String> _strings = {};

  StringSet(String en, String ar) {
    _strings['en'] = en;
    _strings['ar'] = ar;
  }

  StringSet.multiple(Map<String, String> strings) {
    _strings = strings;
  }

  String get en => _strings['en'] ?? '';
  String get ar => _strings['ar'] ?? '';

  String get(bool en) => en ? this.en : ar;

  @override
  String toString() {
    return 'StringSet{strings: $_strings}';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is StringSet &&
          runtimeType == other.runtimeType &&
          _strings == other._strings;

  @override
  int get hashCode => _strings.hashCode;
}