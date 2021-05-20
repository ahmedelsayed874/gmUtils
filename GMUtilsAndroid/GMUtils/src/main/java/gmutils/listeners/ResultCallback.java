package gmutils.listeners;

public interface ResultCallback<R> {
    void invoke(R result);
}