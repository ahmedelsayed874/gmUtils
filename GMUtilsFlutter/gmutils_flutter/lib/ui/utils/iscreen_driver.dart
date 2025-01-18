import 'package:gmutils_flutter/data/data_source/users/users_datasource.dart';
import 'package:gmutils_flutter/zgmutils/ui/utils/drivers_interfaces.dart'
    as sd;

abstract class IScreenDriverDependantDelegate
    extends sd.IScreenDriverDependantDelegate {}

class IScreenDriver extends sd.IScreenDriver {
  final UsersDataSource usersDataSource;

  IScreenDriver(
    IScreenDriverDependantDelegate super.baseDelegate, {
    required this.usersDataSource,
  });
}
