package com.blogspot.gm4s1.gmutils._ui._bases.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public abstract class MultiListAdapter extends RecyclerView.Adapter<MultiListAdapter.ViewHolder> {
    private static final int VIEW_TYPE_TITLE = 1;
    private static final int VIEW_TYPE_CONTENT = 2;

    private final WeakReference<RecyclerView> mRecyclerView;
    private final List<ListWrapper> wrapperList;

    public MultiListAdapter(RecyclerView recyclerView, @NonNull ListWrapper... listWrappers) {
        int x = listWrappers.length; //to throw exception if list equal null
        wrapperList = Arrays.asList(listWrappers);

        mRecyclerView = new WeakReference<>(recyclerView);
        recyclerView.post(() -> setupWithRecyclerView(recyclerView, true));
    }

    public synchronized void setupWithRecyclerView(RecyclerView recyclerView, boolean vertical) {
        recyclerView.setLayoutManager(new LinearLayoutManager(
                recyclerView.getContext(),
                vertical? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL,
                false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this);
    }

    public Context getContext() {
        RecyclerView recyclerView = mRecyclerView.get();
        if (recyclerView != null)
            return recyclerView.getContext();

        return null;
    }

    //--------------------------------------------------------------------------------------------//

    public void addList(@NonNull ListWrapper listWrapper, boolean refresh) {
        if (listWrapper == null) return;

        this.wrapperList.add(listWrapper);
        if (refresh) notifyDataSetChanged();
    }

    public void removeList(@NonNull ListWrapper listWrapper, boolean refresh) {
        if (listWrapper == null) return;

        this.wrapperList.remove(listWrapper);
        if (refresh) notifyDataSetChanged();
    }

    public void removeList(int idOfListWrapper, boolean refresh) {
        for (int i = 0; i < wrapperList.size(); i++) {
            ListWrapper listWrapper = wrapperList.get(i);
            if (listWrapper.id == idOfListWrapper) {
                wrapperList.remove(i);
                break;
            }
        }
        if (refresh) notifyDataSetChanged();
    }

    public void clearList(int idOfListWrapper, boolean refresh) {
        for (int i = 0; i < wrapperList.size(); i++) {
            ListWrapper listWrapper = wrapperList.get(i);
            if (listWrapper.id == idOfListWrapper) {
                listWrapper.list.clear();
                break;
            }
        }
        if (refresh) notifyDataSetChanged();
    }


    public void addItem(int idOfListWrapper, Object item, boolean refresh) {
        if (item == null) return;

        for (ListWrapper listWrapper : wrapperList) {
            if (listWrapper.id == idOfListWrapper) {
                listWrapper.list.add(item);
                break;
            }
        }

        if (refresh) notifyDataSetChanged();
    }

    public void addItem(Object item, int listPosition, boolean refresh) {
        if (item == null) return;

        wrapperList.get(listPosition).list.add(item);
        if (refresh) notifyDataSetChanged();
    }

    public void addItems(int idOfListWrapper, List items, boolean refresh) {
        if (items == null) return;

        for (ListWrapper listWrapper : wrapperList) {
            if (listWrapper.id == idOfListWrapper) {
                listWrapper.list.addAll(items);
                break;
            }
        }
        if (refresh) notifyDataSetChanged();
    }

    public void addItems(List items, int listPosition, boolean refresh) {
        if (items == null) return;

        wrapperList.get(listPosition).list.addAll(items);
        if (refresh) notifyDataSetChanged();
    }


    public void removeItem(int idOfListWrapper, Object item, boolean refresh) {
        if (item == null) return;

        for (ListWrapper listWrapper : wrapperList) {
            if (listWrapper.id == idOfListWrapper) {
                listWrapper.list.remove(item);
                break;
            }
        }

        if (refresh) notifyDataSetChanged();
    }

    public void removeItem(Object item, int listPosition, boolean refresh) {
        if (item == null) return;

        wrapperList.get(listPosition).list.remove(item);
        if (refresh) notifyDataSetChanged();
    }

    public void removeItems(int idOfListWrapper, List items, boolean refresh) {
        if (items == null) return;

        for (ListWrapper listWrapper : wrapperList) {
            if (listWrapper.id == idOfListWrapper) {
                listWrapper.list.removeAll(items);
                break;
            }
        }
        if (refresh) notifyDataSetChanged();
    }

    public void removeItems(List items, int listPosition, boolean refresh) {
        if (items == null) return;

        wrapperList.get(listPosition).list.removeAll(items);
        if (refresh) notifyDataSetChanged();
    }


    public ListWrapper getListById(int idOfListWrapper) {
        for (ListWrapper listWrapper : wrapperList) {
            if (listWrapper.id == idOfListWrapper) {
                return listWrapper;
            }
        }

        return null;
    }

    public ListWrapper getListByPosition(int listPosition) {
        return wrapperList.get(listPosition);
    }


    //--------------------------------------------------------------------------------------------//

    @Override
    public int getItemCount() {
        int count = 0;
        for (ListWrapper listWrapper : wrapperList) {
            count += listWrapper.getItemsCount();
        }
        return count;
    }

    public int getListWrappersCount() {
        return wrapperList.size();
    }

    private int[] getItemPositionWithinList(int position) {
        int count = 0;
        int size = wrapperList.size();

        for (int i = 0; i < size; i++) {
            ListWrapper listWrapper = wrapperList.get(i);
            count += listWrapper.getItemsCount();

            if (position < count) {
                position = listWrapper.getItemsCount() - count + position;

                return new int[]{i, position};
            }
        }

        return null;
    }

    public Object getItemAtPosition(int position) {
        int[] pos = getItemPositionWithinList(position);
        ListWrapper listWrapper = wrapperList.get(pos[0]);
        position = pos[1];

        if (listWrapper.hasTitle()) {
            if (position == 0) return listWrapper.getTitle();
            else return listWrapper.getItem(position - 1);
        } else {
            return listWrapper.getItem(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int[] pos = getItemPositionWithinList(position);
        ListWrapper listWrapper = wrapperList.get(pos[0]);
        position = pos[1];

        if (position == 0) {
            if (listWrapper.hasTitle()) return VIEW_TYPE_TITLE;
            else return VIEW_TYPE_CONTENT;
        } else {
            return VIEW_TYPE_CONTENT;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = 0;
        if (viewType == VIEW_TYPE_TITLE) layoutRes = getTitleLayoutRes();
        else if (viewType == VIEW_TYPE_CONTENT) layoutRes = getContentLayoutRes();

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);

        return getViewHolder(view, viewType == VIEW_TYPE_TITLE);
    }

    @LayoutRes
    protected abstract int getTitleLayoutRes();

    @LayoutRes
    protected abstract int getContentLayoutRes();

    protected abstract ViewHolder getViewHolder(View view, boolean viewTypeTitle);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int[] pos = getItemPositionWithinList(position);

        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_TITLE) holder.setTitle((String) getItemAtPosition(position));
        else if (viewType == VIEW_TYPE_CONTENT) holder.setValues(wrapperList.get(pos[0]), getItemAtPosition(position));
    }


    //--------------------------------------------------------------------------------------------//

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        private ListWrapper listWrapper;

        public ViewHolder(View v) {
            super(v);
        }

        public abstract void setTitle(String title);

        private void setValues(ListWrapper listWrapper, Object data) {
            this.listWrapper = listWrapper;
            setValues(listWrapper.id, data);
        }

        public abstract void setValues(int listId, Object data);

        public ListWrapper getListWrapper() {
            return listWrapper;
        }

        public Object getItem() {
            return getItemAtPosition(getAdapterPosition());
        }
    }

    //--------------------------------------------------------------------------------------------//

    public static class ListWrapper<T> {
        private final int id;
        private final String title;
        private final List<T> list;
        private final Object parent;

        public ListWrapper(int id, String title, @NonNull List<T> list) {
            this(id, title, list, null);
        }

        public ListWrapper(int id, String title, @NonNull List<T> list, Object parent) {
            this.id = id;
            this.title = title;

            this.list = list;
            list.size();   //to throw exception if list null

            this.parent = parent;
        }

        public List<T> getList() {
            return list;
        }

        int getItemsCount() {
            int count = 0;
            if (list.size() > 0) {
                if (hasTitle()) count += 1;
                count += list.size();
            }
            return count;
        }

        boolean hasTitle() {
            return !TextUtils.isEmpty(title);
        }

        public String getTitle() {
            return title;
        }

        T getItem(int position) {
            return list.get(position);
        }

        public Object getParent() {
            return parent;
        }
    }

}
