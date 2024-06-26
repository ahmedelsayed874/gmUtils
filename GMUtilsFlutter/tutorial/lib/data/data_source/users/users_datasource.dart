
import '../../models/user.dart';

abstract class UsersDataSource {

  Future<User?> getUser({required String username});
}
