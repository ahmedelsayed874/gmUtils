
import '../../zgmutils/utils/mappable.dart';

class User {
  int id;
  String name;

  User({required this.id, required this.name});
}


class UserMapper extends Mappable<User> {
  @override
  User fromMap(Map<String, dynamic> values) {
    return User(id: values['id'], name: values['name']);
  }

  @override
  Map<String, dynamic> toMap(User object) {
    throw UnimplementedError();
  }

}