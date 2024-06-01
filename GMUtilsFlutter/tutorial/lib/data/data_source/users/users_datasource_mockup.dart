
import 'package:tutorial/data/models/user.dart';

import 'users_datasource.dart';

class UsersDataSourceMockup extends UsersDataSource {
  @override
  Future<User?> getUser({required String username}) async {
    //return User(id: 1, name: 'Hagar');
    return Future.delayed(const Duration(seconds: 2), () => User(id: 1, name: 'Hagar'));
  }


}
