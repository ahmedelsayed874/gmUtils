import 'package:flutter/material.dart';
import 'package:tutorial/data/data_source/datasource.dart';
import 'package:tutorial/data/data_source/users/users_datasource.dart';
import 'package:tutorial/zgmutils/ui/utils/base_stateful_state.dart';
import 'package:tutorial/zgmutils/ui/utils/drivers_interfaces.dart';

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
delegete.showWaitDialog(); //todo i just added this for more clarification

    var user = await usersDataSource.getUser(username: username);

await delegete.hideWaitDialog(); //todo i just added this for more clarification

    if (user == null) {
      delegate.showMsg('Returned user: nothing');
    }
    else {
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
