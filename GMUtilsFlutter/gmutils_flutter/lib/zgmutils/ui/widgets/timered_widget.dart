import 'package:flutter/material.dart';

class TimeredWidget extends StatefulWidget {
  final int updateIntervalMs;
  final Widget Function(BuildContext) child;

  const TimeredWidget({
    required this.updateIntervalMs,
    required this.child,
    super.key,
  });

  @override
  State<TimeredWidget> createState() => _TimeredWidgetState();
}

class _TimeredWidgetState extends State<TimeredWidget> {
  bool timerStarted = false;
  bool timerStopped = false;

  @override
  void dispose() {
    timerStopped = true;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!timerStarted) startTimer();

    return widget.child(context);
  }

  void startTimer() {
    if (timerStopped) return;

    timerStarted = true;

    Future.delayed(Duration(milliseconds: widget.updateIntervalMs), () {
      if (!timerStopped) {
        timerStarted = false;
        setState(() {});
      }
    });
  }
}


