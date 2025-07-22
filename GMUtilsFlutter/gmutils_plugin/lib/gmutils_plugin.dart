
import 'gmutils_plugin_platform_interface.dart';

class GmutilsPlugin {
  Future<String?> getPlatformVersion() {
    return GmutilsPluginPlatform.instance.getPlatformVersion();
  }
}
