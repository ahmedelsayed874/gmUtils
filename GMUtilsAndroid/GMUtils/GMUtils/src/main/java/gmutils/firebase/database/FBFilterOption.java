package gmutils.firebase.database;

public class FBFilterOption {
    public final FBFilterTypes type;
    public final String key;
    public  final Object args;
    public final Integer limit;

    public FBFilterOption(FBFilterTypes type, String key, Object args, Integer limit) {
        this.type = type;
        this.key = key;
        this.args = args;
        this.limit = limit;
    }
}
