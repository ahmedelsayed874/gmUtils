package gmutils.ui.adapters;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseRecyclerAdapterDiffCallback<T> extends DiffUtil.Callback {
    private final List<T> oldList;
    private final List<T> newList;

    protected BaseRecyclerAdapterDiffCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    public List<T> getOldList() {
        return oldList;
    }

    @Override
    public int getOldListSize() {
        if (oldList == null) return 0;
        return oldList.size();
    }

    public List<T> getNewList() {
        return newList;
    }

    @Override
    public int getNewListSize() {
        if (newList == null) return 0;
        return newList.size();
    }

}

