package gmutils.listeners;

public interface ResultCallback4<R1, R2, R3, R4> {
    void invoke(R1 result1, R2 result2, R3 result3, R4 result4);
}