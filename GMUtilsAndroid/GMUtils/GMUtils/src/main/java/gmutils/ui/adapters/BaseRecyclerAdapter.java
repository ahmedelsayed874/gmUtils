package gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gmutils.collections.dataGroup.DataGroup3;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.RecyclerViewPaginationListener;
import gmutils.listeners.SimpleWindowAttachListener;


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
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapterViewHolder<T>> {

    private final WeakReference<RecyclerView> mRecyclerView;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private List<T> mList;
    private ClickListener<T> mClickListener;
    private LongClickListener<T> mLongClickListener;
    private OnDataSetChangedListener<T> mOnDataSetChangedListener;
    private OnListItemsChangedListener<T> mOnListItemsChangedListener;
    private OnLoadMoreListener<T> mOnLoadMoreListener;
    private Boolean isFirstItemInitialized = false;
    private RecyclerViewPaginationListener mPaginationListener;
    private boolean enableEndlessScroll = false;


    public BaseRecyclerAdapter(RecyclerView recyclerView) {
        this(recyclerView, new ArrayList<T>());
    }

    public BaseRecyclerAdapter(RecyclerView recyclerView, List<T> list) {
        this(recyclerView, list, true);
    }

    public BaseRecyclerAdapter(RecyclerView recyclerView, List<T> list, boolean vertical) {
        this(recyclerView, list, true, vertical, 0);
    }

    public BaseRecyclerAdapter(RecyclerView recyclerView, List<T> list, int gridColumnCount, boolean horizontal) {
        this(recyclerView, list, false, !horizontal, gridColumnCount);
    }

    private BaseRecyclerAdapter(RecyclerView recyclerView, List<T> list, boolean linearLayout, boolean vertical, int gridColumnCount) {
        mRecyclerView = new WeakReference<>(recyclerView);

        mList = list;
        if (mList == null) mList = new ArrayList<T>();

        if (recyclerView != null) {
            setupLayout(recyclerView, linearLayout, vertical, gridColumnCount);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                setupDetachListener(recyclerView);
            }
        }

        registerAdapterDataObserver();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void setupLayout(RecyclerView recyclerView, boolean linearLayout, boolean vertical, int gridColumnCount) {
        recyclerView.post(() -> {
            RecyclerView recyclerView1 = mRecyclerView.get();
            if (recyclerView1 != null) {
                if (linearLayout) setupWithRecyclerViewAsLinear(recyclerView1, vertical);
                else setupWithRecyclerViewAsGrid(recyclerView1, gridColumnCount, vertical);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setupDetachListener(RecyclerView recyclerView) {
        recyclerView.getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
            @Override
            public void onWindowAttached() {
            }

            @Override
            public void onWindowDetached() {
                dispose();
            }
        });
    }

    private void registerAdapterDataObserver() {
        registerAdapterDataObserver(adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                isFirstItemInitialized = false;
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0) isFirstItemInitialized = false;
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (positionStart == 0) isFirstItemInitialized = false;
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (fromPosition == 0) isFirstItemInitialized = false;
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    public synchronized void setupWithRecyclerViewAsLinear(RecyclerView recyclerView, boolean vertical) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                recyclerView.getContext(),
                vertical ? RecyclerView.VERTICAL : RecyclerView.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this);
    }

    public synchronized void setupWithRecyclerViewAsGrid(RecyclerView recyclerView, int spanCount, boolean horizontal) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                recyclerView.getContext(),
                spanCount,
                horizontal ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL,
                false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void enableDeleteItemOnSwipe() {
        enableDeleteItemOnSwipe(null);
    }

    public void enableDeleteItemOnSwipe(ActionCallback<DataGroup3<BaseRecyclerAdapter<T>, T, Integer>, Boolean> onDelete) {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    if (onDelete == null) {
                        removeAt(position, true);
                    } else {
                        try {
                            boolean delete = onDelete.invoke(new DataGroup3<>(BaseRecyclerAdapter.this, getItem(position), position));
                            if (delete) removeAt(position, true);
                            else notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            touchHelper.attachToRecyclerView(recyclerView);
        }
    }

    public void setEnableEndlessScroll(boolean enableEndlessScroll) {
        this.enableEndlessScroll = enableEndlessScroll;
    }

    public boolean isEnableEndlessScroll() {
        return enableEndlessScroll;
    }

    @Nullable
    public RecyclerView getRecyclerView() {
        RecyclerView recyclerView = mRecyclerView.get();
        return recyclerView;
    }

    public void dispose() {
        onDispose();

        try {
            unregisterAdapterDataObserver(adapterDataObserver);
        } catch (Exception ignored) {

        }
        adapterDataObserver = null;

        mList = null;
        mClickListener = null;
        mLongClickListener = null;
        mOnDataSetChangedListener = null;
        mOnListItemsChangedListener = null;
        mOnLoadMoreListener = null;
    }

    protected abstract void onDispose();

    //------------------------------------------------------------------------------------------------------------------

    public void scrollToEnd() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.scrollToPosition(getItemCount() - 1);
        }
    }

    public void smoothScrollToEnd() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(getItemCount() - 1);
        }
    }

    public void scrollToPosition(int position) {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
        }
    }

    public void smoothScrollToPosition(int position) {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    public void resetLoadingMoreFromTopStatus() {
        isFirstItemInitialized = false;
    }

    //----------------------------------------------------------------------------------------------

    public List<T> getList() {
        return mList;
    }

    public void replaceList(List<T> newList, boolean refresh) {
        isFirstItemInitialized = false;

        List<T> oldList = this.mList;
        this.mList = newList;
        if (refresh) notifyDataSetChanged();

        if (mOnDataSetChangedListener != null) {
            mOnDataSetChangedListener.onDataSetChanged(this, oldList, newList);
        }
    }

    public void add(T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        getList().add(item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void add(List<T> items, boolean refresh) {
        if (items == null || items.size() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        getList().addAll(items);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsAdded(this, items);
    }

    public void addOnTop(T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        getList().add(0, item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void addOnTop(List<T> items, boolean refresh) {
        if (items == null || items.size() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        getList().addAll(0, items);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsAdded(this, items);
    }

    public void replace(T oldItem, T newItem, boolean refresh) {
        if (oldItem == null || newItem == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        List<T> list = getList();

        int i = list.indexOf(oldItem);
        if (i >= 0) list.remove(i);

        list.add(i, newItem);

        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, newItem);
    }

    public void insert(int position, T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        getList().add(position, item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void remove(T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        int position = getList().indexOf(item);
        getList().remove(position);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemRemoved(this, item, position);
    }

    public void removeAt(int position, boolean refresh) {
        try {
            T item = getList().get(position);
            getList().remove(position);
            if (refresh) notifyDataSetChanged();

            if (mOnListItemsChangedListener != null)
                mOnListItemsChangedListener.onItemRemoved(this, item, position);
        } catch (Exception e) {
            e.printStackTrace();
            if (refresh) notifyDataSetChanged();
        }
    }

    public void remove(List<T> items, boolean refresh) {
        if (items == null || items.size() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        getList().removeAll(items);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsRemoved(this, items);
    }

    public void removeFirst(int count, boolean refresh) {
        removeRange(0, count - 1, refresh);
    }

    public void removeLast(int count, boolean refresh) {
        removeRange(getItemCount() - count, getItemCount() - 1, refresh);
    }

    public void removeRange(int firstIndex, int lastIndex, boolean refresh) {
        if (getItemCount() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        if (firstIndex < 0) firstIndex = 0;
        if (lastIndex >= getItemCount()) lastIndex = getItemCount() - 1;
        if (firstIndex > lastIndex) {
            int t = firstIndex;
            firstIndex = lastIndex;
            lastIndex = t;
        }

        List<T> removedItems = null;
        if (mOnListItemsChangedListener != null) removedItems = new ArrayList<>();

        int i = firstIndex;
        while (i <= lastIndex) {
            if (removedItems != null) {
                removedItems.add(getList().get(firstIndex));
            }
            getList().remove(firstIndex);
            i++;
        }

        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsRemoved(this, removedItems);
    }

    public void clear(boolean refresh) {
        isFirstItemInitialized = false;

        getList().clear();
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemCleared(this);
    }

    public T getItem(int position) {
        int size = getList().size();
        if (enableEndlessScroll) position = position % size;
        if (position < size) return getList().get(position);
        return null;
    }

    public boolean hasItem(@NotNull ActionCallback<T, Boolean> comparator) {
        List<T> list = getList();
        for (T it : list) {
            if (comparator.invoke(it)) return true;
        }

        return false;
    }

    public boolean hasItem(T item) {
        return getList().contains(item);
    }

    public boolean hasItems() {
        List<T> list = getList();
        return (list != null && list.size() > 0);
    }

    //----------------------------------------------------------------------------------------------

    public void setOnItemClickListener(ClickListener<T> clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOnItemLongClickListener(LongClickListener<T> longClickListener) {
        this.mLongClickListener = longClickListener;
    }

    public void setOnDataSetChangedListener(OnDataSetChangedListener<T> listener) {
        this.mOnDataSetChangedListener = listener;
    }

    public void setOnListItemsChangedListener(OnListItemsChangedListener<T> listener) {
        this.mOnListItemsChangedListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener<T> listener) {
        this.mOnLoadMoreListener = listener;
        this.isFirstItemInitialized = false;
    }

    public void setOnLoadMoreListener(RecyclerViewPaginationListener listener) {
        if (listener != null) {
            getRecyclerView().addOnScrollListener(listener);
            this.mPaginationListener = listener;

        } else {
            if (this.mPaginationListener != null) {
                RecyclerView rv = getRecyclerView();
                if (rv != null) {
                    rv.removeOnScrollListener(this.mPaginationListener);
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int getItemCount() {
        return enableEndlessScroll ? Integer.MAX_VALUE : getList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(getItem(position));
    }

    public int getItemViewType(T item) {
        return 0;
    }

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract BaseRecyclerAdapterViewHolder<T> getViewHolder(int viewType, @NotNull LayoutInflater inflater, ViewGroup container);

    @NotNull
    @Override
    public BaseRecyclerAdapterViewHolder<T> onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return getViewHolder(viewType, LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NotNull BaseRecyclerAdapterViewHolder<T> holder, int position) {
        holder.mClickListener = mClickListener;
        holder.mLongClickListener = mLongClickListener;
        holder.setValuesAndPosition(getItem(position), position);

        if (mOnLoadMoreListener != null) {
            try {
                if (position == 0) {
                    if (isFirstItemInitialized) {
                        mOnLoadMoreListener.onLoadingMore(this, false);
                    }

                    isFirstItemInitialized = true;

                } else if (position == getItemCount() - 1) {
                    mOnLoadMoreListener.onLoadingMore(this, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public interface ClickListener<T> {
        void onItemClicked(BaseRecyclerAdapterViewHolder<T> viewHolder, T item, int position);
    }

    public interface LongClickListener<T> {
        boolean onItemLongClicked(BaseRecyclerAdapterViewHolder<T> viewHolder, T item, int position);
    }

    public interface OnLoadMoreListener<T> {
        void onLoadingMore(BaseRecyclerAdapter<T> adapter, boolean toBottom);
    }

    public interface OnDataSetChangedListener<T> {
        void onDataSetChanged(BaseRecyclerAdapter<T> adapter, List oldList, List newList);
    }

    public abstract static class OnListItemsChangedListener<T> {
        public void onItemAdded(BaseRecyclerAdapter<T> adapter, T item) {
        }

        public void onItemsAdded(BaseRecyclerAdapter<T> adapter, List<T> item) {
        }

        public void onItemRemoved(BaseRecyclerAdapter<T> adapter, T item, int position) {
        }

        public void onItemsRemoved(BaseRecyclerAdapter<T> adapter, List<T> item) {
        }

        public void onItemCleared(BaseRecyclerAdapter<T> adapter) {
        }
    }

}
