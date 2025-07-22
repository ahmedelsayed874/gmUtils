import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'gmutils_plugin_method_channel.dart';

abstract class GmutilsPluginPlatform extends PlatformInterface {
  /// Constructs a GmutilsPluginPlatform.
  GmutilsPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static GmutilsPluginPlatform _instance = MethodChannelGmutilsPlugin();

  /// The default instance of [GmutilsPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelGmutilsPlugin].
  static GmutilsPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GmutilsPluginPlatform] when
  /// they register themselves.
  static set instance(GmutilsPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
