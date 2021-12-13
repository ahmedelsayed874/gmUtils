package gmutils.listeners;

public interface ResultCallback3<R1, R2, R3> {
    void invoke(R1 result1, R2 result2, R3 result3);
}