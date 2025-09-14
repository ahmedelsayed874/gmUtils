import 'package:flutter/material.dart';

class TimeredWidget extends StatefulWidget {
  final int updateIntervalMs;
  final Widget Function(BuildContext, TimerWidgetStatus) child;

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

  int _loop = 0;
  int _elapsedTimeMs = 0;

  @override
  void dispose() {
    timerStopped = true;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!timerStarted) startTimer();

    var status = TimerWidgetStatus(loop: _loop, elapsedTimeMs: _elapsedTimeMs);
    var child = widget.child(context, status);
    timerStopped = status.stopTimer;
    return child;

  }

  void startTimer() {
    if (timerStopped) return;

    _loop++;
    timerStarted = true;

    Future.delayed(Duration(milliseconds: widget.updateIntervalMs), () {
      _elapsedTimeMs += widget.updateIntervalMs;

      if (!timerStopped) {
        timerStarted = false;
        setState(() {});
      }
    });
  }
}

class TimerWidgetStatus {
  final int loop;
  final int elapsedTimeMs;

  TimerWidgetStatus({required this.loop, required this.elapsedTimeMs,});

  bool stopTimer = false;

}