package gmutils.listeners;

public interface ResultCallback2<R1, R2> {
    void invoke(R1 result1, R2 result2);
}