
class ObservableValue<T> {
  ObservableValue([T? value]) : _value = value;

  //---------------------------------

  final Map<int, Observer<T>> _observers = {};

  void addObserver(int id, Observer<T> o) {
    _observers[id] = o;
    _callObserver();
  }

  void removeObserver(int id) {
    _observers.remove(id);
  }

  void _callObserver() {
    if (_observers.isNotEmpty) {
      for (var o in _observers.values) {
        o.call(_value);
      }
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

