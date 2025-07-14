import 'package:flutter/material.dart';

class InvalidatorWidgetWrapper extends StatefulWidget {
  final Widget Function(
    BuildContext,
    StatefulWrapperController,
    dynamic args,
  ) child;

  const InvalidatorWidgetWrapper({
    required this.child,
    super.key,
  });

  @override
  State<InvalidatorWidgetWrapper> createState() => _StatefulWrapperState();
}

class _StatefulWrapperState extends State<InvalidatorWidgetWrapper> {
  late StatefulWrapperController controller;

  dynamic _argsCache;

  @override
  void initState() {
    super.initState();

    controller = StatefulWrapperController._init(
      invalidate: (args) => setState(() {
        _argsCache = args;
      }),
    );
  }

  @override
  Widget build(BuildContext context) {
    final a = _argsCache;
    final w = widget.child(context, controller, a);
    _argsCache = null;
    return w;
  }
}

class StatefulWrapperController {
  final void Function(dynamic args) invalidate;

  StatefulWrapperController._init({required this.invalidate});
}
