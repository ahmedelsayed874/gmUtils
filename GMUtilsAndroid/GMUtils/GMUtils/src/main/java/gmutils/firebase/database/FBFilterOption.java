package gmutils.firebase.database;

import java.util.Objects;

public class FBFilterOption {
    public final FBFilterTypes type;
    public final String key;
    public  final Object args;
    public final Limit limit;

    public FBFilterOption(FBFilterTypes type, String key, Object args) {
        this(type, key, args, null);
    }

    public FBFilterOption(FBFilterTypes type, String key, Object args, Limit limit) {
        this.type = type;
        this.key = key;
        this.args = args;
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FBFilterOption that)) return false;
        return type == that.type && Objects.equals(key, that.key) && Objects.equals(args, that.args) && Objects.equals(limit, that.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key, args, limit);
    }

    @Override
    public String toString() {
        return "FBFilterOption{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", args=" + args +
                ", limit=" + limit +
                '}';
    }

    public static class Limit {
        public final int count;
        public final boolean fromStart;

        public Limit(int count, boolean fromStart) {
            this.count = count;
            this.fromStart = fromStart;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Limit limit)) return false;
            return count == limit.count && fromStart == limit.fromStart;
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, fromStart);
        }

        @Override
        public String toString() {
            return "Limit{" +
                    "count=" + count +
                    ", fromStart=" + fromStart +
                    '}';
        }
    }
}
