import 'package:flutter/material.dart';

import '../../../zgmutils/ui/utils/base_stateful_state.dart';
import 'start_screen_driver.dart';

class StartScreen extends StatefulWidget {
  const StartScreen({super.key});

  @override
  State<StartScreen> createState() => _StartScreenState();
}

class _StartScreenState extends BaseState<StartScreen>
    implements StartScreenDelegate {
  late StartScreenDriverAbs screenDriver;

  @override
  void initState() {
    super.initState();
    screenDriver = StartScreenDriver(this);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
            onPressed: () => screenDriver.getUser('username'), child: Text('Get User'),),
      ],
    );
  }

  @override
  void showMsg(String m) {
    showMessage('Message', message: m);
  }
}
