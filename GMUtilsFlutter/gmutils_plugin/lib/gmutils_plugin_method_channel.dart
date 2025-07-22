import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'gmutils_plugin_platform_interface.dart';

/// An implementation of [GmutilsPluginPlatform] that uses method channels.
class MethodChannelGmutilsPlugin extends GmutilsPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('gmutils_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
