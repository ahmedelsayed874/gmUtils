import 'dart:async';
import 'dart:convert';

import 'package:crypto/crypto.dart';
import 'package:encrypt/encrypt.dart';

class DataSecurity {
  static const _securityPasscode = '0g2!k#p\$\$wrd';
  String securityPasscode;
  late String _secretKey; //must be 32 length
  late String _vector; //must be 16 length

  DataSecurity([this.securityPasscode = _securityPasscode]) {
    var bytes = utf8.encode(securityPasscode);
    var digest = sha1.convert(bytes).toString();

    if (digest.length > 32) {
      _secretKey = digest.substring(0, 32);
    } else {
      _secretKey = digest;
      var rem = 32 - digest.length;
      for (int i = 0; i < rem; i++) {
        _secretKey += '.';
      }
    }

    if (digest.length > 16) {
      _vector = digest.substring(digest.length - 16);
    } else {
      _vector = digest;
      var rem = 16 - digest.length;
      for (int i = 0; i < rem; i++) {
        _vector += '.';
      }
    }

  }

  Future<String> encrypt(String plainText) async {
    final key = Key.fromUtf8(_secretKey);
    final iv = IV.fromUtf8(_vector);

    final encrypter = Encrypter(AES(key));
    final encrypted = encrypter.encrypt(plainText, iv: iv);
    var encText = encrypted.base64;

    return encText;
  }

  Future<String> decrypt(String encryptedText) async {
    final key = Key.fromUtf8(_secretKey);
    final iv = IV.fromUtf8(_vector);

    final encrypter = Encrypter(AES(key));
    var encrypted = Encrypted.fromBase64(encryptedText);
    final decrypted = encrypter.decrypt(encrypted, iv: iv);

    return decrypted;
  }

}
