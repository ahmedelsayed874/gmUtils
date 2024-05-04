package gmutils.firebase.database;

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

    public static class Limit {
        public final int count;
        public final boolean fromStart;

        public Limit(int count, boolean fromStart) {
            this.count = count;
            this.fromStart = fromStart;
        }
    }
}
