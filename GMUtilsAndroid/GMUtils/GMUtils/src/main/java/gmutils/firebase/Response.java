package gmutils.firebase;

import gmutils.StringSet;

public class Response<DATA> {
    public final DATA data;
    public final StringSet error;
    public final boolean connectionFailed;

    private Response(DATA data, StringSet error, boolean connectionFailed) {
        this.data = data;
        this.error = error;
        this.connectionFailed = connectionFailed;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(data, null, false);
    }

    public static <T> Response<T> failed(StringSet error) {
        return new Response<>(null, error, false);
    }

    public static <T> Response<T> failed(StringSet error, boolean connectionFailed) {
        return new Response<>(null, error, connectionFailed);
    }

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                ", error=" + error +
                ", connectionFailed=" + connectionFailed +
                '}';
    }
}
