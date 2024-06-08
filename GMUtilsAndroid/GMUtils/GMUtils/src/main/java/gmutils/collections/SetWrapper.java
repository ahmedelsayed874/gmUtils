package gmutils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Set;

import gmutils.collections.dataGroup.DataGroup1;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;


public class SetWrapper<T> {

    public static <T> SetWrapper<T> create(Class<T> tClass) {
        return new SetWrapper<>(null);
    }

    public static <T> SetWrapper<T> create(Set<T> set) {
        return new SetWrapper<>(set);
    }

    //----------------------------------------------------------------------------------------------

    private final Set<T> set;

    private SetWrapper(Set<T> set) {
        if (set == null) {
            this.set = new HashSet<>();
        } else {
            this.set = set;
        }
    }

    //region add methods
    public SetWrapper<T> add(T item) {
        this.set.add(item);
        return this;
    }

    public SetWrapper<T> add(Set<T> otherSet) {
        this.set.addAll(otherSet);
        return this;
    }

    public SetWrapper<T> add(List<T> items) {
        if (items == null) return this;
        set.addAll(items);
        return this;
    }
    
    public SetWrapper<T> add(T[] items) {
        if (items == null) return this;
        set.addAll(Arrays.asList(items));
        return this;
    }
    //endregion

    //region remove methods
    public SetWrapper<T> remove(T item) {
        this.set.remove(item);
        return this;
    }

    public SetWrapper<T> remove(ActionCallback<T, Boolean> remover) {
        if (remover == null) return this;

        LinkedList<T> itemsToRemove = new LinkedList<>();

        for (T item : set) {
            Boolean rem = remover.invoke(item);
            if (rem == null) rem = false;

            if (rem)
                itemsToRemove.add(item);
        }
        
        for (T item : itemsToRemove) {
            set.remove(item);
        }

        return this;
    }
    //endregion

    //region processing methods
    /**
     * @return new SetWrapper with filtered items
     */
    public SetWrapper<T> filterAndSwitch(ActionCallback<T, Boolean> action) {
        Set<T> newSet = filter(action);
        return SetWrapper.create(newSet);
    }

    public SetWrapper<T> filter(ActionCallback<T, Boolean> action, ResultCallback<SetWrapper<T>> result) {
        if (result == null) throw new IllegalArgumentException();
        Set<T> newSet = filter(action);
        result.invoke(SetWrapper.create(newSet));
        return this;
    }

    public Set<T> filter(ActionCallback<T, Boolean> action) {
        if (action == null) return null;

        Set<T> newSet = new HashSet<>();

        for (T item : set) {
            Boolean selected = action.invoke(item);
            if (selected == null) selected = false;
            if (selected) {
                newSet.add(item);
            }
        }

        return newSet;
    }


    /**
     * @return new SetWrapper with mapped items
     */
    public <Tn> SetWrapper<Tn> mapAndSwitch(ActionCallback<T, Tn> action) {
        Set<Tn> set = map(action);
        return SetWrapper.create(set);
    }

    public <Tn> SetWrapper<T> map(ActionCallback<T, Tn> action, ResultCallback<SetWrapper<Tn>> result) {
        if (result == null) throw new IllegalArgumentException();

        Set<Tn> set = map(action);
        result.invoke(SetWrapper.create(set));

        return this;
    }

    public <Tn> Set<Tn> map(ActionCallback<T, Tn> action) {
        if (action == null) throw new IllegalArgumentException();

        Set<Tn> outSet = new HashSet<>();

        for (T t : set) {
            Tn newValue = action.invoke(t);
            outSet.add(newValue);
        }

        return outSet;
    }


    public SetWrapper<T> serialize(ActionCallback<DataGroup1<T>, String> action, ResultCallback<String> result) {
        if (action == null) throw new IllegalArgumentException();
        if (result == null) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder();

        for (T item : set) {
            sb.append(action.invoke(new DataGroup1<>(item)));
        }

        result.invoke(sb.toString());

        return this;
    }

    public String serialize(ActionCallback<DataGroup1<T>, String> action) {
        final String[] result = new String[1];

        serialize(action, r -> {
            result[0] = r;
        });

        return result[0];
    }

    public String serialize(String prefix, String postfix) {
        StringBuilder sb = new StringBuilder();

        for (T item : set) {
            sb.append(prefix);

            if (item != null)
                sb.append(item);
            else
                sb.append("null");

            sb.append(postfix);
        }

        return sb.toString();
    }

    public ListWrapper<T> convertToList() {
        ListWrapper<T> listWrapper = ListWrapper.create(new ArrayList<>());
        for (T t : set) listWrapper.add(t);
        return listWrapper;
    }

    //endregion

    public Set<T> getSet() {
        return set;
    }
}
