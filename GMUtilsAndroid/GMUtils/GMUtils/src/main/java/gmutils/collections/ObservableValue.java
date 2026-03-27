package gmutils.collections;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gmutils.collections.values.Value;
import gmutils.listeners.ResultCallback2;

public class ObservableValue<T> {
    private Value<T> value;
    private final List<ResultCallback2<T, ObservableValueArgs>> observers = new ArrayList<>();

    public void setValue(T value) {
        this.value = new Value<>(value);

        List<Integer> toDeleteObservers = new ArrayList<>();

        for (int i = 0; i < observers.size(); i++) {
            ResultCallback2<T, ObservableValueArgs> observer = observers.get(i);

            if (observer != null) {
                ObservableValueArgs args = new ObservableValueArgs();
                observer.invoke(value, args);
                if (!args.keepAlive) toDeleteObservers.add(i);
            }
        }

        if (!toDeleteObservers.isEmpty()) {
            int x = 0;
            do {
                int idx = toDeleteObservers.getFirst();
                toDeleteObservers.removeFirst();

                observers.remove(idx - x);
                x++;
            } while (!toDeleteObservers.isEmpty());
        }
    }

    public void addObserver(@NotNull ResultCallback2<T, ObservableValueArgs> callback) {
        observers.add(callback);
        if (value != null) callback.invoke(value.value, null);
    }
}
