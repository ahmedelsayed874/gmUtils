import 'package:gmutils_flutter/zgmutils/utils/mappable.dart';

class UserAccountIdentifier {
  int accountId;
  String username;

  UserAccountIdentifier({
    required this.accountId,
    required this.username,
  });

  @override
  String toString() {
    return 'UserAccountIdentifier{accountId: $accountId, username: $username}';
  }
}

class UserAccountIdentifierMapper extends Mappable<UserAccountIdentifier> {
  @override
  UserAccountIdentifier fromMap(Map<String, dynamic> values) {
    return UserAccountIdentifier(
      accountId: values['accountId'],
      username: values['userName'] ?? values['username'],
    );
  }

  @override
  Map<String, dynamic> toMap(UserAccountIdentifier object) {
    return {
      'accountId': object.accountId,
      'username': object.username,
    };
  }
}
