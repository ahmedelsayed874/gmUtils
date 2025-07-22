import 'package:flutter_test/flutter_test.dart';
import 'package:gmutils_plugin/gmutils_plugin.dart';
import 'package:gmutils_plugin/gmutils_plugin_platform_interface.dart';
import 'package:gmutils_plugin/gmutils_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGmutilsPluginPlatform
    with MockPlatformInterfaceMixin
    implements GmutilsPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final GmutilsPluginPlatform initialPlatform = GmutilsPluginPlatform.instance;

  test('$MethodChannelGmutilsPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGmutilsPlugin>());
  });

  test('getPlatformVersion', () async {
    GmutilsPlugin gmutilsPlugin = GmutilsPlugin();
    MockGmutilsPluginPlatform fakePlatform = MockGmutilsPluginPlatform();
    GmutilsPluginPlatform.instance = fakePlatform;

    expect(await gmutilsPlugin.getPlatformVersion(), '42');
  });
}
