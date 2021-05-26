package gmutils.net.retrofit.responseHolders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import gmutils.net.retrofit.RetrofitService;

public final class StringResponse extends BaseResponse {
    private String text;

    public StringResponse(String text) {
        Log.d(StringResponse.class.getSimpleName(), "Please make sure of enable " +
                "String Response Converter when calling " +
                "\"" + RetrofitService.class.getName() + "\" through \"" + RetrofitService.Parameters.class.getName() + "\"");
    }

    public String getText() {
        return text;
    }

    @Override
    public Statuses getResponseStatus() {
        return null;
    }
}
