package gmutils.collections;

public class DataGroup {
    public static class One<T> {
        public final T value;

        public One(T value) {
            this.value = value;
        }
    }

    public static class Two<T1, T2> {
        public final T1 value1;
        public final T2 value2;

        public Two(T1 value1, T2 value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    public static class Three<T1, T2, T3> {
        public final T1 value1;
        public final T2 value2;
        public final T3 value3;

        public Three(T1 value1, T2 value2, T3 value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }
    }

    public static class Four<T1, T2, T3, T4> {
        public final T1 value1;
        public final T2 value2;
        public final T3 value3;
        public final T4 value4;

        public Four(T1 value1, T2 value2, T3 value3, T4 value4) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
        }
    }

    public static class Five<T1, T2, T3, T4, T5> {
        public final T1 value1;
        public final T2 value2;
        public final T3 value3;
        public final T4 value4;
        public final T5 value5;

        public Five(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
            this.value5 = value5;
        }
    }
}
