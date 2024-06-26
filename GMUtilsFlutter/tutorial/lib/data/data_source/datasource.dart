import '../../main.dart';
import 'users/users_datasource.dart';
import 'users/users_datasource_mockup.dart';
import 'users/users_datasource_production.dart';

class Datasource {
  static Datasource? _instance;

  static Datasource get instance => _instance ??= Datasource();

  bool get production => useProductionData;

  UsersDataSource get users =>
      production ? UsersDataSourceProduction() : UsersDataSourceMockup();
}
