package gmutils.net.retrofit.responseHolders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

public abstract class BaseStringResponse extends BaseResponse implements CharSequence {
    private String text;

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return text.subSequence(start, end);
    }

}
