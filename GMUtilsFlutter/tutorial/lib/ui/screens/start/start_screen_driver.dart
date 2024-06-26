import '../../../data/data_source/datasource.dart';
import '../../../data/data_source/users/users_datasource.dart';
import '../../../zgmutils/ui/utils/drivers_interfaces.dart';

abstract class StartScreenDelegate extends IScreenDriverDependantDelegate {
  void showMsg(String m);
}

abstract class StartScreenDriverAbs extends IScreenDriver {
  late StartScreenDelegate delegate;
  UsersDataSource usersDataSource;

  StartScreenDriverAbs(
    this.delegate, {
    required this.usersDataSource,
  }) : super(delegate);

  void getUser(String username) async {
    delegate.showWaitView();

    var user = await usersDataSource.getUser(username: username);

    await delegate.hideWaitView();

    if (user == null) {
      delegate.showMsg('Returned user: nothing');
    } else {
      delegate.showMsg('Returned user: ${user.name}');
    }
  }
}

class StartScreenDriver extends StartScreenDriverAbs {
  StartScreenDriver(super.delegate)
      : super(
          usersDataSource: Datasource.instance.users,
        );
}

class StartScreenDriver2 extends StartScreenDriverAbs {
  StartScreenDriver2(super.delegate, {required super.usersDataSource});
}
