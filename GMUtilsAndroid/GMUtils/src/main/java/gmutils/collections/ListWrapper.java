package gmutils.collections;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gmutils.listeners.ActionCallback;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ResultCallback;

public class ListWrapper<T> {

    public static <T> ListWrapper<T> create(Class<T> cls) {
        return new ListWrapper<>(null);
    }

    public static <T> ListWrapper<T> create(List<T> list) {
        return new ListWrapper<>(list);
    }

    //----------------------------------------------------------------------------------------------

    private final List<T> list;

    private ListWrapper(List<T> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
    }

    //region add methods
    public ListWrapper<T> add(T item) {
        this.list.add(item);
        return this;
    }

    public ListWrapper<T> add(T item, int at) {
        this.list.add(at, item);
        return this;
    }

    public ListWrapper<T> add(List<T> items) {
        this.list.addAll(items);
        return this;
    }

    public ListWrapper<T> add(List<T> items, int at) {
        this.list.addAll(at, items);
        return this;
    }

    public ListWrapper<T> add(int count, ActionCallback<Integer, T> adder) {
        if (count == 0 || adder == null) return this;

        for (int i = 0; i < count; i++) {
            T item = adder.invoke(i);
            add(item);
        }

        return this;
    }
    //endregion

    //region remove methods
    public ListWrapper<T> remove(T item) {
        this.list.remove(item);
        return this;
    }

    public ListWrapper<T> removeAt(int index) {
        this.list.remove(index);
        return this;
    }

    public ListWrapper<T> remove(ActionCallback2<Integer, T, Boolean> remover) {
        if (remover == null) return this;

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Boolean rem = remover.invoke(i, list.get(i));
            if (rem == null) rem = false;

            if (rem) {
                removeAt(i);
                size--;
                i--;
            }
        }

        return this;
    }
    //endregion

    //region processing methods
    /**
     * @return new ListWrapper with filtered items
     */
    public ListWrapper<T> filterAndSwitch(ActionCallback<T, Boolean> action) {
        List<T> newList = filter(action);
        return ListWrapper.create(newList);
    }

    public ListWrapper<T> filter(ActionCallback<T, Boolean> action, ResultCallback<ListWrapper<T>> result) {
        if (result == null) throw new IllegalArgumentException();

        List<T> newList = filter(action);
        result.invoke(ListWrapper.create(newList));

        return this;
    }

    public List<T> filter(ActionCallback<T, Boolean> action) {
        if (action == null) throw new IllegalArgumentException();

        List<T> newList = new ArrayList<>();

        for (T t : list) {
            Boolean selected = action.invoke(t);
            if (selected == null) selected = false;
            if (selected) {
                newList.add(t);
            }
        }

        return newList;
    }


    /**
     * @return new ListWrapper with mapped items
     */
    public <M> ListWrapper<M> mapAndSwitch(ActionCallback<T, M> action) {
        List<M> outList = this.map(action);
        return ListWrapper.create(outList);
    }

    public <M> ListWrapper<T> map(ActionCallback<T, M> action, ResultCallback<ListWrapper<M>> result) {
        if (result == null) throw new IllegalArgumentException();

        List<M> outList = this.map(action);
        result.invoke(ListWrapper.create(outList));

        return this;
    }

    public <M> ListWrapper<T> map(List<M> outList, ActionCallback<T, M> action) {
        if (outList == null) throw new IllegalArgumentException();
        if (action == null) throw new IllegalArgumentException();

        for (T t : this.list) {
            M value = action.invoke(t);
            outList.add(value);
        }

        return this;

    }

    public <M> List<M> map(ActionCallback<T, M> action) {
        List<M> outList = new ArrayList<>(this.list.size());

        map(outList, action);

        return outList;
    }

    public ListWrapper<T> performAction(ActionCallback<ListWrapper<T>, Void> action) {
        if (action != null)
            action.invoke(this);
        return this;
    }
    //endregion

    public List<T> getList() {
        return list;
    }
}
