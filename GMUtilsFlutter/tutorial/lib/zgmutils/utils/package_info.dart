
import 'package:package_info_plus/package_info_plus.dart' as flutterPackageInfo;

/// package_info_plus: ^3.0.2
/// Be sure to add this line if `PackageInfo.fromPlatform()` is called before runApp()
/// WidgetsFlutterBinding.ensureInitialized();

class PackageInfo {

  Future<flutterPackageInfo.PackageInfo> get packageInfo async => await flutterPackageInfo.PackageInfo.fromPlatform();

  Future<String> get appName async => (await packageInfo).appName;

  Future<String> get packageName async => (await packageInfo).packageName;

  Future<String> get version async => (await packageInfo).version;

  Future<String> get buildNumber async => (await packageInfo).buildNumber;

}
