package gmutils.collections;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gmutils.collections.values.Value;
import gmutils.listeners.ResultCallback2;

public class ObservableValue<T> {
    private Value<T> value;
    private final List<ResultCallback2<T, Args>> observers = new ArrayList<>();

    public void setValue(T value) {
        this.value = new Value<>(value);

        List<Integer> toDeleteObservers = new ArrayList<>();

        for (int i = 0; i < observers.size(); i++) {
            ResultCallback2<T, Args> observer = observers.get(i);

            if (observer != null) {
                Args args = new Args();
                observer.invoke(value, args);
                if (!args.keepAlive) toDeleteObservers.add(i);
            }
        }

        if (!toDeleteObservers.isEmpty()) {
            int x = 0;
            do {
                int idx = toDeleteObservers.get(0);
                toDeleteObservers.remove(0);

                observers.remove(idx - x);
                x++;
            } while (!toDeleteObservers.isEmpty());
        }
    }

    public void addObserver(@NotNull ResultCallback2<T, Args> callback) {
        observers.add(callback);
        if (value != null) callback.invoke(value.value, null);
    }

    public static class Args {
        public boolean keepAlive = false;
    }
}
