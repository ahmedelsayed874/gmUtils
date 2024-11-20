import 'package:flutter/material.dart';

import '../../utils/observable_value.dart';

class ObservableWidget<T> extends StatefulWidget {
  final ObservableValue<T> observableValue;
  final Widget Function(BuildContext, bool, T?) child;

  const ObservableWidget({
    required this.observableValue,
    required this.child,
    Key? key,
  }) : super(key: key);

  @override
  State<ObservableWidget<T>> createState() => _ObservableWidgetState<T>();
}

class _ObservableWidgetState<T> extends State<ObservableWidget<T>> {
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
    return widget.child(
      context,
      _isValueSet,
      widget.observableValue.value,
    );
  }
}
