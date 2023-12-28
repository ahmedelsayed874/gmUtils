package gmutils.net.retrofit.responseHolders;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gmutils.listeners.ActionCallback;

public abstract class BaseListResponse<T> extends BaseResponse implements java.util.List<T> {

    public final ArrayList<T> list = new ArrayList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    //----------------------------------------------------------------------------------------------

    private boolean allowCopyList = true;

    @Override
    public void copyFrom(@NonNull BaseResponse otherResponse) {
        super.copyFrom(otherResponse);
        if (allowCopyList) {
            if (otherResponse instanceof BaseListResponse) {
                ArrayList otherList = ((BaseListResponse) otherResponse).list;
                this.list.clear();
                this.list.addAll((Collection<? extends T>) otherList);
            }
        }
        allowCopyList = true;
    }

    public <Tc> void cast(BaseListResponse<Tc> otherResponse, ActionCallback<T, Tc> converter) {
        otherResponse.allowCopyList = false;
        otherResponse.copyFrom(this);

        otherResponse.list.clear();
        for (T item : this.list) {
            Tc item2 = converter.invoke(item);
            otherResponse.add(item2);
        }
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (T t : list) {
            if (s.length() > 0)
                s.append(", ");

            if (t != null)
                s.append(t.toString());
        }
        return "BaseListResponse{" + "\n" +
                "list=' [" + list.size() + "-items] [" + s.toString() + "]" + '\'' + ",\n" +
                "callbackStatus='" + getCallbackStatus() + '\'' + ",\n" +
                "code=" + _code + ",\n" +
                "error='" + _error + '\'' + ",\n" +
                "extras=" + _extras + ",\n" +
                "requestTime=" + _requestTime + ",\n" +
                "responseTime=" + _responseTime + "\n" +
                '}';
    }
}
