import 'package:flutter/material.dart';

import '../../utils/observable_value.dart';

class ObservableWidget extends StatefulWidget {
  final Widget Function(BuildContext, bool, Object?) child;
  final ObservableValue observableValue;

  const ObservableWidget({
    required this.child,
    required this.observableValue,
    Key? key,
  }) : super(key: key);

  @override
  State<ObservableWidget> createState() => _ObservableWidgetState();
}

class _ObservableWidgetState extends State<ObservableWidget> {
  bool _isValueSet = false;

  @override
  void initState() {
    super.initState();
    widget.observableValue.observer = (ov) {
      try {
        _isValueSet = true;
        setState(() {});
      } catch (e) {}
    };
  }

  @override
  void dispose() {
    widget.observableValue.observer = null;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return widget.child(context, _isValueSet, widget.observableValue.value);
  }
}
