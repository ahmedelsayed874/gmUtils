package gmutilssupport.ui.adapters;

import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gmutils.collections.dataGroup.DataGroup3;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.SimpleWindowAttachListener;
import gmutilssupport.ui.utils.DumbViewBinding;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder> {

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
                            boolean delete = onDelete.invoke(new DataGroup3<>(BaseRecyclerAdapter.this, mList.get(position), position));
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

    @Nullable
    public RecyclerView getRecyclerView() {
        RecyclerView recyclerView = mRecyclerView.get();
        return recyclerView;
    }

    public void dispose() {
        unregisterAdapterDataObserver(adapterDataObserver);
        adapterDataObserver = null;

        mList = null;
        mClickListener = null;
        mLongClickListener = null;
        mOnDataSetChangedListener = null;
        mOnListItemsChangedListener = null;
        mOnLoadMoreListener = null;

        onDispose();
    }

    protected abstract void onDispose();

    public void scrollToEnd() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.scrollToPosition(mList.size() - 1);
        }
    }

    public void smoothScrollToEnd() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(mList.size() - 1);
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
        changeDataSet(newList, refresh);
    }

    public void changeDataSet(List<T> newList, boolean refresh) {
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

        mList.add(item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void add(List<T> items, boolean refresh) {
        if (items == null || items.size() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        mList.addAll(items);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsAdded(this, items);
    }

    public void addOnTop(T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        mList.add(0, item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void addOnTop(List<T> items, boolean refresh) {
        if (items == null || items.size() == 0) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        mList.addAll(0, items);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsAdded(this, items);
    }

    public void replace(T oldItem, T newItem, boolean refresh) {
        if (oldItem == null || newItem == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        int i = mList.indexOf(oldItem);
        if (i >= 0) mList.remove(i);

//        if (i == 0) isFirstItemInitialized = false;

        mList.add(i, newItem);

        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, newItem);
    }

    public void insert(int position, T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }

        mList.add(position, item);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemAdded(this, item);
    }

    public void remove(T item, boolean refresh) {
        if (item == null) {
            if (refresh) notifyDataSetChanged();
            return;
        }
        int position = mList.indexOf(item);
        mList.remove(position);
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemRemoved(this, item, position);
    }

    public void removeAt(int position, boolean refresh) {
        try {
            T item = mList.get(position);
            mList.remove(position);
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
        mList.removeAll(items);
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
                removedItems.add(mList.get(firstIndex));
            }
            mList.remove(firstIndex);
            i++;
        }

        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemsRemoved(this, removedItems);
    }

    public void clear(boolean refresh) {
        isFirstItemInitialized = false;

        mList.clear();
        if (refresh) notifyDataSetChanged();

        if (mOnListItemsChangedListener != null)
            mOnListItemsChangedListener.onItemCleared(this);
    }

    public T getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public boolean hasItem(@NotNull ActionCallback<T, Boolean> comparator) {
        for (T it : mList) {
            if (comparator.invoke(it)) return true;
        }

        return false;
    }

    public boolean hasItem(T item) {
        return mList.contains(item);
    }

    public boolean hasItems() {
        return (mList != null && mList.size() > 0);
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

    public ClickListener<T> getClickListener() {
        return mClickListener;
    }

    public LongClickListener<T> getLongClickListener() {
        return mLongClickListener;
    }

    public OnLoadMoreListener<T> getOnLoadMoreListener() {
        return mOnLoadMoreListener;
    }

    public RecyclerViewPaginationListener getPaginationListener() {
        return mPaginationListener;
    }

    public OnDataSetChangedListener<T> getOnDataSetChangedListener() {
        return mOnDataSetChangedListener;
    }

    public OnListItemsChangedListener<T> getOnListItemsChangedListener() {
        return mOnListItemsChangedListener;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int getItemCount() {
        return mList.size();
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
    protected abstract ViewHolder getViewHolder(int viewType, @NotNull LayoutInflater inflater, ViewGroup container);

//    @NotNull
//    protected abstract ViewSource getViewSource(int viewType, @NotNull LayoutInflater inflater, ViewGroup container);
//    protected abstract ViewHolder getViewHolder(View view, int viewType);
//    protected ViewHolder getViewHolder(ViewBinding viewBinding, int viewType) { return null; }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        /*ViewSource viewSource = getViewSource(viewType, LayoutInflater.from(parent.getContext()), parent);
        assert viewSource != null;

        ViewBinding viewBinding = null;

        if (viewSource instanceof ViewSource.LayoutResource) {
            int resId = ((ViewSource.LayoutResource) viewSource).getResourceId();
            View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            viewBinding = new DumbViewBinding(view);

        } else if (viewSource instanceof ViewSource.ViewBinding) {
            viewBinding = ((ViewSource.ViewBinding) viewSource).getViewBinding();

        } else if (viewSource instanceof ViewSource.View) {
            View view = ((ViewSource.View) viewSource).getView();
            viewBinding = new DumbViewBinding(view);
        }

        return getViewHolder(viewBinding, viewType);*/

        return getViewHolder(viewType, LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        holder.setValuesInner(mList.get(position), position);

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

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ViewBinding viewBinding;
        private int itemPosition;

        public ViewHolder(@LayoutRes int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
            this(inflater.inflate(resId, container, false));
        }

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                view.getViewTreeObserver().addOnWindowAttachListener(
                        new SimpleWindowAttachListener() {
                            @Override
                            public void onWindowAttached() {
                            }

                            @Override
                            public void onWindowDetached() {
                                dispose();
                            }
                        }
                );
            }
        }

        public ViewHolder(ViewBinding viewBinding) {
            this(viewBinding.getRoot());
            this.viewBinding = viewBinding;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                SimpleWindowAttachListener listener = new SimpleWindowAttachListener() {
                    @Override
                    public void onWindowAttached() {
                    }

                    @Override
                    public void onWindowDetached() {
                        try {
                            if (BaseRecyclerAdapter.ViewHolder.this.viewBinding instanceof DumbViewBinding) {
                                ((DumbViewBinding) BaseRecyclerAdapter.ViewHolder.this.viewBinding).dispose();
                            }
                        } catch (Exception ignored) {
                        }

                        try {
                            BaseRecyclerAdapter.ViewHolder.this.viewBinding = null;
                        } catch (Exception ignored) {
                        }

                        dispose();
                    }
                };

                try {
                    viewBinding.getRoot().getViewTreeObserver().addOnWindowAttachListener(listener);
                } catch (Exception ignored) {
                }
            }
        }

        @Nullable
        public ViewBinding getViewBinding() {
            return viewBinding;
        }

        public <V extends View> V findViewById(@IdRes int resId) {
            return itemView.findViewById(resId);
        }

        private void setValuesInner(T item, int position) {
            this.itemPosition = position;
            setValues(item);
        }

        protected abstract void setValues(T item);

        public T getItem() {
            return mList.get(itemPosition);
        }

        public int getItemPosition() {
            return itemPosition;
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onItemClicked(BaseRecyclerAdapter.this, itemView, getItem(), itemPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickListener != null)
                return mLongClickListener.onItemLongClicked(BaseRecyclerAdapter.this, itemView, getItem(), getAdapterPosition());
            else
                return false;
        }

        protected abstract void dispose();
    }

    //------------------------------------------------------------------------------------------------------------------

    public interface ClickListener<T> {
        void onItemClicked(BaseRecyclerAdapter<T> adapter, View itemView, T item, int position);
    }

    public interface LongClickListener<T> {
        boolean onItemLongClicked(BaseRecyclerAdapter<T> adapter, View itemView, T item, int position);
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
