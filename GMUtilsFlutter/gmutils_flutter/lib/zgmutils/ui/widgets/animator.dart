import 'package:flutter/material.dart';

class Animator extends StatefulWidget {
  final int start;
  final int end;
  final Duration duration;
  final int? repeats;
  final Widget Function(int value) child;

  const Animator({this.start = 0,
    this.end = 100,
    this.duration = const Duration(milliseconds: 500),
    this.repeats,
    required this.child,
    super.key});

  @override
  State<Animator> createState() => _AnimatorState();
}

class _AnimatorState extends State<Animator>
    with SingleTickerProviderStateMixin {
  late AnimationController controller;
  late Animation<int> animation;

  @override
  void initState() {
    super.initState();

    controller = AnimationController(
      duration: widget.duration,
      animationBehavior: ((widget.repeats ?? 0) > 0) ? AnimationBehavior
          .preserve : AnimationBehavior.normal,
      vsync: this,
    );

    animation = IntTween(
      begin: widget.start,
      end: widget.end,
    ).animate(controller);

    if ((widget.repeats ?? 0) > 0) {
      controller.repeat(
        reverse: true,
        count: widget.repeats,
      );
    } else {
      controller.forward();
    }
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: animation,
      builder: (context, child) => widget.child(animation.value),
    );
  }
}
