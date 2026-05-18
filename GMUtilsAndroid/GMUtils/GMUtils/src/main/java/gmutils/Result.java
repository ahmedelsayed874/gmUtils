package gmutils;

public class Result<T> {
    public final T value;
    public final StringSet error;

    public Result(T value) {
        this(value, (StringSet) null);
    }

    public Result(T value, CharSequence error) {
        this(value, error == null ? null : new StringSet(error));
    }

    public Result(T value, StringSet error) {
        this.value = value;
        this.error = error;
    }

    @Override
    public String toString() {
        return "Result{" +
                "value=" + value +
                ", error=" + error +
                '}';
    }
}
