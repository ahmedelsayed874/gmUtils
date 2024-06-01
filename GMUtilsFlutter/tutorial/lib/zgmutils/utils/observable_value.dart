


class ObservableValue<T> {
  Observer<T>? _observer;
  set observer(Observer<T>? o) {
    _observer = o;
    _callObserver();
  }

  void _callObserver() {
    if (_observer != null) {
      _observer?.call(_value);
    }
  }

  //---------------------------------

  T? _value;
  T? get value => _value;

  set value(T? v) {
    _value = v;
    _callObserver();
  }
}

typedef Observer<T> = void Function(T?);

