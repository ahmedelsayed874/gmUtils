
class AvatarUtils {
  String avatarText(String name) {
    var chars = name
        .split(' ')
        .map((e) => e.isNotEmpty ? e.substring(0, 1).toUpperCase() : '');

    var str = '';

    for (var c in chars) {
      if (str.isNotEmpty) str += ' ';
      str += c;
      if (str.length == 5) break;
    }

    return str;
  }
}